package com.jackdonaldson.majorwork2019.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jackdonaldson.majorwork2019.MessageActivity;
import com.jackdonaldson.majorwork2019.R;
import com.jackdonaldson.majorwork2019.models.Chat;
import com.jackdonaldson.majorwork2019.models.User;
import com.jackdonaldson.majorwork2019.otherProfile;

import java.security.Timestamp;
import java.text.DateFormat;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isSearch;
    String theLastMessage;

    Long theLastTime;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isSearch){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isSearch = isSearch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profileImage.setImageResource(R.mipmap.main_icon);
        }else{
           Glide.with(mContext).load(user.getImageURL()).into(holder.profileImage);
        }

        if(isSearch){
            lastMessage(user.getId(),holder.last_msg,holder.last_msg_time);
        }else{
            //holder.last_msg.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            String[] myData = user.getSubjects().split("/");
            String fin = "";
            for (String s: myData) {
                if(fin.equals("")){
                    fin = s;
                }else{
                    fin = fin+", "+s;
                }
            }
            if(fin.equals("")){
                fin = "No Subjects Selected";
            }
            //holder.last_msg.setMaxLines(10);
            holder.last_msg.setText(fin);
            holder.last_msg_time.setVisibility(View.GONE);
        }

        if(isSearch){
            //Chat window

            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else{
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("userid",user.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });

        }else{
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, otherProfile.class);
                    intent.putExtra("userid",user.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profileImage;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private TextView last_msg_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            last_msg = itemView.findViewById(R.id.last_msg);
            profileImage = itemView.findViewById(R.id.image_profile);
            last_msg_time = itemView.findViewById(R.id.last_time);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }

    //Check for last message
    private void lastMessage(final String userid, final TextView last_msg, final TextView last_msg_time){
        theLastMessage = "default";
        theLastTime = null;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                        theLastTime = chat.getTime();
                        //set last message bold if reciever didn't sees it.
                        if (!chat.isIsseen() && firebaseUser.getUid().equals(chat.getReceiver())){
                            last_msg.setTypeface(null, Typeface.BOLD);
                        }else {
                            last_msg.setTypeface(null,Typeface.NORMAL);
                        }
                    }
                }

                switch(theLastMessage){
                    case "default":
                        //last_msg.setText("No Message");
                        last_msg_time.setText("");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        if(DateUtils.isToday(theLastTime)) {
                            last_msg_time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(theLastTime));
                        }else{
                            last_msg_time.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(theLastTime));
                        }
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
