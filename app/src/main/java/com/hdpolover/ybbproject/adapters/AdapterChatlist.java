package com.hdpolover.ybbproject.adapters;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdpolover.ybbproject.ChatActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.models.ModelChat;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder> {

    Context context;
    List<ModelUser> userList; //get user info
    List<ModelChat> chatList;
    private HashMap<String, String> lastMessageMap;
    private HashMap<String, String> lastClockMap;

    //constructor
    public AdapterChatlist(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
        lastClockMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_chatlist.xml

        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        //get data
        final String hisUid = userList.get(position).getUid();
        //final String hisClock = String.valueOf(chatList.get(position).getTimestamp());
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);
        String lastClock = lastClockMap.get(hisUid);

        //set data
        holder.nameTv.setText(userName);
        if(lastMessage == null || lastMessage.equals("default")){
            holder.lastMessageTv.setVisibility(View.GONE);
            holder.lastClockTv.setVisibility(View.GONE);
        }
        else {
            //convert
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(lastClock));
            String time = DateFormat.format("hh:mm aa", cal).toString();

            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
            holder.lastClockTv.setVisibility(View.VISIBLE);
            holder.lastClockTv.setText(time);
        }

        try{
            Glide.with(context).load(userImage)
                    .fitCenter()
                    .placeholder(R.drawable.ic_undraw_profile_pic)
                    .into(holder.profileIv);
        } catch (Exception e){
        }


        //handle click of user in chatlist
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start chat activity
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put(userId, lastMessage);
    }

    public void setLastClockMap (String userId, String lastClock){
        lastClockMap.put(userId, lastClock);
    }

    @Override
    public int getItemCount() {
        return userList.size(); //size of the list
    }

    class MyHolder extends RecyclerView.ViewHolder {

        //views of row chatlist.xml
        ImageView profileIv;
        TextView nameTv, lastMessageTv, lastClockTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            lastClockTv = itemView.findViewById(R.id.lastClockTv);
        }
    }
}
