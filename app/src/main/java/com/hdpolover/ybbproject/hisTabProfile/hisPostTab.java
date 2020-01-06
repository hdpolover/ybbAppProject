package com.hdpolover.ybbproject.hisTabProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.models.ModelPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class hisPostTab extends Fragment {
    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;

    RecyclerView postsRecyclerView;

    List<ModelPost> postList;
    AdapterPost adapterPost;
    String hisUid;

    LinearLayout noPostsLayout;
    ShimmerFrameLayout shimmerFrameLayout;

    public hisPostTab() {
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
        storageReference = getInstance().getReference(); //firebase storage refence
        postsRecyclerView = view.findViewById(R.id.recyclerview_posts);
        noPostsLayout = view.findViewById(R.id.noPostsLayout);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);

        noPostsLayout.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //get myUid of clicked user
        Intent intent = getActivity().getIntent();
        hisUid = intent.getStringExtra("uid");

        postList = new ArrayList<>();

        //adapter
        adapterPost = new AdapterPost(getActivity(), postList);
        postsRecyclerView.setAdapter(adapterPost);

        loadMyPostsComment();

        return view;
    }

    private void loadMyPostsComment() {
        //init post list
        DatabaseReference ref = firebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(hisUid);
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
                    adapterPost = new AdapterPost(getActivity(), postList);

                    if (postList.size() == 0) {
                        noPostsLayout.setVisibility(View.VISIBLE);
                    } else {
                        noPostsLayout.setVisibility(View.GONE);
                        //set adapter to recycle
                        postsRecyclerView.setAdapter(adapterPost);
                        Collections.reverse(postList);
                        adapterPost.notifyDataSetChanged();
                    }

                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }
}
