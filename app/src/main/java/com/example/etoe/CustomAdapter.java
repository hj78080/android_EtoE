package com.example.etoe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<ScheduleItem> itemList;

    public CustomAdapter(Context context, List<ScheduleItem> itemlist) {
        this.context = context;
        this.itemList = itemlist;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list, null);
        }

        // 텍스트 설정
        TextView textViewItem = view.findViewById(R.id.textViewItem);
        textViewItem.setText(itemList.get(position).getTitle());

        // 삭제 버튼 설정
        Button buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제 하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                itemList.remove(position);
                                notifyDataSetChanged();

                                saveItemListToJSON(itemList);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { }
                        });
                builder.create().show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleItem selectedItem = itemList.get(position);

                Intent intent = new Intent(context, ScheduleActivity.class);

                intent.putExtra("hour", selectedItem.getHour());
                intent.putExtra("min", selectedItem.getMin());
                intent.putExtra("ori", selectedItem.getOri());
                intent.putExtra("dest", selectedItem.getDest());
                intent.putExtra("title", selectedItem.getTitle());
                intent.putExtra("isNew", false);

                context.startActivity(intent);
            }
        });

        return view;
    }

    private void saveItemListToJSON(List<ScheduleItem> itemList) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Gson을 사용하여 itemList을 JSON 문자열로 변환
        Gson gson = new Gson();
        String json = gson.toJson(itemList);

        // JSON 문자열을 SharedPreferences에 저장
        editor.putString("scheduleItemList", json);
        editor.apply();
    }
}