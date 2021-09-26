package com.example.car_mad;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {
    EditText mName,mEmail,mPassword,mConfirmPwd;
    Button btnUpdate,btnDelete;
    ImageView imgUserPic;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    String userId;
    private Uri mImageUri;
    StorageTask storageTask;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mName = findViewById(R.id.et_name);
        mEmail = findViewById(R.id.et_emailId);
        mPassword = findViewById(R.id.et_password);
        mConfirmPwd = findViewById(R.id.et_confirmPassword);
        imgUserPic = findViewById(R.id.imgUserProfile);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("user_image");
        //getCurrent userId for Read Data
        userId = fAuth.getCurrentUser().getUid();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMethod();

            }
        });

        imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");

            }
        });

        StorageReference profileRef = storageReference.child(fAuth.getCurrentUser().getUid() + "/profile.png");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgUserPic);
            }
        });

        //Read Data
        DocumentReference documentReference = fStore.collection("Users-Data").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                mName.setText(documentSnapshot.getString("Name"));
                mEmail.setText(documentSnapshot.getString("Email"));
                mPassword.setText(documentSnapshot.getString("Password"));
                mConfirmPwd.setText(documentSnapshot.getString("ConfirmPassword"));
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String password = mPassword.getText().toString();
                user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentRe = fStore.collection("Users-Data").document(user.getUid());

                        //update all fields
                        Map<String, Object> edited = new HashMap<>();

                        edited.put("Name", mName.getText().toString());
                        edited.put("Email", mEmail.getText().toString());
                        edited.put("Password", mPassword.getText().toString());
                        edited.put("ConfirmPassword", mConfirmPwd.getText().toString());

                        documentRe.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(profile.this, "Profile updated...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                finish();
                            }
                        });
                        Toast.makeText(profile.this, "Password is updated", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    //You are provided with uri of the image . Take this uri and assign it to Picasso
                    //Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //mImageUri = uri.normalizeScheme();
                    Picasso.get().load(uri).fit().into(imgUserPic);
                    uploadImageToCloud(uri);

                }
            });

    private void uploadImageToCloud(Uri uri) {
        // upload image to firebase storage
        StorageReference fileRef = storageReference.child(fAuth.getCurrentUser().getUid() + "/profile.png");
        storageTask = fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Picasso.get().load(mImageUri).into(imgUserPic);
                        Toast.makeText(profile.this,"Image uploaded",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMethod() {
        String currentImageURL = storageReference.child(fAuth.getCurrentUser().getUid() + "/profile.png").toString();;

        ShowDeleteDialogMethod(currentImageURL);
    }

    private void ShowDeleteDialogMethod(String currentImageURL) {
        userID = fAuth.getCurrentUser().getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this user?");
        //Yes
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                fStore.collection("Users-Data").document(userID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(profile.this,"User Deleted...",Toast.LENGTH_LONG).show();
                                //Delete Image From Storage
                                StorageReference mPictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentImageURL);
                                mPictureReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Toast.makeText(AdminProductUpdate.this,"Image Deleted",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(profile.this, Dashboard.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull  Exception e) {
                                        Toast.makeText(profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }


                        });
                    }
                });

            }
        });
        //No
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }
    }
