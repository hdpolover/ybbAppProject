package com.hdpolover.ybbproject.tabProfile;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentTab extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;

    RecyclerView postsRecyclerView;

    List<ModelPost> postList;
    AdapterPost adapterPost;
    String myUid, postId;

    LinearLayout noCommentsLayout;
    ShimmerFrameLayout shimmerFrameLayout;

    TextView noCommentTv;

    List<String> postIdList;

    public CommentTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_tab, container, false);

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        storageReference = getInstance().getReference(); //firebase storage refence
        postsRecyclerView = view.findViewById(R.id.recyclerview_posts);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        noCommentsLayout = view.findViewById(R.id.noCommentsLayout);
        noCommentTv = view.findViewById(R.id.noCommentTv);

        noCommentTv.setText("You have never commented on any posts");

        noCommentsLayout.setVisibility(View.GONE);

        checkUserStatus();

        postList = new ArrayList<>();
        postIdList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //show newest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);
        //adapter
        adapterPost = new AdapterPost(getContext(), postList);
        //set this adapter to recyclerview
        postsRecyclerView.setAdapter(adapterPost);

        loadMyPostsComment();

        return view;
    }

    private void loadMyPostsComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postIdList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1: ds.getChildren()) {
                        String uid = ""+ds1.child("uid").getValue().toString();
                        if (uid.equals(myUid)) {
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
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
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
                        noCommentsLayout.setVisibility(View.VISIBLE);
                    } else {
                        noCommentsLayout.setVisibility(View.GONE);
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

            }
        });
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            myUid = user.getUid();
        }
    }

}
