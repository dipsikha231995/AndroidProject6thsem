package com.example.applicationformcv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class myProfile extends AppCompatActivity {

    private static final String TAG = "MY-APP";

    FrameLayout frameUname, frameNumber, frameEmail, frameNpswd, frameCpswd;
    CardView myprofile, myprofiledetails, update, headingCard;
    LinearLayout linearLayoutButton;
    EditText uname, number, email, npswd, cpswd;
    Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        myprofile = findViewById(R.id.myprofile);
        myprofiledetails = findViewById(R.id.myprofileDetails);
        update = findViewById(R.id.myprofileEdit2);
        headingCard = findViewById(R.id.cardEdit);
        setTitle(R.string.title_profile_epanjeeyan);

        uname = findViewById(R.id.username);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        npswd = findViewById(R.id.npassword);
        cpswd = findViewById(R.id.cpassword);

        change = findViewById(R.id.btnChange);

        frameUname = findViewById(R.id.frameUname);
        frameNumber = findViewById(R.id.frameNumber);
        frameEmail = findViewById(R.id.frameEmail);
        frameNpswd = findViewById(R.id.frameNpswd);
        frameCpswd = findViewById(R.id.frameCpswd);

        linearLayoutButton = findViewById(R.id.linearLayoutButton);

        // set up the initial UI
        updateUI();

    }


    // set up the initial ui and update it when required
    private void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is signed in
            String name = user.getDisplayName();
            String e = user.getEmail();
            String p = user.getPhoneNumber();

            if (name == null || name.isEmpty()) {
                uname.setText("e-PANJEEYAN user");
            } else {
                uname.setText(name);
            }

            if (e == null || e.isEmpty()) {
                frameNumber.setVisibility(View.VISIBLE);
                number.setText(p);
            } else {
                email.setText(e);
                frameEmail.setVisibility(View.VISIBLE);
                frameNpswd.setVisibility(View.VISIBLE);
                frameCpswd.setVisibility(View.VISIBLE);
                linearLayoutButton.setVisibility(View.VISIBLE);

            }
        } else {
            Log.d(TAG, "updateUI: " + false);
            // No user is signed in
            finish();
        }
    }


    public void updateUserName(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = uname.getText().toString();

        // show the dialogSheet
        //dialog.show();

        // update display name
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //updateUI();
                        } else {
                            Toast.makeText(myProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void updateUserPhone(View view) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String phoneNumber = number.getText().toString().trim();
        if (!phoneNumber.startsWith("+91")) {
            phoneNumber = "+91" + phoneNumber;
        }

        // show the dialogSheet

        // authenticate the new phone number
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                myProfile.this,               // Activity (for callback binding)

                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {       // OnVerificationStateChangedCallbacks
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Log.d(TAG, "auto verification completed");

                        // now update the phone number
                        user.updatePhoneNumber(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // successfully updated
                                            Log.d(TAG, "phone number updated");
                                        } else {
                                            // display error message
                                            Log.d(TAG, task.getException().getMessage());
                                        }
                                    }
                                });

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // display error message
                        Log.d(TAG, e.getMessage());
                    }
                });
    }

    public void updateUserEmail(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailString = email.getText().toString();

        // show the dialogSheet

        // update email
        user.updateEmail(emailString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //updateUI();
                            Log.d(TAG, "email updated");

                        } else {
                            Log.d(TAG, task.getException().getMessage());
                        }
                    }
                });
    }


    public void updatePassword(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String pwd = npswd.getText().toString().trim();
        String c_pwd = cpswd.getText().toString().trim();

        if (pwd.isEmpty() || c_pwd.isEmpty() || !pwd.equals(c_pwd)) {
            Toast.makeText(this, "Enter a valid password", Toast.LENGTH_SHORT).show();
            return;
        }

        // show the dialogSheet


        // update password
        user.updatePassword(pwd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //updateUI();
                            Log.d(TAG, "password updated");
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                        }
                    }
                });

    }


    public void editProfile(View view) {

        myprofile.setVisibility(View.GONE);
        myprofiledetails.setVisibility(View.GONE);
        update.setVisibility(View.VISIBLE);
        headingCard.setVisibility(View.VISIBLE);

    }
}

