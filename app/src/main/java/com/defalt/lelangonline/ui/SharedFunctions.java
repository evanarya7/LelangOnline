package com.defalt.lelangonline.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SharedFunctions {

    private static final String URL = "https://projectlab.co.id/";
    private static Retrofit retrofit = null;

    public static String formatRupiah(Double price) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        kursIndonesia.setMaximumFractionDigits(0);
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        return kursIndonesia.format(price);
    }

    public static Timestamp parseDate(String date) {
        Timestamp result = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(date);
            if (parsedDate != null) {
                result = new Timestamp(parsedDate.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    public static void disableSpinner(Spinner spinner) {
        spinner.setClickable(false);
        spinner.setFocusable(false);
        spinner.setEnabled(false);
    }

    public static void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }

    public static void enableSpinner(Spinner spinner) {
        spinner.setClickable(true);
        spinner.setFocusable(true);
        spinner.setEnabled(true);
    }

    public static CurrencyEditText setEditTextCurrency(CurrencyEditText currencyEditText) {
        currencyEditText.setCurrency(CurrencySymbols.INDONESIA);
        currencyEditText.setDelimiter(true);
        currencyEditText.setSpacing(true);
        currencyEditText.setDecimals(false);
        currencyEditText.setSeparator(".");
        return currencyEditText;
    }

    public static void showDatePicker(Activity mActivity, final EditText editText) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String thisDay = String.valueOf(dayOfMonth);
                        String thisMonth = String.valueOf(monthOfYear);

                        if (dayOfMonth < 10) {
                            thisDay = "0" + thisDay;
                        }
                        if (monthOfYear < 10) {
                            thisMonth = "0" + thisMonth;
                        }

                        String date = year + "-" + thisMonth + "-" + thisDay;
                        editText.setText(date);
                        editText.setCompoundDrawables(null, null, null, null);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public static void showTimePicker(Activity mActivity, final EditText editText) {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String thisHour = String.valueOf(hourOfDay);
                        String thisMinute = String.valueOf(minute);

                        if (hourOfDay < 10) {
                            thisHour = "0" + thisHour;
                        }
                        if (minute < 10) {
                            thisMinute = "0" + thisMinute;
                        }

                        String time = thisHour + ":" + thisMinute + ":00";
                        editText.setText(time);
                        editText.setCompoundDrawables(null, null, null, null);
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public static Retrofit getRetrofit(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .build();
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
