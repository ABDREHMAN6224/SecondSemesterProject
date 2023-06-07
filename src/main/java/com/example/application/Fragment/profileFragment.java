package com.example.application.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.application.Adapter.FollowersAdapter;
import com.example.application.model.FollowModel;
import com.example.application.R;
import com.example.application.databinding.FragmentProfileBinding;
import com.example.application.model.UserModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class profileFragment extends Fragment {
    private int i = 0;
    FragmentProfileBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    //    RecyclerView rv;
    ArrayList<FollowModel> list;

    public profileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

      list = new ArrayList<>();

        FollowersAdapter adapter = new FollowersAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.friendsRV.setLayoutManager(linearLayoutManager);
        binding.friendsRV.setAdapter(adapter);
        database.getReference().child("Users")
                        .child(Objects.requireNonNull(auth.getUid()))
                                .child("followers").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            FollowModel followModel=dataSnapshot.getValue(FollowModel.class);
                            list.add(followModel);

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {


            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel model = snapshot.getValue(UserModel.class);
                    assert model != null;
                    Picasso.get().load(model.getCoverPicture()).placeholder(R.drawable.ic_profile)
                            .into(binding.imageView4);
                    Picasso.get().load(model.getProfilePicture()).placeholder(R.drawable.ic_add_user)
                            .into(binding.userProfile);
                    binding.username.setText(model.getName());
                    binding.profession.setText(model.getProfession());
                    binding.followers.setText(model.getFollowerCount()+"");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.changeUserPhoto.setOnClickListener(view -> {
            i = 2;
            ImagePicker.with(profileFragment.this)
                    .crop()
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        binding.changeProfile.setOnClickListener(view -> {
            i = 3;
            ImagePicker.with(profileFragment.this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (i == 3) {

            if (data.getData() != null) {
                Uri uri = data.getData();
                binding.imageView4.setImageURI(uri);

                final StorageReference reference = storage.getReference().child("cover_photo").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getContext(), "pic added", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(uri1 -> database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).child("coverPicture").setValue(uri1.toString()));
                });
            }
        } else {

            if (data.getData() != null) {
                Uri uri = data.getData();
                binding.userProfile.setImageURI(uri);

                final StorageReference reference = storage.getReference().child("profilePicture").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getContext(), "profile pic added", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(uri12 -> database.getReference().child("Users").child((Objects.requireNonNull(auth.getUid()))).child("profilePicture").setValue(uri12.toString()));
                });
            }
        }
    }
}