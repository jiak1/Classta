package com.jackdonaldson.majorwork2019;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.jackdonaldson.majorwork2019.fragment.ProfileFragment;
import com.jackdonaldson.majorwork2019.models.User;
import com.mikhaellopez.circularimageview.CircularImageView;

public class otherProfile extends AppCompatActivity {

    CircularImageView image_profile;
    TextView username;
    TextView sCount;
    TextView yCount;
    TextView subjectsText;
    TextView pLoc;

    String userID;

    DatabaseReference reference;

    StorageReference storageReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        image_profile = findViewById(R.id.image_profile);
        username = findViewById(R.id.username);
        sCount =findViewById(R.id.SCount);
        yCount = findViewById(R.id.YearCount);
        subjectsText = findViewById(R.id.subjectTitleText);
        pLoc = findViewById(R.id.profileLocation);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        Intent intent = getIntent();
        userID = intent.getStringExtra("userid");
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if(user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.main_icon);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
                }
                yCount.setText(user.getYear());
                pLoc.setText(user.getLocation());

                String[] myData = user.getSubjects().split("/");
                String fin = "";
                int subCount = 0;
                for (String s: myData) {
                    if(fin.equals("")){
                        fin = s;
                    }else{
                        fin = fin+"\n"+s;
                    }
                    subCount++;
                }
                if(fin.equals("")){
                    fin = "No Subjects Selected";
                }
                subjectsText.setText(fin);
                sCount.setText(Integer.toString(subCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(otherProfile.this, MessageActivity.class);
                intent.putExtra("userid",userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

}
