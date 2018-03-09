package com.example.com.hustrclibmobile.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.com.hustrclibmobile.R;
import com.example.com.hustrclibmobile.http.HttpUtil;
import com.example.com.hustrclibmobile.residemenu.ResideMenu;
import com.example.com.hustrclibmobile.residemenu.ResideMenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowRoomActivity extends AppCompatActivity {
    private boolean flag = false;

    private ListView roomList;
    private Button openMenu;
    private ResideMenu resideMenu;
    private ShowRoomActivity mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_room);

        mContext = this;
        progressDialog = new ProgressDialog(ShowRoomActivity.this);
        roomList = (ListView)findViewById(R.id.roomList);
        openMenu=(Button)findViewById(R.id.title_bar_menu);
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
        setUpMenu();
        showData();
    }

    public void showData() {
        final List<Map<String, String>> room_info = new ArrayList<Map<String, String>>();
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String JSESSIONID=sharedPreferences.getString("JSESSIONID", "");
        progressDialog.setMessage("正在查询中...");
        progressDialog.show();
        new Thread()
        {
            public void run() {
                HttpUtil httputil = new HttpUtil();
                httputil.setContext(mContext);
                String result1 = httputil.getRoomList(JSESSIONID);
                String result2 = httputil.getRoomNewList(JSESSIONID);

                if (result1 == null && result2 ==null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(ShowRoomActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try {
                        JSONObject js1 = new JSONObject(result1);
                        int status1 = js1.getInt("status");
                        JSONObject js2 = new JSONObject(result2);
                        int status2 = js2.getInt("status2");
                        if (status1 == 1 && status2 == 1) {
                            JSONArray rooms1 = js1.getJSONArray("rooms");
                            JSONArray rooms2 = js2.getJSONArray("rooms2");
                            for (int i = 0; i < rooms1.length(); i++) {
                                JSONObject room = rooms1.getJSONObject(i);
                                Map<String, String> map_room = new HashMap<String, String>();
                                String rnum = room.getString("rnum");
                                String rname = room.getString("rname");
                                String rseatnum = room.getString("rseatnum");
                                String rstatus = room.getString("rstatus");
                                map_room.put("roomNum", rnum);
                                map_room.put("roomName",rname);
                                map_room.put("roomSeat",rseatnum);
                                map_room.put("roomStatus",rstatus);
                                room_info.add(map_room);
                            }
                            for (int i = 0; i < rooms2.length(); i++) {
                                JSONObject room = rooms2.getJSONObject(i);
                                Map<String, String> map_room = new HashMap<String, String>();
                                String rnum = room.getString("rnum");
                                String rname = room.getString("rname");
                                String rseatnum = room.getString("rseatnum");
                                String rstatus = room.getString("rstatus");
                                map_room.put("roomNum", rnum);
                                map_room.put("roomName",rname);
                                map_room.put("roomSeat",rseatnum);
                                map_room.put("roomStatus",rstatus);
                                room_info.add(map_room);
                            }
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    //新建适配器
                                    SimpleAdapter sa = new SimpleAdapter(ShowRoomActivity.this, room_info, R.layout.list_item,
                                            new String[]{"roomNum", "roomName", "roomSeat", "roomStatus"}, new int[]{
                                            R.id.roomNumber, R.id.roomName, R.id.roomSeat, R.id.roomStatus});
                                    //设置适配器
                                    roomList.setAdapter(sa);
                                    progressDialog.dismiss();
                                }
                            });
                        } else if(status1==0 || status2 == 0)  {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(ShowRoomActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
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
                        Intent in=new Intent(ShowRoomActivity.this,MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if(((Integer)v.getTag())==1)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(ShowRoomActivity.this,OrderSeatActivity.class);
                        startActivity(in);
                        //flag=false;
                        finish();
                    }
                    if(((Integer)v.getTag())==2)
                    {
                        resideMenu.closeMenu();
                    }
                    if(((Integer)v.getTag())==3)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(ShowRoomActivity.this,ShowSeatActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if(((Integer)v.getTag())==4)
                    {
                        resideMenu.closeMenu();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
