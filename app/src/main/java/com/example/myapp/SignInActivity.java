package com.example.myapp;

import static androidx.constraintlayout.motion.widget.TransitionBuilder.validate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private TextView goToForgetPass;

    private EditText email, password;
    private TextView goToSignUp;
    private Button btnSignIn;
    private String emailString, passwordString;
    private final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private CheckBox remembermeSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        goToForgetPass = findViewById(R.id.goToForgetPass);
        email = findViewById(R.id.emailSignIn);
        password = findViewById(R.id.passwordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        goToSignUp = findViewById(R.id.goTosignUp);
        remembermeSignIn = findViewById(R.id.rememberMeSignIn);


        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")) {
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
        } else if ((checkbox.equals("false"))) {
            Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }


        goToForgetPass.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
        });

        goToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        btnSignIn.setOnClickListener(v -> {
            progressDialog.setMessage("Please Wait ! ..");
            if (validate()) {
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkEmailVerification();
                    } else {
                        Toast.makeText(this, "error !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

        remembermeSignIn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember", "true");
                editor.apply();
            } else if (!buttonView.isChecked()) {
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember", "false");
                editor.apply();
            }
        });
    }


    private void checkEmailVerification() {
        FirebaseUser loggedUser = firebaseAuth.getCurrentUser();
        if (loggedUser != null) {
            if (loggedUser.isEmailVerified()) {
                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                progressDialog.dismiss();
                finish();
            } else {
                Toast.makeText(this, "Please verify your email !!", Toast.LENGTH_SHORT).show();
                loggedUser.sendEmailVerification();
                firebaseAuth.signOut();
                progressDialog.dismiss();
            }
        }
    }

    private boolean validate() {
        boolean res = false;
        emailString = email.getText().toString().trim();
        passwordString = password.getText().toString().trim();

        if (!isValidPattern(emailString, EMAIL_PATTERN)) {
            email.setError("Email invalide !!");

        } else if (passwordString.length() < 8) {
            password.setError("password doit contenir au moins 8 caractères");

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
