package com.jackdonaldson.majorwork2019;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jackdonaldson.majorwork2019.adapter.MainPagerAdapter;
import com.jackdonaldson.majorwork2019.models.User;
import com.jackdonaldson.majorwork2019.view.BottomTabView;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        final View background = findViewById(R.id.background_view);

        ViewPager viewPager = findViewById(R.id.view_pager);

        final MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter );

        BottomTabView bottomTabView = findViewById(R.id.bottom_tab_view);
        bottomTabView.setUpWithViewPager(viewPager);

        background.setBackgroundColor(ContextCompat.getColor(this,R.color.middleBackground));

        /*final int color1 = ContextCompat.getColor(this,R.color.colorPrimary);
        final int color2 = ContextCompat.getColor(this,R.color.middleBackground);
        final int color3 = ContextCompat.getColor(this,R.color.colorPrimaryDark);
        final int[] colors = {color1,color2,color3};

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position < (adapter.getCount() -1) && position < (colors.length - 1)) {

                    background.setBackgroundColor((Integer) ArgbEvaluator.getInstance().evaluate(positionOffset, colors[position], colors[position + 1]));

                } else {

                    background.setBackgroundColor(colors[colors.length - 1]);

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseUser = mAuth.getCurrentUser();

        //User is not logged in
        if(firebaseUser == null){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            Intent startIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(startIntent);
            finish();
            return;
        }

        getWindow().setNavigationBarColor(getResources().getColor(R.color.darkDarkPrimary));

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    //profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    //Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("name", user.getUsername()).apply();
                editor.putString("year", user.getYear()).apply();
                editor.putString("email", firebaseUser.getEmail()).apply();
                String[] myData = user.getSubjects().split("/");
                Set<String> sets = new HashSet<>();
                for (String s: myData) {
                    sets.add(s);
                    //editor.putStringSet("subjects", )
                }
                editor.putStringSet("subjects", sets);
                /*Set <String> set = preferences.getStringSet("subjects", null);
                if(set != null) {
                    List<String> list = new ArrayList<String>(set);
                    for (String s : list) {
                        Log.println(Log.WARN, "JACKDEBUG", "BOO:" + s);
                    }
                }*/


                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
