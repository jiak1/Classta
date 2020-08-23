package com.jackdonaldson.majorwork2019.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jackdonaldson.majorwork2019.MessageActivity;
import com.jackdonaldson.majorwork2019.Notifications.Client;
import com.jackdonaldson.majorwork2019.Notifications.Data;
import com.jackdonaldson.majorwork2019.Notifications.MyResponse;
import com.jackdonaldson.majorwork2019.Notifications.Sender;
import com.jackdonaldson.majorwork2019.Notifications.Token;
import com.jackdonaldson.majorwork2019.R;
import com.jackdonaldson.majorwork2019.fragment.APIService;
import com.jackdonaldson.majorwork2019.models.User;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage {

    private boolean notify = false;
    private FirebaseUser fuser;
    APIService apiService;

    public void sendMessage(String sender, final String userid, String message, boolean not,final Context context){

        notify = not;
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //final String userid = intent.getStringExtra("userid");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",userid);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);

        //Add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Log.println(Log.WARN,"JACKDEBUG","HERE1");
                if(notify) {
                    Log.println(Log.WARN,"JACKDEBUG","HERE2");
                    sendNotification(userid, user.getUsername(), msg,userid,context);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message, final String userid,final Context context){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.println(Log.WARN,"JACKDEBUG","HERE3");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.icon,message,username,userid);
                    Sender sender = new Sender(data,token.getToken());
                    Log.println(Log.WARN,"JACKDEBUG","HERE4");

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    Log.println(Log.WARN,"JACKDEBUG","HERE5");
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(context,"Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
