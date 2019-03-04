package com.cs2017.yupool.PoolRegister;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cs2017.yupool.MapController.MapViewController;
import com.cs2017.yupool.PoolList.Search.LocationSearchTask;
import com.cs2017.yupool.PoolList.Search.SearchListAdapter;
import com.cs2017.yupool.R;
import com.cs2017.yupool.Util;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    카풀 리스트를 보여주는 프레그먼트
    지도형식 보기, 리스트 형식 보기
 */
public class PoolRegisterFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Button fromBT;
    private Button toBT;

    private Button searchBT;
    private EditText searchET;
    private LinearLayout searchLayout;
    private ListView searchListView;
    private SearchListAdapter searchListAdapter;
    private TextView searchResultTV;

    private LinearLayout searchBarLayout;
    private LinearLayout fromToLayout;

    private Button myLocationBT;

    private Button commitBT;

    // 지도 뷰와 관련된 변수들
    private RelativeLayout mapLayout;
    private TMapView mapView;

    private TMapPOIItem toItem;
    private TMapPOIItem fromItem;

    private int currentBT = -1;
    private static final int FROM_BT =0;
    private static final int TO_BT = 1;

    private Location lastLocation;

    boolean firstFlag = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firstFlag = true;
        rootView = inflater.inflate(R.layout.fragment_regpool,container,false); // 부모에 붙임

        fromBT = rootView.findViewById(R.id.fromBT);
        toBT = rootView.findViewById(R.id.toBT);
        fromBT.setOnClickListener(this);
        toBT.setOnClickListener(this);


        myLocationBT = rootView.findViewById(R.id.myLocationBT);
        myLocationBT.setOnClickListener(this);

        searchBT = rootView.findViewById(R.id.searchBT);
        searchBT.setOnClickListener(this);
        searchET = rootView.findViewById(R.id.searchET);
        searchBarLayout = rootView.findViewById(R.id.searchBarLayout);
        fromToLayout = rootView.findViewById(R.id.fromToLayout);
        searchResultTV = rootView.findViewById(R.id.resultTV);
        searchLayout = rootView.findViewById(R.id.searchLayout);
        searchListView = rootView.findViewById(R.id.searchListView);
        searchListAdapter = new SearchListAdapter(rootView.getContext());
        searchListView.setAdapter(searchListAdapter);

        commitBT = rootView.findViewById(R.id.commitBT);
        commitBT.setOnClickListener(this);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 if(currentBT == FROM_BT){
                     fromItem = searchListAdapter.getItem(i);
                     MapViewController.getInstance().setFromItem(fromItem);
                     fromBT.setText("출발지 [" + fromItem.getPOIName()+"]");
                     MapViewController.getInstance().setFromMarker(mapView,fromItem,MapViewController.FROM);
                     if(toItem !=null) {
                         MapViewController.getInstance().clearRoute(mapView);
                         MapViewController.getInstance().showRoute(mapView, fromItem, toItem);
                         commitBT.setVisibility(View.VISIBLE);
                     }else{
                         commitBT.setVisibility(View.GONE);
                     }
                 }else if(currentBT == TO_BT){
                     toItem = searchListAdapter.getItem(i);
                     MapViewController.getInstance().setToItem(toItem);
                     toBT.setText("도착지 [" +toItem.getPOIName()+"]");
                     MapViewController.getInstance().setFromMarker(mapView,toItem,MapViewController.TO);
                     if(fromItem!=null){
                         MapViewController.getInstance().clearRoute(mapView);
                         MapViewController.getInstance().showRoute(mapView,fromItem,toItem);
                         commitBT.setVisibility(View.VISIBLE);
                     }else{
                         commitBT.setVisibility(View.GONE);
                     }
                 }
                setSearchLayoutVisible(false);
                Util.hideKeyboard(rootView);
            }
        });
        initMapView();
        return rootView;
    }


    private void initMapView(){
        mapLayout = (RelativeLayout)rootView.findViewById(R.id.mapView);
        mapView = new TMapView(rootView.getContext());
        mapView.setSKPMapApiKey(getString(R.string.SK_Key));
        // 언어 설정
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        // 중심 좌표 이동 (경도,위도)
        //mapView.setCenterPoint(128.754573,35.830514);
        // 현재위치로 표시될 좌표의 경도,위도 표시(경도,위도)
        //mapView.setLocationPoint(128.754573,35.830514);
        // 현재 위치로 표시될 아이콘 설정
        //mapView.setIcon(BitmapFactory.decodeResource(getResources(),R.drawable.other));
        mapView.setIconVisibility(false);
        // 지도 축적 레벨 설정 7-19레벨 설정 가능
        mapView.setZoomLevel(13);
        // 지도 한단계 줌 인 / 아웃
        //mapView.MapZoomIn();
        //mapView.MapZoomOut();
        /*
            지도 타입 선택
            MAPTYPE_STANDARD: 일반지도 / MAPTYPE_TRAFFIC: 실시간 교통지도
         */
        mapView.setMapType(TMapView.MAPTYPE_HYBRID);
        // 단말기 방향에 따라 움직이는 나침반 모드로 설정
        mapView.setCompassMode(false);
        // 화면 중심을 단말의 현재위치로 이동시켜주는 트래킹 모드 설정
        mapView.setTrackingMode(false);
        // 시야 표출 여부 설정정
        //mapView.setSightVisible(true);
        mapLayout.addView(mapView);
        mapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
                // 드래그 리스너

            }
        });
    }



    public void setMapCenter(Location location){
        MapViewController.getInstance().showMyLocation(mapView,location);
    }

    public void setUserLocation(Location location){
        lastLocation = location;
        if(firstFlag) {
            setMapCenter(location);
            firstFlag = false;
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fromBT:
                setSearchLayoutVisible(true);
                currentBT = FROM_BT;
                commitBT.setVisibility(View.GONE);
                break;
            case R.id.toBT:
                setSearchLayoutVisible(true);
                currentBT = TO_BT;
                commitBT.setVisibility(View.GONE);
                break;
            case R.id.searchBT:
                if(searchET.getText().length()==0){
                    Toast.makeText(view.getContext(),"검색어를 입력해 주세요",Toast.LENGTH_SHORT).show();
                    break;
                }
                LocationSearchTask locationSearchTask = new LocationSearchTask(searchListAdapter,searchResultTV,searchET.getText().toString());
                locationSearchTask.execute();
                break;
            case R.id.myLocationBT:
                if(lastLocation!=null)
                    MapViewController.getInstance().showMyLocation(mapView,lastLocation);
                break;
            case R.id.commitBT:
                RegisterDetailDialog registerDetailDialog = new RegisterDetailDialog(getContext());
                registerDetailDialog.show();
                break;
        }
    }

    public void setSearchLayoutVisible(boolean visible){
        if(visible){
            searchLayout.setVisibility(View.VISIBLE);
            searchBarLayout.setVisibility(View.VISIBLE);
            fromToLayout.setVisibility(View.GONE);
        } else {
            searchLayout.setVisibility(View.GONE);
            searchBarLayout.setVisibility(View.GONE);
            fromToLayout.setVisibility(View.VISIBLE);
        }
    }

}
