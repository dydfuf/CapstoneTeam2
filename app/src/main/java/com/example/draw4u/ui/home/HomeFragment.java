package com.example.draw4u.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.GridView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.draw4u.DiaryDayView;
import com.example.draw4u.DiaryInfo;
import com.example.draw4u.R;
import com.example.draw4u.ResultDiary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private GridView monthView;
    private TextView monthText;
    private TextView yearText;
    private TextView prevMonth;
    private TextView nextMonth;
    private MonthAdapter adt;
    private HomeViewModel homeViewModel;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<ResultDiary> diaryInfos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.activity_calendar, container, false);
        monthView = (GridView) root.findViewById(R.id.monthView);
        adt = new MonthAdapter(getActivity());
        monthView.setAdapter(adt);
        yearText = (TextView) root.findViewById(R.id.yearText);
        monthText = (TextView) root.findViewById(R.id.monthText);
        prevMonth = (TextView) root.findViewById(R.id.monthPrevious);
        nextMonth = (TextView) root.findViewById(R.id.monthNext);
        setMonthText();

        Button monthPrevious = null;
        monthPrevious = (Button) root.findViewById(R.id.monthPrevious);
        Button monthNext = null;
        monthNext = (Button) root.findViewById(R.id.monthNext);

        Switch mSwitch = null;
        mSwitch = (Switch) root.findViewById(R.id.switch2);
        mSwitch.setChecked(false);

        initDataset();

        Switch finalMSwitch1 = mSwitch;
        monthPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finalMSwitch1.setThumbResource(R.drawable.cal_icon);
                finalMSwitch1.setChecked(false);
                adt.setPreviousMonth();

                adt.notifyDataSetChanged();
                setMonthText();
                System.out.println("prev clicked : "+ String.valueOf(adt.getCurMonth()));
            }
        });

        monthNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finalMSwitch1.setThumbResource(R.drawable.cal_icon);
                finalMSwitch1.setChecked(false);
                adt.setNextMonth();
                adt.notifyDataSetChanged();
                setMonthText();
                System.out.println("next clicked : "+ String.valueOf(adt.getCurMonth()));
            }
        });

        Switch finalMSwitch = mSwitch;
        mSwitch.setThumbResource(R.drawable.cal_icon);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                initDataset();

                if(isChecked){
                    finalMSwitch.setThumbResource(R.drawable.img_icon);
                }
                else{
                    finalMSwitch.setThumbResource(R.drawable.cal_icon);
                }
                adt.setisChecked(isChecked);
                adt.setDiaryInfos(diaryInfos);
                adt.notifyDataSetChanged();
                //finalMSwitch.setThumbResource(R.drawable.cal_icon);

            }
        });

        return root;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public String monthToText(int month){
        String res = "";
        int temp = (month + 1)%12;
        switch(temp){
            case 1:
                res="jAn";
                break;
            case 2:
                res="feb";
                break;
            case 3:
                res="mAr";
                break;
            case 4:
                res="Apr";
                break;
            case 5:
                res="mAy";
                break;
            case 6:
                res="jUn";
                break;
            case 7:
                res="jUl";
                break;
            case 8:
                res="AUg";
                break;
            case 9:
                res="sep";
                break;
            case 10:
                res="oct";
                break;
            case 11:
                res="nov";
                break;
            case 0:
                res="dec";
                break;
        }
        return res;
    }

    public void setMonthText(){
        int curYear = adt.getCurYear();
        int curMonth = adt.getCurMonth();
        int prev = curMonth - 1;
        int next = curMonth + 1;
        String year="";
        String month="";
        String month_prev="";
        String month_next="";
        switch(curYear){
            case 2010:
                year="@010";
                break;
            case 2011:
                year="@011";
                break;
            case 2012:
                year="@012";
                break;
            case 2013:
                year="@013";
                break;
            case 2014:
                year="@014";
                break;
            case 2015:
                year="@01%";
                break;
            case 2016:
                year="@016";
                break;
            case 2017:
                year="@017";
                break;
            case 2018:
                year="@018";
                break;
            case 2019:
                year="@019";
                break;
            case 2020:
                year="@0@0";
                break;
            case 2021:
                year="@0@1";
                break;
            case 2022:
                year="@0@@";
                break;
            case 2023:
                year="@0@3";
                break;
            case 2024:
                year="@024";
                break;
            case 2025:
                year="@0@5";
                break;
            case 2026:
                year="@0@6";
                break;
            case 2027:
                year="@0@7";
                break;
            case 2028:
                year="@0@8";
                break;
            case 2029:
                year="@0@9";
                break;
            case 2030:
                year="@030";
                break;
        }
        month = monthToText(curMonth);
        month_prev = "<   " + monthToText(prev);
        month_next = monthToText(next) + "   >";

        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"ab.otf");
        Typeface typeface2 = Typeface.createFromAsset(getResources().getAssets(),"kyobo.ttf");

        yearText.setTypeface(typeface);
        yearText.setTextColor(Color.BLACK);
        yearText.setText(year);
        yearText.setTextSize(30);

        monthText.setTypeface(typeface);
        monthText.setTextColor(Color.BLACK);
        monthText.setText(month);
        monthText.setTextSize(60);

        prevMonth.setTypeface(typeface2);
        prevMonth.setTextColor(Color.rgb(50,48,48));
        prevMonth.setText(month_prev);
        prevMonth.setTextSize(20);

        nextMonth.setTypeface(typeface2);
        nextMonth.setTextColor(Color.rgb(50,48,48));
        nextMonth.setText(month_next);
        nextMonth.setTextSize(20);

    }


    private void initDataset() {//맨 처음 data set 설정
        db.collection(mAuth.getUid())
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {//data 가져오기
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DiaryInfo diaryInfo = document.toObject(DiaryInfo.class);
                                ResultDiary resultDiary = new ResultDiary(diaryInfo);
                                diaryInfos.add(resultDiary);
                            }
                        } else {
                            Toast.makeText(getActivity(), "ERROR!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}