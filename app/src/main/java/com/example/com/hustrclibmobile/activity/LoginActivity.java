package com.example.com.hustrclibmobile.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.com.hustrclibmobile.R;
import com.example.com.hustrclibmobile.http.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity  {
    private EditText userEdit;
    private EditText pwdEdit;
    private String userName;
    private String passWord;
    private Button loginBtn;
    private Button exitBtn;
    private Context mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        progressDialog = new ProgressDialog(LoginActivity.this);
        userEdit = (EditText)findViewById(R.id.username);
        pwdEdit = (EditText)findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.login);
        exitBtn = (Button)findViewById(R.id.exit);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userEdit.getText().toString();
                passWord = pwdEdit.getText().toString();
                progressDialog.setMessage("正在登录中...");
                progressDialog.show();
                if(TextUtils.isEmpty(userName)||TextUtils.isEmpty(passWord))
                {
                    Toast.makeText(LoginActivity.this,"用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
                    //prog.dismiss();
                    progressDialog.dismiss();
                }else
                {
                    new Thread()
                    {
                        public void run()
                        {
                            HttpUtil httputil=new HttpUtil();
                            httputil.setContext(mContext);

                            final String result=httputil.loginByGet(userName, passWord);
                            JSONObject loginresult;
                            try {
                                if(result!=null)
                                {
                                    loginresult = new JSONObject(result);
                                    String status=loginresult.getString("result");
                                    final int int_status=Integer.parseInt(status);
                                    if(int_status==1)
                                    {
                                        //	String JSESSIONID=loginresult.getString("JSESSIONID");
                                        String userRealname=loginresult.getString("userRealname");
                                        String userId=loginresult.getString("userId");
                                        SharedPreferences sh=getSharedPreferences("user", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sh.edit();
                                        //editor.putString("JSESSIONID", JSESSIONID);
                                        editor.putString("userRealname", userRealname);
                                        editor.putString("userId", userId);
                                        editor.commit();
                                    }
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            if(int_status==1)
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                LoginActivity.this.finish();
                                            }else if(int_status==0)
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "登录失败，请检查用户名和密码！", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                }
                                else
                                {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            //prog.dismiss();
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "登录失败！请检查网络！", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                // TODO 自动生成的 catch 块
                                e.printStackTrace();
                            }
                        }


                    }.start();
                }
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

}

