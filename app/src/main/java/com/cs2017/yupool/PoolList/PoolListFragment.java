package com.cs2017.yupool.PoolList;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.ViewFlipper;

import com.cs2017.yupool.PoolList.Adapter.PoolListAdapter;
import com.cs2017.yupool.MapController.MapViewController;
import com.cs2017.yupool.PoolList.Search.LocationSearchTask;
import com.cs2017.yupool.PoolList.Search.SearchListAdapter;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.R;
import com.cs2017.yupool.UI.DetailActivity;
import com.cs2017.yupool.UI.MainActivity;
import com.cs2017.yupool.Util;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    카풀 리스트를 보여주는 프레그먼트
    지도형식 보기, 리스트 형식 보기
 */
public class PoolListFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ListView poolListView;
    private PoolListAdapter poolListAdapter;
    private Button changeBT;
    private Button currentBT;
    private EditText searchET;
    private Button searchBT;

    private LinearLayout searchLayout;
    private ListView searchListView;
    private SearchListAdapter searchListAdapter;
    private TextView searchResultTV;
    private boolean isSearchShowing = false;

    private ViewFlipper flipper;

    boolean firstFlag = true;

    boolean searchResult = false;


    // 지도 뷰와 관련된 변수들
    private boolean isMapShowing = false;
    private RelativeLayout mapLayout;
    private TMapView mapView;

    private Location lastLocation; // 최근 현재위치

    private ArrayList<PoolItem>list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        firstFlag = true;

        rootView = inflater.inflate(R.layout.fragment_pool_list,container,false); // 부모에 붙임
        poolListView = (ListView)rootView.findViewById(R.id.poolListView);

        changeBT = (Button)rootView.findViewById(R.id.changeModBT);
        changeBT.setOnClickListener(this);

        currentBT = rootView.findViewById(R.id.currentBT);
        currentBT.setOnClickListener(this);

        searchET = rootView.findViewById(R.id.searchET);
        searchBT = rootView.findViewById(R.id.searchBT);
        searchBT.setOnClickListener(this);

        flipper = rootView.findViewById(R.id.flipper);

        searchResultTV = rootView.findViewById(R.id.resultTV);
        searchLayout = rootView.findViewById(R.id.searchLayout);
        searchListView = rootView.findViewById(R.id.searchListView);
        searchListAdapter = new SearchListAdapter(rootView.getContext());
        searchListView.setAdapter(searchListAdapter);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 선택한 항목에 대해 검색하여 리스트 정렬
                TMapPoint searchTarget = searchListAdapter.getItem(i).getPOIPoint();
                //poolListAdapter.searchSort(searchTarget,searchListAdapter.getItem(i).getPOIName());
                String filterString = searchTarget.getLatitude() +":"+searchTarget.getLongitude();
                System.err.println(filterString);
                poolListView.setTextFilterEnabled(true);
                ((PoolListAdapter)poolListView.getAdapter()).getFilter().filter(filterString);
               // poolListView.setFilterText(filterString);
                setSearchLayoutVisible(false);
                Util.hideKeyboard(rootView);
                searchBT.setText("X");
                searchBT.setBackgroundResource(R.drawable.round_red_square);
                searchResult = true;
            }
        });

        poolListAdapter = new PoolListAdapter(rootView.getContext());
        poolListView.setAdapter(poolListAdapter);

        list = DatabaseManager.getInstance().getPoolList("");
        if(list != null){
            poolListAdapter.setList(list);
            poolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    PoolItem item = poolListAdapter.getItem(i);
                    if(item!=null){
                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtra("item",item);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(),"에러!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(getContext(),"서버와 통신 실패",Toast.LENGTH_SHORT).show();
        }
        initMapView();

        Log.d("PoolListFragment","initView");
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

        if(list!=null){
            // 맵뷰에 리스트들 보여줌
            for(PoolItem item : list){
                MapViewController.getInstance().addMarker(mapView,item,getContext());
            }
        }

        mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                PoolItem item = poolListAdapter.getItemFromIDX(tMapMarkerItem.getID());
                if(item!=null){
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("item",item);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"에러!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.changeModBT:
                if(isMapShowing){
                    isMapShowing = false;
                    changeBT.setText("지도뷰");
                }else{
                    isMapShowing = true;
                    changeBT.setText("리스트뷰");
                }
                flipper.showNext();
                break;
            case R.id.currentBT:
                if(lastLocation!=null)
                    MapViewController.getInstance().showMyLocation(mapView,lastLocation);
                break;
            case R.id.searchBT:
                if(searchResult) {
                    searchET.setText("");
                    searchBT.setText("검색");
                    searchBT.setBackgroundResource(R.drawable.round_blue_square);
                    //poolListView.clearTextFilter();
                    ((PoolListAdapter)poolListView.getAdapter()).getFilter().filter("");
                    searchResult = false;
                    break;
                }else{
                    if(searchET.getText().length()==0){
                        Toast.makeText(view.getContext(),"검색어를 입력해 주세요",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    LocationSearchTask locationSearchTask = new LocationSearchTask(searchListAdapter,searchResultTV,searchET.getText().toString());
                    locationSearchTask.execute();
                    setSearchLayoutVisible(true);
                    break;
                }
        }
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

    public boolean isSearchLayoutVisible(){
        if(searchLayout.getVisibility()==View.VISIBLE)
            return true;
        else
            return false;
    }

    public void setSearchLayoutVisible(boolean visible){
        if(visible)
            searchLayout.setVisibility(View.VISIBLE);
        else
            searchLayout.setVisibility(View.GONE);
    }

}
