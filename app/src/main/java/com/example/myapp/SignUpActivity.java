package com.example.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    //declaration de variables
    private TextView goToSignIn;
    private EditText fullName, email, cin, phone, password, confirmPassword;
    private Button btnSignUp;
    private String fullNameString, emailString, cinString, phoneString, passwordString, confirmPasswordString;
    private final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        //affectation des views
        goToSignIn = findViewById(R.id.goTosignIn);
        fullName = findViewById(R.id.fullNameSignUp);
        email = findViewById(R.id.emailSignUp);
        cin = findViewById(R.id.cinSignUp);
        phone = findViewById(R.id.phoneSignUp);
        password = findViewById(R.id.passwordSignUp);
        confirmPassword = findViewById(R.id.confirmPasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //actions
        goToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });

        btnSignUp.setOnClickListener(v -> {

            if (validate()){
                progressDialog.setMessage("Please wait... !");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        sendEmailVerification();
                    }else {
                        Toast.makeText(this, "register failed !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser loggedUser = firebaseAuth.getCurrentUser();
        if (loggedUser!= null){
            loggedUser.sendEmailVerification().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    sendUserData();
                    Toast.makeText(this, "Registration is done , please check your email .", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                    progressDialog.dismiss();
                    finish();
                }else {
                    Toast.makeText(this, "Registration failed !", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }

    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("users"); //récupére le lien de la base de données
        User user = new User(fullNameString, emailString, cinString, phoneString);
        //user.setFullName("fullNameString");
        databaseReference.child(""+firebaseAuth.getUid()).setValue(user);
    }

    //validation des données
    private boolean validate() {

        boolean result = false;
        fullNameString = fullName.getText().toString().trim();
        emailString = email.getText().toString().trim();
        cinString = cin.getText().toString().trim();
        phoneString = phone.getText().toString().trim();
        passwordString = password.getText().toString().trim();
        confirmPasswordString = confirmPassword.getText().toString().trim();

        if (fullNameString.length() < 7) {
            fullName.setError("fullname invalide !");
        } else if (!isValidPattern(emailString, EMAIL_PATTERN)) {
            email.setError("Email invalide !!");

        } else if (cinString.length() != 8) {
            cin.setError("CIN invalide ! Cela nécessite 8 numéros");
        } else if (phoneString.length() != 8) {
            phone.setError("Phone invalide !");

        } else if (passwordString.length()<8) {
            password.setError("password doit contenir au moins 8 caractères");

        } else if (!(confirmPasswordString.equals(passwordString))) {
            confirmPassword.setError("tu dois confirmer le password");
        } else
            result = true ;
        return result;
    }

    private boolean isValidPattern(String mot, String patternn) {
        Pattern pattern = Pattern.compile(patternn);
        Matcher matcher = pattern.matcher(mot);
        return matcher.matches();
    }
}