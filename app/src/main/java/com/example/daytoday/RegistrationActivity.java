package com.example.daytoday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private TextView mEmail;
    private TextView mPass;
    private LinearLayout btnReg;
    private TextView mLoginHere;

    private ProgressDialog progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() !=null ){
            startActivity(new Intent(getApplicationContext(),ListOfListsActivity.class));
        }

        mEmail = findViewById(R.id.reg_uname);
        mPass = findViewById(R.id.reg_pwd);
        btnReg = findViewById(R.id.reg_btn);
        mLoginHere = findViewById(R.id.login_in_signup);

        progress = new ProgressDialog(this);

        mLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                progress.setMessage("Signing Up");

                progress.show();

                if (TextUtils.isEmpty(mail)){
                    mEmail.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    mPass.setError("Password required");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                        }else{

                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                        progress.dismiss();
                    }
                });
            }
        });

    }

    protected void onResume() {
        super.onResume();

        mAuth= FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() !=null ){
            mAuth.signOut();
        }
    }
}