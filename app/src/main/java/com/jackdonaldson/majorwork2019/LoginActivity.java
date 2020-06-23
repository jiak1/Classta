package com.jackdonaldson.majorwork2019;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView mEmail;
    private TextView mPassword;
    private Button mSignInButton;
    private ProgressDialog LoadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoadingBar = new ProgressDialog(this);
        LoadingBar.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        //Make title a gradient
        TextView secondTextView = findViewById(R.id.title);
        float width = secondTextView.getPaint().measureText(secondTextView.getText().toString());

        secondTextView.setTextColor(Color.parseColor("#4f55a3"));
        LinearGradient textShader = new LinearGradient(0f, 0f, width, 0f, Color.parseColor("#4f55a3"), Color.parseColor("#666efd"), Shader.TileMode.MIRROR);
        secondTextView.getPaint().setShader(textShader);

        findViewById(R.id.logInTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(startIntent);
            }
        });

        mEmail = (TextView) findViewById(R.id.signUp_email_txt);
        mPassword = (TextView) findViewById(R.id.login_password_txt);
        mSignInButton = (Button) findViewById(R.id.login_button);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingBar.setMessage("Logging in...");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                login_user(email,password);
            }
        });
    }

    void login_user(String email, String password){
        LoadingBar.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            LoadingBar.hide();
                            Intent startIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(startIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Error: "+task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            LoadingBar.hide();
                        }

                    }
                });
    }

}
