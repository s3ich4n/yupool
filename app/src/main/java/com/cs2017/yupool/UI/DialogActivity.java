package com.cs2017.yupool.UI;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cs2017.yupool.R;
import com.cs2017.yupool.UI.MainActivity;

public class DialogActivity extends Activity {


    private TextView titleTV;
    private TextView messageTV;
    private Button button;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        String title = getIntent().getExtras().getString("title");
        String message = getIntent().getExtras().getString("msg");
        type = getIntent().getExtras().getString("type");
        if(type.equals("f"))
            setContentView(R.layout.activity_dialog2);
        else
            setContentView(R.layout.activity_dialog);

        titleTV = findViewById(R.id.title);
        messageTV = findViewById(R.id.message);

        titleTV.setText(title);
        messageTV.setText(message);

        button = findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                System.out.println("다이얼로그 타입 "+type);
                if(type.equals("d"))
                    intent.putExtra("type","d");
                else if(type.equals("g"))
                    intent.putExtra("type","g");
                else if(type.equals("f"))
                    intent.putExtra("type","f");
                startActivity(intent);
                finish();
            }
        });

    }
}
