package com.noxmi.youren.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.noxmi.youren.R;
import com.noxmi.youren.update.download;
import com.noxmi.youren.update.updateinfo;

public class settingmain extends Activity {
    ListView LIST;
    ImageView t;
    //数据传输
    String Tagname, updialog,uppackname,Currenttagname,DownloadUrl;
    String[] data={"检查更新","反馈问题请联系\nqq875675620"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        inition();

    }
    void inition(){
        Intent intent = getIntent();
        Tagname = intent.getStringExtra("Tagname");
        updialog = intent.getStringExtra("updialog");
        uppackname = intent.getStringExtra("uppackname");
        Currenttagname = intent.getStringExtra("Currenttagname");
        DownloadUrl = intent.getStringExtra("DownloadUrl");
        //LIST.findViewById(R.id.LIST);
        t=(ImageView)findViewById(R.id.testview);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("name","mane");
            }
        });
        data[0]+="\n当前版本"+Tagname;
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        LIST=(ListView)findViewById(R.id.LIST);
        LIST.setAdapter(adapter);
        LIST.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                //Log.e("asd","position"+position+"\nid"+id);
                switch (position){
                    case 0:
                        if(!Tagname.equals(updateinfo.getVersionName(settingmain.this))){
                            //Log.e("up","needup");
                            download.downloadask(settingmain.this,Tagname,updialog,DownloadUrl,Tagname);
                        }
                        else{
                            //Log.e("up","noneed");
                            //Toast.makeText(settingmain.this,"已经是最新版本的了",Toast.LENGTH_SHORT);
                            download.NOneedup(settingmain.this);
                        }
                }
            }
    });
    }
}
