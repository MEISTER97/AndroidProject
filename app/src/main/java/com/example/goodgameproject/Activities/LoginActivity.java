package com.example.goodgameproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goodgameproject.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private MaterialButton buttonLog;
    private  ProgressBar progressBar;
    private MaterialButton buttonLogPhone,btn_registerNow;


    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private static final int SIGN_IN_DELAY_MS = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        findViews();
        initViews();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    transactToMainActivity();
                }
            }
        }, SIGN_IN_DELAY_MS); // 1000 milliseconds = 1 second
    }

    private void findViews()
    {
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);
        buttonLog=findViewById(R.id.btn_login);
        progressBar=findViewById(R.id.progressBar);
        btn_registerNow=findViewById(R.id.btn_registerNow);
        buttonLogPhone=findViewById(R.id.btn_moveToPhoneRegister);
    }

    private void initViews() {

        btn_registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), RegisterPhoneActivity.class);
               startActivity(intent);
               finish();

            }
        });

        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password= String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


    }

    private void transactToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );



    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            transactToMainActivity();
        } else {
            // Sign in failed. If response is null the user canceled the sign-in flow using the back button.
            if (response == null) {
                Log.e(TAG, "Sign-in canceled by user.");
                Toast.makeText(this, "Sign-in canceled", Toast.LENGTH_SHORT).show();
            } else {
                int errorCode = Objects.requireNonNull(response.getError()).getErrorCode();
                handleSignInError(errorCode);
            }
        }
    }


            private void handleSignInError(int errorCode) {
        switch (errorCode) {
            case ErrorCodes.NO_NETWORK:
                Log.e(TAG, "No network connection");
                Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show();
                break;
            case ErrorCodes.UNKNOWN_ERROR:
                Log.e(TAG, "Unknown error occurred");
                Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_LONG).show();
                break;

            default:
                Log.e(TAG, "Error code: " + errorCode);
                Toast.makeText(this, "Sign-in failed with error code: " + errorCode, Toast.LENGTH_LONG).show();
                break;
        }
    }




}