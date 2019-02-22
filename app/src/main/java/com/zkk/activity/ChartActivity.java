package com.zkk.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.zkk.rulerview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by glorizz on 2019/2/21
 * Describe:
 */
public class ChartActivity extends AppCompatActivity {
    LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        lineChart = (LineChart) findViewById(R.id.chart);
        lineChart.fitScreen();

        lineChart.setDragEnabled(true); //是否支持拖动平移, 默认就是true
//        lineChart.setDragEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.YELLOW);
        xAxis.setDrawAxisLine(true);
        //设置x轴显示的坐标样式
        final String[] xName = new String[]{"6周", "7周", "8周", "9周", "10周", "11周", "12周", "13周"
                , "6周", "7周", "8周", "9周", "10周", "11周", "12周", "13周"
                , "6周", "7周", "8周", "9周", "10周", "11周", "12周", "13周"
                , "6周", "7周", "8周", "9周", "10周", "11周", "12周", "13周"
                , "6周", "7周", "8周", "9周", "10周", "11周", "12周", "13周"};
        IAxisValueFormatter xFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xName[(int) value];

            }
        };
        xAxis.setValueFormatter(xFormatter);
        xAxis.setGranularity(1f);

        List<Entry> vals = new ArrayList<Entry>();
        vals.add(new Entry(0f, 130.30f));
        vals.add(new Entry(1f, 130.30f));
        vals.add(new Entry(2f, 143.30f));
        vals.add(new Entry(3f, 170.30f));
        vals.add(new Entry(4f, 170.30f));
        vals.add(new Entry(5f, 170.30f));
        vals.add(new Entry(6f, 170.30f));
        vals.add(new Entry(7f, 170.30f));
        vals.add(new Entry(8f, 170.30f));
        vals.add(new Entry(9f, 170.30f));
        vals.add(new Entry(10f, 130.30f));
        vals.add(new Entry(11f, 130.30f));
        vals.add(new Entry(12f, 143.30f));
        vals.add(new Entry(13f, 170.30f));
        vals.add(new Entry(14f, 170.30f));
        vals.add(new Entry(15f, 170.30f));
        vals.add(new Entry(16f, 170.30f));
        vals.add(new Entry(17f, 170.30f));
        vals.add(new Entry(18f, 170.30f));
        vals.add(new Entry(19f, 170.30f));
        vals.add(new Entry(20f, 130.30f));
        vals.add(new Entry(21f, 130.30f));
        vals.add(new Entry(22f, 143.30f));
        vals.add(new Entry(23f, 170.30f));
        vals.add(new Entry(24f, 170.30f));
        vals.add(new Entry(25f, 170.30f));
        vals.add(new Entry(26f, 170.30f));
        vals.add(new Entry(27f, 170.30f));
        vals.add(new Entry(28f, 170.30f));
        vals.add(new Entry(29f, 170.30f));
        vals.add(new Entry(30f, 130.30f));
        vals.add(new Entry(31f, 130.30f));
        vals.add(new Entry(32f, 143.30f));
        vals.add(new Entry(33f, 170.30f));
        vals.add(new Entry(34f, 170.30f));

        LineDataSet set = new LineDataSet(vals, "");
        LineData lineData = new LineData(set);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
