package com.cs2017.yupool.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.cs2017.yupool.Login.DO.UserData;
import com.cs2017.yupool.Login.Interface.OnLoginFinishListener;
import com.cs2017.yupool.Login.Util.LoginUtil;
import com.cs2017.yupool.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

/**
 * Created by User on 2017-05-03.
 */

public class LoginTask extends AsyncTask<Boolean,Boolean,Boolean> {
    private static final String TAG = "LoginTask";


    private Map<String,String> lectureCookies; // 강의지원 쿠키
    private String p;  //
    private String errMSG; // 에러 메세지

    private String id; // 아이디
    private String pw; // 비밀번호
    private boolean auto; // 자동 로그인 체크시
    private SharedPreferences.Editor editor; // 로그인 정보 저장시 SharedPrefernce 에 값을 쓰기위함
    private UserData userData; // 사용자 정보 DO
    private Context context; // 어플리케이션 컨텍스트
    private ProgressDialog progressDialog; // 프로그레스 다이얼로그
    private OnLoginFinishListener onLoginFinishListener;

    // 생성자 : 아이디, 비밀번호, 자동로그인, 에디터, 컨텍스트
    public LoginTask(String id, String pw, boolean auto, SharedPreferences.Editor editor,boolean isDriver,
                     OnLoginFinishListener onLoginFinishListener, Context context){
        this.id = id;
        this.pw = pw;
        this.auto = auto;
        this.editor = editor;
        this.context = context;
        this.onLoginFinishListener = onLoginFinishListener;
        userData = new UserData();
        userData.setIsDriver(isDriver);
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("로그인 중");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Boolean... booleen) {
        if(pProcess() && lectureLoginProcess()&&initUserData())
            return true;
        return false;
    }

    private boolean pProcess(){
        Document doc;
        try {
            doc = Jsoup.connect("http://m.yu.ac.kr/go.php?type=lecture")
                    .userAgent(context.getString(R.string.user_agent))
                    .method(Connection.Method.GET)
                    .get();
            p = doc.select("input[name=p]").attr("value");
            if(p.length()<=0){
                errMSG = "p 값을 읽어오지 못했습니다";
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errMSG = e.getMessage();
            return false;
        }
        return true;
    }
    private boolean lectureLoginProcess(){
        Connection.Response res;
        try {
            res = Jsoup.connect("https://portal.yu.ac.kr/sso/mlogin_process.jsp")
                    .referrer("http://portal.yu.ac.kr/sso/mlogin.jsp?type=lecture&cReturn_Url=http%3A%2F%2Fcyber.yu.ac.kr%2Flecture%2F_sso%2Fm_sso.php")
                    //http://portal.yu.ac.kr/sso/mlogin.jsp?type=portal&cReturn_Url=http%3A%2F%2Fportal.yu.ac.kr
                    .userAgent(context.getString(R.string.user_agent))
                    .method(Connection.Method.POST)
                    .data("_enpass_login_","submit")
                    .data("username",id)
                    .data("cReturn_Url","http://cyber.yu.ac.kr/lecture/_sso/m_sso.php") // http://portal.yu.ac.kr
                    .data("type","lecture") // portal
                    .data("p",p)
                    .data("userId",id)
                    .data("password",pw)
                    .data("login_gb","0")
                    .ignoreHttpErrors(true)
                    .execute();
            errMSG = Jsoup.parse(res.body()).select("input[name=err_msg]").attr("value");
            if(errMSG.length()>0 || res.cookies().size()==0) return false;
        } catch (IOException e) {
            e.printStackTrace();
            errMSG = e.getMessage();
            return false;
        }
        lectureCookies = res.cookies();
        return true;
    }
    private boolean initUserData(){
        try {
            Document doc = Jsoup.connect("http://cyber.yu.ac.kr/mlecture/mypage.php")
                    .userAgent(context.getString(R.string.user_agent))
                    .method(Connection.Method.GET)
                    .cookies(lectureCookies)
                    .referrer("http://cyber.yu.ac.kr/mlecture/mypage.php")
                    .get();
            String url = doc.select("div.top_left a.name").attr("href");

            if(url.length()<10){
                errMSG = "사용자 초기화에 실패하였습니다";
                return false;
            }
            doc = Jsoup.connect("http://cyber.yu.ac.kr"+url)
                    .userAgent(context.getString(R.string.user_agent))
                    .method(Connection.Method.GET)
                    .cookies(lectureCookies)
                    .referrer("http://cyber.yu.ac.kr/mlecture/mypage.php")
                    .get();

            // 사용자 사진 읽어오기
            String imageURL = "";
            if(doc.select("div.info_photo img").attr("src").length()>1){
                userData.setImage(LoginUtil.getBase64Image("http://cyber.yu.ac.kr/mlecture"+doc.select("div.info_photo img").attr("src").substring(1),lectureCookies.toString()));
            }
            // 사진 밑 사용자 테이블로부터 읽어오기
            Elements el = doc.select("div.info_txt ul li");
            if(el.size()==0){
                errMSG = "사용자 초기화에 실패하였습니다";
                return false;
            }
            for (int i = 0; i < el.size(); i++) {
                String sentence =  el.get(i).select("li").text();
                if(i==0){
                    sentence = sentence.substring(sentence.indexOf(":")+1, sentence.length());
                    sentence = sentence.replaceAll("\\u00a0 ", "");

                    sentence = sentence.substring(1,sentence.indexOf('(')-1);
                    userData.setName(sentence);
                }else{
                    switch(i){
                        case 1: // 전공
                            break;
                        case 2: // 학년
                            break;
                        case 3: // 학적상태
                            break;
                        case 4: // 다음학적상태
                            break;
                        case 5: userData.setPhone(LoginUtil.splitData(sentence, 1));
                            break;
                        case 6: // 집전화
                            break;
                        case 7: // 메일
                            break;
                        default: break;
                    }
                }
            }
            userData.setStNum(id);

        } catch (Exception e) {
            e.printStackTrace();
            errMSG = "어플리케이션 오류, 개발자에게 문의해주세요";
            Log.e(TAG,"initUserData() 에러발생 \n " + e.getMessage());
            return false;
        }
        return true;
    }

    // 백그라운드 작업이 완료되고 호출되는 메소드
    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        try{
            progressDialog.dismiss();
        }catch (Exception e){

        }
        if(isSuccess) {
            if(auto){
                editor.putString("ID",id);
                editor.putString("PW",pw);
                editor.putBoolean("ISDRIVER",userData.isDriver());
                editor.commit();
            }
            Log.d(TAG, "로그인 성공");
            onLoginFinishListener.onLoginFinish(true,userData,"");
        }
        else{
            Log.d(TAG, "로그인 실패");
            onLoginFinishListener.onLoginFinish(false,null,errMSG);
        }
    }
}
