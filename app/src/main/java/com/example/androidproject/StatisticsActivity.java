package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        updateUI();
    }

    public ArrayList<String> getAreaCount(List<String> timeframe) {
        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i < timeframe.size(); i++) {
            label.add(timeframe.get(i));
        }
        return label;
    }

    private void updateUI() {
        RadioGroup viceRadioGroup = findViewById(R.id.radioGroupVices);
        RadioButton chosenVice = findViewById(viceRadioGroup.getCheckedRadioButtonId());
        RadioGroup timeframeRadioGroup = findViewById(R.id.radioGroupTimeframe);
        RadioButton chosenTimeframe = findViewById(timeframeRadioGroup.getCheckedRadioButtonId());

        ArrayList<AddVice> addTobaccos = EventSingleton.getEventInstance().getSpecificViceEvents("Tobacco");
        ArrayList<AddVice> addAlcohols = EventSingleton.getEventInstance().getSpecificViceEvents("Alcohol");

        if (chosenVice == findViewById(R.id.radioAlcohol)) {
            if (chosenTimeframe == findViewById(R.id.radioWeekly)) {
                createChart(addAlcohols, "Week", "Alcohol");
            }
            if (chosenTimeframe == findViewById(R.id.radioYearly)) {
                createChart(addAlcohols, "Yearly", "Alcohol");
            }
        }
        if (chosenVice == findViewById(R.id.radioTobacco)) {
            if (chosenTimeframe == findViewById(R.id.radioWeekly)) {
                createChart(addTobaccos, "Week", "Tobacco");
            }
            if (chosenTimeframe == findViewById(R.id.radioYearly)) {
                createChart(addTobaccos, "Yearly", "Tobacco");
            }
        }
    }

    private void createChart(ArrayList<AddVice> viceArrayList, String timeframe, String type) {

        List<String> months = Arrays.asList("Tammi", "Helmi", "Maalis", "Huhti", "Touko", "Kesä", "Heinä", "Elo", "Syys", "Loka", "Marras", "Joulu");
        List<String> days = Arrays.asList("Ma", "Ti", "Ke", "To", "Pe", "La", "Su");

        List<BarEntry> entries = new ArrayList<>();
        int amount = 0;
        Calendar eventCalendar = Calendar.getInstance();
        switch(timeframe) {
            case "Week":
                amount = 7;
                for (int i = 0; i < 12; i++) {
                    for (int p = 0; i < amount; p++) {
                        int viceCount = 0;
                        for (AddVice addVice : viceArrayList) {
                            eventCalendar.setTime(addVice.getCurrentTime());
                            if (eventCalendar.get(Calendar.MONTH) == i && eventCalendar.get(Calendar.DAY_OF_WEEK) == p) {
                                viceCount++;
                            }
                        }
                        entries.add(new BarEntry(p, viceCount));
                    }
                }

                break;
            case "Year":
                amount = 12;
                for (int i = 0; i < amount; i++) {
                    int viceCount = 0;
                    for (AddVice addVice : viceArrayList) {
                        eventCalendar.setTime(addVice.getCurrentTime());
                        if (eventCalendar.get(Calendar.MONTH) == i) {
                            viceCount++;
                        }
                    }
                    entries.add(new BarEntry(i, viceCount));
                }
                break;
        }


        BarDataSet dataSet;
        switch(type) {
            case "Alcohol":
                dataSet = new BarDataSet(entries, "Annoksia juotu");
                break;
            case "Tobacco":
                dataSet = new BarDataSet(entries, "Tupakoita poltettu");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        dataSet.setValueFormatter(new DefaultValueFormatter(0));
        BarData barData = new BarData(dataSet);
        BarChart chart = findViewById(R.id.chart);
        chart.setData(barData);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        if (timeframe.equals("Week")) {
            xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount(days)));
        } else {
            xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount(months)));
        }

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setValueFormatter(new DefaultValueFormatter(0));
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        chart.invalidate();
    }

}