package com.alarm.model.sqlite;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

@Entity
public class Alarm  implements Parcelable {

    public static int NUM_DAYS_IN_WEEK=7;


    @PrimaryKey(autoGenerate = true)
    private   long id ;


    private long time;

    private int hour;
    private int minute;
    private int second;

    private String label;
    private String days;


    public Alarm() {
    }

    @Ignore
    public Alarm(long id, long time,int hour,int minute ,int second ,  String label, String days) {
        this.id = id;
        this.time = time;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.label = label;
        this.days = days;
     }


    protected Alarm(Parcel in) {
        id = in.readLong();
        time = in.readLong();
        hour = in.readInt();
        minute = in.readInt();
        second = in.readInt();
        label = in.readString();
        days = in.readString();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }



    public static ArrayList<Integer> getEmptyDaysArr()
    {
        //0 monday
        //1 tuesday
        //2 wednesday
        //3 thursday
        //4 friday
        //5 saturday
        //6 sunday
         ArrayList<Integer> json = new ArrayList<>();
        for (int i = 0; i <NUM_DAYS_IN_WEEK ; i++) {
            json.add(0);
        }
        return json ;
    }

    public static ArrayList<Integer> convertDaysToArr(String days)
    {
        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        Gson gson=new Gson();
        ArrayList<Integer> json = (gson.fromJson(days, listType));
         return json ;
    }

  public static String convertDaysToStr(ArrayList<Integer>days)
  {
      Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
      Gson gson=new Gson();
      String json = gson.toJson(days, listType);
      return json ;
   }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(time);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(second);
        dest.writeString(label);
        dest.writeString(days);
    }


    public static class AlarmTimeComparator implements Comparator<Alarm>
    {
        public int compare(Alarm left, Alarm right) {
            return Long.valueOf(left.time).compareTo(Long.valueOf(right.time));
        }
    }

    public Alarm addDayToAlarm( ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getTime());
        calendar.add(Calendar.DATE, 1);
        setTime(calendar.getTimeInMillis());
        return this ;
    }
    public Alarm(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getHour());
        calendar.set(Calendar.MINUTE, getMinute());
        calendar.set(Calendar.SECOND, getSecond());
        calendar.set(Calendar.MILLISECOND, 0);
        setTime(calendar.getTimeInMillis());
    }
    public void editAlarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.second = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getHour());
        calendar.set(Calendar.MINUTE, getMinute());
        calendar.set(Calendar.SECOND, getSecond());
        calendar.set(Calendar.MILLISECOND, 0);
        setTime(calendar.getTimeInMillis());
    }
}
