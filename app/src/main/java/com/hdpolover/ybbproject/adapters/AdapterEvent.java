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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.EventDetailActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.helpers.SocialTimeConverter;
import com.hdpolover.ybbproject.models.ModelEvent;

import java.util.List;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.MyHolderEvent> {

    Context context;
    List<ModelEvent> eventList;
    String myUid;
    boolean isJoined;

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
        final String eId = eventList.get(position).geteId();
        String eImage = eventList.get(position).geteImage();
        String eTitle = eventList.get(position).geteTitle();
        String eDate = eventList.get(position).geteStart();
        String eSpeaker = eventList.get(position).geteSpeaker();
        String eConfirmStatus = eventList.get(position).geteConfirmStatus();
        String eLocation = eventList.get(position).geteLocation();
        String eCategory = eventList.get(position).geteCategory();

        //set data
        holder.eTitleTv.setText(eTitle);
        //get time and date
        SocialTimeConverter stc = new SocialTimeConverter();
        holder.eStartDateTv.setText(stc.getEventStartDate(Long.parseLong(eDate)));
        holder.eStartTimeTv.setText(stc.getEventStartTime(Long.parseLong(eDate)));

        holder.eLocationTv.setText(eLocation);
        holder.eSpeakerTv.setText(eSpeaker);
        holder.eCategoryTv.setText(eCategory);

        if (!uid.equals(myUid)) {
            holder.confirmStatusLayout.setVisibility(View.GONE);
        } else {
            holder.eStatusTv.setText(eConfirmStatus);
        }

        //event Image
            try {
                Glide.with(context).load(eImage).placeholder(R.drawable.placeholder_ybb_news).into(holder.eImageIv);
            }
            catch (Exception e) {

            }

        //handle button click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCurrentlyJoined(eId)) {
                    //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra("eId", eId);
                    intent.putExtra("uid", uid);
                    intent.putExtra("isJoined", "true");
                    context.startActivity(intent);
                } else {
                    //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra("eId", eId);
                    intent.putExtra("uid", uid);
                    intent.putExtra("isJoined", "false");
                    context.startActivity(intent);
                }
            }
        });
    }

    private boolean checkCurrentlyJoined(String e) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EventParticipants").child(e);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(myUid)) {
                        isJoined = true;
                    } else {
                        isJoined = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return isJoined;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    //view holder class
    public static class MyHolderEvent extends RecyclerView.ViewHolder{

        public ImageView eImageIv;
        public TextView eTitleTv, eStartDateTv, eStartTimeTv, eCategoryTv, eLocationTv, eSpeakerTv, eStatusTv;
        LinearLayout confirmStatusLayout;

        //view from row_event.xml
        public MyHolderEvent(@NonNull View itemView) {
            super(itemView);

            //init view
            eTitleTv = itemView.findViewById(R.id.eTitleTv);
            eImageIv = itemView.findViewById(R.id.eImageIv);
            eStartDateTv = itemView.findViewById(R.id.eStartDateTv);
            eStartTimeTv = itemView.findViewById(R.id.eStartTimeTv);
            eCategoryTv = itemView.findViewById(R.id.eCategoryTv);
            eLocationTv = itemView.findViewById(R.id.eLocationTv);
            eSpeakerTv = itemView.findViewById(R.id.eSpeakerTv);
            eStatusTv = itemView.findViewById(R.id.eStatusTv);
            confirmStatusLayout = itemView.findViewById(R.id.confirmStatusLayout);
        }
    }
}
