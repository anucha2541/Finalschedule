package com.example.expensemanager;

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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.lang.reflect.Method;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnRgn;
    private TextView mSignin;

    private ProgressDialog  mDialog;

    //Firebase

    private FirebaseAuth mAuth;
    private EditText password_reg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        password_reg2 = findViewById(R.id.password_reg2);
        mAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);

        registration();

    }

    private  void registration(){

        mEmail=findViewById(R.id.email_reg);
        mPass=findViewById(R.id.password_reg);
        btnRgn=findViewById(R.id.btn_reg);
        mSignin=findViewById(R.id.signin_here);

        btnRgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();
                String pass2=password_reg2.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required..");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    mPass.setError("Password Required..");
                    return;
                }
                if (TextUtils.isEmpty(pass2)){
                    password_reg2.setError("Password Required..");
                    return;
                }

                if(!pass.equals(pass2)){
                    password_reg2.setError("Password is not matched");
                    return;
                }

                mDialog.setMessage("processing");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Complete",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Field..",Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , MainActivity.class));
            }
        });


    }

}