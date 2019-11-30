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

public class AdapterPostUpvoter extends RecyclerView.Adapter<AdapterPostUpvoter.MyHolder> {

    Context context;
    List<ModelUser> upvoterList;

    String myUid;
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
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();

        //get data
        final String hisUid = upvoterList.get(position).getUid();
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

        holder.upvoterAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("myUid", hisUid);
                context.startActivity(intent);
            }
        });

        if (hisUid.equals(myUid)) {
            holder.followUpvoterBtn.setVisibility(View.GONE);
        } else {
            holder.followUpvoterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.followUpvoterBtn.getText().toString().equals("Follow")) {
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

        isFollowing(hisUid, holder.followUpvoterBtn);
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
