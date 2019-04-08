package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestActivity extends AppCompatActivity {

    private RecyclerView myFriendRequestsList;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, friendReqRef;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendReqRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(currentUserId);

        myFriendRequestsList = (RecyclerView) findViewById(R.id.friend_requests_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendRequestsList.setLayoutManager(linearLayoutManager);

        DisplayAllFriendRequests();
    }

    private void DisplayAllFriendRequests() {
        FirebaseRecyclerAdapter<FindFriends, FriendRequestsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FindFriends, FriendRequestsViewHolder>(
                        FindFriends.class,
                        R.layout.all_users_display_layout,
                        FriendRequestsViewHolder.class,
                        friendReqRef
                ) {
                    @Override
                    protected void populateViewHolder(final FriendRequestsViewHolder viewHolder, FindFriends model, int position) {
                        final String userIDs = getRef(position).getKey();

                        usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    final String userName = dataSnapshot.child("fullname").getValue().toString();
                                    final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                                    final String status = dataSnapshot.child("status").getValue().toString();

                                    viewHolder.setFullname(userName);
                                    viewHolder.setProfileimage(profileImage);
                                    viewHolder.setStatus(status);

                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent profileIntent = new Intent(FriendRequestActivity.this, PersonProfileActivity.class);
                                            profileIntent.putExtra("Visit User Id", userIDs);
                                            startActivity(profileIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };
        myFriendRequestsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendRequestsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FriendRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(String profileimage) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).into(myImage);
        }
        public void setFullname(String fullname) {
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_name);
            myName.setText(fullname);
        }
        public void setStatus(String status) {
            TextView myStatus = (TextView) mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }
    }
}
