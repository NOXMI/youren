package com.noxmi.youren.mainpicdownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class picdownlod {

    private Bitmap BM=null;
    public void setuserbimap(String strurl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(strurl);
                    InputStream inputStream = url.openStream();
                    BM = BitmapFactory.decodeStream(inputStream);
                    //handler.sendEmptyMessage(1);//主线程中是不能更新的，所以得发送消息到handler，到handleMessage方法中设置获取得到的图片
                    inputStream.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("fail", "MalformedURLException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("fail", "IOException");
                }
            }
        }).start();
    }
    public Bitmap getBM(){
        return BM;
    }
}
