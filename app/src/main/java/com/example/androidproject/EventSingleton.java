package com.example.androidproject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.EventLog;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.androidproject.MainActivity.PREFS_NAME;

public class EventSingleton {
    private static final EventSingleton eventInstance = new EventSingleton();
    private ArrayList<AddVice> viceEventList;

    private ArrayList<AddMovement> movementEventList;

    private DecimalFormat df = new DecimalFormat("0.00");
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols();

    private EventSingleton() {
        viceEventList = new ArrayList<>();
        movementEventList = new ArrayList<>();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

    }

    public static EventSingleton getEventInstance() {
        return eventInstance;
    }

    public void AddViceEvent(AddVice addVice) {
        viceEventList.add(addVice);
    }

    public void AddMovementEvent(AddMovement addMovement) {
        movementEventList.add(addMovement);
    }

    public ArrayList<AddVice> getViceEventList() {
        return viceEventList;
    }

    public AddVice getViceEvent(int index) {
        return viceEventList.get(index);
    }

    public ArrayList<AddMovement> getMovementEventList() {
        return movementEventList;
    }

    public AddMovement getMovementEvent(int index) {
        return movementEventList.get(index);
    }

    public void setViceEventList(ArrayList<AddVice> viceEventList) {
        this.viceEventList = viceEventList;
    }

    public void setMovementEventList(ArrayList<AddMovement> movementEventList) {
        this.movementEventList = movementEventList;
    }

    /**
     *
     * @param vice
     * @param timeframe
     * @return
     */
    public double getPrice(String vice, String timeframe) {
        double price = 0;
        int currentTime = 0;
        int eventTime = 0;
        Calendar eventCalendar = Calendar.getInstance();

        for (int i = 0; i < viceEventList.size(); i++) {
            eventCalendar.setTime(viceEventList.get(i).getCurrentTime());
            switch(timeframe) {
                case "Week":
                    currentTime = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                    eventTime = eventCalendar.get(Calendar.WEEK_OF_YEAR);
                    break;
                case "Month":
                    currentTime = Calendar.getInstance().get(Calendar.MONTH);
                    eventTime = eventCalendar.get(Calendar.MONTH);
                    break;
            }
            if (currentTime == eventTime) {
                switch(vice) {
                    case "Tobacco":
                        if (viceEventList.get(i) instanceof AddTobacco) {
                            price += ((AddTobacco) viceEventList.get(i)).getPrice();
                        }
                        break;
                    case "Alcohol":
                        if (viceEventList.get(i) instanceof AddAlcohol) {
                            price += ((AddAlcohol) viceEventList.get(i)).getPrice();
                        }
                        break;
                }
            }
        }
        Log.i("price: ", String.valueOf(price));
        return Double.parseDouble(df.format(price));
    }

    /**
     *
     * @param timeframe
     * @return
     */
    public String getTobaccoTime(String timeframe) {
        String spentTimeString = "Menetetty aika ";
        int spentTimeMinutes = 0;
        int spentTimeHours = 0;
        ArrayList<AddVice> tobaccoEvents = getSpecificViceEvents("Tobacco", timeframe);
        for (int i = 0; i < tobaccoEvents.size(); i++) {
            spentTimeMinutes += 5;
        }
        if (spentTimeMinutes >= 60) {
            spentTimeHours = spentTimeMinutes / 60;
            spentTimeMinutes = spentTimeMinutes - (spentTimeHours * 60);
            spentTimeString += spentTimeHours + " h " + spentTimeMinutes + " min";
        } else {
            spentTimeString += spentTimeMinutes + " min";
        }
        return spentTimeString;
    }

    /**
     *
     * @param timeframe
     * @return
     */
    public String getAlcoholConsumption(String timeframe) {
        SharedPreferences prefGet = MyApplication.getAppContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        String gender = prefGet.getString("GENDER_KEY", "");
        String dosesWeekly = "";
        String dosesMonthly = "";
        if (gender.equals("Male")) {
            dosesWeekly = "/14 annosta";
            dosesMonthly = "/56 annosta";
        }
        else if (gender.equals("Female")) {
            dosesWeekly = "/7 annosta";
            dosesMonthly = "/28 annosta";
        }
        String alcoholConsumptionString = "";
        ArrayList<AddVice> alcoholEvents = getSpecificViceEvents("Alcohol", timeframe);
        switch(timeframe) {
            case "Week":
                alcoholConsumptionString = alcoholEvents.size() + dosesWeekly;
                break;
            case "Month":
                alcoholConsumptionString = alcoholEvents.size() + dosesMonthly;
                break;
        }
        return alcoholConsumptionString;
    }

    /**
     *
     * @param vice
     * @param timeframe
     * @return
     */
    public ArrayList<AddVice> getSpecificViceEvents(String vice, String timeframe) {
        ArrayList<AddVice> viceEvents = new ArrayList<>();
        int currentTime = 0;
        int eventTime = 0;
        Calendar eventCalendar = Calendar.getInstance();

        for (int i = 0; i < viceEventList.size(); i++) {
            eventCalendar.setTime(viceEventList.get(i).getCurrentTime());
            switch(timeframe) {
                case "Week":
                    currentTime = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                    eventTime = eventCalendar.get(Calendar.WEEK_OF_YEAR);
                    break;
                case "Month":
                    currentTime = Calendar.getInstance().get(Calendar.MONTH);
                    eventTime = eventCalendar.get(Calendar.MONTH);
                    break;
            }
            if (currentTime == eventTime) {
                switch(vice) {
                    case "Tobacco":
                        if (viceEventList.get(i) instanceof AddTobacco) {
                            viceEvents.add(viceEventList.get(i));
                        }
                        break;
                    case "Alcohol":
                            if (viceEventList.get(i) instanceof AddAlcohol) {
                                viceEvents.add(viceEventList.get(i));
                            }
                        break;
                }
            }
        }
        return viceEvents;
    }

    /**
     *
     * @param vice
     * @return
     */
    public ArrayList<AddVice> getSpecificViceEvents(String vice) {
        ArrayList<AddVice> viceEvents = new ArrayList<>();

        for (int i = 0; i < viceEventList.size(); i++) {
            switch(vice) {
                case "Tobacco":
                    if (viceEventList.get(i) instanceof AddTobacco) {
                        viceEvents.add(viceEventList.get(i));
                    }
                    break;
                case "Alcohol":
                    if (viceEventList.get(i) instanceof AddAlcohol) {
                        viceEvents.add(viceEventList.get(i));
                    }
                    break;
            }
        }
        return viceEvents;
    }

    public int getDoses(String timeframe) {
        SharedPreferences prefGet = MyApplication.getAppContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        String gender = prefGet.getString("GENDER_KEY", "Male");
        int weeklyDoses = 0;
        int monthlyDoses = 0;
        switch (gender) {
            case "Male":
                weeklyDoses = 14;
                monthlyDoses = 56;
                break;
            case "Female":
                weeklyDoses = 7;
                monthlyDoses = 28;
                break;
        }
        if (timeframe == "Week") {
            return weeklyDoses;
        } else {
            return monthlyDoses;
        }
    }
}
