package com.jackdonaldson.majorwork2019.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;
import com.jackdonaldson.majorwork2019.R;
import com.jackdonaldson.majorwork2019.models.User;
import com.jackdonaldson.majorwork2019.settings_activity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BaseFragment{

    CircularImageView image_profile;
    TextView username;
    TextView sCount;
    TextView yCount;
    TextView subjectsText;
    TextView pLoc;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    public static ProfileFragment create(){
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);

        image_profile = rootView.findViewById(R.id.image_profile);
        username = rootView.findViewById(R.id.username);
        sCount = rootView.findViewById(R.id.SCount);
        yCount = rootView.findViewById(R.id.YearCount);
        subjectsText = rootView.findViewById(R.id.subjectTitleText);
        pLoc = rootView.findViewById(R.id.profileLocation);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if(user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.main_icon);
                }else{
                    Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
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

        ImageButton settingsButton = rootView.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getActivity(), settings_activity.class);
                startActivity(startIntent);
            }
        });

        return rootView;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }
}
