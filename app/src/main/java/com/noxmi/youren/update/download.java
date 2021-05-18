package com.noxmi.youren.update;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import com.noxmi.youren.MainActivity;
import com.noxmi.youren.R;

public class download {

    //该方法是调用了系统的下载管理器
    public static void downLoadApk(Context context, String url, String Tagname){
        Uri uri = Uri.parse(url);//下载连接
        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);  //得到系统的下载管理
        DownloadManager.Request requestApk = new DownloadManager.Request(uri);  //得到连接请求对象
        requestApk.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);   //指定在什么网络下进行下载，这里我指定了WIFI网络
        requestApk.setDestinationInExternalPublicDir(context.getPackageName()+"/myDownLoad","youren.apk");  //制定下载文件的保存路径，我这里保存到根目录
        requestApk.setVisibleInDownloadsUi(true);  //设置显示下载界面
        requestApk.allowScanningByMediaScanner();  //表示允许MediaScanner扫描到这个文件，默认不允许。
        requestApk.setTitle("游人"+Tagname);      //设置下载中通知栏的提示消息
        requestApk.setDescription("游人更新下载");//设置设置下载中通知栏提示的介绍
        long downLoadId = manager.enqueue(requestApk);               //启动下载,该方法返回系统为当前下载请求分配的一个唯一的ID
    }

    //更新询问
    public static void downloadask(Context context,String uppackname,String updialog,String DownloadUrl,String Tagname){

        AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setTitle("更新提示：");
        normalDialog.setMessage("发现更新版本: "+uppackname+ System.getProperty ("line.separator")+
                "更新日志： "+updialog);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setCancelable(true);
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk(context,DownloadUrl,Tagname);
                dialog.dismiss();
            }
        });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = normalDialog.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });
        //对话框消失的监听事件
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        dialog.show();
    }
}
