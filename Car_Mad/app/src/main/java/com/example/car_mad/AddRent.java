package com.example.car_mad;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddRent extends AppCompatActivity {
    private EditText mBrand,mModel,mRegisNo,mDescription;
    private ImageView productImage;
    private Button btnRentCar;

    private Uri mImageUri;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rent);

        mBrand = findViewById(R.id.et_brand);
        mModel = findViewById(R.id.et_model);
        mRegisNo = findViewById(R.id.et_registrationNo);
        mDescription = findViewById(R.id.et_description);
        productImage = findViewById(R.id.productImage);
        btnRentCar = findViewById(R.id.btn_rentCar);

        storageReference = FirebaseStorage.getInstance().getReference("rent-car");
        databaseReference = FirebaseDatabase.getInstance().getReference("rent-car");

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        btnRentCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storageTask != null && storageTask.isInProgress()) {
                    Toast.makeText(AddRent.this, "Upload In Progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

            }
        });

    }
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        //You are provided with uri of the image . Take this uri and assign it to Picasso
                        //Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        mImageUri = uri.normalizeScheme();
                        Picasso.get().load(mImageUri).fit().into(productImage);
                    }
                });


        private  String getFileExtension(Uri uri){
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }

    private void uploadFile(){
        if(mImageUri != null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+ "."+ getFileExtension(mImageUri));
            storageTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(AddRent.this,"Car added to Rental",Toast.LENGTH_LONG).show();
                                    RentCarModel rentCar = new RentCarModel(mBrand.getText().toString().trim(),
                                            mModel.getText().toString().trim(),
                                            mRegisNo.getText().toString().trim(),
                                            mDescription.getText().toString().trim(),
                                            uri.toString());
                                    String uploadId = databaseReference.push().getKey();
                                    databaseReference.child(uploadId).setValue(rentCar);

                                }


                            });
                            startActivity(new Intent(AddRent.this, RentList.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddRent.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this,"Image not selected",Toast.LENGTH_SHORT).show();
        }
    }




}