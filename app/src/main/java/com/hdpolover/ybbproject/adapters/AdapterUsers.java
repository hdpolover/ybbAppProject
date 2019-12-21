package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdpolover.ybbproject.ChatActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUser> userList;

    //contructor

    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout row_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String hisUID = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        final String userEmail = userList.get(position).getEmail();
        final String userPhone = userList.get(position).getPhone();

        //set data
        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);
        holder.mPhoneTv.setText(userPhone);
        try {
            Glide.with(context).load(userImage)
                    .placeholder(R.drawable.ic_undraw_profile_pic)
                    .into(holder.mAvatarIv);
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

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv, mPhoneTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            mPhoneTv = itemView.findViewById(R.id.phoneTv);
        }

    }
}
