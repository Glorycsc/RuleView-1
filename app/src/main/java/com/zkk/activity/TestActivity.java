package com.zkk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zkk.rulerview.R;

/**
 * Create by glorizz on 2019/2/19
 * Describe:
 */
public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final VerticalRulerView rulerView = (VerticalRulerView) findViewById(R.id.rulerView);
        final TextView showText = (TextView) findViewById(R.id.weight);

        rulerView.setOnValueChangeListener(new VerticalRulerView.OnValueChangeListener() {
            @Override
            public void onChange(float value) {
                showText.setText(value + "");
            }
        });
    }
}
