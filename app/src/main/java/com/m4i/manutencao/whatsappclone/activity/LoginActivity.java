package com.m4i.manutencao.whatsappclone.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.model.User;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText fieldEmail, fieldPassword;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fieldEmail = findViewById(R.id.activity_login_etEmail);
        fieldPassword = findViewById(R.id.activity_login_etPassword);

        fbAuth = FirebaseConfiguration.getFirebaseAuth();
    }

    public void loginUser(User user) {
        fbAuth.signInWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    openMainActivity();
                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        exception = "The user is not registered";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "E-mail and password don't correspond to a registered user";
                    } catch (Exception e) {
                        exception = "Error on trying to register: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateUserLogin(View v) {

        //Recover the text from the input fields
        String textEmail = fieldEmail.getText().toString();
        String textPassword = fieldPassword.getText().toString();

        if (!textEmail.isEmpty()) {
            if (!textPassword.isEmpty()) {

                //Create user
                User user = new User();
                user.setEmail(textEmail);
                user.setPassword(textPassword);
                loginUser(user);

            } else {
                Toast.makeText(LoginActivity.this, "Please fill the password field", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Please fill the e-mail field", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser actualUser = fbAuth.getCurrentUser();
        if (actualUser != null) {
            openMainActivity();
        }
    }

    public void openRegisterActivity(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
