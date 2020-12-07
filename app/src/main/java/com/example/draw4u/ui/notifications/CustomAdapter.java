package com.example.draw4u.ui.notifications;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.draw4u.DiaryDayView;
import com.example.draw4u.R;
import com.example.draw4u.SelectImage;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Dictionary> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected Button id;

        public CustomViewHolder(View view) {
            super(view);
            this.id = (Button) view.findViewById(R.id.id_listitem);
        }
    }


    public CustomAdapter(ArrayList<Dictionary> list) {
        this.mList = list;
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, final int position) {

        viewholder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        viewholder.id.setGravity(Gravity.CENTER);

        viewholder.id.setText(mList.get(position).getKeyword());
        viewholder.id.setTextSize(13);

        viewholder.id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(position);
                System.out.println(mList.get(position).getKeyword());
                Intent intent = new Intent(v.getContext(), SelectImage.class);
                intent.putExtra("fname","noti");
                intent.putExtra("keyword", mList.get(position).getKeyword());
                startActivity(v.getContext(),intent,null);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}