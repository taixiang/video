package com.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * @author tx
 * @date 2018/4/11
 */
public class LaunchActivity extends RxAppCompatActivity {

    private Button btnCommon;
    private Button btnSoftDrop;
    private Button btnHard;
    private Button btnHardDrop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        btnCommon = findViewById(R.id.btn_common);
        btnSoftDrop = findViewById(R.id.btn_soft_drop);
        btnHard = findViewById(R.id.btn_hard);
        btnHardDrop = findViewById(R.id.btn_hard_drop);

        btnCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actMain(LaunchActivity.this, 0);
            }
        });
        btnSoftDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actMain(LaunchActivity.this, 1);
            }
        });
        btnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actMain(LaunchActivity.this, 2);
            }
        });
        btnHardDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actMain(LaunchActivity.this, 3);
            }
        });
    }
}
