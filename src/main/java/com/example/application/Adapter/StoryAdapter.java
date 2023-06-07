package com.example.application.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.model.UserStories;
import com.example.application.R;
import com.example.application.databinding.StoriesViewBinding;
import com.example.application.model.StoryModel;
import com.example.application.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {
    ArrayList<StoryModel> list;
    Context context;

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {

        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stories_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        StoryModel model = list.get(position);
        if (model.getList().size() > 0) {
            UserStories lastStory = model.getList().get(model.getList().size() - 1);
            Picasso.get().load(lastStory.getImage()).placeholder(R.drawable.ic_profile).into(holder.binding.uploadedStory);

            holder.binding.view.setPortionsCount(model.getList().size());
            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(model.getStoryUploadedBy()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UserModel user=snapshot.getValue(UserModel.class);
                                assert user != null;
                                Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.ic_profile)
                                        .into(holder.binding.notificationProfile);
                                holder.binding.name.setText(user.getName());
                                holder.binding.uploadedStory.setOnClickListener(view -> {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UserStories stories : model.getList()) {
                                        myStories.add(new MyStory(stories.getImage(), new Date(stories.getAddTimeOfStory())));
                                    }

                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(user.getName()) // Default is Hidden
                                            .setSubtitleText("") // Default is Hidden
                                            .setTitleLogoUrl(user.getProfilePicture()) // Default is Hidden
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position1) {
                                                    //your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position1) {
                                                    //your action
                                                }
                                            }) // Optional Listeners
                                            .build() // Must be called before calling show method
                                            .show();
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        StoriesViewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoriesViewBinding.bind(itemView);

        }
    }
}
