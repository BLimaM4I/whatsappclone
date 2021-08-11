package com.m4i.manutencao.whatsappclone.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.Base64Custom;
import com.m4i.manutencao.whatsappclone.model.User;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText fieldName, fieldEmail, fieldPassword;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fieldName = findViewById(R.id.activity_register_etName);
        fieldEmail = findViewById(R.id.activity_register_etEmail);
        fieldPassword = findViewById(R.id.activity_register_etPassword);
    }

    public void saveUserInFirebase(final User user) {
        fbAuth = FirebaseConfiguration.getFirebaseAuth();
        fbAuth.createUserWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this,"User registered successfully", Toast.LENGTH_SHORT).show();
                    try {
                        String userIdIdentification = Base64Custom.encodeBase64(user.getEmail());
                        user.setUserId(userIdIdentification);
                        user.save();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        finish();
                    }
                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exception = "Please introduce a stronger password";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "Please insert a valid e-mail";
                    } catch (FirebaseAuthUserCollisionException e) {
                        exception = "This account is already registered";
                    } catch (Exception e) {
                        exception = "Error on trying to register: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateRegisterUser(View view) {

        //recover the texts for the fields
        String textName = fieldName.getText().toString();
        String textEmail = fieldEmail.getText().toString();
        String textPassword = fieldPassword.getText().toString();

        if (!textName.isEmpty()) {
            if (!textEmail.isEmpty()) {
                if (!textPassword.isEmpty()) {

                    User user = new User();

                    user.setName(textName);
                    user.setEmail(textEmail);
                    user.setPassword(textPassword);

                    saveUserInFirebase(user);

                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill the password field", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Please fill the e-mail field", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Please fill the name field", Toast.LENGTH_SHORT).show();
        }
    }
}
