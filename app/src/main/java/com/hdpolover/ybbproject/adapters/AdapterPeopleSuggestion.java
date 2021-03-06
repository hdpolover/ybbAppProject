package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.models.ModelUser;
import com.hdpolover.ybbproject.notifications.APIService;
import com.hdpolover.ybbproject.notifications.Client;
import com.hdpolover.ybbproject.notifications.Data;
import com.hdpolover.ybbproject.notifications.Response;
import com.hdpolover.ybbproject.notifications.Sender;
import com.hdpolover.ybbproject.notifications.Token;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class AdapterPeopleSuggestion extends RecyclerView.Adapter<AdapterPeopleSuggestion.MyHolder> {

    Context context;
    List<ModelUser> peopleList;

    String myUid, myName, publisherId;

    APIService apiService;
    ModelUser modelUser;

    public AdapterPeopleSuggestion(Context context, List<ModelUser> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_people_suggestion, viewGroup, false);

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();

        setCurrentUserName(myUid);

        //get data
        final String hisUid = peopleList.get(position).getUid();
        String name = peopleList.get(position).getName();
        String image = peopleList.get(position).getImage();
        String job = peopleList.get(position).getJob();

        int nameLength = name.length();
        if (nameLength > 10) {
            String shortName = name.substring(0, 10) + "...";
            //set the data
            holder.profileNameTv.setText(shortName);
        } else {
            //set the data
            holder.profileNameTv.setText(name);
        }

        int jobLength = job.length();
        if (jobLength > 10) {
            String shortJob = job.substring(0, 10) + "...";
            //set the data
            holder.userJobTv.setText(shortJob);
        } else {
            //set the data
            holder.userJobTv.setText(job);
        }

        //set user profile
        try {
            Glide.with(context).load(image)
                    .placeholder(R.drawable.ic_undraw_profile_pic)
                    .centerCrop()
                    .into(holder.profileImageIv);
        } catch (Exception e) {

        }

        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.followBtn.getText().toString().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(myUid)
                            .child("Followings").child(hisUid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(hisUid)
                            .child("Followers").child(myUid).setValue(true);

                    publisherId = hisUid;
                    addNotification(hisUid, "");
                    myName = modelUser.getName();
                    sendNotification(hisUid,  myName," started following you");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(myUid)
                            .child("Followings").child(hisUid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(hisUid)
                            .child("Followers").child(myUid).removeValue();
                }
            }
        });

        holder.profileImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid", hisUid);
                context.startActivity(intent);
            }
        });

        holder.profileNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid", hisUid);
                context.startActivity(intent);
            }
        });


        isFollowing(hisUid, holder.followBtn);
    }

    //for notification
    private void sendNotification(final String hisUid, final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data("2", "", myUid, name + "" + message, "New notification", hisUid, R.drawable.ic_notif);


                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String userid, String postid){
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", myUid);
        hashMap.put("publisherid", publisherId);
        hashMap.put("text", "started following you");
        hashMap.put("postid", postid);
        hashMap.put("timestamp", timeStamp);

        reference.push().setValue(hashMap);
    }

    private void setCurrentUserName(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelUser = dataSnapshot.getValue(ModelUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isFollowing(final String followerId, final Button followBtn) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follows").child(myUid).child("Followings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(followerId).exists()) {
                    followBtn.setText("Following");
                } else {
                    followBtn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {
        //view from row_people_suggestion.xml
        ImageView profileImageIv;
        TextView profileNameTv, userJobTv;
        MaterialButton followBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileImageIv = itemView.findViewById(R.id.profileImageIv);
            profileNameTv = itemView.findViewById(R.id.profileNameTv);
            userJobTv = itemView.findViewById(R.id.userJobTv);
            followBtn = itemView.findViewById(R.id.followBtn);
        }
    }
}
