package com.noxmi.youren.location;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.model.Poi;
import com.noxmi.youren.R;

/**
 * AMapV2地图中介绍定位几种类型
 */
public class LocationModeSourceActivity extends Activity implements AMap.OnMyLocationChangeListener, AdapterView.OnItemSelectedListener, AMap.OnPOIClickListener, AMap.OnMarkerClickListener {
	private AMap aMap;
	private MapView mapView;
	private Spinner spinnerGps;
	private String[] itemLocationTypes = { "展示", "定位", "追随", "旋转", "旋转位置", "追随不移动到中心点", "旋转不移动到中心点", "旋转位置不移动到中心点" };

	private MyLocationStyle myLocationStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.locationmodesource_activity);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {

		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}

		spinnerGps = (Spinner) findViewById(R.id.spinner_gps);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemLocationTypes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerGps.setAdapter(adapter);

		spinnerGps.setOnItemSelectedListener(this);

		//设置SDK 自带定位消息监听
		aMap.setOnMyLocationChangeListener(this);
		//地图点击
		aMap.setOnPOIClickListener(this);
		aMap.setOnMarkerClickListener(this);


	}


	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {

		// 如果要设置定位的默认状态，可以在此处进行设置
		myLocationStyle = new MyLocationStyle();
		aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));

		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.getUiSettings().setCompassEnabled(true);

	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
				// 只定位，不进行其他操作
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
				break;
			case 1:
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
				break;
			case 2:
				// 设置定位的类型为 跟随模式
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));
				break;
			case 3:
				// 设置定位的类型为根据地图面向方向旋转
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE));
				break;
			case 4:
				// 定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
				break;
			case 5 :
				// 定位、但不会移动到地图中心点，并且会跟随设备移动。
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
				break;
			case 6 :
				// 定位、但不会移动到地图中心点，地图依照设备方向旋转，并且会跟随设备移动。
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER));
				break;
			case 7 :
				// 定位、但不会移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
				aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER));
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onMyLocationChange(Location location) {
		// 定位回调监听
		if(location != null) {
			Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
			Bundle bundle = location.getExtras();
			if(bundle != null) {
				int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
				String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
				// 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
				int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);

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

	@Override
	public boolean onMarkerClick(Marker marker) {
		// 构造导航参数
		NaviPara naviPara = new NaviPara();
		// 设置终点位置
		naviPara.setTargetPoint(marker.getPosition());
		// 设置导航策略，这里是避免拥堵
		naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
		try {
			// 调起高德地图导航
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// 如果没安装会进入异常，调起下载页面
			AMapUtils.getLatestAMapApp(getApplicationContext());
		}
		aMap.clear();
		return false;
	}

	@Override
	public void onPOIClick(Poi poi) {
		aMap.clear();
		Log.i("MY", poi.getPoiId()+poi.getName());
		MarkerOptions markOptiopns = new MarkerOptions();
		markOptiopns.position(poi.getCoordinate());
		TextView textView = new TextView(getApplicationContext());
		textView.setText("到"+poi.getName()+"去");
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		textView.setBackgroundResource(R.drawable.custom_info_bubble);
		markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
		aMap.addMarker(markOptiopns);
	}
}
