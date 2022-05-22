package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class event_form extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    EditText title, desc, sdate, edate, loc;
    Button addEvent;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        title = findViewById(R.id.etName);
        desc = findViewById(R.id.etDesription);
        sdate = findViewById(R.id.etSdate);
        edate = findViewById(R.id.etEdate);
        loc = findViewById(R.id.etLocation);
        addEvent = findViewById(R.id.btnCreate);

        Intent intent = getIntent();
        String eventTitle = intent.getStringExtra("Title");
        String eventDate = intent.getStringExtra("sDate");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");
        String location = intent.getStringExtra("loc");

        String sdateTime = eventDate + " " + startTime;
        String edateTime = eventDate + " " + endTime;
        title.setText(eventTitle);
        sdate.setText(sdateTime);
        edate.setText(edateTime);
        loc.setText(location);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startDateTime = sdate.getText().toString();
                String[] sdatetime = startDateTime.split("\\s+");
                String stime = timeConvert(sdatetime[1]);

                String endDateTime = edate.getText().toString();
                String[] edatetime = endDateTime.split("\\s+");
                String etime = timeConvert(edatetime[1]);

                if(!title.getText().toString().isEmpty() && !sdate.getText().toString().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE, title.getText().toString());
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, desc.getText().toString());
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, loc.getText().toString());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getMiliseconds(sdatetime[0], stime));
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getMiliseconds(edatetime[0], etime));
                    if(!desc.getText().toString().isEmpty()){
                        intent.putExtra(Intent.EXTRA_EMAIL, desc.getText().toString());
                    }

                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "There is No app can Support this action", Toast.LENGTH_LONG).show();
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), "Please fill Name and Start Date", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    static long getMiliseconds(String date, String time){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            Date date1 = sdf.parse(date+" "+time);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar beginCal = Calendar.getInstance();

            beginCal.set(cal1.get(Calendar.YEAR),cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH), cal1.get(Calendar.HOUR_OF_DAY), cal1.get(Calendar.MINUTE));
            return beginCal.getTimeInMillis();
        }
        catch (Exception e) {
            return new Date().getTime();
        }
    }

    public void getStartTime(View view) {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("STDate - ",year + " " + month + " " + day);
        DatePickerDialog datePickerDialog = new DatePickerDialog(event_form.this, event_form.this, year, month,day);
        datePickerDialog.show();
    }

    public void getEndTime(View view) {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(event_form.this, event_form.this, year, month,day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month + 1;
        //Log.d("date - ",myYear + " " + myMonth + " " + myday);
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(event_form.this, event_form.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        if(sdate.hasFocus()){
            sdate.setText(myMonth+"/"+myday+"/"+myYear + " " + myHour+":"+myMinute);
            //Log.d("Sdate - ", myMonth+"/"+myday+"/"+myYear + " " + myHour+":"+myMinute);
        }
        if(edate.hasFocus()){
            edate.setText(myMonth+"/"+myday+"/"+myYear + " " + myHour+":"+myMinute);
            //Log.d("Edate - ", myMonth+"/"+myday+"/"+myYear + " " + myHour+":"+myMinute);
        }
    }

    public String timeConvert(String str){
        String output = "";
        try {
            String _24HourTime = str;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            output = _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

}