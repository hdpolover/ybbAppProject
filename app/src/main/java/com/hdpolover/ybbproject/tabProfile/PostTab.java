package com.hdpolover.ybbproject.tabProfile;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.hdpolover.ybbproject.MainActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.models.ModelPost;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostTab extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    RecyclerView postsRecyclerView;

    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    TextView noDataTv;
    ImageView noDataIv;

    public PostTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_tab, container, false);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference(); //firebase storage refence
        postsRecyclerView = view.findViewById(R.id.recyclerview_posts);
        noDataIv = view.findViewById(R.id.noDataIv);
        noDataTv = view.findViewById(R.id.noDataTv);

        checkUserStatus();

        postList = new ArrayList<>();

        loadMyPosts();

        return view;

    }

    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref = firebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to list
                    postList.add(myPosts);

                    //adapter
                    adapterPost = new AdapterPost(getContext(), postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPost);
                }

                if (postList.size() > 0) {
                    noDataIv.setVisibility(View.GONE);
                    noDataTv.setVisibility(View.GONE);
                } else {
                    noDataIv.setVisibility(View.VISIBLE);
                    noDataTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            uid = user.getUid();
        }
    }
}
