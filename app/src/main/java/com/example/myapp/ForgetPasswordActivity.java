package com.example.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button goToSignIn;
    private EditText email;
    private Button btnForgPass;
    private String emailString;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        goToSignIn = findViewById(R.id.goTosignIn);
        email = findViewById(R.id.emailForgPass);
        btnForgPass = findViewById(R.id.btnForgPass);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();


        goToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(ForgetPasswordActivity.this, SignInActivity.class));
        });

        btnForgPass.setOnClickListener(v -> {
            progressDialog.setMessage("Please wait ");
            if (validate()) {
                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(emailString).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "password reset email has been sent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgetPasswordActivity.this, SignInActivity.class));
                        progressDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

    private boolean validate() {
        boolean res = false;
        emailString = email.getText().toString().trim();
        if (!isValidPattern(emailString, EMAIL_PATTERN)) {
            email.setError("Email invalide !!");

        } else
            res = true;
        return res;
    }

    private boolean isValidPattern(String mot, String patternn) {
        Pattern pattern = Pattern.compile(patternn);
        Matcher matcher = pattern.matcher(mot);
        return matcher.matches();
    }
}