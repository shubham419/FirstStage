package com.example.firststage02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.firststage02.Fragment.DataFragment;
import com.example.firststage02.Fragment.HomeFragment;
import com.example.firststage02.Fragment.InfoFragment;
import com.example.firststage02.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    case R.id.menu_home:
                        transaction.replace(R.id.framelayout,new HomeFragment());
                        break;
                    case R.id.menu_data:
                        transaction.replace(R.id.framelayout,new DataFragment());
                        break;
                    case R.id.menu_info:
                        transaction.replace(R.id.framelayout,new InfoFragment());
                        break;
                }
                transaction.commit();

                return true;
            }
        });

    }
}