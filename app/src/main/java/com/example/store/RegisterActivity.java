package com.example.store;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class RegisterActivity extends AppCompatActivity {

    public String companyId, companyNameText, companyEmailText, companyPhoneText, passwordText, productsText;
    public EditText companyName, companyEmail, companyPhone, password, products;
    private FirebaseAuth auth = null;
    String AES = "AES";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public Company newCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        companyName = findViewById(R.id.company_name);
        companyEmail = findViewById(R.id.company_email);
        companyPhone = findViewById(R.id.company_phone);
        password = findViewById(R.id.company_password);
        products = findViewById(R.id.company_products);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    public void registerPerson(View view) throws Exception {
        companyNameText = companyName.getText().toString();
        companyEmailText = companyEmail.getText().toString();
        companyPhoneText = companyPhone.getText().toString();
        passwordText = password.getText().toString();
        productsText = products.getText().toString();
        if (checkText() == false) {
            String encryptPassword = encrypt(companyEmailText, passwordText);
            addUser(companyEmailText, encryptPassword);
        }

    }

    public boolean checkText() {
        boolean empty = false;
        companyNameText = companyName.getText().toString();
        companyEmailText = companyEmail.getText().toString();
        companyPhoneText = companyPhone.getText().toString();
        passwordText = password.getText().toString();
        productsText = products.getText().toString();
        if (companyNameText.equals("") || companyEmailText.equals("") || companyPhoneText.equals("") || passwordText.equals("") ||
                productsText.equals("") || companyEmailText.contentEquals("@") || companyEmailText.length() < 15 ||
                passwordText.length() < 6 || companyNameText.length() < 3 || productsText.length() < 3 || companyPhoneText.length() < 11) {
            if (companyNameText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Company Name!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (companyEmailText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Company Email!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (companyPhoneText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Company Phone!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (passwordText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Password!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (productsText.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Company Products!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if ((!companyEmailText.contains("@")) || companyEmailText.length() < 15) {
                Toast.makeText(getApplicationContext(), "Please Enter Correct Email!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (passwordText.length() < 6) {
                Toast.makeText(getApplicationContext(), "Please Password must be at least 6 letters!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (companyNameText.length() < 3) {
                Toast.makeText(getApplicationContext(), "Please Company Name must be at least 3 letters!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (productsText.length() < 3) {
                Toast.makeText(getApplicationContext(), "Please Company Products must be at least 3 letters!", Toast.LENGTH_LONG).show();
                empty = true;
            } else if (companyPhoneText.length() < 11) {
                Toast.makeText(getApplicationContext(), "Please Company Phone must be 11 Numbers!", Toast.LENGTH_LONG).show();
                empty = true;
            }
        }
        return empty;
    }

    public void addUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_LONG).show();
                            sendEmailVerification();
                            addCompany();
                        } else {
                            Toast.makeText(getApplicationContext(), "Register Failed : " + task.getException().getMessage()
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void sendEmailVerification() {
        FirebaseUser currentUser = auth.getCurrentUser();
        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Check Your Email For Verification",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Register Failed : " + task.getException().getMessage()
                            , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String encrypt(String username, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(username.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes();
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
        return secretKeySpec;
    }

    public void addCompany() {
        FirebaseUser currentUser = auth.getCurrentUser();
        companyId = currentUser.getUid();
        newCompany = new Company(companyId, companyNameText, companyEmailText, companyPhoneText, passwordText, productsText);
        databaseReference.child(companyId).setValue(newCompany);
    }


}
