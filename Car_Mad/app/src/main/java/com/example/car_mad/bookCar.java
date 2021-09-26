package com.example.car_mad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class bookCar extends AppCompatActivity {
    EditText etLocation,etDate,etTime,etDays,etTotalAmount;
    Button btnBook;
    DatabaseReference databaseReference;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public static final String TAG = "TAG";
    int tHour,tMinute;
    double total;
    BookCarModel bookCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_car);

        etLocation = findViewById(R.id.et_location);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etDays = findViewById(R.id.et_days);
        etTotalAmount = findViewById(R.id.et_totAmount);
        btnBook = findViewById(R.id.btn_book);

        bookCar = new BookCarModel();
        databaseReference = FirebaseDatabase.getInstance().getReference("book-car");

        //Date Choose picker
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        bookCar.this,
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
                etDate.setText(birthday);
            }
        };

        //Time Picker
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        bookCar.this,
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
                                    etTime.setText(f12hours.format(date));
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
        etDays.addTextChangedListener(textWatcher);


        //save
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(etLocation.getText().toString())|| TextUtils.isEmpty(etDate.getText().toString()) || TextUtils.isEmpty(etTime.getText().toString()) || TextUtils.isEmpty(etDays.getText().toString()) || TextUtils.isEmpty(etTotalAmount.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Please Fill All the fields",Toast.LENGTH_SHORT).show();
                }

                else {

                    Log.d("nav", "test");
                    Intent intent1 = new Intent(bookCar.this, BookList.class);
                    startActivity(intent1);

                    //myRef = FirebaseDatabase.getInstance().getReference().child("Book-car");

                    bookCar.setLocation(etLocation.getText().toString().trim());
                    bookCar.setDate(etDate.getText().toString().trim());
                    bookCar.setTime(etTime.getText().toString().trim());
                    bookCar.setDays(etDays.getText().toString().trim());
                    bookCar.setTotal(etTotalAmount.getText().toString().trim());



                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(bookCar);

                    Toast.makeText(getApplicationContext(), "Booking Success", Toast.LENGTH_LONG).show();
                    clearData();


                }}
        });

    }

    private void clearData() {
        etLocation.setText(null);
        etDate.setText(null);
        etTime.setText(null);
        etDays.setText(null);
        etTotalAmount.setText(null);
    }

    private void calculate() {
        double daysInt = 1;

        if (etDays != null)
            daysInt = Double.parseDouble(!etDays.getText().toString().equals("") ?
                    etDays.getText().toString() : "0");

        total = daysInt * 500.0;
        String textResult = "Rs."+ total;
        etTotalAmount.setText(textResult);
    }
}