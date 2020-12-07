package com.example.draw4u.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import com.example.draw4u.DiaryDayView;
import com.example.draw4u.R;
import com.example.draw4u.ResultDiary;
import com.example.draw4u.SelectImage;
import com.google.android.gms.common.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import static androidx.core.content.ContextCompat.startActivity;
public class MonthItemView extends AppCompatButton {

    private MonthItem item;
    private Calendar calendar;

    ArrayList<ResultDiary> diaryInfos = new ArrayList<>();

    public void setDiaryInfos(ArrayList<ResultDiary> diaryInfos) {
        this.diaryInfos = diaryInfos;
    }

    public MonthItemView(Context context){
        super(context);
        init();
    }

    public MonthItemView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        setBackgroundColor(Color.WHITE);
    }

    public MonthItem getItem(){
        return item;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setItem(MonthItem item, int curMonth, int curYear, boolean isChecked){
        this.item = item;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar =  Calendar.getInstance();
        String today = sdf.format(calendar.getTime());

        int day = item.getDay();

        String month = "";
        if(curMonth < 9){
            month = "0"+String.valueOf(curMonth+1);
        }else{
            month = String.valueOf(curMonth+1);
        }

        setBackgroundColor(Color.argb(0,0,0,0));
        if(day != 0){
            String temp ="";
            switch(day){
                case 1:
                    temp = "1";
                    break;
                case 2:
                    temp = "@";
                    break;
                case 3:
                    temp = "3";
                    break;
                case 4:
                    temp = "4";
                    break;
                case 5:
                    temp = "%";
                    break;
                case 6:
                    temp = "6";
                    break;
                case 7:
                    temp = "7";
                    break;
                case 8:
                    temp = "8";
                    break;
                case 9:
                    temp = "9";
                    break;
                case 10:
                    temp = "10";
                    break;
                case 11:
                    temp = "11";
                    break;
                case 12:
                    temp = "1@";
                    break;
                case 13:
                    temp = "13";
                    break;
                case 14:
                    temp = "14";
                    break;
                case 15:
                    temp = "1%";
                    break;
                case 16:
                    temp = "16";
                    break;
                case 17:
                    temp = "17";
                    break;
                case 18:
                    temp = "18";
                    break;
                case 19:
                    temp = "19";
                    break;
                case 20:
                    temp = "@0";
                    break;
                case 21:
                    temp = "@1";
                    break;
                case 22:
                    temp = "@@";
                    break;
                case 23:
                    temp = "@3";
                    break;
                case 24:
                    temp = "@4";
                    break;
                case 25:
                    temp = "@%";
                    break;
                case 26:
                    temp = "@6";
                    break;
                case 27:
                    temp = "@7";
                    break;
                case 28:
                    temp = "@8";
                    break;
                case 29:
                    temp = "@9";
                    break;
                case 30:
                    temp = "30";
                    break;
                case 31:
                    temp = "31";
                    break;
            }
            setText(temp);
            setTextSize(25);
            setTextColor(Color.rgb(80,80,80));
            setForegroundGravity(Gravity.CENTER);
            Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"ab.otf");
            setTypeface(typeface);
            String date = "";
            if(day < 10){
                date = "" + String.valueOf(curYear) + month + "0" + String.valueOf(day);
            }else{
                date = "" + String.valueOf(curYear) + month + String.valueOf(day);
            }
            if(Integer.parseInt(date) <= Integer.parseInt(today)){

                String finalMonth = month;
                this.setOnClickListener(new OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {
                        String fname;
                        if(day < 10){
                            fname = "" + String.valueOf(curYear) + "-" + finalMonth + "" + "-0" + String.valueOf(day);
                        }else{
                            fname = "" + String.valueOf(curYear) + "-" + finalMonth + "" + "-" + String.valueOf(day);
                        }
                        Intent intent = new Intent(v.getContext(),DiaryDayView.class);
                        intent.putExtra("fname",fname);
                        startActivity(v.getContext(),intent,null);
                    }
                });
                if(isChecked){
                    String fname;
                    if(day < 10){
                        fname = "" + String.valueOf(curYear) + "-" + String.valueOf(curMonth+1) + "" + "-0" + String.valueOf(day);
                    }else{
                        fname = "" + String.valueOf(curYear) + "-" + String.valueOf(curMonth+1) + "" + "-" + String.valueOf(day);
                    }
                    String url = "";
                    for(int i = 0; i < diaryInfos.size(); i++){
                        if(diaryInfos.get(i).getResultdiary().getDate().equals(fname)){
                            url = diaryInfos.get(i).getResultdiary().getImageURL();
                        }
                    }

                    String finalUrl = url;
                    final Drawable[] drawable = new Drawable[1];
                    Thread t = new Thread(){
                        public void run(){
                            try {
                                drawable[0] = drawableFromUrl(finalUrl);
                                System.out.println("after drawable !!");
                                drawable[0].setBounds(0,0,130,150);
                                setCompoundDrawables(drawable[0],null,null,null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    t.start();

                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    setCompoundDrawables(null,null,null,null);
                }
            }
            else{
                this.setOnClickListener(new OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "현재 날짜 이후에 글을 쓸 수 없습니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }else{
            //setText("");
        }
    }

    private Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection =
                (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(getResources(),x);
    }
}
