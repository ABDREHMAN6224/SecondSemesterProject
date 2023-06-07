package com.example.application.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.databinding.FragmentAddBinding;
import com.example.application.model.PostModel;
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

import java.util.Date;
import java.util.Objects;


public class addFragment extends Fragment {

    FragmentAddBinding binding;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    ProgressDialog dialog;

    public addFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog=new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading your Posting");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        firebaseDatabase.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            assert user != null;
                            Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.ic_profile).into(binding.profile);
                            binding.name.setText(user.getName());
                            binding.profession.setText(user.getProfession());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.addImage.setOnClickListener(view -> ImagePicker.with(addFragment.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());
        binding.postData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String post = binding.postData.getText().toString();
                if (!post.isEmpty()) {
                    binding.post.setEnabled(true);
                    binding.post.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.follow_btn_bg));
                    binding.post.setTextColor(requireContext().getResources().getColor(R.color.white));
                } else {
                    binding.post.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.done_following));
                    binding.post.setTextColor(requireContext().getResources().getColor(R.color.black));

                    binding.post.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
        binding.post.setOnClickListener(view -> {
            dialog.show();
            final StorageReference storageReference = storage.getReference().child("posts")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(new Date().getTime() + "");
            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                PostModel post = new PostModel();
                post.setPostImage(uri.toString());
                post.setPostedBy(FirebaseAuth.getInstance().getUid());
                post.setPostDescription(binding.postData.getText().toString());
                post.setPostedAt(new Date().getTime());

                firebaseDatabase.getReference().child("posts").push()
                        .setValue(post).addOnSuccessListener(unused -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "post added", Toast.LENGTH_SHORT).show();
                        });

            }));
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (data.getData() != null) {
            uri = data.getData();
            binding.postPic.setImageURI(uri);
            binding.postPic.setVisibility(View.VISIBLE);
            binding.post.setEnabled(true);
            binding.post.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.follow_btn_bg));
            binding.post.setTextColor(requireContext().getResources().getColor(R.color.white));
        }
    }
}
