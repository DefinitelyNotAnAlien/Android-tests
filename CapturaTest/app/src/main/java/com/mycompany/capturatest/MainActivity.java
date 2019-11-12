package com.mycompany.capturatest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add items to aircraft selector
        List<String> aircraft = Arrays.asList((getResources().getStringArray(R.array.aircraft_array)));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, aircraft);

        // Add items to Recycler View
        RecyclerView recyclerView = findViewById(R.id.logItems);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<LogItem> items = new ArrayList<>();
        for (int i = 1; i <= 3; i++)
            items.add(new LogItem(i));
        LogAdapter adapter = new LogAdapter(items);
        recyclerView.setAdapter(adapter);
    }
}
