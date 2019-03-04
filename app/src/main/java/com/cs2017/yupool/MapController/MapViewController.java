package com.cs2017.yupool.MapController;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.bumptech.glide.Glide;
import com.cs2017.yupool.PoolRegister.PoolItem;
import com.cs2017.yupool.R;
import com.cs2017.yupool.Util;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by cs2017 on 2017-11-09.
 */

public class MapViewController {

    public static final int FROM = 0;
    public static final int TO = 1;


    private TMapPOIItem fromItem = null;
    private TMapPOIItem toItem = null;


    private static MapViewController instance = new MapViewController();
    private MapViewController(){
    };
    public static MapViewController getInstance(){
        return instance;
    }


    public void setFromMarker(TMapView mapView, TMapPOIItem tMapPOIItem, int which){
        double longitude = tMapPOIItem.getPOIPoint().getLongitude();
        double latitude = tMapPOIItem.getPOIPoint().getLatitude();
        mapView.setCenterPoint(longitude,latitude,false);
        TMapMarkerItem item = new TMapMarkerItem();
        item.setTMapPoint(new TMapPoint(latitude,longitude));
        item.setName(tMapPOIItem.getPOIName());
        item.setVisible(TMapMarkerItem.VISIBLE);
        item.setCanShowCallout(true); // 풍선뷰 표시 여부를 결정한다.
        item.setAutoCalloutVisible(true); // 풍선뷰가 자동으로 활성화 되도록 한다.


        if(which == FROM){
            item.setCalloutTitle("[출발지]"+tMapPOIItem.getPOIName()); // 풍선뷰의 타이틀을 결정한다.
            mapView.addMarkerItem("from",item);
        } else if(which == TO){
            item.setCalloutTitle("[도착지]"+tMapPOIItem.getPOIName()); // 풍선뷰의 타이틀을 결정한다
            mapView.addMarkerItem("to",item);

        }
    }

    public void clearRoute(TMapView mapView){
        mapView.removeAllTMapPolyLine();
    }
    public void showRoute(TMapView mapView,TMapPOIItem start, TMapPOIItem end){
        TMapPoint startPoint = new TMapPoint(start.getPOIPoint().getLatitude(),start.getPOIPoint().getLongitude());
        TMapPoint endPoint = new TMapPoint(end.getPOIPoint().getLatitude(),end.getPOIPoint().getLongitude());
        RouteRequestRun routeRequestRun =new RouteRequestRun(startPoint,endPoint);
        Thread t = new Thread(routeRequestRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mapView.addTMapPolyLine("route",routeRequestRun.getRoute());
    }

    public void showRoute(TMapView mapView,PoolItem item){
        TMapPoint from = new TMapPoint(Double.valueOf(item.getFromLati()),Double.valueOf(item.getFromLongi()));
        TMapPoint to = new TMapPoint(Double.valueOf(item.getToLati()),Double.valueOf(item.getToLongi()));

        TMapMarkerItem fromItem = new TMapMarkerItem();
        fromItem.setCanShowCallout(true);
        fromItem.setAutoCalloutVisible(true);
        fromItem.setCalloutTitle("[출발지]"+item.getFromName());
        fromItem.setCalloutSubTitle(item.getFromAddr());
        fromItem.setTMapPoint(from);

        TMapMarkerItem toItem = new TMapMarkerItem();
        toItem.setCanShowCallout(true);
        toItem.setAutoCalloutVisible(true);
        toItem.setCalloutTitle("[도착지]"+item.getToName());
        toItem.setCalloutSubTitle(item.getToAddr());
        toItem.setTMapPoint(to);

        mapView.addMarkerItem("from",fromItem);
        mapView.addMarkerItem("to",toItem);

        mapView.setCenterPoint(from.getLongitude(),from.getLatitude());

        RouteRequestRun routeRequestRun =new RouteRequestRun(from,to);
        Thread t = new Thread(routeRequestRun);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mapView.addTMapPolyLine("route",routeRequestRun.getRoute());
    }

    public void showMyLocation(TMapView mapView, Location location){
        if(location != null && mapView !=null) {
            double lati = location.getLatitude();
            double longi = location.getLongitude();
            mapView.setCenterPoint(longi, lati);
            mapView.setLocationPoint(longi, lati);
            mapView.setIconVisibility(true);
        }
    }


    public void addMarker(final TMapView mapView, final PoolItem item, final Context context){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                TMapMarkerItem markerItem = new TMapMarkerItem();
                markerItem.setTMapPoint(new TMapPoint(Double.valueOf(item.getFromLati()),Double.valueOf(item.getFromLongi())));
                markerItem.setVisible(TMapMarkerItem.VISIBLE);
                markerItem.setCanShowCallout(true); // 풍선뷰 표시 여부를 결정한다.
                //markerItem.setAutoCalloutVisible(true); // 풍선뷰가 자동으로 활성화 되도록 한다.
                markerItem.setCalloutTitle(item.getFromAddr()); // 풍선뷰의 타이틀을 결정한다
                markerItem.setCalloutSubTitle(item.getStartDate());
                Bitmap icon = Util.drawableToBitmap(context.getResources().getDrawable(R.drawable.icon_check));
                //System.err.println(icon.getDensity());
                System.out.println(icon);
                markerItem.setCalloutRightButtonImage(icon);
                mapView.addMarkerItem(item.getIdx(),markerItem);
                try {
                    Bitmap image = Glide.
                            with(context).
                            load("http://alsdn.iptime.org:9986/yupool/profile_pic/"+item.getId()+".jpg").
                            asBitmap().
                            into(150, 150). // Width and height // Not work
                            get();
                    image = Util.getCroppedBitmap(Bitmap.createScaledBitmap(image,180,180,true));
                    markerItem.setIcon(image);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public TMapPOIItem getFromItem() {
        return fromItem;
    }

    public void setFromItem(TMapPOIItem fromItem) {
        this.fromItem = fromItem;
    }

    public TMapPOIItem getToItem() {
        return toItem;
    }

    public void setToItem(TMapPOIItem toItem) {
        this.toItem = toItem;
    }


}
