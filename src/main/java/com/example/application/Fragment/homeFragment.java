package com.example.application.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.Adapter.PostAdapter;
import com.example.application.Adapter.StoryAdapter;
import com.example.application.R;
import com.example.application.model.PostModel;
import com.example.application.model.StoryModel;
import com.example.application.model.UserStories;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class homeFragment extends Fragment {

    RecyclerView storyRv, dashboardRv;
    ArrayList<StoryModel> list;
    ArrayList<PostModel> dList;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    ImageView addStory;
    ProgressDialog dialog;


    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storyRv = view.findViewById(R.id.stories_view);
        list = new ArrayList<>(1);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading Story..");
        dialog.setMessage("please wait..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        StoryAdapter adapter = new StoryAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRv.setLayoutManager(linearLayoutManager);
        storyRv.setAdapter(adapter);
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        StoryModel storyModel = new StoryModel();
                        storyModel.setStoryUploadedBy(dataSnapshot.getKey());
                        if (dataSnapshot.exists())
                            if (dataSnapshot.child("storyUploadedAt").exists())
                                storyModel.setStoryUploadedAt(dataSnapshot.child("storyUploadedAt").getValue(Long.class));


                        ArrayList<UserStories> userStories = new ArrayList<>();
                        for (DataSnapshot snapshot1 : dataSnapshot.child("list").getChildren()) {
                            UserStories stories = snapshot1.getValue(UserStories.class);
                            userStories.add(stories);
                        }
                        storyModel.setList(userStories);
                        list.add(storyModel);
                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dashboardRv = view.findViewById(R.id.dashboardRV);
        dList = new ArrayList<>();

        PostAdapter dAdapter = new PostAdapter(dList, getContext());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        dashboardRv.setLayoutManager(linearLayoutManager1);
        dashboardRv.setNestedScrollingEnabled(false);
        dashboardRv.setAdapter(dAdapter);
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel postModel = dataSnapshot.getValue(PostModel.class);
                    assert postModel != null;
                    postModel.setPostID(dataSnapshot.getKey());
                    dList.add(postModel);
                }
                dAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStory = view.findViewById(R.id.addStory);
        addStory.setOnClickListener(view1 -> ImagePicker.with(homeFragment.this)
                .crop()
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (data.getData() != null) {
            Uri uri = data.getData();
            dialog.show();
            final StorageReference reference = storage.getReference().child("stories")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child(new Date().getTime() + "");
            reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                StoryModel story = new StoryModel();
                story.setStoryUploadedAt(new Date().getTime());
                database.getReference().child("stories")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("storyUploadedAt").setValue(story.getStoryUploadedAt()).addOnSuccessListener(unused -> {
                            UserStories stories = new UserStories(uri1.toString(), story.getStoryUploadedAt());
                            database.getReference("stories").child(FirebaseAuth.getInstance().getUid())
                                    .child("list").push()
                                    .setValue(stories).addOnSuccessListener(unused1 -> dialog.dismiss());
                        });
            }));
        }
    }
}