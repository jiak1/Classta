package com.jackdonaldson.majorwork2019.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jackdonaldson.majorwork2019.R;
import com.jackdonaldson.majorwork2019.adapter.UserAdapter;
import com.jackdonaldson.majorwork2019.models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment{

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    EditText search_users;

    public static SearchFragment create(){
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search,container,false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        readUsers();

        search_users = rootView.findViewById(R.id.search_users);
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

        return rootView;
    }

    private void searchUsers(String s){
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);

                        assert user != null;
                        assert fuser != null;
                        if(!user.getId().equals(fuser.getUid())){
                            mUsers.add(user);
                        }
                    }

                    userAdapter = new UserAdapter(getContext(),mUsers,false);
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

                    userAdapter = new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter((userAdapter));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_search;
    }

    @Override
    public void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }
}
