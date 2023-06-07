package com.example.application.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.example.application.databinding.SearchSampleBinding;
import com.example.application.model.FollowModel;
import com.example.application.model.NotificationModel;
import com.example.application.model.UserModel;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder> {
    Context context;
    ArrayList<UserModel> list;
    boolean following = false;

    public SearchAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_sample, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        UserModel model = list.get(position);

        Picasso.get().load(model.getProfilePicture()).placeholder(R.drawable.ic_profile).into(holder.binding.profile);
        holder.binding.name.setText(model.getName());
        holder.binding.profession.setText(model.getProfession());
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getUserID())
                .child("followers")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            following=false;
                            holder.binding.followButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.done_following));
                            holder.binding.followButton.setText("FOLLOWING");
                            holder.binding.followButton.setTextColor(context.getResources().getColor(R.color.black));
//                            holder.binding.followButton.setEnabled(false);
                        }else {
                            holder.binding.followButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_btn_bg));

                            following=true;
                        }
                        if (following) {
                            holder.binding.followButton.setOnClickListener(view -> {
                                FollowModel follow = new FollowModel();
                                follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                follow.setTimeOfFollow(new Date().getTime());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(model.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(follow).addOnSuccessListener(unused -> FirebaseDatabase.getInstance().getReference()
                                                    .child("Users")
                                                    .child(model.getUserID())
                                                    .child("followerCount").setValue(model.getFollowerCount() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {


                                                            holder.binding.followButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.done_following));
                                                            holder.binding.followButton.setText("FOLLOWING");
                                                            holder.binding.followButton.setTextColor(context.getResources().getColor(R.color.black));
//                                                            holder.binding.followButton.setEnabled(false);
                                                            Toast.makeText(context, "You followed", Toast.LENGTH_SHORT).show();

                                                            NotificationModel notificationModel = new NotificationModel();
                                                            notificationModel.setNotificationSentBy(FirebaseAuth.getInstance().getUid());
                                                            notificationModel.setNotificationSentAt(new Date().getTime());
                                                            notificationModel.setType("follow");

                                                            FirebaseDatabase.getInstance().getReference().child("notifications")
                                                                    .child(model.getUserID()).push()
                                                                    .setValue(notificationModel);
                                                        }
                                                    }));
                                });}
                                else if(!following){

                                  holder.binding.followButton.setOnClickListener(view -> {
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("Users")
                                                .child(model.getUserID())
                                                .child("followerCount").setValue(model.getFollowerCount()-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {


                                                        holder.binding.followButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_btn_bg));
                                                        holder.binding.followButton.setText("FOLLOW");
                                                        holder.binding.followButton.setTextColor(context.getResources().getColor(R.color.black));
//                                                            holder.binding.followButton.setEnabled(f);
                                                        Toast.makeText(context, "You unfollowed", Toast.LENGTH_SHORT).show();
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Users")
                                                                .child(model.getUserID())
                                                                .child("followers")
                                                                .child(FirebaseAuth.getInstance().getUid()).removeValue();

                                                    }
                                                });


                                    });}



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        SearchSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchSampleBinding.bind(itemView);

        }
    }
}
