package com.jackdonaldson.majorwork2019;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jackdonaldson.majorwork2019.adapter.UserAdapter;
import com.jackdonaldson.majorwork2019.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchUsers extends AppCompatActivity {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;
    public boolean[] checkedItems;
    public String[] itemVals;
    EditText search_users;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        checkedItems =new boolean[34];

        for (int i = 0; i < 34; i++) {
            checkedItems[i] = false;
        }
        itemVals = getResources().getStringArray(R.array.subject_entries);

        context = this;

        recyclerView = this.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUsers = new ArrayList<>();

        readUsers();

        search_users = this.findViewById(R.id.searchUsersInput);

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void backButton(View v){
        Intent startIntent = new Intent(this, HomeActivity.class);
        startActivity(startIntent);
        finish();
    }

    public void subjectClick(View v){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchUsers.this);
            alertDialog.setTitle("Subjects");
            alertDialog.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    searchUsers(search_users.getText().toString());
                }
            });

            alertDialog.setMultiChoiceItems(itemVals,checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    checkedItems[which] = isChecked;
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();

    }


    private void searchUsers(String s){
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query;
        if(s.isEmpty() == false) {
             query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(s).endAt(s + "\uf8ff");
        }else{
             query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search");
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    int i = 0;
                    boolean failed = false;
                    for (Boolean _b : checkedItems){
                        if(_b){
                            if(user.getSubjects().contains(itemVals[i]) == false){
                                failed = true;
                            }
                        }
                        i++;
                    }
                    if(failed == false) {
                        assert user != null;
                        assert fuser != null;
                        if (!user.getId().equals(fuser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(SearchUsers.this,mUsers,false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_users.getText().toString().equals("")) {

                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }

                    userAdapter = new UserAdapter(getApplicationContext(), mUsers, false);
                    recyclerView.setAdapter((userAdapter));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
