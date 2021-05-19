package com.noxmi.youren.gonglue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class gonglueinfo {
    private String
            biaoti,//标题
            theurl,//连接
            name,//作者
            date,//出发时间
            days,//天数
            photo_nums,//照片数量
            people,//人数
            trip,//玩法
            fee,//费用
            icon_view,//阅读数
            icon_love,//点赞数
            icon_comment,//评论数
            picurl,//图片url
            userurl;//用户url
    private Bitmap BM=null,BM2=null;

    public void setBiaoti(String biaoti) {
        this.biaoti = biaoti;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public void setIcon_comment(String icon_comment) {
        this.icon_comment = icon_comment;
    }

    public void setIcon_love(String icon_love) {
        this.icon_love = icon_love;
    }

    public void setIcon_view(String icon_view) {
        this.icon_view = icon_view;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public void setPhoto_nums(String photo_nums) {
        this.photo_nums = photo_nums;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public void setTheurl(String theurl) {
        this.theurl = theurl;
    }
    public void setUserurl(String u)
    {this.userurl=u;}
    public void setTrip(String trip) {
        this.trip = trip;
    }

    public String getBiaoti() {
        return biaoti;
    }

    public String getDate() {
        return date;
    }

    public String getDays() {
        return days;
    }

    public String getFee() {
        return fee;
    }

    public String getIcon_comment() {
        return icon_comment;
    }

    public String getIcon_love() {
        return icon_love;
    }

    public String getIcon_view() {
        return icon_view;
    }

    public String getName() {
        return name;
    }

    public String getPeople() {
        return people;
    }

    public String getPhoto_nums() {
        return photo_nums;
    }

    public String getPicurl() {
        return picurl;
    }

    public String getTheurl() { return theurl; }

    public String getTrip() {
        return trip;
    }
    public String getUserurl(){return userurl;}
    public void setbitmap(String strurl){
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
    public void setuserbimap(String strurl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(strurl);
                    InputStream inputStream = url.openStream();
                    BM2 = BitmapFactory.decodeStream(inputStream);
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

    public Bitmap getBM2(){
        return BM2;
    }
}
