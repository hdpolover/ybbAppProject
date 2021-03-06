package com.hdpolover.ybbproject.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
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
import com.hdpolover.ybbproject.DashboardActivity;
import com.hdpolover.ybbproject.DashboardActivity.*;
import com.hdpolover.ybbproject.FeedbackActivity;
import com.hdpolover.ybbproject.PostDetailActivity;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.helpers.SocialTimeConverter;
import com.hdpolover.ybbproject.models.ModelPost;
import com.hdpolover.ybbproject.models.ModelUser;
import com.hdpolover.ybbproject.notifications.APIService;
import com.hdpolover.ybbproject.notifications.Client;
import com.hdpolover.ybbproject.notifications.Data;
import com.hdpolover.ybbproject.notifications.Response;
import com.hdpolover.ybbproject.notifications.Sender;
import com.hdpolover.ybbproject.notifications.Token;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost> postList;

    String myUid, myName, publisherId;

    APIService apiService;

    SocialTimeConverter stc;

    ModelUser modelUser;

    private long mLastClickTime = 0;

    public  AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public AdapterPost.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup, false);

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int position) {
        //get current user id
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setCurrentUserName(myUid);

        stc = new SocialTimeConverter();

        //get data
        final String hisUid = postList.get(position).getUid();
        final String pId = postList.get(position).getpId();
        final String pDesc = postList.get(position).getpDesc();
        final String pImage = postList.get(position).getpImage();
        final String pTime = postList.get(position).getpTime();

        myHolder.pTimeTv.setText(stc.getSocialTimeFormat(pTime));

        //this is to get the user data
        getUserData(myHolder.uPictureIv, myHolder.uNameTv, hisUid);

        if (pDesc.length() > 450) {
            //set data
            myHolder.pDescTv.setText(pDesc.substring(0, 450) + "...");
            myHolder.readmoreTv.setVisibility(View.VISIBLE);
        } else {
            //set data
            myHolder.pDescTv.setText(pDesc);
            myHolder.readmoreTv.setVisibility(View.GONE);
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
                Glide.with(context).load(pImage)
                        .fitCenter()
                        .into(myHolder.pImageIv);
            } catch (Exception e) {

            }
        }

        isUpvoted(pId, myHolder.upvoteIv);
        setCountText(myHolder.upvoteTv, myHolder.commentTv, pId);

        myHolder.upvoteLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myHolder.upvoteIv.getTag().equals("upvote")) {
                    FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(pId)
                            .child(myUid).setValue(true);

                    if (!hisUid.equals(myUid)) {
                        publisherId = hisUid;
                        addNotification(hisUid, pId);
                        myName = modelUser.getName();
                        sendNotification(hisUid,  myName," upvoted your post", pId);
                    }
                } else {
                    FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(pId)
                            .child(myUid).removeValue();
                }
            }
        });

        myHolder.commentLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start postdetailactivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                intent.putExtra("uid", hisUid);
                context.startActivity(intent);
            }
        });

        //handle button clicks
        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(myHolder.moreBtn, hisUid, myUid, pId, pDesc, pImage, myHolder.pImageIv);
            }
        });

        myHolder.pImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                
                //start postdetailactivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                intent.putExtra("uid", hisUid);
                context.startActivity(intent);
            }
        });
        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if post user is me, then go to profile
                if (hisUid.equals(myUid)) {
                    //((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                      //      .replace(R.id.fragment_container, new ProfileFragment()).commit();
//                    Intent intent = new Intent(context, UserProfileActivity.class);
//                    intent.putExtra("uid", myUid);
//                    context.startActivity(intent);
                    Toast.makeText(context, "Go to Profile", Toast.LENGTH_SHORT).show();
                } else {
                    //will be used to go to userprofileactivity
                    //with myUid to show user's posts
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("uid", hisUid);
                    context.startActivity(intent);
                }
            }
        });
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                intent.putExtra("uid", hisUid);
                context.startActivity(intent);
            }
        });
    }

    //for notification
    private void sendNotification(final String hisUid, final String name, final String message, final String postId) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data("1", postId, myUid, name + "" + message, "New notification", hisUid, R.drawable.ic_notif);


                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String userid, String postid){
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", myUid);
        hashMap.put("publisherid", publisherId);
        hashMap.put("text", "upvoted your post");
        hashMap.put("postid", postid);
        hashMap.put("timestamp", timeStamp);

        reference.push().setValue(hashMap);
    }

    private void setCurrentUserName(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelUser = dataSnapshot.getValue(ModelUser.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserData(final ImageView userImage, final TextView username, String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                //get user data
                String image = user.getImage();
                try {
                    Glide.with(context).load(image)
                            .placeholder(R.drawable.ic_undraw_profile_pic)
                            .centerCrop()
                            .into(userImage);
                } catch (Exception e) {

                }

                String name = user.getName();
                if (name.length() > 30) {
                    username.setText(name.substring(0, 27) + "...");
                } else {
                    username.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageButton moreBtn, String uid, final String myUid, final String pId, final String pDesc,
                                 final String pImage, final ImageView pImageIv) {
        //creating popup menu currently having delete
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        //show delete option in only post of currently signedin user
        if (uid.equals(myUid)) {
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        } else {
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "Report");
        }

        popupMenu.getMenu().add(Menu.NONE, 3, 0, "Share");

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    //delete is clicked
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Post")
                            .setMessage("Are you sure to delete this post?\nThis action cannot be undone.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    beginDelete(pId, pImage);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else if (id == 1) {
                    //edit is clicked
                    //start addpostactivity with key "edit post" and the id of the post clicked
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                } else if (id == 2) {
                    reportPost(pId);
                } else if (id == 3) {
                    try {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) pImageIv.getDrawable();
                        //post with image
                        // convert image to bitmap
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        shareImageAndText(pDesc, bitmap);
                    } catch (Exception e) {
                        //post without image
                        shareTextOnly(pDesc);
                    }
                }

                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void reportPost(final String pId) {
        //option camera/gallery to show in dialog
        String[] options = {"Spam", "Inappropiate"};

        //dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Why are you reporting this post?");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    reportCurrentPost(pId, 0);
                }
                if (which == 1) {
                   reportCurrentPost(pId, 1);
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void reportCurrentPost(String pId, int type) {
        String reportType = "";
        if (type == 0) {
            reportType = "spam";
        } else {
            reportType = "inappropiate";
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        //each post will have a child "comments" taht willc ontain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reports").child("Posts");

        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("rId", timeStamp);
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("reporterId", myUid);
        hashMap.put("violation", reportType);

        //put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        Toast.makeText(context, "Thank you for reporting this post. We will review it and take further actions", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void shareTextOnly(String pDesc) {
        //concantenate title and desc to share
        String shareBody = pDesc;

        //share intnet
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void shareImageAndText(String pDesc, Bitmap bitmap) {
        String shareBody = pDesc;

        Uri uri = saveImageToShare(bitmap);

        //share
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("iamge/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;

        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.hdpolover.ybbproject.fileprovider", file);

        } catch (Exception e) {
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  uri;
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
        TextView uNameTv, pTimeTv, pDescTv, readmoreTv;
        ImageButton moreBtn;
        LinearLayout profileLayout;
        ImageView upvoteIv, commentIv;
        TextView upvoteTv, commentTv;

        LinearLayout upvoteLayoutBtn, commentLayoutBtn;

        MaterialCardView postCard;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pDescTv = itemView.findViewById(R.id.pDescTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);

            upvoteIv = itemView.findViewById(R.id.upvoteIv);
            commentIv = itemView.findViewById(R.id.commentIv);
            upvoteTv = itemView.findViewById(R.id.upvoteTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            readmoreTv = itemView.findViewById(R.id.readmoreTv);

            postCard = itemView.findViewById(R.id.postCard);

            upvoteLayoutBtn = itemView.findViewById(R.id.upvoteLayoutBtn);
            commentLayoutBtn = itemView.findViewById(R.id.commentLayoutBtn);
        }
    }
}
