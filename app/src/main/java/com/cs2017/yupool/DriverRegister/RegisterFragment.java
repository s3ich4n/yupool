package com.cs2017.yupool.DriverRegister;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cs2017.yupool.Login.ImageCUploadTask;
import com.cs2017.yupool.Login.LoginManager;
import com.cs2017.yupool.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cs2017 on 2017-11-05.
 */

/*
    운전자 등록을 위한 프래그먼트
 */
public class RegisterFragment extends Fragment {

    public static final int REQ_CODE_SELECT_IMAGE = 777;
    private View rootView;

    private EditText num1;
    private EditText num2;
    private EditText num3;
    private EditText name;
    private EditText jumin;
    private EditText secure;
    private Spinner city;
    private Button registerBT;
    private Button imageBT;
    private ImageView imageView;
    private Bitmap image;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register,container,false); // 부모에 붙임
        initViews();
        return rootView;
    }


    private void initViews(){
        num1 = rootView.findViewById(R.id.num1);
        num2 = rootView.findViewById(R.id.num2);
        num3= rootView.findViewById(R.id.num3);
        name = rootView.findViewById(R.id.name);
        jumin = rootView.findViewById(R.id.jumin);
        secure = rootView.findViewById(R.id.secure);
        city = rootView.findViewById(R.id.city);
        registerBT = rootView.findViewById(R.id.registerBT);
        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(image==null){
                    Toast.makeText(rootView.getContext(),"차량 사진을 등록 해 주세요",Toast.LENGTH_SHORT).show();
                }else{
                    if(num1.getText().length()==2 && num2.getText().length()==6 && num3.getText().length()==2 && name.getText().length()>1 && jumin.getText().length() ==6 && secure.getText().length() ==6){
                        LicenseItem item = new LicenseItem();
                        item.setCity(city.getSelectedItem().toString());
                        item.setJumin(jumin.getText().toString());
                        item.setName(name.getText().toString());
                        item.setNum1(num1.getText().toString());
                        item.setNum2(num2.getText().toString());
                        item.setNum3(num3.getText().toString());
                        item.setSecure(secure.getText().toString());
                        LicenseAuthTask licenseAuthTask = new LicenseAuthTask(rootView.getContext(),item);
                        licenseAuthTask.execute();
                    }else{
                        Toast.makeText(rootView.getContext(),"제대로 입력해 주세요",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        imageBT = rootView.findViewById(R.id.imageBT);
        imageBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        imageView = rootView.findViewById(R.id.imageView);
    }

    public void setImage(Bitmap bitmap){
        this.image = bitmap;
        imageView.setImageBitmap(bitmap);
        ImageCUploadTask imageCUploadTask = new ImageCUploadTask(rootView.getContext(),image, LoginManager.getInstance().getUserData().getStNum());
        imageCUploadTask.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQ_CODE_SELECT_IMAGE && resultCode ==RESULT_OK){
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(), data.getData());
                setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
