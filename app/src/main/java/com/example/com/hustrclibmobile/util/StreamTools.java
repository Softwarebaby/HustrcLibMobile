package com.example.com.hustrclibmobile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamTools {
    public static String readInputStream(InputStream is)
    {

        try {

            ByteArrayOutputStream bao=new ByteArrayOutputStream();
            int len=0;
            byte[] bt=new byte[1024];
            while ((len=is.read(bt))!=-1)
            {
                bao.write(bt,0,len);

            }
            is.close();
            bao.close();
            byte[] btresult=bao.toByteArray();
            String results=new String(btresult);
            return results;
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
            return "failed";
        }
    }

}
