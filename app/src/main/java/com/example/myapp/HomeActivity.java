package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private NavigationView navigationView;
    private EditText deviceName, deviceValue;
    private Button addDeviceBtn;
    private ListView deviceList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout_home);
        menuIcon = findViewById(R.id.menu_home);
        navigationView = findViewById(R.id.navigation_view_home);

        deviceName = findViewById(R.id.device_name);
        deviceValue = findViewById(R.id.device_value);
        addDeviceBtn = findViewById(R.id.add_device);
        deviceList = findViewById(R.id.list_devices);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        ArrayList<String> deviceArrayList = new ArrayList<>();
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, R.layout.list_item, deviceArrayList);
        deviceList.setAdapter(deviceAdapter);

        DatabaseReference deviceReference = firebaseDatabase.getReference().child("Devices");
        deviceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot devices) {
                deviceArrayList.clear();
                for (DataSnapshot deviceSnapshot:devices.getChildren()){
                    deviceArrayList.add(deviceSnapshot.child("name").getValue().toString()+" : "+deviceSnapshot.child("value").getValue().toString());
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });

        navigationDrawer();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
            }else if (item.getItemId() == R.id.ticket) {
                startActivity(new Intent(HomeActivity.this, TicketElectrique.class));
            }
            return true;
        });

        addDeviceBtn.setOnClickListener(v -> {
            String name = deviceName.getText().toString().trim();
            String value = deviceValue.getText().toString().trim();
            if (name.isEmpty()) {
                deviceName.setError("Device Name is required");
            } else if (value.isEmpty()) {
                deviceValue.setError("Device Value is required");
            } else {
                addDevice(name, value);
            }
        });
    }

    private void addDevice(String name, String value) {
        HashMap<String, String> deviceMap = new HashMap<>();
        deviceMap.put("name", name);
        deviceMap.put("value", value);
        databaseReference.child("Devices").push().setValue(deviceMap);
        deviceName.setText("");
        deviceValue.setText("");
        deviceName.clearFocus();
        deviceValue.clearFocus();
        Toast.makeText(this, "New Device added successfully", Toast.LENGTH_SHORT).show();
    }

    private void navigationDrawer() {

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);
        navigationView.bringToFront();

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        drawerLayout.setScrimColor(getResources().getColor(R.color.colorApp));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }


}

