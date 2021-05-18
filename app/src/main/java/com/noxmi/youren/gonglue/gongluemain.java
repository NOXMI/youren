package com.noxmi.youren.gonglue;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noxmi.youren.R;
import com.noxmi.youren.setting.settingmain;
import com.noxmi.youren.update.download;
import com.noxmi.youren.update.updateinfo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class gongluemain extends Activity {
    String path1;
    ListView LIST;
    String[] data={"测试"};
    RecyclerView rl;
    List<gonglueinfo> list=new ArrayList<gonglueinfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclepad);

        //Log.e("as",list.get(0).toString());
        readCsv("data1.csv");
        init();
    }

    void init(){
        rl = findViewById(R.id.rl);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        rl.setLayoutManager(layoutManager);
        gonglueadapter adapter = new gonglueadapter(list);
        rl.setAdapter(adapter);
    }
    private void readCsv(String path) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("data1.csv"));
            BufferedReader reader = new BufferedReader(inputReader);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                gonglueinfo apacheBean = new gonglueinfo();
                apacheBean.setBiaoti(csvRecord.get("\uFEFF标题"));
                apacheBean.setTheurl(csvRecord.get("链接"));
                apacheBean.setName(csvRecord.get("作者"));
                apacheBean.setDate(csvRecord.get("出发日期"));
                apacheBean.setDays(csvRecord.get("天数"));
                apacheBean.setPhoto_nums(csvRecord.get("照片数"));
                apacheBean.setPeople(csvRecord.get("人数"));
                apacheBean.setTrip(csvRecord.get("玩法"));
                apacheBean.setFee(csvRecord.get("费用"));
                apacheBean.setIcon_view(csvRecord.get("阅读数"));
                apacheBean.setIcon_love(csvRecord.get("点赞数"));
                apacheBean.setIcon_comment(csvRecord.get("评论数"));
                apacheBean.setPicurl(csvRecord.get("图片url"));
                list.add(apacheBean);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("swt", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("swt", e.toString());
        }
    }
}
