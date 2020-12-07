package com.example.draw4u.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.draw4u.DrawingActivity;
import com.example.draw4u.Keyword;
import com.example.draw4u.LoginActivity;
import com.example.draw4u.MainActivity;
import com.example.draw4u.MySharedPreferences;
import com.example.draw4u.R;
import com.example.draw4u.SelectImage;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class NotificationsFragment extends Fragment {

    private  Button btn_logout;
    private Button btn_Drawing;

    private ArrayList<Dictionary> mArrayList;
    private CustomAdapter mAdapter;
    private int count = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        btn_logout = (Button)view.findViewById(R.id.btn_logout);
        btn_Drawing = (Button)view.findViewById(R.id.button4);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_main_list);


        GridLayoutManager mGridLayoutManager;

        int numberOfColumns = 5; // 한줄에 5개의 컬럼을 추가합니다.
        mGridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns);

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mArrayList = new ArrayList<>();

        mAdapter = new CustomAdapter( mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        Keyword keyword = new Keyword();
        ArrayList<String> mKeyword = keyword.getList();


//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mGridLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        for(int i = 0; i<mKeyword.size(); i++){
            count++;

            Dictionary data = new Dictionary(Integer.toString(count), mKeyword.get(i));
            mArrayList.add(data); // RecyclerView의 마지막 줄에 삽입

            mAdapter.notifyDataSetChanged();
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MySharedPreferences.INSTANCE.clearUser(getActivity());
                        Intent intent = new Intent(v.getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });

        btn_Drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DrawingActivity.class);
                intent.putExtra("fname","noti");
                startActivity(intent);
            }
        });
        return view;
    }

}