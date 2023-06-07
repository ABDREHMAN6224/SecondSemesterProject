package com.example.application.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.CommentActivity;
import com.example.application.R;
import com.example.application.databinding.Notification2viewBinding;
import com.example.application.model.NotificationModel;
import com.example.application.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {
    ArrayList<NotificationModel> list;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notification2view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NotificationModel model=list.get(position);
        String type= model.getType();
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getNotificationSentBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user=snapshot.getValue(UserModel.class);
                        assert user != null;
                        String name="<b>"+user.getName()+"</b>";
                        Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.ic_profile)
                                .into(holder.binding.notificationProfile);
                        switch (type) {
                            case "like":
                                name += " liked your post";
                                holder.binding.notification.setText(Html.fromHtml(name));
                                break;
                            case "follow":
                                name += " started following you";
                                holder.binding.notification.setText(Html.fromHtml(name));
                                break;
                            case "comment":
                                name += " commented on your post";
                                holder.binding.notification.setText(Html.fromHtml(name));
                                break;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.notificationBox.setOnClickListener(view -> {
                holder.binding.notificationBox.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if(type.equals("comment")) {
                FirebaseDatabase.getInstance().getReference().child("notifications")
                        .child(model.getPostedBy()).child(model.getNotificationId())
                        .child("checkOpened").setValue(true);
                Intent intent1 = new Intent(context, CommentActivity.class);
                intent1.putExtra("postId", model.getPostId());
                intent1.putExtra("postBy", model.getPostedBy());
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }});
    boolean check=model.isCheckOpened();
    if(check){
        holder.binding.notificationBox.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
     Notification2viewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding= Notification2viewBinding.bind(itemView);

        }
    }
}
