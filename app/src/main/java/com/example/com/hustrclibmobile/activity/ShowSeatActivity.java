package com.example.com.hustrclibmobile.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.com.hustrclibmobile.R;
import com.example.com.hustrclibmobile.http.HttpUtil;
import com.example.com.hustrclibmobile.residemenu.ResideMenu;
import com.example.com.hustrclibmobile.residemenu.ResideMenuItem;
import com.example.com.hustrclibmobile.util.StringUtil;

public class ShowSeatActivity extends AppCompatActivity {
    private boolean flag = false;
    private GridView gv;
    private SimpleAdapter adapter;
    private static final int ROW = 6;// 设置列数
    private ProgressDialog progressDialog;
    private ArrayList<Map<String, Object>> list;

    private Button openMenu;
    private ResideMenu resideMenu;
    private ShowSeatActivity mContext;
    private List<Map<String,Object>> seatList;
    private Spinner room;
    private TextView seatNums_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seat);

        setUpMenu();
        mContext = this;
        progressDialog = new ProgressDialog(ShowSeatActivity.this);
        gv = (GridView) findViewById(R.id.gridView1);
        room=(Spinner) this.findViewById(R.id.roomlist);
        seatList=new ArrayList<Map<String,Object>>();
        seatNums_TV=(TextView)this.findViewById(R.id.seatNums);
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
        getRoomInfo();
    }

    private void DrawGridView()
    {
        adapter = new SimpleAdapter(// 创建适配器
                this,// Context
                generateDataList(), // 数据List
                R.layout.item,// 行对应layout 的 id
                new String[] { "Image", "Name" }, // 列名列表
                new int[] { R.id.item_ImagView, R.id.item_TextView });// 列对应空间id列表
        gv.setAdapter(adapter);// 为GridView设置数据适配器
        gv.setNumColumns(ROW);
        gv.setVerticalSpacing(20);
    }

    private List<Map<String, Object>> generateDataList() {
        if (list == null) {
            list = new ArrayList<Map<String, Object>>();
        } else {
            list.clear();
        }
        int seat_selected = R.drawable.seatselected;
        int seat_ok = R.drawable.seatok;
        int seatNums = seatList.size();
        seatNums_TV.setText("" + seatNums);
        for (int i = 0; i < seatNums; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> seat = new HashMap<String, Object>();
            seat = seatList.get(i);
            if (((Integer) seat.get("sstatus")) == 1) {
                map.put("Image", seat_ok);
                map.put("Name", seat.get("snum"));
            } else if (((Integer) seat.get("sstatus")) == 0) {
                map.put("Image", seat_selected);
                map.put("Name", seat.get("snum"));
            }
            list.add(map);
        }
        return list;
    }

    public void getRoomInfo()
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
                    String result=httputil.getRoomList(JSESSIONID);
                    final List<Map<String,String>> room_info=new ArrayList<Map<String,String>>();
                    final List<String> rnamelist=new ArrayList<String>();
                    if(result==null)
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(ShowSeatActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                    {
                        try {
                            JSONObject js=new JSONObject(result);
                            int status=js.getInt("status");
                            if(status==1)
                            {
                                JSONArray rooms=js.getJSONArray("rooms");
                                for(int i=0;i<rooms.length();i++)
                                {
                                    JSONObject room=rooms.getJSONObject(i);
                                    Map<String,String> map_room=new HashMap<String, String>();
                                    String rnum=room.getString("rnum");
                                    String rname=room.getString("rname");
                                    map_room.put(rname, rnum);
                                    rnamelist.add(rname);
                                    room_info.add(map_room);
                                }
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO 自动生成的方法存根
                                        ArrayAdapter roomadapter=new ArrayAdapter(mContext, android.R.layout.simple_spinner_item,rnamelist);
                                        roomadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        room.setAdapter(roomadapter);

                                        room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                            @Override
                                            public void onItemSelected(
                                                    AdapterView<?> parent,
                                                    View view, int position,
                                                    long id) {
                                                // TODO 自动生成的方法存根
                                                String selectedRoom=null;
                                                Spinner room_spinner=(Spinner) parent;
                                                selectedRoom=(String) room_spinner.getItemAtPosition(position);
                                                Log.i("Rname", selectedRoom);
                                                Map<String,String> room=room_info.get(position);
                                                String rnum=room.get(selectedRoom);
                                                Log.i("Rnum", rnum);
                                                getSeatsInfo(rnum);
                                                progressDialog.dismiss();

                                            }

                                            @Override
                                            public void onNothingSelected(
                                                    AdapterView<?> parent) {
                                                // TODO 自动生成的方法存根

                                            }
                                        });

                                    }
                                });

                            }else if(status==0)
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(ShowSeatActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
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

    public void getSeatsInfo( String rnum)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String userId=sharedPreferences.getString("userId", "");
        final String JSESSIONID=sharedPreferences.getString("JSESSIONID", "");
        progressDialog.setMessage("正在查询座位...");
        progressDialog.show();
        final String final_rnum=rnum;
        if(StringUtil.isEmpty(userId)||StringUtil.isEmpty(JSESSIONID))
        {
            Toast.makeText(getApplicationContext(),"获取用户信息失败", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else
        {
            new Thread()
            {
                public void run()
                {
                    HttpUtil httputil=new HttpUtil();
                    httputil.setContext(mContext);
                    String result=httputil.getAllSeatsList(JSESSIONID, final_rnum);
                    // final List<Map<String,String>> seats_info=new ArrayList<Map<String,String>>();
                    //	final List<String> snumlist=new ArrayList<String>();
                    if(result==null)
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(ShowSeatActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                    {
                        try {
                            JSONObject js=new JSONObject(result);
                            int status=js.getInt("status");
                            if(status==1)
                            {
                                JSONArray seats=js.getJSONArray("seats");
                                if(!seatList.isEmpty())
                                {
                                    seatList.clear();
                                }
                                for(int i=0;i<seats.length();i++)
                                {
                                    JSONObject json_seats=seats.getJSONObject(i);
                                    Map<String,Object> map_seats=new HashMap<String, Object>();
                                    String id=json_seats.getString("id");
                                    String snum=json_seats.getString("snum");
                                    int sstatus=json_seats.getInt("sstatus");
                                    String rnum=json_seats.getString("rnum");
                                    map_seats.put("id", id);
                                    map_seats.put("snum", snum);
                                    map_seats.put("sstatus", sstatus);
                                    map_seats.put("rnum", rnum);
                                    seatList.add(map_seats);

                                }
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO 自动生成的方法存根
                                        DrawGridView();
                                    }
                                });


                            }else if(status==0)
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(ShowSeatActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }else if(status==2)
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(ShowSeatActivity.this, "该阅览室没有座位可以占了!", Toast.LENGTH_SHORT).show();

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
                        Intent in=new Intent(ShowSeatActivity.this,MainActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if(((Integer)v.getTag())==1)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(ShowSeatActivity.this,OrderSeatActivity.class);
                        startActivity(in);
                        //flag=false;
                        finish();
                    }
                    if(((Integer)v.getTag())==2)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(ShowSeatActivity.this,ShowRoomActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if(((Integer)v.getTag())==3)
                    {
                        resideMenu.closeMenu();
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
