package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.PostDetailActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.helpers.SocialTimeConverter;
import com.hdpolover.ybbproject.models.ModelNotification;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.MyHolder> {

    private Context mContext;
    private List<ModelNotification> mNotification;

    boolean isExist;

    SocialTimeConverter stc;

    public AdapterNotifications(Context context, List<ModelNotification> notification){
        mContext = context;
        mNotification = notification;
    }

    @NonNull
    @Override
    public AdapterNotifications.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_notification, parent, false);
        return new AdapterNotifications.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        stc = new SocialTimeConverter();

        final ModelNotification notification = mNotification.get(position);

        final String userId = notification.getUserid();
        final  String publisherId = notification.getPublisherid();
        final String postId = notification.getPostid();
        String notifContent = notification.getText();
        String notifTime = notification.getTimestamp();

        getUserData(holder.notifUserIv, holder.notifContentTv, notifContent, userId);

        holder.notifTimeTv.setText(stc.getSocialTimeFormat(notifTime));
        holder.notifMoreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.notifMoreBtn, postId, publisherId);
            }
        });

        holder.notifUserIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("uid", userId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (postId.isEmpty()) {
                        Intent intent = new Intent(mContext, UserProfileActivity.class);
                        intent.putExtra("uid", userId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, PostDetailActivity.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("uid", publisherId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                 Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkPostExist(final String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()) {
                    isExist =  true;
                } else {
                    isExist = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageView moreBtn, final String postId, final String uid) {
        PopupMenu popupMenu = new PopupMenu(mContext, moreBtn, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    deleteNotif(postId, uid);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteNotif(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView notifUserIv, notifMoreBtn;
        public TextView notifContentTv, notifTimeTv;

        public MyHolder(View itemView) {
            super(itemView);

            notifUserIv = itemView.findViewById(R.id.notifUserIv);
            notifContentTv = itemView.findViewById(R.id.notifContentTv);
            notifTimeTv = itemView.findViewById(R.id.notifTimeTv);
            notifMoreBtn = itemView.findViewById(R.id.notifMoreBtn);
        }
    }

    private void getUserData(final ImageView userImage, final TextView notifContentTv, final String notifContent, String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                //get user data
                String image = user.getImage();
                try {
                    Glide.with(mContext).load(image)
                            .placeholder(R.drawable.ic_undraw_profile_pic)
                            .centerCrop()
                            .into(userImage);
                } catch (Exception e) {

                }

                String username = user.getName();
                if (username.length() > 15) {
                    notifContentTv.setText(username.substring(0, 16) + "... " + notifContent);
                } else {
                    notifContentTv.setText(username + " " + notifContent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}