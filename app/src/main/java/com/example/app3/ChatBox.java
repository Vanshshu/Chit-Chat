package com.example.app3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ChatBox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        //let's set adapter into viewpager---
        ViewPager2 views=findViewById(R.id.viewpagger);
        ChatFragmentAdapter  adapter=new ChatFragmentAdapter
                (getSupportFragmentManager(),getLifecycle());
        views.setAdapter(adapter);
        views.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

            }
        });
        //lets set position of tabs according to the position of viewpager
        TabLayout tabs=findViewById(R.id.Tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                views.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //change position of tab on change of view pages
        views.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tabs.getTabAt(position).select();
             }
        });
        }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            Toast.makeText(this, "please login first,", Toast.LENGTH_SHORT).show();
            Intent in=new Intent(this, Login.class);
            startActivity(in);
        }
    }
}