package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.application.Fragment.addFragment;
import com.example.application.Fragment.notificationFragment;
import com.example.application.Fragment.profileFragment;
import com.example.application.Fragment.searchFragment;
import com.example.application.Fragment.homeFragment;
import com.example.application.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar3);
        MainActivity.this.setTitle("My Profile");

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,new homeFragment());
        binding.toolbar3.setVisibility(View.GONE);
        transaction.commit();
        binding.bnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                int id=item.getItemId();
                if(id==R.id.nav_home){
                    binding.toolbar3.setVisibility(View.GONE);
                    transaction.replace(R.id.container,new homeFragment());
                    transaction.commit();
                }
                else if(id==R.id.nav_notification){
                    binding.toolbar3.setVisibility(View.GONE);
                    transaction.replace(R.id.container,new notificationFragment());
                    transaction.commit();

                }
                else if(id==R.id.nav_add){
                    binding.toolbar3.setVisibility(View.GONE);
                    transaction.replace(R.id.container,new addFragment());
                    transaction.commit();

                }
                else if(id==R.id.nav_search){
                    binding.toolbar3.setVisibility(View.GONE);
                    transaction.replace(R.id.container,new searchFragment());
                    transaction.commit();

                }
                else{//profile
                    binding.toolbar3.setVisibility(View.VISIBLE);
                    transaction.replace(R.id.container,new profileFragment());
                    transaction.commit();

                }
                return false;
            }



        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LiginActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }
}