package com.noxmi.youren;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.mbsgood.viewpaperslip.FlipViewPaper;
import com.mbsgood.viewpaperslip.utils.CommonUtils;
import com.noxmi.youren.basicmap.WeatherSearchActivity;
import com.noxmi.youren.location.LocationModeSourceActivity;
import com.noxmi.youren.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
        implements AMap.OnMyLocationChangeListener, GeocodeSearch.OnGeocodeSearchListener, WeatherSearch.OnWeatherSearchListener
        , PoiSearch.OnPoiSearchListener {

    ImageView Startimg,show;
    String addressName="定位中",cityname;
    String[] Cname;
    Button ditu,zhuye,geren;
    ViewPager viewPager;
    AMap mainaMap;
    MapView mainmapView;
    MyLocationStyle myLocationStyle;
    ArrayList<String> list=new ArrayList<>();
    ArrayList<View> views=new ArrayList<>();
    LinearLayout Downbuttontab,Wethertab;
    int preposirion=0,currentPage;
    private TextView reporttime1;
    private TextView reporttime2;
    private TextView weather;
    private TextView Temperature;
    private TextView wind;
    private TextView humidity;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    public TextView city;
    public GeocodeSearch geocoderSearch;
    public Location mainlocation;
    private LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    public static final String LatestUrl="https://api.github.com/repos/NOXMI/youren/releases/latest";
    Bitmap bitmap;//得到获取的图片
    Handler handler = new Handler(){
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             //show.setImageBitmap(bitmap);
         }
     };

    Thread thread = new Thread(){
         @Override
         public void run() {
             super.run();
             try {
                 URL url = new URL("https://img1.qunarzz.com/travel/d4/1704/af/9ec6d621b3f92cb5.jpg_160x120x95_ba3e7b3c.jpg");
                 InputStream inputStream = url.openStream();
                 bitmap = BitmapFactory.decodeStream(inputStream);
                 handler.sendEmptyMessage(1);//主线程中是不能更新的，所以得发送消息到handler，到handleMessage方法中设置获取得到的图片
                 inputStream.close();
             } catch (MalformedURLException e) {
                 e.printStackTrace();
                 Log.e("fail","MalformedURLException");
             } catch (IOException e) {
                 e.printStackTrace();
                 Log.e("fail","IOException");
             }
         }
     };
    Thread thread2=new Thread(){
        public void run() {
            try {

                //首先声明url连接对象
                URL url=new URL(LatestUrl);
                //获取HttpURLConnection对象
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置连接超时时间,毫秒为单位
                connection.setConnectTimeout(5000);

                //http方式
                connection.setRequestMethod("GET");
                //获取返回码//200为正常      404 找不到资源
                int code = connection.getResponseCode();
                if(code==200){

                    //获取字节流
                    InputStream inputStream = connection.getInputStream();
                    //解析字节流
                    BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    StringBuilder sb=new StringBuilder();
                    String s=null;
                    while ((s=bf.readLine())!=null) {
                        sb.append(s+"\r\n");
                    }
                    //解析json对象
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    JSONObject assets =new JSONObject(jsonObject.getJSONArray("assets").get(0).toString());
                    Log.e("url",assets.getString("browser_download_url"));

                }
                else{
                    Toast.makeText(MainActivity.this,"无法检测更新",Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error","fail");
            }
        }

    };
    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainmapView = (MapView) findViewById(R.id.map);
        //mainmapView.onCreate(savedInstanceState);// 此方法必须重写

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        inition();

        //地图
        ditu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, LocationModeSourceActivity.class);
                startActivity(intent);
                //getAddress(latLonPoint);
            }
        });
        //主页
        zhuye.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            }
        });
        //个人
        geren.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //downLoadApk(MainActivity.this,"https://github.com/NOXMI/youren/releases/latest");
            }
        });
    }
    /**
     * 该方法是调用了系统的下载管理器
     */
    public void downLoadApk(Context context, String url){
        /**
         * 在这里返回的 reference 变量是系统为当前的下载请求分配的一个唯一的ID，
         * 我们可以通过这个ID重新获得这个下载任务，进行一些自己想要进行的操作
         * 或者查询下载的状态以及取消下载等等
         */
        Log.e("download","loading");
        Uri uri = Uri.parse(url);        //下载连接
        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);  //得到系统的下载管理
        DownloadManager.Request requestApk = new DownloadManager.Request(uri);  //得到连接请求对象
        requestApk.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);   //指定在什么网络下进行下载，这里我指定了WIFI网络
        requestApk.setDestinationInExternalPublicDir(context.getPackageName()+"/myDownLoad","xiaoyuantong.apk");  //制定下载文件的保存路径，我这里保存到根目录
        requestApk.setVisibleInDownloadsUi(true);  //设置显示下载界面
        requestApk.allowScanningByMediaScanner();  //表示允许MediaScanner扫描到这个文件，默认不允许。
        requestApk.setTitle("xxx更新下载");      //设置下载中通知栏的提示消息
        requestApk.setDescription("xxx更新下载");//设置设置下载中通知栏提示的介绍
        long downLoadId = manager.enqueue(requestApk);               //启动下载,该方法返回系统为当前下载请求分配的一个唯一的ID
    }
    /*************************************** 初始化******************************************************/
    public void inition(){
        show=(ImageView) findViewById(R.id.SHOW);
        thread.start();
        thread2.start();
        myLocationStyle = new MyLocationStyle();
        zhuye=(Button)findViewById(R.id.zhuyebtn);
        ditu=(Button)findViewById(R.id.mapbtn);
        geren=(Button)findViewById(R.id.zijibtn);
        Downbuttontab=(LinearLayout)findViewById(R.id.bottom_tab_layout) ;
        Wethertab=(LinearLayout)findViewById(R.id.weathertab);

        Startimg= (ImageView) findViewById(R.id.startimg);
        setTitle("游人" + MapsInitializer.getVersion());

        if(Build.VERSION.SDK_INT > 28
                && getApplicationContext().getApplicationInfo().targetSdkVersion > 28) {
            needPermissions = new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    BACK_LOCATION_PERMISSION
            };
            needCheckBackLocation = true;
        }
        if (mainaMap == null) {
            mainaMap = mainmapView.getMap();
            setUpMap();
        }
        //设置SDK 自带定位消息监听
        mainaMap.setOnMyLocationChangeListener(this);
        //天气简版
        reporttime1 = (TextView) findViewById(R.id.reporttime1);
        weather = (TextView) findViewById(R.id.weather);
        Temperature = (TextView) findViewById(R.id.temp);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        city = (TextView) findViewById(R.id.city);
        Wethertab.setOnClickListener(new View.OnClickListener() {//详细天气
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WeatherSearchActivity.class);
                intent.putExtra("citynameString", addressName);
                startActivity(intent);
            }
        });


    }
    /*************************************** 定位******************************************************/

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if(location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            latLonPoint.setLatitude(location.getLatitude());
            latLonPoint.setLongitude(location.getLongitude());
            getAddress(latLonPoint);
            Bundle bundle = location.getExtras();
            if(bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                mainlocation =location;

                /*
                errorCode
                errorInfo
                locationType
                */
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }

        } else {
            Log.e("amap", "定位失败");
            Toast.makeText(this,"定位失败",Toast.LENGTH_SHORT);
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        // 如果要设置定位的默认状态，可以在此处进行设置
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        mainaMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));

        mainaMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mainaMap.getUiSettings().setCompassEnabled(true);
        mainaMap.getUiSettings().setRotateGesturesEnabled(true);
        mainaMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }
/*************************************** 权限检查******************************************************/

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            BACK_LOCATION_PERMISSION
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        try{
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try{
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try{
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if(!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try{
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        try{
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress();

                String[] Cname=addressName.split("省|市");
                if (addressName.indexOf("省") >= 0)  cityname=Cname[1]+"市";//省区
                else cityname=Cname[0]+"市";//直辖市
                city.setText(cityname);
                //天气
                searchliveweather();
                //POI搜索
                doSearchQuery();
                //载入完后关闭开始图
                Startimg.animate().alpha(0).setDuration(1000).setListener((null));
                Startimg.setEnabled(false);
                ToastUtil.show(MainActivity.this, addressName);
            } else {
                //ToastUtil.show(MainActivity.this, R.string.no_result);
            }
        } else {
            //ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        return;
    }

    /**
     * 实时天气查询
     */
    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                reporttime1.setText(weatherlive.getReportTime() + "发布");
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "°");
                wind.setText(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
                humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
            } else {
                ToastUtil.show(MainActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(MainActivity.this, rCode);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        Poirefresh(poiResult);
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        ToastUtil.show(this,
                                R.string.no_result);
                    } else {
                        ToastUtil.show(this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query("旅游", "", cityname);
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
    public void Poirefresh(PoiResult POIR){
//卡片
        for(int i=0;i<10;i++){
            list.add("test"+i);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) viewPager.getLayoutParams();
        params.width= com.mbsgood.viewpaperslip.utils.CommonUtils.getScreenWidth(this)-CommonUtils.dp2px(this,80);
        params.setMargins(CommonUtils.dp2px(this,16),
                CommonUtils.dp2px(this,20+16),
                CommonUtils.dp2px(this,16),
                CommonUtils.dp2px(this,20+16));
        viewPager.setLayoutParams(params);
        viewPager.setPageMargin(CommonUtils.dp2px(this,8));
        for(int i=0;i<list.size(); i++){
            CardView cardView= (CardView) LayoutInflater.from(this).inflate(R.layout.cardview_item,null,false);
            TextView textView=(TextView) cardView.findViewById(R.id.tv_name);
            textView.setText(
                    "名称:"+POIR.getPois().get(i).getTitle()+ System.getProperty ("line.separator")+
                    "地址:"+POIR.getPois().get(i).getSnippet()+ System.getProperty ("line.separator")+
                    "商圈:"+POIR.getPois().get(i).getBusinessArea()+ System.getProperty ("line.separator")+
                    "导航:"+POIR.getPois().get(i).getDirection()+ System.getProperty ("line.separator")+
                    "网页"+POIR.getPois().get(i).getWebsite()+System.getProperty ("line.separator")+
                    "图片数量："+POIR.getPois().get(i).getPhotos().size()+System.getProperty ("line.separator")+
                    POIR.getSearchSuggestionCitys().size());
            ImageView IMG=(ImageView)cardView.findViewById(R.id.SITEIMG) ;
            IMG.setImageBitmap(bitmap);
            cardView.setTag(textView);
            views.add(cardView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("clic","clicked");
                }
            });
        }
        FlipViewPaper flipViewPaper=new FlipViewPaper(views);
        viewPager.setAdapter(flipViewPaper);
        viewPager.setOffscreenPageLimit(2);viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//position第几页

                //Log.e("onPageScrolled","position "+position+"positionOffset"+positionOffset+"positionOffsetPixels"+positionOffsetPixels);
                if(position-preposirion==1){
                    Downbuttontab.animate().alpha(0).setDuration(500).setListener(null);
                }
                else {
                    Downbuttontab.animate().alpha(1).setDuration(500).setListener(null);
                }
                preposirion=position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public Bitmap PICSET()
    {
        Bitmap BM=null;
        try{
            URL url=new URL("https://img1.qunarzz.com/travel/d8/1702/85/8eba3c25781398b5.jpg_r_680x510x95_839186f7.jpg");
            // 从URL获取对应资源的 InputStream
            InputStream inputStream = url.openStream();
            // 用inputStream来初始化一个Bitmap 虽然此处是Bitmap，但是URL不一定非得是Bitmap
            BM = BitmapFactory.decodeStream(inputStream);
            // 关闭 InputStream
            inputStream.close();
            return BM;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return BM;
    }
}
