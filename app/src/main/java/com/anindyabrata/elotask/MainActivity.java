package com.anindyabrata.elotask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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

    /**
     * Get string from email text field
     * @return string containing user's email
     */
    private String getEmail(){
        EditText emailText = (EditText)findViewById(R.id.emailEditText) ;
        return emailText.getText().toString();
    }

    /**
     * Get string from password text field
     * @return string containing user's password
     */
    private String getPassword(){
        EditText passText = (EditText) findViewById(R.id.passwordEditText);
        return passText.getText().toString();
    }

    /**
     * Clears the Text Fields of the EditTexts.
     */
    private void clearTextFields(){
        EditText emailText = (EditText)findViewById(R.id.emailEditText) ;
        EditText passText = (EditText)findViewById(R.id.passwordEditText);
        emailText.setText("");
        passText.setText("");
    }

    /**
     * When a user successfully logs in, go to the next activity.
     */
    private void loginSuccessful(){
        clearTextFields();
        Intent toList = new Intent(this, ListActivity.class) ;
        startActivity(toList);
    }

    /**
     * Called when Log in button is pressed. Tries to Log in using given credentials
     * @param view View for context
     */
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

    /**
     * Called when Register button is pressed. Tries to create new user using given email and
     * password.
     * @param view View for context
     */
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
