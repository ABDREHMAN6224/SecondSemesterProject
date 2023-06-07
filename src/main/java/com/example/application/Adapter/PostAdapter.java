package com.example.application.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.CommentActivity;
import com.example.application.R;
import com.example.application.databinding.DashboardViewBinding;
import com.example.application.model.NotificationModel;
import com.example.application.model.PostModel;
import com.example.application.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<PostModel> list;
    Context context;
    boolean liked=true;

    public PostAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_view, parent, false);

        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PostModel model = list.get(position);
        Picasso.get().load(model.getPostImage()).placeholder(R.drawable.ic_profile)
                .into(holder.binding.postImage);
        holder.binding.noOfLikes.setText(model.getPostLikes() + "");
        holder.binding.noOfComments.setText(model.getNoOfComments()+"");
        String data = model.getPostDescription();
        if (data.equals("")) {
            holder.binding.postDescription.setVisibility(View.GONE);
        } else {
            holder.binding.postDescription.setVisibility(View.VISIBLE);
            holder.binding.postDescription.setText(model.getPostDescription());
        }
        FirebaseDatabase.getInstance().getReference("Users").child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()) {
                UserModel user=snapshot.getValue(UserModel.class);
                assert user != null;
                Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.ic_profile)
                        .into(holder.binding.notificationProfile);
                holder.binding.nameOfProfile.setText(user.getName());
                holder.binding.aboutName.setText(user.getProfession());
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("posts")
                .child(model.getPostID()).child("likes")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            liked=false;
                            holder.binding.noOfLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_clicked, 0, 0, 0);
                        }else {
                            liked=true;
                            holder.binding.noOfLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_not_clicked, 0, 0, 0);

                        }
                        if(liked){
                            holder.binding.noOfLikes.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(model.getPostID())
                                    .child("likes")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(true).addOnSuccessListener(unused -> FirebaseDatabase.getInstance().getReference()
                                            .child("posts").child(model.getPostID())
                                            .child("postLikes")
                                            .setValue(model.getPostLikes() + 1).addOnSuccessListener(unused1 -> {
                                                holder.binding.noOfLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_clicked, 0, 0, 0);
                                                NotificationModel notificationModel=new NotificationModel();
                                                notificationModel.setType("like");
                                                notificationModel.setNotificationSentBy(FirebaseAuth.getInstance().getUid());
                                                notificationModel.setNotificationSentAt(new Date().getTime());
                                                notificationModel.setPostId(model.getPostID());
                                                notificationModel.setPostedBy(model.getPostedBy());
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("notifications")
                                                        .child(model.getPostedBy())
                                                        .push().setValue(notificationModel);
                                            })));
                        }else {
                            holder.binding.noOfLikes.setOnClickListener(view ->

                            FirebaseDatabase.getInstance().getReference()
                                    .child("posts").child(model.getPostID())
                                    .child("postLikes")
                                    .setValue(model.getPostLikes() - 1).addOnSuccessListener(unused1 -> {
                                        holder.binding.noOfLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_not_clicked, 0, 0, 0);
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("posts")
                                                .child(model.getPostID())
                                                .child("likes")
                                                .child(FirebaseAuth.getInstance().getUid()).removeValue();})

                            );
//                            -------------------

//                            ------------------
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        holder.binding.noOfComments.setOnClickListener(view -> {
            Intent intent1=new Intent(context, CommentActivity.class);
            intent1.putExtra("postId",model.getPostID());
            intent1.putExtra("postBy",model.getPostedBy());
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        DashboardViewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardViewBinding.bind(itemView);
        }
    }
}

