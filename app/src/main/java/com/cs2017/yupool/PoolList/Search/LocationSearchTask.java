package com.cs2017.yupool.PoolList.Search;

import android.os.AsyncTask;
import android.widget.TextView;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by cs2017 on 2017-11-07.
 */

public class LocationSearchTask extends AsyncTask {

    private SearchListAdapter searchListAdapter;
    private TextView searchResultTV;
    private String keyword;

    private boolean noResult = true;

    public LocationSearchTask(SearchListAdapter searchListAdapter,TextView searchResultTV,String keyword){
        this.searchListAdapter = searchListAdapter;
        this.searchResultTV = searchResultTV;
        this.keyword = keyword;
    }
    @Override
    protected Object doInBackground(Object[] objects) {

                TMapData tMapData = new TMapData();
                ArrayList<TMapPOIItem> items = new ArrayList<>();
                try {
                    ArrayList<TMapPOIItem> result = tMapData.findAllPOI(keyword);
                    if(result!=null) items = result;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                if(items.size()==0){
                    noResult = true;
                }else{
                    noResult = false;
                }
        searchListAdapter.setArray(items);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(noResult){
            searchResultTV.setText("\""+keyword +"\" 에 대한 검색결과가 없습니다.");
        }else{
            searchResultTV.setText("검색결과 \""+keyword+"\"");
        }
        searchListAdapter.notifyDataSetChanged();
        super.onPostExecute(o);
    }
}
