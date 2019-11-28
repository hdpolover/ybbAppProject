package com.hdpolover.ybbproject.adapters;

import android.content.Context;
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
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.models.ModelPeopleSuggestion;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPeopleSuggestion extends RecyclerView.Adapter<AdapterPeopleSuggestion.MyHolder> {

    Context context;
    List<ModelPeopleSuggestion> peopleList;

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
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String uid = peopleList.get(position).getUid();
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
                Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
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
