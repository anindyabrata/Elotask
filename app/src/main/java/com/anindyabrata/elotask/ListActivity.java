package com.anindyabrata.elotask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        RecyclerView list = (RecyclerView)findViewById(R.id.itemList);
        Task[] taskList = { new Task( "Start creating project", true),
                    new Task("Finish project", false),
                    new Task("asdasdfasd fasf asdf as fasd fas fas fasdf asdf as fas dfasd fasd fasdfas fadsfa dfs fja sdfkjasdfj asfj asdjfa sdfji asdflijalidsfj aidj fslija sdfi dfjaslgaiwl ewaswgeihlgei h", false) } ;

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        // specify an adapter
        TaskAdapter mAdapter = new TaskAdapter(taskList);
        list.setAdapter(mAdapter);


    }

    public void addnew(View view) {
        Intent toNewItem = new Intent(this, NewItemInput.class) ;
        startActivity(toNewItem);
    }
}
