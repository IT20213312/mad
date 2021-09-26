package com.example.car_mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookUpdate extends AppCompatActivity {
    TextView mLocation,mDate,mTime,mDays,mTotalAmount;
    Button btnUpdate,btnDelete;
    String getLocation, getDate, getTime, getDays, getTotal;
    DatabaseReference databaseReference;
    double total;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    int tHour,tMinute;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_update);

        mLocation = findViewById(R.id.et_location);
        mDate = findViewById(R.id.et_Date);
        mTime = findViewById(R.id.et_time);
        mDays = findViewById(R.id.et_days);
        mTotalAmount = findViewById(R.id.et_totAmount);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        databaseReference = FirebaseDatabase.getInstance().getReference("book-car");

        Intent intent = getIntent();
        getLocation = intent.getStringExtra("location");
        getDate = intent.getStringExtra("date");
        getTime = intent.getStringExtra("time");
        getDays = intent.getStringExtra("day");
        getTotal = intent.getStringExtra("total");


        mLocation.setText(getLocation);
        mDate.setText(getDate);
        mTime.setText(getTime);
        mDays.setText(getDays);
        mTotalAmount.setText(getTotal);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateDatabase();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFunction();

            }
        });

        //EditText Autofill
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                calculate();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

        };
        mDays.addTextChangedListener(textWatcher);

        //Date Choose picker
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        BookUpdate.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy:" + month + "/" + day + "/" + year);
                String birthday = day + "/" + month + "/" + year;
                mDate.setText(birthday);
            }
        };

        //Time Picker
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        BookUpdate.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                tHour =  i;
                                tMinute = i1;
                                String time = tHour + ":" + tMinute;
                                SimpleDateFormat f24hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date  = f24hours.parse(time);
                                    SimpleDateFormat f12hours = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );
                                    mTime.setText(f12hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } ,12,0,false

                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(tHour,tMinute);
                timePickerDialog.show();
            }
        });

    }

    private void calculate() {
        double daysInt = 1;

        if (mDays != null)
            daysInt = Double.parseDouble(!mDays.getText().toString().equals("") ?
                    mDays.getText().toString() : "0");

        total = daysInt * 500.0;
        String textResult = "Rs."+ total;
        mTotalAmount.setText(textResult);
    }

    private void deleteFunction() {
        String currentLocation = getLocation;
        ShowDeleteDialog(currentLocation);
    }

    private void ShowDeleteDialog(String currentLocation) {
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(BookUpdate.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this Booked Item?");
        //Yes Button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Query mquery = databaseReference.orderByChild("location").equalTo(currentLocation);
                mquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(BookUpdate.this,"Product Deleted...",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BookUpdate.this, BookList.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {
                        Toast.makeText(BookUpdate.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //No button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    private void updateDatabase() {
        String locationS = mLocation.getText().toString();
        String dateS = mDate.getText().toString();
        String timeS = mTime.getText().toString();
        String daysS = mDays.getText().toString();
        String totalS = mTotalAmount.getText().toString();

        Query query = databaseReference.orderByChild("location").equalTo(getLocation);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //Update Data
                    ds.getRef().child("location").setValue(locationS);
                    ds.getRef().child("date").setValue(dateS);
                    ds.getRef().child("time").setValue(timeS);
                    ds.getRef().child("days").setValue(daysS);
                    ds.getRef().child("total").setValue(totalS);

                }
                Toast.makeText(BookUpdate.this, "Updating Successful..", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BookUpdate.this, BookList.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}