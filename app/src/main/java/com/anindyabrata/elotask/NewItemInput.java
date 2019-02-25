package com.anindyabrata.elotask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewItemInput extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_input);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private String getMessage(){
        TextView messageText = (TextView)findViewById(R.id.newItemEditText) ;
        return messageText.getText().toString();
    }

    public void addItem(View view) {
        String message = getMessage();
        db.collection("users/"+mAuth.getUid()+"/tasks")
                .add((new Task("0",message, false)).toMap());
        finish();
    }
}
