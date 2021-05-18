package com.noxmi.youren.update;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class updateinfo extends Activity {
    private String Tagname//网络获取程序名字
            ,updialog//网络获取更新日志
            ,uppackname//安装包名称
            ,Currenttagname//现版本名字
            ,DownloadUrl;//下载链接
    public static final String LatestUrl="https://api.github.com/repos/NOXMI/youren/releases/latest";
    void setTagname(String a)
    {this.Tagname=a;}
    void setUpdialog(String a)
    {this.updialog=a; }
    void setUppackname(String a){this.uppackname=a;}
    void setCurrenttagname(String a){this.Currenttagname=a;}
    void setDownloadUrl(String a){this.DownloadUrl=a;}
    String getTagname(){return this.Tagname;}
    String getUpdialog(){return this.updialog;}
    String getUppackname(){return this.uppackname;}
    String getCurrenttagname(){return this.Currenttagname;}
    String getDownloadUrl(){return this.DownloadUrl;}
    String getLatestUrl(){return LatestUrl;}

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
