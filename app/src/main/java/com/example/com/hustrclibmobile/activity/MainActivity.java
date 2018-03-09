package com.example.com.hustrclibmobile.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.com.hustrclibmobile.R;
import com.example.com.hustrclibmobile.http.HttpUtil;
import com.example.com.hustrclibmobile.http.WebTime;
import com.example.com.hustrclibmobile.residemenu.ResideMenu;
import com.example.com.hustrclibmobile.residemenu.ResideMenuItem;
import com.example.com.hustrclibmobile.util.CountTimeUtil;
import com.example.com.hustrclibmobile.util.StringUtil;

public class MainActivity extends AppCompatActivity {
    private boolean flag=false;
    private  static CountTimeUtil timeUtil=null;
    private MainActivity mContext;
    private ResideMenu resideMenu;
    public Button openMenu;
    private LinearLayout noHttp; //未连接网络的布局
    private LinearLayout orderInfo;  //订单信息的布局
    private LinearLayout noOrder; //没有订单的布局
    private TextView name;
    private TextView userid;
    private TextView orderid;
    private TextView room;
    private TextView seat;
    private TextView stime;
    private TextView etime;
    private TextView daojishiTime;
    private TextView orderStatus;
    private ProgressDialog progressDialog;
    private Button refresh;
    private Button reload;
    private Button newOrder;
    private Button cancelSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpMenu();
        mContext = this;
        noHttp=(LinearLayout) this.findViewById(R.id.noHttp);
        orderInfo=(LinearLayout) this.findViewById(R.id.orderInfo);
        noOrder=(LinearLayout) this.findViewById(R.id.noOrder);
        progressDialog = new ProgressDialog(MainActivity.this);

        name=(TextView) this.findViewById(R.id.name);
        userid=(TextView) this.findViewById(R.id.userid);
        orderid=(TextView) this.findViewById(R.id.orderid);
        room=(TextView) this.findViewById(R.id.room);
        seat=(TextView) this.findViewById(R.id.seat);
        stime=(TextView) this.findViewById(R.id.stime);
        etime=(TextView) this.findViewById(R.id.etime);
        orderStatus=(TextView) this.findViewById(R.id.orderStatus);
        daojishiTime=(TextView) this.findViewById(R.id.daojishiTime);
        refresh=(Button)this.findViewById(R.id.title_bar_refresh);
        newOrder=(Button)this.findViewById(R.id.newOrder);
        reload=(Button)this.findViewById(R.id.reload);
        cancelSeat=(Button)this.findViewById(R.id.cancelseat);
        openMenu=(Button)findViewById(R.id.title_bar_menu);

        cancelSeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                AlertDialog.Builder builder = new Builder(mContext);
                builder.setMessage("确定要取消占座吗?");
                builder.setTitle("提示");
                builder.setPositiveButton("确认",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                cancelOrder();
                            }
                        });
                builder.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

            }
        });


        reload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                noOrder.setVisibility(View.GONE);
                noHttp.setVisibility(View.GONE);
                orderInfo.setVisibility(View.VISIBLE);
                getOrderInfo();

            }
        });

        newOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                Intent in=new Intent(MainActivity.this,OrderSeatActivity.class);
                startActivity(in);
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                getOrderInfo();
            }
        });

        openMenu.setOnClickListener(new View.OnClickListener() {
            boolean flag=false;
            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                if(!flag)
                {
                    resideMenu.openMenu();
                    flag=true;
                }else
                {
                    resideMenu.closeMenu();
                    flag=false;
                }

            }
        });
        getOrderInfo();
    }

    //连接服务端获取订单信息
    public void getOrderInfo()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String userId=sharedPreferences.getString("userId", "");
        final String JSESSIONID=sharedPreferences.getString("JSESSIONID", "");
        progressDialog.setMessage("正在查询中...");
        progressDialog.show();
        if(StringUtil.isEmpty(userId)||StringUtil.isEmpty(JSESSIONID))
        {
            Toast.makeText(getApplicationContext(),"获取用户信息失败！", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else
        {
            new Thread()
            {
                public void run()
                {
                    HttpUtil httputil=new HttpUtil();
                    httputil.setContext(mContext);

                    String result=httputil.SeartchOrderByUserid(userId, JSESSIONID);
                    final Date webTime= WebTime.getWebTime();
                    try {

                        if(result!=null)
                        {

                            final JSONObject js=new JSONObject(result);
                            String status=js.getString("status");
                            int int_status=Integer.parseInt(status);
                            if(int_status==1)//成功获取
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try{

                                            JSONObject orderinfo =js.getJSONObject("orderInfo");
                                            Log.i("orderinfo", orderinfo.toString());
                                            name.setText(orderinfo.getString("urealname"));
                                            userid.setText(orderinfo.getString("userid"));
                                            orderid.setText(orderinfo.getString("onum"));
                                            room.setText(orderinfo.getString("rnum"));
                                            seat.setText(orderinfo.getString("snum"));
                                            stime.setText(orderinfo.getString("ostime"));
                                            etime.setText(orderinfo.getString("oetime"));
                                            int o_sid=orderinfo.getInt("o_sid");

                                            SharedPreferences sh=getSharedPreferences("user", Context.MODE_APPEND);
                                            SharedPreferences.Editor editor=sh.edit();
                                            editor.putInt("o_sid", o_sid);
                                            editor.commit();

                                            orderStatus.setText(orderinfo.getString("ostatus"));
                                            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            Date date_etime=dateFormat.parse(orderinfo.getString("oetime"));
                                            long time=0;
                                            if(webTime==null)
                                            {
                                                Date sys_date=new Date();
                                                time=date_etime.getTime()-sys_date.getTime();
                                            }else
                                            {
                                                time=date_etime.getTime()-webTime.getTime();
                                            }
                                            if(time>0)
                                            {
                                                if(timeUtil!=null)
                                                {
                                                    timeUtil.cancel();
                                                }
                                                timeUtil=CountTimeUtil.getCountTimeUtil(time, 1000);
                                                timeUtil.setTextView(daojishiTime);
                                                timeUtil.setContext(mContext);
                                                timeUtil.start();

                                            }else
                                            {
                                                daojishiTime.setText("00:00:00");
                                            }

                                            progressDialog.dismiss();
                                            noHttp.setVisibility(View.GONE);
                                            noOrder.setVisibility(View.GONE);
                                            orderInfo.setVisibility(View.VISIBLE);

                                        }catch (Exception e) {
                                            // TODO: handle exception
                                            e.printStackTrace();
                                        }
                                    }
                                });


                            }else if(int_status==0) //尚无订单
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        noOrder.setVisibility(View.VISIBLE);
                                        noHttp.setVisibility(View.GONE);
                                        orderInfo.setVisibility(View.GONE);
                                    }
                                });


                            }else if(int_status==-1) //订单不止一条，出错
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "服务端数据出错！请到图书馆现场预定", Toast.LENGTH_SHORT).show();
                                        noHttp.setVisibility(View.VISIBLE);
                                        noOrder.setVisibility(View.GONE);
                                        orderInfo.setVisibility(View.GONE);
                                    }
                                });

                            }else if(int_status==2) //userid 为空
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "服务端数据出错，请重试！", Toast.LENGTH_SHORT).show();
                                        noHttp.setVisibility(View.VISIBLE);
                                        noOrder.setVisibility(View.GONE);
                                        orderInfo.setVisibility(View.GONE);
                                    }
                                });

                            }
                            else if(int_status==3) //登录失效，超时，或者未登录
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "用户登录已失效，请重新登录！", Toast.LENGTH_SHORT).show();
                                        noHttp.setVisibility(View.VISIBLE);
                                        noOrder.setVisibility(View.GONE);
                                        orderInfo.setVisibility(View.GONE);
                                        Intent in=new Intent(MainActivity.this,LoginActivity.class);
                                        startActivity(in);
                                    }
                                });
                            }
                        }else
                        {//没有获取到订单信息
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
                                    noHttp.setVisibility(View.VISIBLE);
                                    noOrder.setVisibility(View.GONE);
                                    orderInfo.setVisibility(View.GONE);
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

    public void cancelOrder()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final int o_sid=sharedPreferences.getInt("o_sid", 0);
        final String JSESSIONID=sharedPreferences.getString("JSESSIONID", "");
        progressDialog.setMessage("正在取消占座中...");
        progressDialog.show();
        if(o_sid==0||StringUtil.isEmpty(JSESSIONID))
        {
            Toast.makeText(getApplicationContext(),"获取用户信息失败！", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else
        {
            new Thread()
            {
                public void run()
                {
                    HttpUtil httputil=new HttpUtil();
                    httputil.setContext(mContext);
                    String result=httputil.cancelOrder(JSESSIONID,o_sid);
                    if(result==null)
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
                                noHttp.setVisibility(View.VISIBLE);
                                noOrder.setVisibility(View.GONE);
                                orderInfo.setVisibility(View.GONE);
                            }
                        });
                    }else
                    {
                        try {
                            JSONObject js=new JSONObject(result);
                            int status=js.getInt("status");
                            if(status==1) //取消成功
                            {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO 自动生成的方法存根
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "成功取消占座！", Toast.LENGTH_SHORT).show();
                                        getOrderInfo();

                                    }
                                });

                            }else if(status==0) //用户未登录，或者超时
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }else if(status==2) //未成功取消
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "后台出错，取消占座失败!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            // TODO 自动生成的 catch 块
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }


    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.main_bg);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        // create menu items;
        //监听其响应，可以写死Item的个数，添加进Menu 根据Item的不同实现不同的OnclickListenner
        String titles[] = { "我的订单", "预订座位", "阅览室列表","座位情况", "退出软件" };
        int icon[] = { R.drawable.home, R.drawable.order, R.drawable.room,R.drawable.seat, R.drawable.exit };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setTag(i);
            item.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(((Integer)v.getTag())==0)
                    {
                        resideMenu.closeMenu();
                    }
                    if(((Integer)v.getTag())==1)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(MainActivity.this,OrderSeatActivity.class);
                        startActivity(in);
                        finish();
                        //flag=false;
                    }
                    if(((Integer)v.getTag())==2)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(MainActivity.this,ShowRoomActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if(((Integer)v.getTag())==3)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(MainActivity.this,ShowSeatActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if(((Integer)v.getTag())==4)
                    {
                        resideMenu.closeMenu();
                        AlertDialog.Builder builder = new Builder(mContext);
                        builder.setMessage("确定要退出吗?");
                        builder.setTitle("提示");
                        builder.setPositiveButton("确认",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //android.os.Process.killProcess(android.os.Process.myPid());
                                        ExitApp();
                                    }
                                });
                        builder.setNegativeButton("取消",
                                new android.content.DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    }
                    // TODO 自动生成的方法存根
                    //	Toast.makeText(v.getContext(), "test", Toast.LENGTH_SHORT).show();

                }
            });
            resideMenu.addMenuItem(item);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.onInterceptTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //   Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show()
            flag=true;
        }

        @Override
        public void closeMenu() {
            //  Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
            flag=false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO 自动生成的方法存根
        if(keyCode==KeyEvent.KEYCODE_MENU)
        {
            if(!flag)
            {
                resideMenu.openMenu();
                flag=true;
                return super.onKeyDown(keyCode, event);
            }else
            {
                resideMenu.closeMenu();
                flag=false;
                return super.onKeyDown(keyCode, event);
            }
        }
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            AlertDialog.Builder builder = new Builder(this);
            builder.setMessage("确定要退出吗?");
            builder.setTitle("提示");
            builder.setPositiveButton("确认",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
            builder.setNegativeButton("取消",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }

        return super.onKeyDown(keyCode, event);
    }

    protected void ExitApp() {
        Intent intent = new Intent();
        intent.setAction("ExitApp");
        this.sendBroadcast(intent);
        super.finish();
    }

}
