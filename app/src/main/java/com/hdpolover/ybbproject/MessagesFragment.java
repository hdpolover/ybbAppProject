package com.hdpolover.ybbproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hdpolover.ybbproject.adapters.AdapterChatlist;
import com.hdpolover.ybbproject.adapters.AdapterUsers;
import com.hdpolover.ybbproject.models.ModelChat;
import com.hdpolover.ybbproject.models.ModelChatlist;
import com.hdpolover.ybbproject.models.ModelUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MessagesFragment extends Fragment {

    RecyclerView recyclerView;
    //AdapterUsers adapterUsers;
    List<ModelChatlist> chatList;
    List<ModelUser> userList;

    DatabaseReference reference;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;

    AdapterChatlist adapterChatlist;
    FloatingActionButton fab;

    ImageView chatIv;
    TextView chatTv, clockTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        fab = view.findViewById(R.id.fab);
        chatTv = view.findViewById(R.id.chatTv);
        chatIv = view.findViewById(R.id.chatIv);
        //clockTv = view.findViewById(R.id.lastClockTv);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerView);

        //Layout for recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(linearLayoutManager);

        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatList.add(chatlist);
                }

                if (chatList.size() > 0) {
                    loadChats();
                    chatTv.setVisibility(View.GONE);
                    chatIv.setVisibility(View.GONE);
                } else {
                    chatTv.setVisibility(View.VISIBLE);
                    chatIv.setVisibility(View.VISIBLE);
                }
            }

            //clockTv.setText(ds.child("timestamp").getValue().toString());
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkUserStatus();

        //handle fab click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContact();
            }
        });

//        //init user list
//        userList = new ArrayList<>();
        return view;
    }

    private void showContact() {
//        ContactFragment fragment = new ContactFragment();
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_messages, fragment);
//        fragmentTransaction.commit();

        Intent intent = new Intent(getActivity(), ContactActivity.class);
        startActivity(intent);
    }


    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUser user = ds.getValue(ModelUser.class);
                    for (ModelChatlist chatlist : chatList) {
                        if (user.getUid() != null && user.getUid().equals(chatlist.getId())) {
                            userList.add(user);
                            break;
                        }
                    }
                    //adapter
                    adapterChatlist = new AdapterChatlist(getContext(), userList);
                    //set adapter
                    recyclerView.setAdapter(adapterChatlist);
                    //set last message
                    for (int i = 0; i < userList.size(); i++) {
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                String theLastClock = "0000";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(currentUser.getUid())) {

                        if (chat.getType().equals("image")) {
                            theLastMessage = "Sent a photo";
                            theLastClock = String.valueOf(chat.getTimestamp());
                            Log.e("jam if", theLastClock);
                        } else {
                            theLastMessage = chat.getMessage();
                            theLastClock = String.valueOf(chat.getTimestamp());
                            Log.e("jam else", theLastClock);
                        }
                    }
                }

                adapterChatlist.setLastMessageMap(userId, theLastMessage);
                //adapterChatlist.setLastClockMap(userId, theLastClock);
                adapterChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//
//    private void getAllUsers() {
//        //get current user
//        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//        //get path of database name "Users" containing users info
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        //get all data from path
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for(DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelUser modelUser = ds.getValue(ModelUser.class);
//
//                    //get all users except currently signed in user
//                    if(!modelUser.getUid().equals(fUser.getUid())){
//                        userList.add(modelUser);
//                    }
//
//                    //adapter
//                    adapterUsers = new AdapterUsers(getActivity(), userList);
//                    //set adapter to recycler view
//                    postRecyclerView.setAdapter(adapterUsers);
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
//
//    private void searchUsers(final String query) {
//        //get current user
//        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//        //get path of database name "Users" containing users info
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        //get all data from path
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for(DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelUser modelUser = ds.getValue(ModelUser.class);
//
//                    //conditions to fulfil search
//                    // 1. User not current user
//                    // 2. The user name of email contains text entered in Searchview (case insersitive)
//
//                    //get all searched users except currently signed in user
//
//                    if(!modelUser.getUid().equals(fUser.getUid())){
//                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
//                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){
//                            userList.add(modelUser);
//                        }
//                    }
//
//                    //adapter
//                    adapterUsers = new AdapterUsers(getActivity(), userList);
//                    //refresh adapter
//                    adapterUsers.notifyDataSetChanged();
//                    //set adapter to recycler view
//                    postRecyclerView.setAdapter(adapterUsers);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set users of logged in user
//            mMasukTv.setText(user.getEmail());

        } else {
            //user not signed in, go to welcome
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    //Inflate options menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        //hide searchview, as we dont need it here
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_notif).setVisible(false);

        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                //called when user press search button from keyboard
//                //if search query is not empty the search
//                if(!TextUtils.isEmpty(s.trim())){
//                    //search text contains text, search it
//                    searchUsers(s);
//                }
//                else{
//                    //search text empty, get all users
//                    getAllUsers();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                //called whenever user press any single letter
//                //if search query is not empty the search
//                if(!TextUtils.isEmpty(s.trim())){
//                    //search text contains text, search it
//                    searchUsers(s);
//                }
//                else{
//                    //search text empty, get all users
//                    getAllUsers();
//                }
//                return false;
//            }
//        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    //handle menu item click

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item id
        int id = item.getItemId();
//        if (id == R.id.action_logout) {
//            firebaseAuth.signOut();
//            checkUserStatus();
//        }

        return super.onOptionsItemSelected(item);
    }

}
