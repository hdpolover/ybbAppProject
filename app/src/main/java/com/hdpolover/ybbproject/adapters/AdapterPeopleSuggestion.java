package com.hdpolover.ybbproject.adapters;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hdpolover.ybbproject.AddPostActivity;
import com.hdpolover.ybbproject.PostDetailActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.models.ModelPeopleSuggestion;
import com.hdpolover.ybbproject.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPeopleSuggestion extends RecyclerView.Adapter<AdapterPeopleSuggestion.MyHolder> {

    Context context;
    List<ModelPeopleSuggestion> peopleList;

    private MyHolder holder;

    public  AdapterPeopleSuggestion(Context context, List<ModelPeopleSuggestion> peopleList) {
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

    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {
        //view from row_people_suggestion.xml
        ImageView profileImageIv;
        TextView profileNameTv;
        Button followBtn;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileImageIv = itemView.findViewById(R.id.profileImageIv);
            profileNameTv = itemView.findViewById(R.id.profileNameTv);
            followBtn = itemView.findViewById(R.id.followBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
