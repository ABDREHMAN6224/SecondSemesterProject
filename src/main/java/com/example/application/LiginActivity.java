package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.databinding.ActivityLiginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LiginActivity extends AppCompatActivity {
    ActivityLiginBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        binding.loginBtn.setOnClickListener(view -> {
            String emailEntered = Objects.requireNonNull(binding.emailET.getText()).toString();
            String passwordEntered = Objects.requireNonNull(binding.passwordET.getText()).toString();

            auth.signInWithEmailAndPassword(emailEntered, passwordEntered).addOnCompleteListener(task -> {
              if(task.isSuccessful()){
                  Toast.makeText(LiginActivity.this, "login done", Toast.LENGTH_SHORT).show();
                  Intent intent=new Intent(LiginActivity.this,MainActivity.class);
                  startActivity(intent);
              }else
                  Toast.makeText(LiginActivity.this, "register first", Toast.LENGTH_SHORT).show();
            });
        });
        binding.goToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LiginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user!=null){
            Intent intent=new Intent(LiginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}