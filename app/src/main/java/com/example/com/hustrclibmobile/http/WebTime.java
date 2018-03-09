package com.example.com.hustrclibmobile.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class WebTime {
    public static Date getWebTime()
    {
        URL urlTime;
        Date date=null;
        try {
            urlTime = new URL("http://www.bjtime.cn");
            URLConnection uc=urlTime.openConnection();//生成连接对象
            uc.connect();
            //发出连接
            long ld=uc.getDate(); //取得网站日期时间
            date=new Date(ld); //转换为标准时间对象

        } catch (MalformedURLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }//取得资源对象
        catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return date;

    }
}
