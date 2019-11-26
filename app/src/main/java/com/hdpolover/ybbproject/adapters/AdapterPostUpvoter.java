package com.hdpolover.ybbproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hdpolover.ybbproject.ChatActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.models.ModelPostUptover;
import com.hdpolover.ybbproject.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPostUpvoter extends RecyclerView.Adapter<AdapterPostUpvoter.MyHolder> {

    Context context;
    List<ModelUser> upvoterList;

    //contructor

    public AdapterPostUpvoter(Context context, List<ModelUser> upvoterList) {
        this.context = context;
        this.upvoterList = upvoterList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout row_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_post_upvoter_detail, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String hisUID = upvoterList.get(position).getUid();
        String userImage = upvoterList.get(position).getImage();
        String userName = upvoterList.get(position).getName();

        //set data
        holder.upvoterNameTv.setText(userName);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_undraw_profile_pic)
                    .into(holder.upvoterAvatarIv);
        } catch (Exception e) {

        }

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog
                //chat clicked
                //Click user from user list to start chatting/messaging
                //Start activity by putting UID of receiver
                //we will use that UID to identify the user we are gonna chat

//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("hisUid", hisUID);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);

                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.followUpvoterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Follow", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return upvoterList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        ImageView upvoterAvatarIv;
        TextView upvoterNameTv;
        Button followUpvoterBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            upvoterAvatarIv = itemView.findViewById(R.id.upvoterAvatarIv);
            upvoterNameTv = itemView.findViewById(R.id.upvoterNameTv);
            followUpvoterBtn = itemView.findViewById(R.id.followUpvoterBtn);
        }

    }
}
