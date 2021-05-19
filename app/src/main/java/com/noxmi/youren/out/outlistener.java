package com.noxmi.youren.out;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


public class outlistener extends Activity {
    public outlistener(String url) {
        try {

            Log.e("asd",url+"");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            if (intent.resolveActivity(getPackageManager()) == null) {
                //startActivity(intent);
                Log.e("ad","fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
