package com.cs2017.yupool.DriverRegister;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.cs2017.yupool.AlarmDialog.AlarmDialog;
import com.cs2017.yupool.Database.DatabaseManager;
import com.cs2017.yupool.FragmentUtil.FragmentManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by cs2017 on 2017-11-10.
 */

public class LicenseAuthTask extends AsyncTask {
    private static final String TAG = "LicenseAuthTask";

    private Context context;

    private LicenseItem licenseItem;

    private boolean success = false;
    private ProgressDialog progressDialog; // 프로그레스 다이얼로그

    private String errorMSG;

    public LicenseAuthTask(Context context,LicenseItem licenseItem){
        this.context = context;
        this.licenseItem = licenseItem;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("면허증 진위여부 조회중");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            String DrvLink	= "https://dls.koroad.or.kr/jsp/ool/olq/OL_LcnsTruthYnRtvV.jsp";
            String DrvAuthLink	= "https://dls.koroad.or.kr/jsp/ool/olq/OL_LcnsTruthYnRtvV_01.jsp";
            String phoneUserAgent	= "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36";

            Map<String, String> DrvCookies;
            Connection.Response res	 = null;
            res = Jsoup.connect(DrvLink)
                    .method(Connection.Method.GET)
                    .userAgent(phoneUserAgent)
                    .execute();
            DrvCookies = res.cookies();
            Document doc = res.parse();
            Elements contents = doc.select("input");
            Element test = contents.get(1);

            String oelnumber = test.attr("value");
            String	accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
            String	ae = "gzip, deflate, br";
            String	al = "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
            String	maxAge = "max-age=0";
            String	connection = "keep-alive";
            String	veraport20Use = "_vp_object_load=N";
            String	contentType = "application/x-www-form-urlencoded";
            String	host = "dls.koroad.or.kr";
            String	origin = "https://dls.koroad.or.kr";

            String	licenLocal = licenseItem.getCity();
            String 	name  = licenseItem.getName();
            String	sJumin = licenseItem.getJumin();
            String	licence01 = licenseItem.getCityCode();
            String	licence02 = licenseItem.getNum1();
            String	licence03 = licenseItem.getNum2();
            String	licence04 = licenseItem.getNum3();
            String	serialnum =  licenseItem.getSecure();

            res = Jsoup.connect(DrvAuthLink)
                    .method(Connection.Method.POST)
                    .header("Accept", accept)
                    .header("Accept-Encoding", ae)
                    .header("Accept-Language", al)
                    .header("Cache-Control", maxAge)
                    .header("Connection", connection)
                    .cookies(DrvCookies)
                    .header("Veraport20Use=N", veraport20Use)
                    .header("Host", host)
                    .header("Upgrade-Insecure-Requests", "1")		// 이건 1 고정
                    .userAgent(phoneUserAgent)
                    .header("Content-Type", contentType)
                    .header("Origin", origin)
                    .header("referrer", DrvAuthLink)
                    .referrer(DrvLink)								// 이전 링크가 referrer 주소
                    .data("oelnumber", oelnumber)					//
                    .data("sNameEncode", URLEncoder.encode(name,"UTF-8"))			//
                    .data("licenLocal",	 URLEncoder.encode(licenLocal, "UTF-8"))	//
                    .data("sName", name)							//
                    .data("sJumin1", sJumin)							//
                    .data("licence01", licence01)
                    .data("licence02", licence02)
                    .data("licence03", licence03)
                    .data("licence04", licence04)					//
                    .data("serialnum", serialnum)					//
                    .followRedirects(true)
                    .execute();

            doc = res.parse();
            contents = doc.select("li.point");
            if(contents.size()>0){
                errorMSG = contents.get(0).text();
                success = false;
            }else{
                contents = doc.select("strong.fwb");
                errorMSG = contents.get(0).text();
                success = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressDialog.dismiss();
        if(!success){
            AlarmDialog.getInstance().show(context,"면허증 진위여부 조회 실패",errorMSG);
        }else{
            if(DatabaseManager.getInstance().setApproved()){
                AlarmDialog.getInstance().show(context,"축하합니다",errorMSG+"\n드라이버로 등록되셨습니다");
                FragmentManager.getInstance().changeToDriverMain();
            }else{
                AlarmDialog.getInstance().show(context,"서버 에러",errorMSG+"\n서버와 통신 할 수 없습니다");
                FragmentManager.getInstance().changeToDriverMain();
            }
        }

    }
}
