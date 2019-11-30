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
import com.hdpolover.ybbproject.models.ModelPeopleSuggestion;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPeopleSuggestion extends RecyclerView.Adapter<AdapterPeopleSuggestion.MyHolder> {

    Context context;
    List<ModelPeopleSuggestion> peopleList;

    String myUid;

    public AdapterPeopleSuggestion(Context context, List<ModelPeopleSuggestion> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_people_suggestion, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = firebaseUser.getUid();

        //get data
        final String hisUid = peopleList.get(position).getUid();
        String name = peopleList.get(position).getName();
        String image = peopleList.get(position).getImage();

        //set the data
        holder.profileNameTv.setText(name);

        //set user profile
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_undraw_profile_pic).into(holder.profileImageIv);
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

        isFollowing(hisUid, holder.followBtn);
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
        TextView profileNameTv, userDescTv;
        MaterialButton followBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileImageIv = itemView.findViewById(R.id.profileImageIv);
            profileNameTv = itemView.findViewById(R.id.profileNameTv);
            userDescTv = itemView.findViewById(R.id.userDescTv);
            followBtn = itemView.findViewById(R.id.followBtn);
        }
    }
}
