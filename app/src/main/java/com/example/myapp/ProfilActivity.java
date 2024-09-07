package com.example.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ProfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText fullName, email, cin, phone;
    private Button btnEdit, btnLogOut;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser loggedUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        fullName = findViewById(R.id.fullNameProfil);
        email = findViewById(R.id.emailProfil);
        cin = findViewById(R.id.cinProfil);
        phone = findViewById(R.id.phoneProfil);
        btnEdit = findViewById(R.id.btnEdit);
        btnLogOut = findViewById(R.id.btnLogOut);
        drawerLayout = findViewById(R.id.drawer_layout_profile);
        menuIcon = findViewById(R.id.menu_profile);
        navigationView = findViewById(R.id.navigation_view_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        loggedUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference().child("users").child(loggedUser.getUid());
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Please wait ... ");
        progressDialog.show();

        navigationDrawerProfile();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.profile) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(ProfilActivity.this, HomeActivity.class));
            }
            return true;

        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullNameS = snapshot.child("fullName").getValue().toString();
                String emailS = snapshot.child("email").getValue().toString();
                String cinS = snapshot.child("cin").getValue().toString();
                String phoneS = snapshot.child("phone").getValue().toString();

                fullName.setText(fullNameS);
                email.setText(emailS);
                cin.setText(cinS);
                phone.setText(phoneS);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }

        });
        btnEdit.setOnClickListener(v -> {
            String updatedFullName = fullName.getText().toString();
            String updatedCin = cin.getText().toString();
            String updatedPhone = phone.getText().toString();

            databaseReference.child("fullName").setValue(updatedFullName);
            databaseReference.child("cin").setValue(updatedCin);
            databaseReference.child("phone").setValue(updatedPhone);

            Toast.makeText(this, "Data has been changed successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfilActivity.this, ProfilActivity.class));
        });

        btnLogOut.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
            firebaseAuth.signOut();
            startActivity(new Intent(ProfilActivity.this, SignInActivity.class));
            Toast.makeText(this, "Log out successfully !!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }


    private void navigationDrawerProfile() {

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.profile);
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
