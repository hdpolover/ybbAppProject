package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hdpolover.ybbproject.EventDetailActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.models.ModelEvent;

import java.util.List;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.MyHolderEvent> {

    Context context;
    List<ModelEvent> eventList;
    String myUid;

    public AdapterEvent(Context context, List<ModelEvent> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public MyHolderEvent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate row layout row_event.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_event, parent, false );

        return new MyHolderEvent(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolderEvent holder, int position) {
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // get data
        final String uid = eventList.get(position).getUid();
        final String eId = eventList.get(position).getEId();
        String eImage = eventList.get(position).geteImage();
        String eTitle = eventList.get(position).geteTitle();
        String eDate = eventList.get(position).geteDateFrom();
        String eTime = eventList.get(position).geteTimeFrom();
        String eSpeaker = eventList.get(position).geteSpeaker();
        String eConfirmStatus = eventList.get(position).geteConfirmStatus();
        String eLocation = eventList.get(position).geteLocation();
        String eCategory = eventList.get(position).geteCategory();
        String eStatus = eventList.get(position).geteStatus();

        //set data
        holder.myeventTit.setText(eTitle);
        holder.myeventDate.setText(eDate);
        holder.myeventTime.setText(eTime);
        holder.myeventLocation.setText(eLocation);
        holder.myeventSpeaker.setText(eSpeaker);
        holder.myeventCategory.setText(eCategory);

        if (!uid.equals(myUid)) {
            holder.confirmStatusLayout.setVisibility(View.GONE);
        } else {
            holder.myeventStatus.setText(eConfirmStatus);
        }

        //event Image

            try {
                Glide.with(context).load(eImage).placeholder(R.drawable.placeholder_ybb_news).into(holder.myeventImg);
            }
            catch (Exception e) {

            }


        //handle button click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("eId", eId);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    //view holder class
    public static class MyHolderEvent extends RecyclerView.ViewHolder{

        public ImageView myeventImg;
        public TextView myeventTit;
        public TextView myeventDate;
        public TextView myeventTime;
        public TextView myeventLocation;
        public TextView myeventCategory;
        public TextView myeventSpeaker;
        TextView myeventStatus;
        LinearLayout confirmStatusLayout;

        //view from row_event.xml
        public MyHolderEvent(@NonNull View itemView) {
            super(itemView);

            //init view
            myeventImg = itemView.findViewById(R.id.myeventImg);
            myeventTit = itemView.findViewById(R.id.myeventTit);
            myeventDate = itemView.findViewById(R.id.myeventDate);
            myeventLocation = itemView.findViewById(R.id.myeventLocation);
            myeventCategory = itemView.findViewById(R.id.myeventCategory);
            myeventSpeaker = itemView.findViewById(R.id.myeventSpeaker);
            myeventTime = itemView.findViewById(R.id.myeventTime);
            myeventStatus = itemView.findViewById(R.id.myeventStatus);
            confirmStatusLayout = itemView.findViewById(R.id.confirmStatusLayout);

            //Event Detail


        }
    }
}
