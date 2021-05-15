package com.noxmi.youren.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PicDownload extends AsyncTask {
    private ImageView mImageView;
    public PicDownload(ImageView imageView)
    {
        mImageView = imageView;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            // 创建一个URL
            URL url = new URL("https://img1.qunarzz.com/travel/d8/1702/85/8eba3c25781398b5.jpg_r_680x510x95_839186f7.jpg");

            // 从URL获取对应资源的 InputStream
            InputStream inputStream = url.openStream();
            // 用inputStream来初始化一个Bitmap 虽然此处是Bitmap，但是URL不一定非得是Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            // 关闭 InputStream
            inputStream.close();

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        // 此处的形参o，是doInBackground的返回值
        mImageView.setImageBitmap((Bitmap)o);
    }
}

