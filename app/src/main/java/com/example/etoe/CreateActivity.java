package com.example.etoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        TimePicker timePicker = findViewById(R.id.time_picker);
        EditText ori = findViewById(R.id.ori);
        EditText dest = findViewById(R.id.dest);
        Button btStart = findViewById(R.id.button_start);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ori.getText().toString().equals("")) {
                    Toast.makeText(CreateActivity.this, "출발지를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dest.getText().toString().equals("")) {
                    Toast.makeText(CreateActivity.this, "도착지를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                builder.setTitle("스케줄을 생성하시겠습니까?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CreateActivity.this, ScheduleActivity.class);

                        intent.putExtra("hour", timePicker.getHour());
                        intent.putExtra("min", timePicker.getMinute());
                        intent.putExtra("ori", ori.getText().toString());
                        intent.putExtra("dest", dest.getText().toString());
                        intent.putExtra("title", "Schedule");
                        intent.putExtra("isNew", true);

                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

                builder.show();
            }

        });
    }
}
