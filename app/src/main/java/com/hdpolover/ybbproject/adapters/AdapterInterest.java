package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.List;

public class AdapterInterest extends RecyclerView.Adapter<AdapterInterest.MyHolder> {

    private Context context;
    private List<String> interest;
    List<ModelUser> userList;

    public AdapterInterest(Context context, List<String> interest, List<ModelUser> userList) {
        this.context = context;
        this.interest = interest;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate row layout row_event.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_interest, parent, false );

        return new AdapterInterest.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String uid = userList.get(position).getUid();

        for (String interest : interest) {
            holder.interestTv.setText(interest);
        }
    }

    @Override
    public int getItemCount() {
        return interest.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        ImageView dotIv;
        TextView interestTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            dotIv = itemView.findViewById(R.id.dotIv);
            interestTv = itemView.findViewById(R.id.interestTv);
        }
    }
}
