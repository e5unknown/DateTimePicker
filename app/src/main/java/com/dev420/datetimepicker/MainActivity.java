package com.dev420.datetimepicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickOpenDialog2(View view) {
        DialogFragment dialog = new DateTimePickerDialog();
        dialog.show(getSupportFragmentManager(), "dateTimePicker");
    }


}
