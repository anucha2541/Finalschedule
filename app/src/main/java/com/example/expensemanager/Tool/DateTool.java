package com.example.expensemanager.Tool;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateTool {

    public DateTool() {

    }

    public String getDateFromTm(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }
    public String getDateFromTm2(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }

    public long getTmFromDate(String str_date){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = null;
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public String getDateOnlyFromTm(String getd){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(Long.parseLong(getd)));
        return dateString;
    }

    public String getDateCurrent(int status){
        if(status==0){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            String dateNew = dateFormat.format(date);
            Date date2 = new Date(dateNew);
            return String.valueOf(date2.getTime());
        }else{
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            return dateFormat.format(date);
        }

    }
    public String getDateCurrent3(int status){
        if(status==0){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            String dateNew = dateFormat.format(date);
            Date date2 = new Date(dateNew);
            return String.valueOf(date2.getTime());
        }else{
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            return dateFormat.format(date);
        }

    }
    public String getDatePlus1Day(String dateGet){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(dateFormat.parse(dateGet));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, +1);
        return dateFormat.format(cal.getTime());
    }

    public String getDatePlusHour(String dateGet){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(dateFormat.parse(dateGet));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.HOUR, +2);
        return dateFormat.format(cal.getTime());
    }

    public String getDateCurrent2(int status){
        if(status==0){
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            String dateNew = dateFormat.format(date);
            Date date2 = new Date(dateNew);
            return String.valueOf(date2.getTime());
        }else{
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            return dateFormat.format(date);
        }

    }

    public ArrayList<String> getAllDateBetween(String str_date, String end_date){
        List<Date> dates = new ArrayList<Date>();

        DateFormat formatter;
        ArrayList<String> datelist = new ArrayList<>();
        formatter = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date startDate = (Date) formatter.parse(str_date);
            Date endDate = (Date) formatter.parse(end_date);

            long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
            long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
            long curTime = startDate.getTime();
            while (curTime <= endTime) {
                dates.add(new Date(curTime));
                curTime += interval;
            }
//            dates.add(new Date(endTime));
//            curTime += interval;
            for (int i = 0; i < dates.size(); i++) {
                Date lDate = (Date) dates.get(i);
                String ds = formatter.format(lDate);

                SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd");
                Date newDate = spf.parse(ds);
                spf = new SimpleDateFormat("dd/MM/yyyy");
                String newDateString = spf.format(newDate);

                datelist.add(newDateString);
                Log.d("DATEGET"," Date is ..." + newDateString);
            }
        } catch (Exception e) {

        }
        return datelist;
    }

    public static String formatdate(String fdate)
    {
        String datetime=null;
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat d= new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        }catch (ParseException e)
        {

        }
        return  datetime;


    }


    public String getDateNextDay(int status){
        if(status==0){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");// HH:mm:ss");
            String reg_date = df.format(c.getTime());
            c.add(Calendar.DATE, 1);
            String end_date = df.format(c.getTime());
//            Log.d("TESTNOW",end_date);
//            Date date = new Date();
//            String dateNew = dateFormat.format(date);
            Date date2 = new Date(String.valueOf(c.getTime()));
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Log.d("TESTNOW",String.valueOf(date2.getTime()));
            return String.valueOf(date2.getTime());
        }else{
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TimeZone thais= TimeZone.getTimeZone("Asia/Bangkok");
            dateFormat.setTimeZone(thais);
            Date date = new Date();
            return dateFormat.format(date);
        }

    }

}
