package com.example.etoe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CheckActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        ListView listView = findViewById(R.id.listView);

        List<ScheduleItem> itemList = loadData();

        // 어댑터 생성
        CustomAdapter adapter = new CustomAdapter(this, itemList);
        listView.setAdapter(adapter);
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
