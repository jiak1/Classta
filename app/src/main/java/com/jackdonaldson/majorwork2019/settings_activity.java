package com.jackdonaldson.majorwork2019;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.jackdonaldson.majorwork2019.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

public class settings_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            Fragment preferenceFragment = new SettingsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.settings, preferenceFragment);
            ft.commit();
        }

        /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

        private static final int IMAGE_REQUEST = 1;
        private Uri imageUri;
        private StorageTask uploadTask;

        DatabaseReference reference;
        FirebaseUser firebaseUser;
        StorageReference storageReference;
        FirebaseAuth mAuth;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            storageReference = FirebaseStorage.getInstance().getReference("uploads");

            setPreferencesFromResource(R.xml.root_preferences, rootKey);


        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();

            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, final String key)
        {
            Log.println(Log.WARN,"JACKDEBUG","11111111111"+key);
            String name = sharedPreferences.getString("name", "NULL");
            final String email = sharedPreferences.getString("email", "NULL");
            if (name.equals("NULL") == false && key.equals("name")) {

                Map<String,Object> hashMap = new HashMap<>();
                hashMap.put("username",name);
                hashMap.put("search",name.toLowerCase());
                Log.println(Log.WARN,"JACKDEBUG","nnnnnn");
                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(),"Updated Profile Display Name",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(),"Error: "+task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else if(email.equals("NULL") == false && key.equals("email")){


                LinearLayout container = new LinearLayout(getContext());
                container.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(16, 4, 16, 4);

                final EditText input = new EditText(getContext());

                input.setLayoutParams(lp);
                input.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setHint("Password");
                input.setLines(1);
                input.setMaxLines(1);

                container.addView(input, lp);

                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Password")
                        .setMessage("In order to change your email you must re-enter your password.")
                        .setView(container)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(firebaseUser.getEmail(), value.toString());
                                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Updated Email Address", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                        EditTextPreference p = (EditTextPreference) findPreference(key);
                                                        p.setText(firebaseUser.getEmail());
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            EditTextPreference p = (EditTextPreference) findPreference(key);
                                            p.setText(firebaseUser.getEmail());
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();

            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            if (preference.getKey().equals("logout")) {

                FirebaseUser currentUser = mAuth.getCurrentUser();

                //User is logged in sign them out
                if(currentUser != null){
                    mAuth.signOut();
                }

                Intent startIntent = new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startIntent);
                getActivity().finish();
                return true;
            }else if(preference.getKey().equals("profile")){
                openImage();
            }
            return false;
        }

        private void openImage(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST);
        }

        private String getFileExtension(Uri uri){
            ContentResolver contentResolver = getContext().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }

        private void uploadImage(){
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("Uploading");
            pd.show();

            if(imageUri != null){
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

                uploadTask = fileReference.putFile(imageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){

                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception{
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();

                            reference  = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL",mUri);
                            reference.updateChildren(map);

                            Toast.makeText(getContext(),"Updated Profile Image",Toast.LENGTH_LONG).show();

                            pd.dismiss();
                        }else{
                            Toast.makeText(getContext(),"Error: "+task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }else{
                Toast.makeText(getContext(),"No image selected",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
                imageUri = data.getData();

                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_SHORT).show();
                }else{
                    uploadImage();
                }
            }
        }
    }
}