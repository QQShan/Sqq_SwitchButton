package com.example.sqq.sqq_switchbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sqq.sqqswitchbutton.SqqSwitchButton;

public class MainActivity extends AppCompatActivity {
    SqqSwitchButton tB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tB = (SqqSwitchButton) findViewById(R.id.sdf);
        tB.setToggleOn(false);
        tB.setOnToggleChanged(new SqqSwitchButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                Log.d("sqq", "" + (on ? "打开" : "关闭"));
            }
        });
    }
}
