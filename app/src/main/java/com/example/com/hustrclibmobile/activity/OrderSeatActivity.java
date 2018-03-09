package com.example.com.hustrclibmobile.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.com.hustrclibmobile.R;
import com.example.com.hustrclibmobile.http.HttpUtil;
import com.example.com.hustrclibmobile.residemenu.ResideMenu;
import com.example.com.hustrclibmobile.residemenu.ResideMenuItem;
import com.example.com.hustrclibmobile.util.StringUtil;

public class OrderSeatActivity extends AppCompatActivity {
    private boolean flag=false;
    private ProgressDialog progressDialog;
    private TextView userId_tv;
    private EditText stime;
    private EditText etime;
    private Button openMenu;
    private Button addOrder;
    private Spinner room;
    private Spinner seat;
    //用于存储订单信息
    private Map<String,String> order_info=new HashMap<String, String>();
    private ResideMenu resideMenu;
    private OrderSeatActivity mContext;
    private  Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_seat);

        mContext = this;
        calendar = Calendar.getInstance();

        userId_tv = (TextView)findViewById(R.id.userid_seat);
        stime = (EditText)findViewById(R.id.stime_seat);
        etime = (EditText)findViewById(R.id.etime_seat);
        room=(Spinner)this.findViewById(R.id.room_seat);
        seat=(Spinner) this.findViewById(R.id.seat_seat);
        progressDialog = new ProgressDialog(OrderSeatActivity.this);
        openMenu=(Button)findViewById(R.id.title_bar_menu);
        addOrder = (Button)findViewById(R.id.addOrder);

        setUpMenu();
        getRoomInfo();

        stime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                final int year = calendar.get(Calendar.YEAR)-1900;
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(OrderSeatActivity.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       //系统当前时间
                       calendar.setTimeInMillis(System.currentTimeMillis());
                       calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                       calendar.set(Calendar.MINUTE, minute);
                       calendar.set(Calendar.SECOND, 0);
                       calendar.set(Calendar.MILLISECOND, 0);

                       Date date=new Date();
                       date.setYear(year);
                       date.setMonth(month);
                       date.setDate(day);
                       date.setHours(hourOfDay);
                       date.setMinutes(minute);
                       SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                       order_info.put("stime", sp.format(date));
                       stime.setText(sp.format(date));
                   }
               },hour,minute,true).show();
            }
        });

        etime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                final int year = calendar.get(Calendar.YEAR)-1900;
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(OrderSeatActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //系统当前时间
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        Date date=new Date();
                        date.setYear(year);
                        date.setMonth(month);
                        date.setDate(day);
                        date.setHours(hourOfDay);
                        date.setMinutes(minute);
                        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        order_info.put("etime", sp.format(date));
                        etime.setText(sp.format(date));
                    }
                },hour,minute,true).show();
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

        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrder();
            }
        });
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
                        Intent in=new Intent(OrderSeatActivity.this,MainActivity.class);
                        startActivity(in);
                        //flag=false;
                        finish();
                    }
                    if(((Integer)v.getTag())==1)
                    {
                        resideMenu.closeMenu();
                    }
                    if(((Integer)v.getTag())==2)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(OrderSeatActivity.this,ShowRoomActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if(((Integer)v.getTag())==3)
                    {
                        resideMenu.closeMenu();
                        Intent in=new Intent(OrderSeatActivity.this,ShowSeatActivity.class);
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

    //获取阅览室状况
    public void getRoomInfo()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String userId=sharedPreferences.getString("userId", "");
        final String JSESSIONID=sharedPreferences.getString("JSESSIONID", "");
        order_info.put("userId", userId);
        userId_tv.setText(userId);

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
                                Toast.makeText(OrderSeatActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
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

                                        room.setOnItemSelectedListener(new OnItemSelectedListener() {

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
                                                order_info.put("rnum", rnum);
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
                                        Toast.makeText(OrderSeatActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
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
        progressDialog.setMessage("正在查询可用座位...");
        progressDialog.show();
        final String final_rnum=rnum;
        if(StringUtil.isEmpty(userId)|| StringUtil.isEmpty(JSESSIONID))
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
                    String result=httputil.getSeatsList(JSESSIONID, final_rnum);
                    final List<Map<String,String>> seats_info=new ArrayList<Map<String,String>>();
                    final List<String> snumlist=new ArrayList<String>();
                    if(result==null)
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(OrderSeatActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
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
                                for(int i=0;i<seats.length();i++)
                                {
                                    JSONObject json_seats=seats.getJSONObject(i);
                                    Map<String,String> map_seats=new HashMap<String, String>();
                                    String id=json_seats.getString("id");
                                    String snum=json_seats.getString("snum");
                                    map_seats.put(snum, id);
                                    snumlist.add(snum);
                                    seats_info.add(map_seats);
                                }
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO 自动生成的方法存根
                                        ArrayAdapter seatsadapter=new ArrayAdapter(mContext, android.R.layout.simple_spinner_item,snumlist);
                                        seatsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        seat.setAdapter(seatsadapter);
                                        seat.setOnItemSelectedListener(new OnItemSelectedListener() {

                                            @Override
                                            public void onItemSelected(
                                                    AdapterView<?> parent,
                                                    View view, int position,
                                                    long id) {
                                                // TODO 自动生成的方法存根
                                                String selectedSeat=null;
                                                Spinner room_spinner=(Spinner) parent;
                                                selectedSeat=(String) room_spinner.getItemAtPosition(position);
                                                Log.i("snum", selectedSeat);
                                                Map<String,String> seat_from_map=seats_info.get(position);
                                                String sid=seat_from_map.get(selectedSeat);
                                                order_info.put("sid", sid);
                                                Log.i("sid", sid);
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
                                        Toast.makeText(OrderSeatActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }else if(status==2)
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(OrderSeatActivity.this, "该阅览室没有座位可以占了！", Toast.LENGTH_SHORT).show();

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

    //添加订单
    public void addOrder()
    {
        Log.i("order_info", order_info.toString());
        String stime=order_info.get("stime");
        String etime=order_info.get("etime");
        String userid=order_info.get("userId");
        String rnum=order_info.get("rnum");
        String sid=order_info.get("sid");
        if(StringUtil.isEmpty(stime)||StringUtil.isEmpty(etime))
        {
            Toast.makeText(mContext, "时间不能为空！", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(StringUtil.isEmpty(sid)||StringUtil.isEmpty(rnum)||StringUtil.isEmpty(userid))
        {
            Toast.makeText(mContext, "信息不全，请重新核实！", Toast.LENGTH_SHORT).show();
            return ;
        }
        SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date_stime;
        Date date_etime;
        try {
            date_stime=sd.parse(stime);
            date_etime=sd.parse(etime);
            if(date_stime.after(date_etime))
            {
                Toast.makeText(mContext, "时间好像不对，改一下吧！", Toast.LENGTH_SHORT).show();
                return ;
            }else if((date_etime.getTime()-date_stime.getTime())<=1000*60*30)
            {
                Toast.makeText(mContext, "不学习个30分钟，都不好意思和学霸说话！", Toast.LENGTH_SHORT).show();
                return ;

            }else
            {
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                final String JSESSIONID=sharedPreferences.getString("JSESSIONID", "");
                progressDialog.setMessage("正在占座中...");
                progressDialog.show();
                if(StringUtil.isEmpty(JSESSIONID))
                {
                    Toast.makeText(getApplicationContext(),"获取Session失败！", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else
                {
                    new Thread()
                    {
                        public void run()
                        {
                            HttpUtil httputil=new HttpUtil();
                            httputil.setContext(mContext);
                            String result=httputil.addOrder(order_info, JSESSIONID);

                            if(result==null)
                            {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(OrderSeatActivity.this, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else
                            {
                                try {
                                    JSONObject js=new JSONObject(result);
                                    int status=js.getInt("status");
                                    if(status==1) //添加成功
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                Toast.makeText(OrderSeatActivity.this, "占座成功!", Toast.LENGTH_SHORT).show();
                                                Intent in=new Intent(OrderSeatActivity.this,MainActivity.class);
                                                startActivity(in);
                                                finish();
                                            }
                                        });

                                    }else if(status==0)  //用户登陆过期或者未登录
                                    {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                Toast.makeText(OrderSeatActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
                                            }
                                        });



                                    }else if(status==2) //座位已被占用
                                    {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                Toast.makeText(OrderSeatActivity.this, "该座位已被占用，换个座位试试吧！", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    }
                                    if(status==3)  //学号不存在
                                    {
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                progressDialog.dismiss();
                                                Toast.makeText(OrderSeatActivity.this, "学号不存在!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    if(status==4)  //该学生已有订单存在
                                    {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                Toast.makeText(OrderSeatActivity.this, "一人一个座，不要太贪心!", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                    if(status==5)  //添加失败
                                    {
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                progressDialog.dismiss();
                                                Toast.makeText(OrderSeatActivity.this, "占座失败，重新试试吧!", Toast.LENGTH_LONG).show();
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

        } catch (ParseException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }



    }
    protected void ExitApp() {
        Intent intent = new Intent();
        intent.setAction("ExitApp");
        this.sendBroadcast(intent);
        super.finish();
    }
}
