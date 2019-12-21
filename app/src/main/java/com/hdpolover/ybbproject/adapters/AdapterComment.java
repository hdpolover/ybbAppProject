package com.hdpolover.ybbproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.UserProfileActivity;
import com.hdpolover.ybbproject.helpers.SocialTimeConverter;
import com.hdpolover.ybbproject.models.ModelComment;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyHolder> {

    Context context;
    List<ModelComment> commentList;
    String myUid, postId;

    SocialTimeConverter stc;

    public AdapterComment(Context context, List<ModelComment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind the row_comments layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        stc = new SocialTimeConverter();

        //get data
        final String uid = commentList.get(position).getUid();
        final String cid = commentList.get(position).getcId();
        String comment = commentList.get(position).getComment();
        String timestamp = commentList.get(position).getTimestamp();

        //set user data
        getUserData(holder.avatarIv, holder.nameTv, uid);

        //set the data
        //holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        holder.timeTv.setText(stc.getSocialTimeFormat(timestamp));

        //comment click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //my comment, show delete dialog
                if (myUid.equals(uid)) {
                    //if my comment
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete this comment?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //delete comment
                            deleteComment(cid);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    //show dialog
                    builder.create().show();
                }
            }
        });

        holder.nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid", uid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid", uid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void getUserData(final ImageView userImage, final TextView username, String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                //get user data
                String image = user.getImage();
                try {
                    Glide.with(context).load(image)
                            .placeholder(R.drawable.ic_undraw_profile_pic)
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

    private void deleteComment(String cid) {
        //final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        //ref.child(cid).removeValue(); //it will delete the comment

        Query fQuery = FirebaseDatabase.getInstance().getReference("Comments").child(postId).child(cid);
        fQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ds.getRef().removeValue(); //remove values from firebase where pid matches
                }
                //deleted
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        //declare views from row_comments
        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
