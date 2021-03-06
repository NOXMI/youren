package com.noxmi.youren;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
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
import com.noxmi.youren.basicmap.WeatherSearchActivity;
import com.noxmi.youren.basicmap.weatherpic;
import com.noxmi.youren.card.FlipViewPaper;
import com.noxmi.youren.gonglue.gongluemain;
import com.noxmi.youren.location.LocationModeSourceActivity;
import com.noxmi.youren.setting.settingmain;
import com.noxmi.youren.update.download;
import com.noxmi.youren.update.updateinfo;
import com.noxmi.youren.util.CommonUtils;
import com.noxmi.youren.util.ToastUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements AMap.OnMyLocationChangeListener, GeocodeSearch.OnGeocodeSearchListener, WeatherSearch.OnWeatherSearchListener
        , PoiSearch.OnPoiSearchListener {

    public static final String LatestUrl = "https://api.github.com/repos/NOXMI/youren/releases/latest";
    private static final int PERMISSON_REQUESTCODE = 0;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static final String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    public String addressName = "定位中"//定位图片中转
            , cityname//定位城市名字
            , Tagname//网络获取程序名字
            , updialog//网络获取更新日志
            , uppackname//安装包名称
            , Currenttagname//现版本名字
            , DownloadUrl;//下载链接
    public GeocodeSearch geocoderSearch;//逆向定位
    public Location mainlocation;//主要定位
    //权限检查
    protected String[] needPermissions =
            {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    BACK_LOCATION_PERMISSION
            };
    ImageView Startimg//开始图片
            , show;//网络图片
    Button ditu, zhuye, geren;//主页按钮
    ViewPager viewPager;//卡片载体
    AMap mainaMap;//主页地图，天气定位载体
    MapView mainmapView;//主页地图载体
    MyLocationStyle myLocationStyle;//定位模式
    ArrayList<String> list = new ArrayList<>();//附近POI
    ArrayList<View> views = new ArrayList<>();//卡片容器
    LinearLayout Downbuttontab, Wethertab;//按钮框，天气框
    int preposirion = 0//前一个卡片位置翻页按钮隐形判断
            , currentPage//现在卡片位置
            , MAX = 0;
    FlipViewPaper flipViewPaper;
    Bitmap bitmap;//得到获取的图片
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    },
            apkdownloadhandler = new Handler() {//apk更新下载
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    //downLoadApk(MainActivity.this,DownloadUrl);
                    download.downloadask(MainActivity.this, Tagname, updialog, DownloadUrl, Tagname);
                }
            };
    //网络图片获取
    Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL("https://store.is.autonavi.com/showpic/2340ed5d2cc172b24e2aab60c1a3c36a");
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                handler.sendEmptyMessage(1);//主线程中是不能更新的，所以得发送消息到handler，到handleMessage方法中设置获取得到的图片
                inputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("fail", "MalformedURLException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("fail", "IOException");
            }
        }
    };
    //自动更新检查
    Thread updatecheck = new Thread() {
        public void run() {
            super.run();
            try {

                //首先声明url连接对象
                URL url = new URL(LatestUrl);
                //获取HttpURLConnection对象
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置连接超时时间,毫秒为单位
                connection.setConnectTimeout(5000);

                //http方式
                connection.setRequestMethod("GET");
                //获取返回码//200为正常      404 找不到资源
                int code = connection.getResponseCode();
                if (code == 200) {

                    //获取字节流
                    InputStream inputStream = connection.getInputStream();
                    //解析字节流
                    BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String s = null;
                    while ((s = bf.readLine()) != null) {
                        sb.append(s + "\r\n");
                    }
                    //解析json对象
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    JSONObject assets = new JSONObject(jsonObject.getJSONArray("assets").get(0).toString());
                    DownloadUrl = assets.getString("browser_download_url");
                    Tagname = jsonObject.getString("tag_name");
                    updialog = jsonObject.getString("body");
                    uppackname = jsonObject.getString("name");
                    if (!Tagname.equals(updateinfo.getVersionName(MainActivity.this))) {
                        //Log.e("up","needup");
                        apkdownloadhandler.sendEmptyMessage(1);//移handler
                    } else {
                        //Log.e("up","noneed");
                    }

                } else {
                    Toast.makeText(MainActivity.this, "无法检测更新", Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", "fail");
            }
        }
    };
    private int POIcurrentPage = 0;// 当前页面，从0开始计数
    private TextView reporttime1, weather, Temperature, wind, humidity//天气简版
            , city;//城市名字板
    private WeatherSearchQuery mquery;//天气查询
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private final LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    //判断是否需要检测，防止不停的弹框
    private boolean isNeedCheck = true;

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
        ditu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationModeSourceActivity.class);
                startActivity(intent);
                //getAddress(latLonPoint);
            }
        });
        //攻略
        zhuye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, gongluemain.class);
                startActivity(intent);
            }
        });
        //设置
        geren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, settingmain.class);
                intent.putExtra("Tagname", Tagname);
                intent.putExtra("updialog", updialog);
                intent.putExtra("uppackname", uppackname);
                intent.putExtra("Currenttagname", Currenttagname);
                intent.putExtra("DownloadUrl", DownloadUrl);
                startActivity(intent);
            }
        });
    }

    //初始化
    public void inition() {
        show = (ImageView) findViewById(R.id.weatherSHOW);
        //thread.start();
        updatecheck.start();//检查更新

        myLocationStyle = new MyLocationStyle();
        zhuye = (Button) findViewById(R.id.zhuyebtn);
        ditu = (Button) findViewById(R.id.mapbtn);
        geren = (Button) findViewById(R.id.zijibtn);
        Downbuttontab = (LinearLayout) findViewById(R.id.bottom_tab_layout);
        Wethertab = (LinearLayout) findViewById(R.id.weathertab);

        Startimg = (ImageView) findViewById(R.id.startimg);
        setTitle("游人" + MapsInitializer.getVersion());

        if (Build.VERSION.SDK_INT > 28
                && getApplicationContext().getApplicationInfo().targetSdkVersion > 28) {
            needPermissions = new String[]{
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
                Intent intent = new Intent(MainActivity.this, WeatherSearchActivity.class);
                intent.putExtra("citynameString", addressName);
                startActivity(intent);
            }
        });


    }

    //定位
    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if (location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            latLonPoint.setLatitude(location.getLatitude());
            latLonPoint.setLongitude(location.getLongitude());
            getAddress(latLonPoint);
            Bundle bundle = location.getExtras();
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                mainlocation = location;
                Log.e("llenr",mainlocation.toString());


                /*
                errorCode
                errorInfo
                locationType
                */
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }

        } else {
            Log.e("amap", "定位失败");
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT);
        }
    }

    //地图设置
    private void setUpMap() {

        // 如果要设置定位的默认状态，可以在此处进行设置
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        mainaMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));

        mainaMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mainaMap.getUiSettings().setCompassEnabled(true);
        mainaMap.getUiSettings().setRotateGesturesEnabled(true);
        mainaMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }

    //权限检查
    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", String[].class, int.class);
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        } catch (Throwable e) {
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
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if (!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", String.class);
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", String.class);
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
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
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
        } catch (Throwable e) {
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

    //地址获取
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    //逆向地理获取，程序开启设置
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress();

                String[] Cname = addressName.split("省|市");
                if (addressName.indexOf("省") >= 0) cityname = Cname[1] + "市";//省区
                else cityname = Cname[0] + "市";//直辖市
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
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "°");
                int i = weatherpic.picpick(weatherlive.getWeather());
                InputStream is = null;
                try {
                    is = this.getResources().getAssets().open("weatherpic/pic (" + i + ").png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                show.setImageBitmap(bitmap);

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
                        if (POIcurrentPage == 0) Poirefresh(poiResult);
                        else cardset(poiResult);
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
        POIcurrentPage = 0;
        query = new PoiSearch.Query("旅游", "", cityname);
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(POIcurrentPage);// 设置查第一页
        query.setExtensions("all");

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    //poi卡片
        public void Poirefresh(PoiResult POIR) {
        for (int i = 0; i < 10; i++) {
            list.add("card" + i);
        }
        MAX = list.size() - 2;

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) viewPager.getLayoutParams();
        params.width = CommonUtils.getScreenWidth(this) - CommonUtils.dp2px(this, 80);
        params.setMargins(CommonUtils.dp2px(this, 16),
                CommonUtils.dp2px(this, 20 + 16),
                CommonUtils.dp2px(this, 16),
                CommonUtils.dp2px(this, 20 + 16));
        flipViewPaper = new FlipViewPaper(views);
        viewPager.setLayoutParams(params);
        viewPager.setPageMargin(CommonUtils.dp2px(this, 8));
        nextPAGE();
        viewPager.setAdapter(flipViewPaper);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//position第几页

                //Log.e("onPageScrolled","position "+position+"positionOffset"+positionOffset+"positionOffsetPixels"+positionOffsetPixels);
                if (position - preposirion == 1) {
                    Downbuttontab.animate().alpha(0).setDuration(500).setListener(null);
                } else {
                    Downbuttontab.animate().alpha(1).setDuration(500).setListener(null);
                }
                preposirion = position;
                if (position >= MAX) {
                    nextPAGE();
                    MAX = flipViewPaper.getCount() - 1;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void nextPAGE() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() - 1 > POIcurrentPage) {
                POIcurrentPage++;
                query.setPageNum(POIcurrentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {
                ToastUtil.show(MainActivity.this,
                        R.string.no_result);
            }
        }
    }

    public void cardset(PoiResult POIR) {
        List<PoiItem> poiItems = poiResult.getPois();
        for (int i = 0; i < poiItems.size(); i++) {
            CardView cardView = (CardView) LayoutInflater.from(this).inflate(R.layout.cardview_item, null, false);
            TextView textView = (TextView) cardView.findViewById(R.id.tv_name);
            ImageView IMG = (ImageView) cardView.findViewById(R.id.SITEIMG);
            textView.setText(POIR.getPois().get(i).getAdName()+POIR.getPois().get(i).getTitle() + "\n" +
                    "地址: " + POIR.getPois().get(i).getSnippet()+"\n"+
                    "类型: "+POIR.getPois().get(i).getTypeDes()+"\n"+"电话"+POIR.getPois().get(i).getTel()
            );
            LatLonPoint LP = POIR.getPois().get(i).getLatLonPoint();
            if (!poiItems.get(i).getPhotos().isEmpty()) {
                Bitmap BM1 = null;
                String[] temp = POIR.getPois().get(i).getPhotos().get(0).getUrl().split(":");
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        IMG.setImageBitmap(bitmap);
                    }
                };
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            URL url = new URL("https:" + temp[1]);
                            InputStream inputStream = url.openStream();
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            handler.sendEmptyMessage(1);
                            inputStream.close();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            Log.e("fail", "MalformedURLException");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("fail", "IOException");
                        }
                    }
                }.start();
            }

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("clic", "clicked" + textView.getText() + LP);

                    // 构造导航参数
                    NaviPara naviPara = new NaviPara();
                    // 设置终点位置
                    LatLng LL = new LatLng(LP.getLatitude(), LP.getLongitude());
                    naviPara.setTargetPoint(LL);
                    // 设置导航策略，这里是避免拥堵
                    naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
                    try {
                        // 调起高德地图导航
                        AMapUtils.openAMapNavi(naviPara, getApplicationContext());
                    } catch (com.amap.api.maps.AMapException e) {
                        // 如果没安装会进入异常，调起下载页面
                        AMapUtils.getLatestAMapApp(getApplicationContext());
                    }

                }
            });
            cardView.setTag(textView);
            views.add(cardView);
        }
        flipViewPaper.notifyDataSetChanged();
    }
}
