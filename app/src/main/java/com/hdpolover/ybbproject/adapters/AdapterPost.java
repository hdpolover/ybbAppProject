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
import com.google.firebase.auth.FirebaseUser;
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
import com.hdpolover.ybbproject.ProfileFragment;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost> postList;

    String myUid;

    FirebaseUser firebaseUser;

    public  AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public AdapterPost.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final ModelPost post = postList.get(position);

        //get data
        final String uid = post.getUid();
        final String pId = post.getpId();
        final String pImage = postList.get(position).getpImage();

        //convert timestamp to dd/mm/yyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(post.getpTime()));
        String pTime = DateFormat.format("dd/MM/yyy hh:mm aa", calendar).toString();

        //set data
        myHolder.uNameTv.setText(post.getuName());
        myHolder.pTimeTv.setText(pTime);
        myHolder.pDescTv.setText(post.getpDesc());
        //myHolder.pUpvotesTv.setText(pUpvotes + " upvotes");
        //myHolder.pCommentsTv.setText(pComments + " comments");
        //set upvotes for each post
        //setUpvotes(myHolder, pId);

        //set user dp
        try {
            Picasso.get().load(post.getuDp()).placeholder(R.drawable.ic_undraw_profile_pic).fit().centerInside().into(myHolder.uPictureIv);
//            Picasso.get().load(uDp)
//                    .placeholder(R.drawable.ic_undraw_profile_pic)
//                    .centerInside()
//                    .into(myHolder.uPictureIv);
        } catch (Exception e) {

        }

        //set post image
        //if no image then hide the imageview
        if (pImage.equals("noImage")) {
            //hide imageview
            myHolder.pImageIv.setVisibility(View.GONE);
        } else {
            //hide imageview
            myHolder.pImageIv.setVisibility(View.VISIBLE);

            try {
                Picasso.get().load(post.getpImage())
                        .into(myHolder.pImageIv);
            } catch (Exception e) {

            }
        }


        //publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());
        isUpvoted(post.getpId(), myHolder.upvoteIv);
        setCountText(myHolder.upvoteTv, myHolder.commentTv, post.getpId());
        //getCommetns(post.getPostid(), holder.comments);

        myHolder.upvoteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myHolder.upvoteIv.getTag().equals("upvote")) {
                    FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(post.getpId())
                            //.child("Upvotes")
                            .child(firebaseUser.getUid()).setValue(true);
                    //addNotification(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(post.getpId())
                            //.child("Upvotes")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        myHolder.commentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //start postdetailactivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

        //handle button clicks
        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(myHolder.moreBtn, uid, myUid, pId, pImage);
            }
        });
//        myHolder.upvoteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //get total number of upvotes for the post, whose like buton is clicked
//                //if currently signed in user has not liked it before
//                //increase value by 1, otherwose decrease value by 1
//                final int pUpvotes = Integer.parseInt(postList.get(position).getpUpvotes());
//                mProcessUpvote = true;
//                //get id of the post clicked
//                final String postIde = postList.get(position).getpId();
//                upvotesRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (mProcessUpvote) {
//                            if (dataSnapshot.child(postIde).hasChild(myUid)) {
//                                //already upvoted, so remove upvote
//                                postsRef.child(postIde).child("pUpvotes").setValue(""+(pUpvotes-1));
//                                upvotesRef.child(postIde).child(myUid).removeValue();
//                                mProcessUpvote = false;
//                            }
//                             else {
//                                 //not upvotes, upvte it
//                                postsRef.child(postIde).child("pUpvotes").setValue(""+(pUpvotes+1));
//                                upvotesRef.child(postIde).child(myUid).setValue("Upvotes");
//                                mProcessUpvote = false;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               //start postdetailactivity
//                Intent intent = new Intent(context, PostDetailActivity.class);
//                intent.putExtra("postId", pId);
//                context.startActivity(intent);
//            }
//        });
        myHolder.pImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start postdetailactivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });
        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will be used to go to userprofileactivity
                //with uid to show user's posts
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });
    }

    private void setCountText(final TextView upvotes, final TextView comments, String postId){
        DatabaseReference upvotesRef = FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(postId);
        upvotesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    upvotes.setText("Upvote");
                } else {
                    upvotes.setText(dataSnapshot.getChildrenCount() + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).child("Comments");
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    comments.setText("Comment");
                } else {
                    comments.setText(dataSnapshot.getChildrenCount() + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isUpvoted(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("PostUpvotes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_upvote_filled);
                    imageView.setTag("upvoted");
                } else{
                    imageView.setImageResource(R.drawable.ic_upvote);
                    imageView.setTag("upvote");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void setUpvotes(final MyHolder holder, final String postKey) {
//        upvotesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(postKey).hasChild(myUid)) {
//                    //user has liked this post
////                    to indicate that the post is liked by this signedin user
////                            change drawable left icon of upvote button
////                            change text of upvote button from upvote to upvoted
//                    holder.upvoteBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_filled, 0, 0, 0);
//                    holder.upvoteBtn.setText("Upvoted");
//                } else {
//                    //user has not liked this post
//                    //to indicate that the post is not liked by this signedin user
//                    //change drawable left icon of upvote button
//                    //change text of upvote button from upvoted to upvote
//                    holder.upvoteBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote, 0, 0, 0);
//                    holder.upvoteBtn.setText("Upvotes");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageButton moreBtn, String uid, final String myUid, final String pId, final String pImage) {
        //creating popup menu currently having delete
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        //show delete option in only post of currently signedin user
        if (uid.equals(myUid)) {
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE, 2, 0, "View Detail");

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    //delete is clicked
                    beginDelete(pId, pImage);
                }
                else if (id == 1) {
                    //edit is clicked
                    //start addpostactivity with key "edit post" and the id of the post clicked
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                } else if  (id == 2){
                    //start postdetailactivity
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                }

                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        //post can be with or without image
        if (pImage.equals("noImage")) {
            //post is without image
            deleteWithoutImage(pId);
        } else {
            //post is with image
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithoutImage(String pId) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting post...");

        Query fQuery = FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("pId").equalTo(pId);
        fQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ds.getRef().removeValue(); //remove values from firebase where pid matches
                }
                //deleted
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteWithImage(final String pId, String pImage) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting post...");

        //steps: 1. delte image using uri 2. delete from database
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, now delete database
                        Query fQuery = FirebaseDatabase.getInstance().getReference("Posts")
                                .orderByChild("pId").equalTo(pId);
                        fQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                    ds.getRef().removeValue(); //remove values from firebase where pid matches
                                }
                                //deleted
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed, can;t go further
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {
        //view from row_post.xml
        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pDescTv;
        ImageButton moreBtn;
        //Button upvoteBtn, commentBtn;
        LinearLayout profileLayout;
        ImageView upvoteIv, commentIv;
        TextView upvoteTv, commentTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pDescTv = itemView.findViewById(R.id.pDescTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            //pUpvotesTv = itemView.findViewById(R.id.pUpvotesTv);
            //pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            //upvoteBtn = itemView.findViewById(R.id.upvoteBtn);
            //commentBtn = itemView.findViewById(R.id.commentBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);

            upvoteIv = itemView.findViewById(R.id.upvoteIv);
            commentIv = itemView.findViewById(R.id.commentIv);
            upvoteTv = itemView.findViewById(R.id.upvoteTv);
            commentTv = itemView.findViewById(R.id.commentTv);
        }
    }
}
