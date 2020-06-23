package com.jackdonaldson.majorwork2019;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextView mDisplayName;
    private TextView mEmail;
    private TextView mPassword;
    private Button mCreateBtn;
    private ProgressDialog LoadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        LoadingBar = new ProgressDialog(this);
        LoadingBar.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        //Make title a gradient
        TextView secondTextView = findViewById(R.id.signUp_title);
        float width = secondTextView.getPaint().measureText(secondTextView.getText().toString());

        secondTextView.setTextColor(Color.parseColor("#4f55a3"));
        LinearGradient textShader = new LinearGradient(0f, 0f, width, 0f, Color.parseColor("#4f55a3"), Color.parseColor("#666efd"), Shader.TileMode.MIRROR);
        secondTextView.getPaint().setShader(textShader);

        findViewById(R.id.logInTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(startIntent);
                finish();
            }
        });

        mDisplayName = (TextView) findViewById(R.id.signup_fName_txt);
        mEmail = (TextView) findViewById(R.id.signUp_email_txt);
        mPassword = (TextView) findViewById(R.id.signUp_password_txt);
        mCreateBtn = (Button) findViewById(R.id.signUp_button);

        mCreateBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LoadingBar.setMessage("Creating account...");
                LoadingBar.show();
                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                register_user(display_name,email,password);
            }
        });
    }

    void register_user(final String display_name, String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",display_name);
                            hashMap.put("imageURL","default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            LoadingBar.hide();
                                            Intent startIntent = new Intent(SignUpActivity.this, HomeActivity.class);
                                            startActivity(startIntent);
                                            finish();
                                        }else{
                                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                            LoadingBar.hide();
                                        }
                                }
                            });

                        } else {
                            Toast.makeText(SignUpActivity.this, "Error: "+task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            LoadingBar.hide();
                        }

                        // ...
                    }
                });
    }
}