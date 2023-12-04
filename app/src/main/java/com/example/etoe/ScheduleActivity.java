package com.example.etoe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private String ori, dest;
    private String date, time;
    private String title;
    private int hour, min;
    private int latitude, longitude;
    private String sky, temperature;
    private EditText textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        hour = intent.getIntExtra("hour", 0);
        min = intent.getIntExtra("min", 0);
        ori = intent.getStringExtra("ori");
        dest = intent.getStringExtra("dest");
        title = intent.getStringExtra("title");
        Boolean isNew = intent.getBooleanExtra("isNew", true);

        textTitle = findViewById(R.id.text_title);
        textTitle.setText(title);

        new Thread(() -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate now = LocalDate.now();
            date = now.format(formatter);
            time = String.format("%02d", hour) + "00";
            getCoordinate();

            WeatherData weatherData = new WeatherData();
            String[] data = new String[2];
            Boolean isNullData = false;
            try {
                data = weatherData.lookUpWeather(
                        date, time, String.valueOf(latitude), String.valueOf(longitude)).split(" ");
            } catch (Exception e) {
                try {
                    data = weatherData.lookUpWeather(
                            date, time, "55", "127").split(" ");
                } catch (Exception ex) {
                    e.printStackTrace();
                    isNullData = true;
                }
                e.printStackTrace();
            }
            TextView textWeather = findViewById(R.id.text_weather);
            TextView textTemp = findViewById(R.id.text_temp);
            TextView textCloth = findViewById(R.id.text_cloth);

            if(isNullData) {
                runOnUiThread(() -> {
                    textWeather.append("정보가 없습니다");
                    textTemp.append("정보가 없습니다");
                });
            }
            else {
                sky = data[0];
                temperature = data[1];

                runOnUiThread(() -> {
                    textWeather.append(sky);
                    textTemp.append(temperature);
                    textTemp.append("ºC");
                    textCloth.append(getClothes(temperature));
                });
            }
        }).start();

        TextView textTime = findViewById(R.id.text_time);
        TextView textOri = findViewById(R.id.text_ori);
        TextView textDest = findViewById(R.id.text_dest);
        textTime.append(timeFormat(hour, min));
        textOri.append(ori);
        textDest.append(dest);

        Button btPath = findViewById(R.id.button_path);
        btPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapWithRoute(ori, dest);
            }
        });

        Button btAlarm = findViewById(R.id.button_alarm);
        btAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                startActivity(intent);
            }
        });

        Button btSave = findViewById(R.id.button_save);
        if(!isNew) btSave.setVisibility(View.GONE);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
                builder.setMessage("저장하고 종료하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                saveData();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { }
                        });
                builder.create().show();
            }
        });
    }

    private void openMapWithRoute(String originAddress, String destinationAddress) {

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + Uri.encode(originAddress)
                        + "&daddr=" + Uri.encode(destinationAddress)));

        startActivity(intent);
    }

    private String getClothes(String temperature) {
        int temp = Integer.parseInt(temperature);

        if (temp >= 28) return "민소매, 반팔, 반바지, 치마";
        else if (temp >= 23) return "반팔, 얇은 셔츠, 반바지, 면바지";
        else if (temp >= 20) return "얇은 가디건, 긴팔티, 면바지, 청바지";
        else if (temp >= 17) return "얇은 니트, 가디건, 맨투맨, 얇은 자켓, 청바지";
        else if (temp >= 12) return "자켓, 가디건, 야상, 맨투맨, 니트, 스타킹, 청바지, 면바지";
        else if (temp >= 9) return "자켓, 트렌치 코트, 야상, 니트, 스타킹, 청바지, 면바지";
        else if (temp >= 5) return "코트 히트텍, 니트, 청바지, 레깅스";
        else return "패딩, 두꺼운 코트, 목도리, 기모 제품";
    }

    public static String timeFormat(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        return sdf.format(calendar.getTime());
    }

    private void getCoordinate() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( ScheduleActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (loc == null) {
            latitude = 55;
            longitude = 127;
            return;
        }
        latitude = (int) loc.getLatitude();
        longitude = (int) loc.getLongitude();
    }

    private void saveData(){
        List<ScheduleItem> itemList = loadData();
        title = textTitle.getText().toString();

        // 새로운 일정 추가
        ScheduleItem newItem = new ScheduleItem(hour, min, ori, dest, title);
        itemList.add(newItem);

        // SharedPreferences에 새로운 일정 목록 저장
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 새로운 일정 목록 저장
        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        editor.putString("scheduleItemList", json);

        // 변경사항 저장
        editor.apply();

        // MainActivity로 이동
        Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private List<ScheduleItem> loadData() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String json = preferences.getString("scheduleItemList", "");

        if (json.isEmpty()) {
            return new ArrayList<>(); // 저장된 일정이 없으면 빈 목록 반환
        }

        // Gson을 사용하여 JSON 문자열을 List<ScheduleItem> 객체로 변환
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScheduleItem>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
