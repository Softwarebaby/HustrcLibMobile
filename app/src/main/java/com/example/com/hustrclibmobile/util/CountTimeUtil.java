package com.example.com.hustrclibmobile.util;

import android.os.CountDownTimer;
import java.util.Date;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Vibrator;

import android.widget.TextView;

public class CountTimeUtil extends CountDownTimer {
    private  static CountTimeUtil timeUtil=null;
    private TextView textview=null;
    private Context context;

    public static synchronized CountTimeUtil getCountTimeUtil(long time,long count)
    {
        if(timeUtil==null)
        {
            return new CountTimeUtil(time, count);
        }
        return timeUtil;

    }

    public void setTextView(TextView textview)
    {
        this.textview=textview;
    }


    public void setContext(Context context)
    {
        this.context=context;
    }

    public CountTimeUtil(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        // TODO 自动生成的构造函数存根
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // TODO 自动生成的方法存根


        Date date=new Date(millisUntilFinished);

        SimpleDateFormat sf=new SimpleDateFormat("HH:mm:ss");
        sf.setTimeZone(TimeZone.getTimeZone("GMT +8:00"));
        textview.setText(sf.format(date).toString());
    }

    @Override
    public void onFinish() {
        // TODO 自动生成的方法存根
		/*
	        * 设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
		    * */
        final Vibrator  vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100,300,100,300}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

        Builder builder=new AlertDialog.Builder(context).setTitle("系统提示")
                .setMessage("学习时间结束啦，把座位让给其他同学，去休息下吧").setPositiveButton("我知道了", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法存根

                        vibrator.cancel();

                    }
                });
        AlertDialog al=builder.create();
        al.show();



    }
}
