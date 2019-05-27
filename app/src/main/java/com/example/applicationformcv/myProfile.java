package com.example.applicationformcv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.aviran.cookiebar2.CookieBar;

import java.util.concurrent.TimeUnit;

public class myProfile extends AppCompatActivity {

    private static final String TAG = "MY-APP";

    AlertDialog alertDialog;

    CookieBar.Builder cookieBar;

    TextView textUname, textPhone, textEmail;

    FrameLayout frameUname, frameNumber, frameEmail, frameNpswd, frameCpswd;
    CardView myprofile, myprofiledetails, update, headingCard, changePassword;
    LinearLayout linearLayoutButton;
    EditText uname, number, email, npswd, cpswd;
    Button change;

    ImageButton usernameUpdate, emailUpdate, phoneUpdate;

    TextInputLayout newPswdlayout, confirmPswdlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        myprofile = findViewById(R.id.myprofile);
        myprofiledetails = findViewById(R.id.myprofileDetails);
        update = findViewById(R.id.myprofileEdit2);
        headingCard = findViewById(R.id.cardEdit);
        setTitle(R.string.title_profile_epanjeeyan);

        textUname = findViewById(R.id.myprofName);
        textPhone = findViewById(R.id.myprofNumber);
        textEmail = findViewById(R.id.myprofEmail);

        uname = findViewById(R.id.username);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        npswd = findViewById(R.id.npassword);
        cpswd = findViewById(R.id.cpassword);

        change = findViewById(R.id.btnChange);

        frameUname = findViewById(R.id.frameUname);
        frameNumber = findViewById(R.id.frameNumber);
        frameEmail = findViewById(R.id.frameEmail);
        changePassword = findViewById(R.id.cardChangePassword);
        frameNpswd = findViewById(R.id.frameNpswd);
        frameCpswd = findViewById(R.id.frameCpswd);

        linearLayoutButton = findViewById(R.id.linearLayoutButton);

        usernameUpdate = findViewById(R.id.update_user_name_button);
        emailUpdate = findViewById(R.id.update_email_button);
        phoneUpdate = findViewById(R.id.update_num_button);

        newPswdlayout = findViewById(R.id.npasswordWrapper);
        confirmPswdlayout = findViewById(R.id.cpasswordWrapper);


        // set up the initial UI
        updateUI();

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        ProgressBar progressBar = view.findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);

        alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create();


        cookieBar = CookieBar.build(myProfile.this)
                .setTitle("Network Error")
                .setTitleColor(android.R.color.white)
                .setBackgroundColor(R.color.colorPrimary)
                .setIcon(R.drawable.ic_icon)
                .setEnableAutoDismiss(true)
                .setCookiePosition(CookieBar.TOP)
                .setSwipeToDismiss(true);
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
                uname.setText(R.string.myprofName);
                textUname.setText(R.string.myprofName);
            } else {
                uname.setText(name);
                textUname.setText(name);
            }

            if (e == null || e.isEmpty()) {
                frameNumber.setVisibility(View.VISIBLE);
                number.setText(p);
                textPhone.setText("\t\t" + p);
                textEmail.setVisibility(View.GONE);
            } else {
                email.setText(e);
                textEmail.setText("\t\t" + e);
                textPhone.setVisibility(View.GONE);
                frameEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
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

        alertDialog.show();

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
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }


                        if (task.isSuccessful()) {
                            Log.d(TAG, "Name updated");
                            usernameUpdate.setImageResource(R.drawable.ic_done);
                            Toast.makeText(myProfile.this, "Username updated!", Toast.LENGTH_SHORT).show();

                            //updateUI();
                        } else {
                            cookieBar.setMessage(task.getException().getMessage());
                            cookieBar.show();
                        }
                    }
                });
    }


    public void updateUserPhone(View view) {

        alertDialog.show();

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
                                        if (alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }

                                        if (task.isSuccessful()) {
                                            // successfully updated
                                            phoneUpdate.setImageResource(R.drawable.ic_done);
                                            Toast.makeText(myProfile.this, "Mobile number updated!", Toast.LENGTH_SHORT).show();
                                        } else {

                                            if (alertDialog.isShowing()) {
                                                alertDialog.dismiss();
                                            }

                                            cookieBar.setMessage(task.getException().getMessage());
                                            cookieBar.show();

                                            Log.d(TAG, task.getException().getMessage());
                                        }
                                    }
                                });

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        cookieBar.setMessage(e.getMessage());
                        cookieBar.show();
                    }
                });
    }

    public void updateUserEmail(View view) {

        alertDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailString = email.getText().toString();

        // show the dialogSheet

        // update email
        user.updateEmail(emailString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        if (task.isSuccessful()) {
                            //updateUI();
                            emailUpdate.setImageResource(R.drawable.ic_done);
                            Toast.makeText(myProfile.this, "Email updated!", Toast.LENGTH_SHORT).show();

                        } else {
                            cookieBar.setMessage(task.getException().getMessage());
                            cookieBar.show();
                        }
                    }
                });
    }


    public void updatePassword(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String pwd = npswd.getText().toString().trim();
        String c_pwd = cpswd.getText().toString().trim();

        if (pwd.isEmpty() || c_pwd.isEmpty() || !pwd.equals(c_pwd)) {
            newPswdlayout.setError("Enter a valid password");
            confirmPswdlayout.setError("Enter a valid password");
            return;
        }

        // show the dialogSheet
        alertDialog.show();


        // update password
        user.updatePassword(pwd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        if (task.isSuccessful()) {
                            //updateUI();
                            Log.d(TAG, "password updated");
                        } else {
                            cookieBar.setMessage(task.getException().getMessage());
                            cookieBar.show();
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

