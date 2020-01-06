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
import com.hdpolover.ybbproject.adapters.AdapterEvent;
import com.hdpolover.ybbproject.adapters.AdapterPost;
import com.hdpolover.ybbproject.models.ModelPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class hisUpvotesTab extends Fragment {
    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;

    RecyclerView postsRecyclerView;

    List<ModelPost> postList;
    ArrayList<String> postIdList;
    AdapterPost adapterPost;
    String hisUid;

    LinearLayout noUpvotesLayout;
    ShimmerFrameLayout shimmerFrameLayoutUpvotes;

    public hisUpvotesTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upvotes_tab, container, false);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        storageReference = getInstance().getReference(); //firebase storage refence
        postsRecyclerView = view.findViewById(R.id.upvotesRecyclerView);
        noUpvotesLayout = view.findViewById(R.id.noUpvotesLayout);
        shimmerFrameLayoutUpvotes = view.findViewById(R.id.shimmerFrameLayoutUpvotes);

        noUpvotesLayout.setVisibility(View.GONE);

        //get myUid of clicked user
        Intent intent = getActivity().getIntent();
        hisUid = intent.getStringExtra("uid");

        postIdList = new ArrayList<>();
        postList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        loadMyPostsComment();

        return view;
    }

    private void loadMyPostsComment() {
        //init post list
        DatabaseReference ref = firebaseDatabase.getInstance().getReference("PostUpvotes");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postIdList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        String uid = "" + ds1.getKey();
                        if (uid.equals(hisUid)) {
                            postIdList.add(ds.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    boolean isPost = false;
                    for (String id : postIdList) {
                        if (modelPost.getpId().equals(id)) {
                            isPost = true;
                        }
                    }
                    if (isPost) {
                        postList.add(modelPost);
                    }

                    //adapter
                    adapterPost = new AdapterPost(getActivity(), postList);

                    if (postList.size() == 0) {
                        noUpvotesLayout.setVisibility(View.VISIBLE);
                    } else {
                        noUpvotesLayout.setVisibility(View.GONE);
                        //set adapter to recycle
                        postsRecyclerView.setAdapter(adapterPost);
                        Collections.reverse(postList);
                        adapterPost.notifyDataSetChanged();
                    }

                    shimmerFrameLayoutUpvotes.stopShimmer();
                    shimmerFrameLayoutUpvotes.setVisibility(View.GONE);
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
        shimmerFrameLayoutUpvotes.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayoutUpvotes.stopShimmer();
    }
}
