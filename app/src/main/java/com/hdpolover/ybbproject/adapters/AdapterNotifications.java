package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.AddPostActivity;
import com.hdpolover.ybbproject.PostDetailActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.models.ModelNotification;
import com.hdpolover.ybbproject.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.MyHolder> {

    private Context mContext;
    private List<ModelNotification> mNotification;

    boolean isExist;

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

        final ModelNotification notification = mNotification.get(position);

        final String userId = notification.getUserid();
        final  String publisherId = notification.getPublisherid();
        final String postId = notification.getPostid();
        String notifContent = notification.getText();
        String notifTime = notification.getTimestamp();

        getUserData(holder.notifUserIv, holder.notifContentTv, notifContent, userId);

        //convert timestamp to dd/mm/yyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(notifTime));
        String convertedTime = DateFormat.format("dd/MM/yyy hh:mm aa", calendar).toString();

        String month = "";
        String date = convertedTime.substring(0, 2);
        String time = convertedTime.substring(10);

        String b = convertedTime.substring(3, 5);

        switch (b) {
            case "1":
                month = "Jan";
                break;
            case "2":
                month = "Feb";
                break;
            case "3":
                month = "Mar";
                break;
            case "4":
                month = "Apr";
                break;
            case "5":
                month = "May";
                break;
            case "6":
                month = "Jun";
                break;
            case "7":
                month = "Jul";
                break;
            case "8":
                month = "Aug";
                break;
            case "9":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
            default:
                break;
        }

        holder.notifTimeTv.setText(month + " " + date + " at " + time);
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
//                checkPostExist(postId);
//                Log.e("ee", isExist + "");
//                if (isExist) {
//                    Intent intent = new Intent(mContext, PostDetailActivity.class);
//                    intent.putExtra("postId", postId);
//                    intent.putExtra("uid", publisherId);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent);
//                } else {
//                    Toast.makeText(mContext, "Post doesn't exist anymore...", Toast.LENGTH_SHORT).show();
//                }
                try {
                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("postId", postId);
                    intent.putExtra("uid", publisherId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(mContext, "Post doesn't exist anymore...", Toast.LENGTH_SHORT).show();
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
                    Picasso.get().load(image)
                            .placeholder(R.drawable.ic_undraw_profile_pic)
                            .fit()
                            .centerCrop()
                            .into(userImage);
                } catch (Exception e) {

                }

                String username = user.getName();
                notifContentTv.setText(username + notifContent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}