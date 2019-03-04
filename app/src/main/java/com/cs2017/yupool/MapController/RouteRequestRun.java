package com.cs2017.yupool.MapController;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by cs2017 on 2017-11-09.
 */

public class RouteRequestRun implements Runnable {

    private TMapPolyLine route;
    private TMapData tMapData;
    private TMapPoint startPoint;
    private TMapPoint endPoint;

    public RouteRequestRun(TMapPoint startPoint,TMapPoint endPoint){
        tMapData = new TMapData();
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    @Override
    public void run() {
        try {
            route = tMapData.findPathData(startPoint,endPoint);

            route.setLineWidth(20);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public TMapPolyLine getRoute(){
        return route;
    }
}
