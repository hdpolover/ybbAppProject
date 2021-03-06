package com.hdpolover.ybbproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.hdpolover.ybbproject.adapters.AdapterComment;
import com.hdpolover.ybbproject.helpers.SocialTimeConverter;
import com.hdpolover.ybbproject.models.ModelComment;
import com.hdpolover.ybbproject.models.ModelUser;
import com.hdpolover.ybbproject.notifications.APIService;
import com.hdpolover.ybbproject.notifications.Client;
import com.hdpolover.ybbproject.notifications.Data;
import com.hdpolover.ybbproject.notifications.Response;
import com.hdpolover.ybbproject.notifications.Sender;
import com.hdpolover.ybbproject.notifications.Token;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class PostDetailActivity extends AppCompatActivity {

    //to get detail of user and post
    String hisUid, myUid, myName, myEmail, myDp,
    postId, pImage;

    //progress bar
    ProgressDialog pd;

    //views
    ImageView uPictureIv, pImageIv;
    TextView uNameTv, pTimeTv, pDescTv;
    ImageButton moreBtn;

    TextView upvoteTv, commentTv, upvotersCountTv;
    ImageView upvoteIv, commentIv;
    LinearLayout profileLayout;
    RecyclerView recyclerView;

    LinearLayout upvoteLayoutBtn, commentLayoutBtn;
    MaterialCardView upvotersCard;

    List<ModelComment> commentList;
    AdapterComment adapterComment;

    //add comment views
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;

    FirebaseUser firebaseUser;

    int upvotersCount;

    APIService apiService;

    String publisherId;
    ModelUser modelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        //action bar and its propertoes
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detail");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //get id of post using intent
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        hisUid = intent.getStringExtra("uid");
        publisherId = hisUid;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //init views
        uPictureIv = findViewById(R.id.uPictureIv);
        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pDescTv = findViewById(R.id.pDescTv);
        upvoteIv = findViewById(R.id.upvoteIv);
        commentIv = findViewById(R.id.commentIv);
        upvoteTv = findViewById(R.id.upvoteTv);
        commentTv = findViewById(R.id.commentTv);

        moreBtn = findViewById(R.id.moreBtn);
        profileLayout = findViewById(R.id.profileLayout);
        recyclerView = findViewById(R.id.recyclerView);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);

        upvotersCard = findViewById(R.id.upvotersCard);
        upvotersCountTv = findViewById(R.id.upvotersCountTv);

        upvoteLayoutBtn = findViewById(R.id.upvoteLayoutBtn);
        commentLayoutBtn = findViewById(R.id.commentLayoutBtn);

        checkUserStatus();

        loadPostInfo();
        getUserData(uPictureIv, uNameTv, hisUid);
        loadComments();
        loadCurrentUserImage();

        //setUpvotes();
        isUpvoted(postId, upvoteIv);
        setCountText(upvoteTv, commentTv, postId);

        setCurrentUserName(myUid);
        
        //send comment on button click
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hisUid.equals(myUid)) {
                    postComment();
                } else {
                    postComment();
                    addNotification(hisUid, postId);
                    myName = modelUser.getName();
                    sendNotification(hisUid,  myName," commented on your post", postId);
                }
            }
        });

        upvoteLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upvoteIv.getTag().equals("upvote")) {
                    FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(postId)
                            //.child("Upvotes")
                            .child(firebaseUser.getUid()).setValue(true);
                    //addNotification(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(postId)
                            //.child("Upvotes")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        //more button click handle
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(pDescTv.getText().toString(), pImageIv);
            }
        });

        uPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                intent.putExtra("uid", hisUid);
                startActivity(intent);
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                intent.putExtra("uid", hisUid);
                startActivity(intent);
            }
        });

        upvotersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, PostUpvoterDetailActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }
        });
    }

    private void checkUpvoters(MaterialCardView cardView) {
        Log.d("a", upvotersCount + "");
        if (upvotersCount == 0) {
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.VISIBLE);

            upvotersCountTv.setText(upvotersCount + " people upvoted this");
        }
    }

    private void setCountText(final TextView upvotes, final TextView comments, String postId){
        DatabaseReference upvotesRef = FirebaseDatabase.getInstance().getReference().child("PostUpvotes").child(postId);
        upvotesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    upvotes.setText("Upvote");
                    upvotersCount = 0;
                } else {
                    upvotes.setText(dataSnapshot.getChildrenCount() + "");
                    String a = dataSnapshot.getChildrenCount() + "";
                    upvotersCount = Integer.parseInt(a);
                }
                checkUpvoters(upvotersCard);
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

    private void loadComments() {
        //layout linear for recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init comment list
        commentList = new ArrayList<>();

        //path of the post, to get its comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelComment modelComment = ds.getValue(ModelComment.class);

                    commentList.add(modelComment);

                    //setup adapter
                    adapterComment = new AdapterComment(getApplicationContext(), commentList, myUid, postId);
                    //set adapter
                    recyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(final String pDesc, final ImageView pImageIv) {
        //creating popup menu currently having delete
        PopupMenu popupMenu = new PopupMenu(this, moreBtn, Gravity.END);

        //show delete option in only post of currently signedin user
        if (hisUid.equals(myUid)) {
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
//                    AlertDialog.Builder builder = new AlertDialog.Builder(item.getActionView().getRootView().getContext());
//                    builder.setTitle("Delete");
//                    builder.setMessage("Are you sure to delete this cpost?");
//                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
                            //delete post
                            beginDelete();
//                        }
//                    });
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    //show dialog
//                    builder.create().show();
                }
                else if (id == 1) {
                    //edit is clicked
                    //start addpostactivity with key "edit post" and the id of the post clicked
                    Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", postId);
                    startActivity(intent);
                } else if (id == 2) {
                    reportPost(postId);
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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
                        Toast.makeText(PostDetailActivity.this, "Thank you for reporting this post. We will review it and take further actions.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        //Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
        startActivity(Intent.createChooser(intent, "Share Via"));
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
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;

        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.hdpolover.ybbproject.fileprovider", file);

        } catch (Exception e) {
            //Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  uri;
    }

    private void beginDelete() {
        //post can be with or without image
        if (pImage.equals("noImage")) {
            //post is without image
            deleteWithoutImage();
        } else {
            //post is with image
            deleteWithImage();
        }
    }

    private void deleteWithImage() {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Deleting post...");

        //steps: 1. delte image using uri 2. delete from database
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, now delete database
                        Query fQuery = FirebaseDatabase.getInstance().getReference("Posts")
                                .orderByChild("pId").equalTo(postId);
                        fQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                    ds.getRef().removeValue(); //remove values from firebase where pid matches
                                }
                                //deleted
                                Toast.makeText(PostDetailActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage() {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Deleting...");

        Query fQuery = FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("pId").equalTo(postId);
        fQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ds.getRef().removeValue(); //remove values from firebase where pid matches
                }
                //deleted
                Toast.makeText(PostDetailActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    //for notification
    private void sendNotification(final String hisUid, final String name, final String message, final String pId) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data("1", pId, myUid, name + "" + message, "New notification", hisUid, R.drawable.ic_notif);


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
        hashMap.put("text", "commented on your post");
        hashMap.put("postid", postid);
        hashMap.put("timestamp", timeStamp);

        reference.push().setValue(hashMap);
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding comment...");

        //get data from comment edit text
        final String comment = commentEt.getText().toString().trim();
        //validate
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Comment is empty...", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        //each post will have a child "comments" taht willc ontain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("cId", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("uid", myUid);

        //put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        Toast.makeText(PostDetailActivity.this, "Comment added...", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        pd.dismiss();
                        //Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void getUserData(final ImageView userImage, final TextView username, String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                //get user data
                String image = user.getImage();
                try {
                    Glide.with(getApplicationContext()).load(image)
                            .placeholder(R.drawable.ic_undraw_profile_pic)
                            .centerCrop()
                            .into(userImage);
                } catch (Exception e) {

                }

                String name = user.getName();
                username.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCurrentUserImage() {
        //get current user info
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = myRef.orderByChild("uid").equalTo(myUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    //get data
                    myDp = "" + ds.child("image").getValue();

                    //set data
                    try {

                        Glide.with(getApplicationContext()).load(myDp).placeholder(R.drawable.ic_undraw_profile_pic)
                                .centerInside()
                                .into(cAvatarIv);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {
        //get post using the id of the post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //keep checking the post untill get the required post
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    //get data
                    String pDesc = ""+ds.child("pDesc").getValue();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    pImage = ""+ds.child("pImage").getValue();
                    hisUid = ""+ds.child("uid").getValue();

                    //set data
                    pDescTv.setText(pDesc);

                    SocialTimeConverter stc = new SocialTimeConverter();
                    pTimeTv.setText(stc.getSocialTimeFormat(pTimeStamp));

                    //set image of the user who posted
                    //set post image
                    //if no image then hide the imageview
                    if (pImage.equals("noImage")) {
                        //hide imageview
                        pImageIv.setVisibility(View.GONE);
                    } else {
                        //hide imageview
                        pImageIv.setVisibility(View.VISIBLE);

                        try {
                            Glide.with(getApplicationContext()).load(pImage).into(pImageIv);
                        } catch (Exception e) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
        } else {
            //user not signed in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
