package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.application.databinding.ActivitySignUpBinding;
import com.example.application.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.signUpBtn.setOnClickListener(view -> {
            String emailEntered = Objects.requireNonNull(binding.emailET.getText()).toString();
            String passwordEntered = Objects.requireNonNull(binding.passwordET.getText()).toString();
            auth.createUserWithEmailAndPassword(emailEntered, passwordEntered)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel user = new UserModel((Objects.requireNonNull(binding.nameET.getText())).toString(), (Objects.requireNonNull(binding.ProfessionET.getText())).toString(), emailEntered, passwordEntered);
                            String id= (Objects.requireNonNull(task.getResult().getUser())).getUid();
                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(SignUpActivity.this, "User Data saved", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else
                            Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    });
        });
        binding.goToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LiginActivity.class);
            startActivity(intent);
        });

    }
}