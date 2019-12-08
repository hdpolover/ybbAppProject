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

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFollows extends RecyclerView.Adapter<AdapterFollows.MyHolder> {

    Context context;
    List<ModelUser> userList;

    String myUid;
    //contructor

    public AdapterFollows(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public AdapterFollows.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout row_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_profile_follows, viewGroup, false);

        return new AdapterFollows.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterFollows.MyHolder holder, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();

        //get data
        final String hisUid = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();

        //set data
        holder.upvoterNameTv.setText(userName);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_undraw_profile_pic)
                    .into(holder.uImageIv);
        } catch (Exception e) {

        }

        holder.uImageIv.setOnClickListener(new View.OnClickListener() {
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

        if (hisUid.equals(myUid)) {
            holder.followProfileBtn.setVisibility(View.GONE);
        } else {
            holder.followProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.followProfileBtn.getText().toString().equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(myUid)
                                .child("Followings").child(hisUid).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(hisUid)
                                .child("Followers").child(myUid).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(myUid)
                                .child("Followings").child(hisUid).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(hisUid)
                                .child("Followers").child(myUid).removeValue();
                    }
                }
            });
        }

        isFollowing(hisUid, holder.followProfileBtn);
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
        return userList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        ImageView uImageIv;
        TextView upvoterNameTv;
        Button followProfileBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uImageIv = itemView.findViewById(R.id.uImageIv);
            upvoterNameTv = itemView.findViewById(R.id.uNameTv);
            followProfileBtn = itemView.findViewById(R.id.followProfileBtn);
        }

    }
}
