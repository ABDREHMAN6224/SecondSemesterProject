package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.application.databinding.ActivityCommentBinding;
import com.example.application.model.NotificationModel;
import com.example.application.model.PostModel;
import com.example.application.Adapter.CommentAdapter;
import com.example.application.model.CommentModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    Intent intent;
    String postId,postBy;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<CommentModel> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate((getLayoutInflater()));
        setContentView(binding.getRoot());
        intent = getIntent();

        setSupportActionBar(binding.toolbar2);
        CommentActivity.this.setTitle("Comments");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        postId = intent.getStringExtra("postId");
        postBy=intent.getStringExtra("postBy");
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        database.getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                assert post != null;
                Picasso.get().load(post.getPostImage()).placeholder(R.drawable.ic_profile).into(binding.post);
                binding.description.setText(post.getPostDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.commentBtn.setOnClickListener(view -> {
            CommentModel cmnt = new CommentModel();
            cmnt.setCommentedAt(new Date().getTime());
            cmnt.setCommentedBy(FirebaseAuth.getInstance().getUid());
            cmnt.setComment(binding.comment.getText().toString());
            database.getReference().child("posts")
                    .child(postId).child("comments").push()
                    .setValue(cmnt).addOnSuccessListener(unused -> database.getReference().child("posts")
                            .child(postId).child("noOfComments")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int noOfComments = 0;
                                    if (snapshot.exists())
                                        noOfComments = snapshot.getValue(Integer.class);
                                    database.getReference().child("posts")
                                            .child(postId).child("noOfComments")
                                            .setValue(noOfComments + 1).addOnSuccessListener(unused1 -> {
                                                binding.comment.setText("");
                                                NotificationModel notificationModel=new NotificationModel();
                                                notificationModel.setNotificationSentAt(new Date().getTime());
                                                notificationModel.setPostedBy(postBy);
                                                notificationModel.setNotificationSentBy(FirebaseAuth.getInstance().getUid());
                                                notificationModel.setType("comment");
                                                notificationModel.setPostId(postId);

                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("notifications")
                                                        .child(postBy)
                                                        .push().setValue(notificationModel);
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            }));
        });
        CommentAdapter adapter=new CommentAdapter(this,list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager(linearLayoutManager);
        binding.commentRv.setAdapter(adapter);
        database.getReference().child("posts").child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            CommentModel model=dataSnapshot.getValue(CommentModel.class);
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}