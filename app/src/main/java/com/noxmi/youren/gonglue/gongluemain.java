package com.noxmi.youren.gonglue;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noxmi.youren.R;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class gongluemain extends Activity {
    RecyclerView rl;
    List<gonglueinfo> list = new ArrayList<gonglueinfo>();
    List<gonglueinfo> listTemp = new ArrayList<gonglueinfo>();
    String gonglueURL;
    int MAX = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclepad);

        readCsv();
        init();
    }

    void init() {

        rl = findViewById(R.id.rl);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        rl.setLayoutManager(layoutManager);
        for (int i = 0; i < 11; i++) {
            listTemp.add(list.get(i));
            listTemp.get(i).setbitmap(listTemp.get(i).getPicurl());
            listTemp.get(i).setuserbimap(listTemp.get(i).getUserurl());
        }
        gonglueadapter adapter = new gonglueadapter(listTemp);
        adapter.setOnItemClickListener(new gonglueadapter.OnItemClickListener() {
            @Override
            public void onItemClick(String URL) {
                gonglueURL = URL;

                Uri uri = Uri.parse(gonglueURL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        rl.setAdapter(adapter);
        rl.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("position", layoutManager.findLastCompletelyVisibleItemPosition() + "");
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= MAX) {
                    fresh(MAX);
                    MAX += 10;
                    adapter.notifyItemChanged(MAX);
                }
            }
        });
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
                GLinfoTemp.setPicurl(csvRecord.get("图片url"));
                GLinfoTemp.setUserurl(csvRecord.get("头像url"));
                list.add(GLinfoTemp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void fresh(int M) {
        for (int i = 1; i <= 10; i++) {
            list.get(i + M).setbitmap(list.get(i + M).getPicurl());
            list.get(i + M).setuserbimap(list.get(i + M).getUserurl());
            listTemp.add(list.get(i + M));
        }
    }
}
