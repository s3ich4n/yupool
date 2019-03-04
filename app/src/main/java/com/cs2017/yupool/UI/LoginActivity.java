package com.cs2017.yupool.UI;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cs2017.yupool.AlarmDialog.AlarmDialog;
import com.cs2017.yupool.Login.DO.UserData;
import com.cs2017.yupool.Login.Interface.OnLoginFinishListener;
import com.cs2017.yupool.Login.LoginManager;
import com.cs2017.yupool.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText idET;
    private EditText pwET;
    private Button loginBT;
    private RadioGroup roleRadio;
    private CheckBox autoCheckBox;

    private SharedPreferences pref; // 시스템 내부 SharedPreference 조회를 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginManager.getInstance().initLoginManager(LoginActivity.this);

        initViews();
        checkAutoLogin();
    }

    // 로그인 결과를 반환받는 콜벡 메소드
    private OnLoginFinishListener loginResultCallback = new OnLoginFinishListener() {
        @Override
        public void onLoginFinish(boolean isSuccess, UserData userData, String error) {
            if(isSuccess){
                LoginManager.getInstance().setLoggedIn(true);
                LoginManager.getInstance().setUserData(userData);
                setResult(RESULT_OK);
                finish();
            }else{
                AlarmDialog.getInstance().show(LoginActivity.this,"로그인 실패",error);
            }
        }
    };

    // 뷰들을 초기화 함
    private void initViews(){
        idET = (EditText) findViewById(R.id.id);
        pwET = (EditText)findViewById(R.id.pw);
        roleRadio = (RadioGroup) findViewById(R.id.role);
        loginBT = (Button) findViewById(R.id.login);
        autoCheckBox = (CheckBox)findViewById(R.id.autoCheck);
        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idET.getText().length() ==0 || pwET.getText().length() ==0){
                    Toast.makeText(LoginActivity.this,"아이디와 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    boolean isDriver = false;
                    boolean auto = false;
                    if(roleRadio.getCheckedRadioButtonId() == R.id.driver) isDriver = true;
                    if(autoCheckBox.isChecked()) auto = true;

                    LoginManager.getInstance().login(idET.getText().toString(), pwET.getText().toString(),
                            auto,isDriver,loginResultCallback);
                }
            }
        });
    }


    private void checkAutoLogin(){
        pref = this.getSharedPreferences("pref",MODE_PRIVATE);

        /*
        if(pref.getString("ID","").length() !=0 || pref.getString("PW","").length() !=0){
            String id = pref.getString("ID","");
            String pw = pref.getString("PW","");
            idET.setText(id);
            pwET.setText(pw);
            autoCheckBox.setChecked(true);
            boolean isDriver = pref.getBoolean("ISDRIVER",true);
            LoginManager.getInstance().login(id,pw,true,isDriver,loginResultCallback);
        }*/
    }

    // 백 키가 작동하였을때 실행되는 메소드
    @Override
    public void onBackPressed() {
        // 백키가 불가능하도록 막음
    }
}
