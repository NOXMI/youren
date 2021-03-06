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
    //???????????????target > 28????????????????????????????????????????????????"????????????"???????????????
    private static final String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    public String addressName = "?????????"//??????????????????
            , cityname//??????????????????
            , Tagname//????????????????????????
            , updialog//????????????????????????
            , uppackname//???????????????
            , Currenttagname//???????????????
            , DownloadUrl;//????????????
    public GeocodeSearch geocoderSearch;//????????????
    public Location mainlocation;//????????????
    //????????????
    protected String[] needPermissions =
            {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    BACK_LOCATION_PERMISSION
            };
    ImageView Startimg//????????????
            , show;//????????????
    Button ditu, zhuye, geren;//????????????
    ViewPager viewPager;//????????????
    AMap mainaMap;//?????????????????????????????????
    MapView mainmapView;//??????????????????
    MyLocationStyle myLocationStyle;//????????????
    ArrayList<String> list = new ArrayList<>();//??????POI
    ArrayList<View> views = new ArrayList<>();//????????????
    LinearLayout Downbuttontab, Wethertab;//?????????????????????
    int preposirion = 0//?????????????????????????????????????????????
            , currentPage//??????????????????
            , MAX = 0;
    FlipViewPaper flipViewPaper;
    Bitmap bitmap;//?????????????????????
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    },
            apkdownloadhandler = new Handler() {//apk????????????
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    //downLoadApk(MainActivity.this,DownloadUrl);
                    download.downloadask(MainActivity.this, Tagname, updialog, DownloadUrl, Tagname);
                }
            };
    //??????????????????
    Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL("https://store.is.autonavi.com/showpic/2340ed5d2cc172b24e2aab60c1a3c36a");
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                handler.sendEmptyMessage(1);//?????????????????????????????????????????????????????????handler??????handleMessage????????????????????????????????????
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
    //??????????????????
    Thread updatecheck = new Thread() {
        public void run() {
            super.run();
            try {

                //????????????url????????????
                URL url = new URL(LatestUrl);
                //??????HttpURLConnection??????
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //????????????????????????,???????????????
                connection.setConnectTimeout(5000);

                //http??????
                connection.setRequestMethod("GET");
                //???????????????//200?????????      404 ???????????????
                int code = connection.getResponseCode();
                if (code == 200) {

                    //???????????????
                    InputStream inputStream = connection.getInputStream();
                    //???????????????
                    BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String s = null;
                    while ((s = bf.readLine()) != null) {
                        sb.append(s + "\r\n");
                    }
                    //??????json??????
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    JSONObject assets = new JSONObject(jsonObject.getJSONArray("assets").get(0).toString());
                    DownloadUrl = assets.getString("browser_download_url");
                    Tagname = jsonObject.getString("tag_name");
                    updialog = jsonObject.getString("body");
                    uppackname = jsonObject.getString("name");
                    if (!Tagname.equals(updateinfo.getVersionName(MainActivity.this))) {
                        //Log.e("up","needup");
                        apkdownloadhandler.sendEmptyMessage(1);//???handler
                    } else {
                        //Log.e("up","noneed");
                    }

                } else {
                    Toast.makeText(MainActivity.this, "??????????????????", Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", "fail");
            }
        }
    };
    private int POIcurrentPage = 0;// ??????????????????0????????????
    private TextView reporttime1, weather, Temperature, wind, humidity//????????????
            , city;//???????????????
    private WeatherSearchQuery mquery;//????????????
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private final LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);
    private PoiSearch.Query query;// Poi???????????????
    private PoiSearch poiSearch;// POI??????
    private PoiResult poiResult; // poi???????????????
    //????????????????????????????????????????????????true???????????????????????????????????????????????????????????????
    private boolean needCheckBackLocation = false;
    //????????????????????????????????????????????????
    private boolean isNeedCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainmapView = (MapView) findViewById(R.id.map);
        //mainmapView.onCreate(savedInstanceState);// ?????????????????????

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        inition();

        //??????
        ditu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationModeSourceActivity.class);
                startActivity(intent);
                //getAddress(latLonPoint);
            }
        });
        //??????
        zhuye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, gongluemain.class);
                startActivity(intent);
            }
        });
        //??????
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

    //?????????
    public void inition() {
        show = (ImageView) findViewById(R.id.weatherSHOW);
        //thread.start();
        updatecheck.start();//????????????

        myLocationStyle = new MyLocationStyle();
        zhuye = (Button) findViewById(R.id.zhuyebtn);
        ditu = (Button) findViewById(R.id.mapbtn);
        geren = (Button) findViewById(R.id.zijibtn);
        Downbuttontab = (LinearLayout) findViewById(R.id.bottom_tab_layout);
        Wethertab = (LinearLayout) findViewById(R.id.weathertab);

        Startimg = (ImageView) findViewById(R.id.startimg);
        setTitle("??????" + MapsInitializer.getVersion());

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
        //??????SDK ????????????????????????
        mainaMap.setOnMyLocationChangeListener(this);
        //????????????
        reporttime1 = (TextView) findViewById(R.id.reporttime1);
        weather = (TextView) findViewById(R.id.weather);
        Temperature = (TextView) findViewById(R.id.temp);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        city = (TextView) findViewById(R.id.city);
        Wethertab.setOnClickListener(new View.OnClickListener() {//????????????
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherSearchActivity.class);
                intent.putExtra("citynameString", addressName);
                startActivity(intent);
            }
        });


    }

    //??????
    @Override
    public void onMyLocationChange(Location location) {
        // ??????????????????
        if (location != null) {
            Log.e("amap", "onMyLocationChange ??????????????? lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            latLonPoint.setLatitude(location.getLatitude());
            latLonPoint.setLongitude(location.getLongitude());
            getAddress(latLonPoint);
            Bundle bundle = location.getExtras();
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // ????????????????????????GPS WIFI???????????????????????????????????????SDK??????
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                mainlocation = location;
                Log.e("llenr",mainlocation.toString());


                /*
                errorCode
                errorInfo
                locationType
                */
                Log.e("amap", "??????????????? code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "??????????????? bundle is null ");

            }

        } else {
            Log.e("amap", "????????????");
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT);
        }
    }

    //????????????
    private void setUpMap() {

        // ??????????????????????????????????????????????????????????????????
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        mainaMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));

        mainaMap.getUiSettings().setMyLocationButtonEnabled(true);// ????????????????????????????????????
        mainaMap.getUiSettings().setCompassEnabled(true);
        mainaMap.getUiSettings().setRotateGesturesEnabled(true);
        mainaMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false

    }

    //????????????
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
     * ?????????????????????????????????????????????
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
     * ??????????????????????????????????????????
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
     * ??????????????????
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("??????");
            builder.setMessage("?????????????????????????????????\\n\\n?????????\\\"??????\\\"-\\\"??????\\\"-??????????????????");

            // ??????, ????????????
            builder.setNegativeButton("??????",
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

            builder.setPositiveButton("??????",
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
     * ?????????????????????
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

    //????????????
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// ???????????????????????????Latlng????????????????????????????????????????????????????????????????????????????????????GPS???????????????
        geocoderSearch.getFromLocationAsyn(query);// ?????????????????????????????????
    }

    //???????????????????????????????????????
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress();

                String[] Cname = addressName.split("???|???");
                if (addressName.indexOf("???") >= 0) cityname = Cname[1] + "???";//??????
                else cityname = Cname[0] + "???";//?????????
                city.setText(cityname);
                //??????
                searchliveweather();
                //POI??????
                doSearchQuery();
                //???????????????????????????
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
     * ??????????????????
     */
    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//??????????????????????????????????????????????????????1??????????????????2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //????????????
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "??");
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
            if (result != null && result.getQuery() != null) {// ??????poi?????????
                if (result.getQuery().equals(query)) {// ??????????????????
                    poiResult = result;
                    // ??????????????????poiitems????????????
                    List<PoiItem> poiItems = poiResult.getPois();// ??????????????????poiitem????????????????????????0??????
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// ???????????????poiitem?????????????????????????????????????????????????????????
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
     * ????????????poi??????
     */
    protected void doSearchQuery() {
        POIcurrentPage = 0;
        query = new PoiSearch.Query("??????", "", cityname);
        // ????????????????????????????????????????????????????????????poi????????????????????????????????????poi??????????????????????????????????????????
        query.setPageSize(10);// ?????????????????????????????????poiitem
        query.setPageNum(POIcurrentPage);// ??????????????????
        query.setExtensions("all");

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    //poi??????
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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//position?????????

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
                query.setPageNum(POIcurrentPage);// ??????????????????
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
                    "??????: " + POIR.getPois().get(i).getSnippet()+"\n"+
                    "??????: "+POIR.getPois().get(i).getTypeDes()+"\n"+"??????"+POIR.getPois().get(i).getTel()
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

                    // ??????????????????
                    NaviPara naviPara = new NaviPara();
                    // ??????????????????
                    LatLng LL = new LatLng(LP.getLatitude(), LP.getLongitude());
                    naviPara.setTargetPoint(LL);
                    // ??????????????????????????????????????????
                    naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
                    try {
                        // ????????????????????????
                        AMapUtils.openAMapNavi(naviPara, getApplicationContext());
                    } catch (com.amap.api.maps.AMapException e) {
                        // ???????????????????????????????????????????????????
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
