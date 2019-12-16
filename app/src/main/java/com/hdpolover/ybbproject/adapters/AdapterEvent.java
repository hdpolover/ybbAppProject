package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.models.ModelEvent;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.MyHolderEvent> {

    Context context;
    List<ModelEvent> eventList;

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

        // get data
        String uid = eventList.get(position).getUid();
        String eImage = eventList.get(position).geteImage();
        String eTitle = eventList.get(position).geteTitle();
        String eDate = eventList.get(position).geteDateFrom();
        String eTime = eventList.get(position).geteTimeFrom();
        String eSpeaker = eventList.get(position).geteSpeaker();
        String confirmStatus = eventList.get(position).geteStatus();
        String eLocation = eventList.get(position).geteLocation();
        String eCategory = eventList.get(position).geteCategory();

        //set data
        holder.myeventTit.setText(eTitle);
        holder.myeventDate.setText(eDate);
        holder.myeventTime.setText(eTime);
        holder.myeventLocation.setText(eLocation);
        holder.myeventSpeaker.setText(eSpeaker);
        holder.myeventCategory.setText(eCategory);
        holder.myeventStatus.setText(confirmStatus);

        //event Image

        if (eImage.equals("noImage")){
            //hide image view
            holder.myeventImg.setVisibility(View.GONE);
        }else {
            try {
                Picasso.get().load(eImage).into(holder.myeventImg);
            }
            catch (Exception e) {

            }
        }

        //handle button click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    //view holder class
    class MyHolderEvent extends RecyclerView.ViewHolder{

        ImageView myeventImg;
        TextView myeventTit, myeventDate, myeventTime, myeventLocation, myeventCategory, myeventSpeaker, myeventStatus;
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

        }
    }
}
