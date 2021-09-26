package com.example.car_mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {
    public static final String TAG = "TAG";
    Button signUpBtn, signInBtn;
    EditText etName, etEmail, etPwd, etConfirmPwd;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpBtn = findViewById(R.id.btn_signUp);
        signInBtn = findViewById(R.id.btn_signIn);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_emailId);
        etPwd = findViewById(R.id.et_password);
        etConfirmPwd = findViewById(R.id.et_confirmPassword);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Already user loggedIn
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


        //Signup Btn
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString().trim();
                final String password = etPwd.getText().toString().trim();
                final String name = etName.getText().toString().trim();
                final String confirmPwd = etConfirmPwd.getText().toString().trim();

                //set the error if the fields are empty
                if (TextUtils.isEmpty(name)) {
                    etName.setError("Name is Mandatory");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPwd.setError("Password is Mandatory");
                    return;
                }
                if (TextUtils.isEmpty(confirmPwd)) {
                    etConfirmPwd.setError("Confirm Password is Mandatory");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is Mandatory");
                    return;
                }
                if (password.length() < 6) {
                    etPwd.setError("Password must be >= 6 Characters");
                    return;
                }
                if (!password.equals(confirmPwd)) {
                    Toast.makeText(signUp.this, "Both Passwords must be equal..", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Register use in firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(signUp.this, "Signup Successfully...", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("Users-Data").document(userID);

                            //create Hashmap object as user
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("Email", email);
                            user.put("Password", password);
                            user.put("ConfirmPassword", confirmPwd);


                            //check database
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is Created is for" + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            //Intent
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(signUp.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                //set the fields empty
                etName.setText(null);
                etEmail.setText(null);
                etPwd.setText(null);
                etConfirmPwd.setText(null);

            }
        });
    }
}