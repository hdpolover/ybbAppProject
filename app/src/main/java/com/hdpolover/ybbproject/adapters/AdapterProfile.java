package com.hdpolover.ybbproject.adapters;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.hdpolover.ybbproject.models.ModelPost;
import com.hdpolover.ybbproject.tabProfile.EventsTab;
import com.hdpolover.ybbproject.tabProfile.UpvotesTab;
import com.hdpolover.ybbproject.tabProfile.DashboardTab;
import com.hdpolover.ybbproject.tabProfile.PostTab;
import com.hdpolover.ybbproject.tabProfile.CommentTab;

import java.util.ArrayList;
import java.util.List;

public class AdapterProfile extends FragmentPagerAdapter {

    FirebaseDatabase firebaseDatabase;

    List<ModelPost> postList = new ArrayList<>();
    String uid;

    public AdapterProfile(@NonNull FragmentManager fm) {
        super(fm);

        setPostCount();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DashboardTab();
            case 1:
                return new PostTab();
            case 2:
                return new CommentTab();
            case 3:
                return new UpvotesTab();
            case 4:
                return new EventsTab();
            default:
                return null;
        }
    }

    public int setPostCount() {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            uid = user.getUid();

            //init post list
            DatabaseReference ref = firebaseDatabase.getInstance().getReference("Posts");
            //query to load posts
            Query query = ref.orderByChild("uid").equalTo(uid);
            //get all data
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //postList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelPost myPosts = ds.getValue(ModelPost.class);

                        postList.add(myPosts);
                     }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return postList.size();
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Dashboard";
            case 1:
                return " My Posts ";
            case 2:
                return " 0 Comments ";
            case 3:
                return " 0 Upvotes ";
            case 4:
                return " 0 Events ";
            default:
                return null;
        }
    }
}
