
package com.hdpolover.ybbproject.adapters;

        import android.content.Context;
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

        import com.bumptech.glide.Glide;
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

public class AdapterPeople extends RecyclerView.Adapter<AdapterPeople.MyHolder> {

    Context context;
    List<ModelUser> peopleList;

    String myUid, myName, publisherId;
    ModelUser modelUser;

    APIService apiService;
    //contructor

    public AdapterPeople(Context context, List<ModelUser> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout row_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_people, viewGroup, false);

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
        String userImage = peopleList.get(position).getImage();
        String userName = peopleList.get(position).getName();
        String username = peopleList.get(position).getUsername();

        //set data
        holder.upvoterNameTv.setText(userName);
        holder.usernameTv.setText("@" + username);
        try {
            Glide.with(context).load(userImage)
                    .placeholder(R.drawable.ic_undraw_profile_pic)
                    .into(holder.upvoterAvatarIv);
        } catch (Exception e) {

        }

        holder.upvoterAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("uid", hisUid);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
            }
        });

        holder.upvoterNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("uid", hisUid);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

            }
        });

            holder.followUpvoterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.followUpvoterBtn.getText().toString().equals("Follow")) {
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

        isFollowing(hisUid, holder.followUpvoterBtn);
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

        ImageView upvoterAvatarIv;
        TextView upvoterNameTv, usernameTv;
        Button followUpvoterBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            upvoterAvatarIv = itemView.findViewById(R.id.upvoterAvatarIv);
            upvoterNameTv = itemView.findViewById(R.id.upvoterNameTv);
            followUpvoterBtn = itemView.findViewById(R.id.followUpvoterBtn);
            usernameTv = itemView.findViewById(R.id.usernameTv);
        }

    }
}
