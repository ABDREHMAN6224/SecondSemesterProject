package com.example.application.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.databinding.SampleCommentBinding;
import com.example.application.model.CommentModel;
import com.example.application.model.UserModel;
import com.example.application.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {
    Context context;
    ArrayList<CommentModel> list;

    public CommentAdapter(Context context, ArrayList<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_comment,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        CommentModel model= list.get(position);
        holder.binding.comment.setText(model.getComment());
        String text = TimeAgo.using(model.getCommentedAt());
        holder.binding.time.setText(text);
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel=snapshot.getValue(UserModel.class);
                        assert userModel != null;
                        Picasso.get().load(userModel.getProfilePicture()).placeholder(R.drawable.ic_profile)
                                .into(holder.binding.userProfile);
                        String a="<b>"+userModel.getName()+"</b>: "+model.getComment();
                        holder.binding.comment.setText(Html.fromHtml(a));
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

    public static class viewHolder extends RecyclerView.ViewHolder{
        SampleCommentBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding=SampleCommentBinding.bind(itemView);
        }
    }
}
