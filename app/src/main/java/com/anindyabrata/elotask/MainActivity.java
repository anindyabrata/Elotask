package com.anindyabrata.elotask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private String getEmail(){
        TextView emailText = (TextView)findViewById(R.id.emailEditText) ;
        return emailText.getText().toString();
    }

    private String getPassword(){
        TextView passText = (TextView)findViewById(R.id.passwordEditText);
        return passText.getText().toString();
    }

    private void clearTextFields(){
        TextView emailText = (TextView)findViewById(R.id.emailEditText) ;
        TextView passText = (TextView)findViewById(R.id.passwordEditText);
        emailText.setText("");
        passText.setText("");
    }

    private void loginSuccessful(){
        clearTextFields();
        Intent toList = new Intent(this, ListActivity.class) ;
        startActivity(toList);
    }

    public void login(View view) {
        String email = getEmail();
        String password = getPassword();
        if(email.length()==0 || password.length()==0){
            Toast.makeText(this,"Email and Password cannot be empty", Toast.LENGTH_LONG).show();
        }else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loginSuccessful();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Login Failed\nPlease try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void register(View view) {
        String email = getEmail();
        String password = getPassword();
        if(email.length()==0 || password.length()==0){
            Toast.makeText(this,
                    "Email and Password cannot be empty", Toast.LENGTH_LONG).show();
        }else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if (task.isSuccessful()&&mAuth.getUid()!=null) {
                                Map<String, Object> doc = new HashMap<>();
                                doc.put("email",getEmail());
                                try {
                                    db.collection("users")
                                            .document(mAuth.getUid()).set(doc).wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                loginSuccessful();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Registration Failed\nPlease try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
