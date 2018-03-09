package com.example.com.hustrclibmobile.http;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.com.hustrclibmobile.util.StreamTools;
import com.example.com.hustrclibmobile.util.StringUtil;

public class HttpUtil {
    Context context=null;
    //服务器IP
    String URL="http://118.89.29.130:8888/";

    public  void setContext(Context context)
    {
        this.context=context;
    }


    public String loginByGet(String username,String password)
    {
        if(username.isEmpty()||password.isEmpty())
        {
            return null;
        }
        //只允许Android用户登录
        String path=URL+"HustrcLib/login?username="+username+"&password="+password+"&userType=4";
        Log.i("url", path);
        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code=conn.getResponseCode();
            String cookieval=conn.getHeaderField("Set-Cookie");
            if(cookieval!=null)
            {
                String JSESSIONID=cookieval.substring(0,cookieval.indexOf(";"));
                SharedPreferences sh=context.getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sh.edit();
                editor.putString("JSESSIONID", JSESSIONID);
                editor.commit();
            }
            System.out.println(code);
            if(code==200)//请求成功
            {

                InputStream is=conn.getInputStream();
                String results= StreamTools.readInputStream(is);
                return results;

            }else //请求失败
            {
                return null;
            }

        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }

    public  String SeartchOrderByUserid(String userid,String JSESSIONID)
    {
        if(userid.isEmpty())
        {
            return null;
        }
        String path=URL+"HustrcLib/android_getUserOrderServlet";
        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("JSEESSIONID", JSESSIONID);
            conn.setRequestProperty("Cookie", JSESSIONID);
            //post 数据请求体
            String data="userid="+userid;
            byte[] databyte=data.getBytes("UTF-8");
            // 表示设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(databyte.length));
            // 获得输出流,向服务器输出数据
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(databyte,0,databyte.length);
            outputStream.close();
            // 获得服务器响应的结果和状态码
            int code=conn.getResponseCode();
            Log.e("HttpCode", ""+code);
            if(code==200)//请求成功
            {
                InputStream is=conn.getInputStream();
                String results=StreamTools.readInputStream(is);
                return results;
            }else //请求失败
            {
                return null;
            }
        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }

    public  String getRoomList(String JSESSIONID)
    {
        if(StringUtil.isEmpty(JSESSIONID))
        {
            return null;
        }
        String path=URL+"HustrcLib/android_getRoomListServlet";
        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("JSEESSIONID", JSESSIONID);
            conn.setRequestProperty("Cookie", JSESSIONID);


            // 获得服务器响应的结果和状态码
            int code=conn.getResponseCode();
            Log.e("HttpCode", ""+code);
            if(code==200)//请求成功
            {
                InputStream is=conn.getInputStream();
                String results=StreamTools.readInputStream(is);
                return results;

            }else //请求失败
            {
                return null;
            }



        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }

    public  String getRoomNewList(String JSESSIONID)
    {
        if(StringUtil.isEmpty(JSESSIONID))
        {
            return null;
        }
        String path=URL+"HustrcLib/android_getRoomNewListServlet";
        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("JSEESSIONID", JSESSIONID);
            conn.setRequestProperty("Cookie", JSESSIONID);


            // 获得服务器响应的结果和状态码
            int code=conn.getResponseCode();
            Log.e("HttpCode", ""+code);
            if(code==200)//请求成功
            {
                InputStream is=conn.getInputStream();
                String results=StreamTools.readInputStream(is);
                return results;

            }else //请求失败
            {
                return null;
            }



        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }

    public  String getSeatsList(String JSESSIONID,String rnum)
    {
        if(StringUtil.isEmpty(JSESSIONID))
        {
            return null;
        }
        String path=URL+"HustrcLib/android_getSeatListByRnumServlet";
        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("JSEESSIONID", JSESSIONID);
            conn.setRequestProperty("Cookie", JSESSIONID);

            String data="rnum="+rnum;
            byte[] databyte=data.getBytes("UTF-8");
            // 表示设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(databyte.length));
            // 获得输出流,向服务器输出数据
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(databyte,0,databyte.length);
            outputStream.close();


            // 获得服务器响应的结果和状态码
            int code=conn.getResponseCode();
            Log.e("HttpCode", ""+code);
            if(code==200)//请求成功
            {
                InputStream is=conn.getInputStream();
                String results=StreamTools.readInputStream(is);
                return results;

            }else //请求失败
            {
                return null;
            }



        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }

    public  String getAllSeatsList(String JSESSIONID,String rnum)
    {
        if(StringUtil.isEmpty(JSESSIONID))
        {
            return null;
        }
        String path=URL+"HustrcLib/android_getSeatListAllServlet";
        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("JSEESSIONID", JSESSIONID);
            conn.setRequestProperty("Cookie", JSESSIONID);

            String data="rnum="+rnum;
            byte[] databyte=data.getBytes("UTF-8");
            // 表示设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(databyte.length));
            // 获得输出流,向服务器输出数据
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(databyte,0,databyte.length);
            outputStream.close();


            // 获得服务器响应的结果和状态码
            int code=conn.getResponseCode();
            Log.e("HttpCode", ""+code);
            if(code==200)//请求成功
            {
                InputStream is=conn.getInputStream();
                String results=StreamTools.readInputStream(is);
                return results;

            }else //请求失败
            {
                return null;
            }



        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }

    public  String addOrder(Map<String,String> order_info, String JSESSIONID)
    {
        if(order_info.isEmpty())
        {
            return null;
        }

        String path=URL+"HustrcLib/Android_addOrderServlet";

        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("JSEESSIONID", JSESSIONID);
            conn.setRequestProperty("Cookie", JSESSIONID);
            //post 数据请求体
            StringBuffer sbf=new StringBuffer();
            sbf.append("userId="+order_info.get("userId"));
            sbf.append("&rnum="+order_info.get("rnum"));
            sbf.append("&sid="+order_info.get("sid"));
            sbf.append("&stime="+order_info.get("stime"));
            sbf.append("&etime="+order_info.get("etime"));

            String data=sbf.toString();
            Log.i("addorder", data);
            byte[] databyte=data.getBytes("UTF-8");
            // 表示设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(databyte.length));
            // 获得输出流,向服务器输出数据
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(databyte,0,databyte.length);
            outputStream.close();
            // 获得服务器响应的结果和状态码

            int code=conn.getResponseCode();
            Log.e("HttpCode", ""+code);
            if(code==200)//请求成功
            {

                InputStream is=conn.getInputStream();
                String results=StreamTools.readInputStream(is);
                return results;

            }else //请求失败
            {
                return null;
            }



        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }

    public  String cancelOrder(String JSESSIONID,int o_sid)
    {
        if(o_sid==0)
        {
            return null;
        }

        String path=URL+"HustrcLib/Android_cancelOrderServlet";

        try {
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("JSEESSIONID", JSESSIONID);
            conn.setRequestProperty("Cookie", JSESSIONID);
            //post 数据请求体

            String data="o_sid="+o_sid;

            byte[] databyte=data.getBytes("UTF-8");
            // 表示设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(databyte.length));
            // 获得输出流,向服务器输出数据
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(databyte,0,databyte.length);
            outputStream.close();
            // 获得服务器响应的结果和状态码

            int code=conn.getResponseCode();
            Log.e("HttpCode", ""+code);
            if(code==200)//请求成功
            {

                InputStream is=conn.getInputStream();
                String results=StreamTools.readInputStream(is);
                return results;

            }else //请求失败
            {
                return null;
            }



        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();

        }
        return null;
    }


}
