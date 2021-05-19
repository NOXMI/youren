package com.noxmi.youren.gonglue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noxmi.youren.R;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class gongluemain extends Activity {
    RecyclerView rl;
    List<gonglueinfo> list=new ArrayList<gonglueinfo>();;
    List<gonglueinfo> listTemp=new ArrayList<gonglueinfo>();
    Button bt;
    String gonglueURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclepad);

        //Log.e("as",list.get(0).toString());
        readCsv();
        init();
    }

    void init(){

        bt=(Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("http://www.csdn.net");//网址一定要加http
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        rl = findViewById(R.id.rl);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        rl.setLayoutManager(layoutManager);
        for(int i=0;i<10;i++){
            listTemp.add(list.get(i));
        }
        gonglueadapter adapter = new gonglueadapter(list);
        adapter.setOnItemClickListener(new gonglueadapter.OnItemClickListener() {
            @Override
            public void onItemClick(String URL) {
                gonglueURL=URL;
                Log.e("ASD",gonglueURL);
                //adapter.notifyItemChanged(1);
                Uri uri=Uri.parse(gonglueURL);//网址一定要加http
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        rl.setAdapter(adapter);
    }
    private void readCsv() {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("data1.csv"));
            BufferedReader reader = new BufferedReader(inputReader);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                gonglueinfo GLinfoTemp = new gonglueinfo();
                GLinfoTemp.setBiaoti(csvRecord.get("\uFEFF标题"));
                GLinfoTemp.setTheurl(csvRecord.get("链接"));
                GLinfoTemp.setName(csvRecord.get("作者"));
                GLinfoTemp.setDate(csvRecord.get("出发日期"));
                GLinfoTemp.setDays(csvRecord.get("天数"));
                GLinfoTemp.setPhoto_nums(csvRecord.get("照片数"));
                GLinfoTemp.setPeople(csvRecord.get("人数"));
                GLinfoTemp.setTrip(csvRecord.get("玩法"));
                GLinfoTemp.setFee(csvRecord.get("费用"));
                GLinfoTemp.setIcon_view(csvRecord.get("阅读数"));
                GLinfoTemp.setIcon_love(csvRecord.get("点赞数"));
                GLinfoTemp.setIcon_comment(csvRecord.get("评论数"));
                GLinfoTemp.setbitmap(csvRecord.get("图片url"));
                GLinfoTemp.setuserbimap(csvRecord.get("头像url"));
                list.add(GLinfoTemp);
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
