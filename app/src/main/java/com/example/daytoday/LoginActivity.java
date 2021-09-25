package com.example.daytoday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private TextView mEmail;
    private TextView mPass;
    private TextView btnLogin;
    private TextView mSignupHere;

    private ProgressDialog progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() !=null ){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        mEmail = findViewById(R.id.login_uname);
        mPass = findViewById(R.id.login_pwd);
        btnLogin = findViewById(R.id.login_btn);
        mSignupHere = findViewById(R.id.signup_in_login);

        progress = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();

                progress.setMessage("Logging In");

                progress.show();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    mPass.setError("Password required");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                }
                                progress.dismiss();
                            }
                        });
            }
        });

        mSignupHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth= FirebaseAuth.getInstance();

        /*if (mAuth.getCurrentUser() !=null ){
            mAuth.signOut();
        }*/
    }
}
