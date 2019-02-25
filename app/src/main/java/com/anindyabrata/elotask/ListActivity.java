package com.anindyabrata.elotask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setupList();
        retrieveAll();
    }

    private void setupList(){
        RecyclerView list = (RecyclerView)findViewById(R.id.itemList);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        // specify an adapter
        final TaskAdapter mAdapter = new TaskAdapter();
        list.setAdapter(mAdapter);

        // swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Remove item from backing list here
                if(swipeDir == ItemTouchHelper.LEFT || swipeDir == ItemTouchHelper.RIGHT){
                    int i = viewHolder.getAdapterPosition();
                    Task t = mAdapter.get(i);
                    mAdapter.remove(i);
                    mAdapter.fireDelete(t);
                    mAdapter.notifyItemRangeChanged(i,mAdapter.getItemCount());
                }
            }
        });

        // activate swipe to delete
        itemTouchHelper.attachToRecyclerView(list);
    }

    private TaskAdapter getListAdapter(){
        RecyclerView list = (RecyclerView)findViewById(R.id.itemList);
        return (TaskAdapter) list.getAdapter();
    }

    private void loadList(ArrayList<Task> a){
        TaskAdapter mAdapter = getListAdapter();
        mAdapter.clear();
        for(Task x:a)mAdapter.add(x);
        mAdapter.notifyDataSetChanged();
    }

    private void retrieveAll(){
        CollectionReference all = db.collection("users/"+mAuth.getUid()+"/tasks");
        /*all.addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null && queryDocumentSnapshots != null){
                            TaskAdapter l = getListAdapter();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.get("done") != null) {
                                    Task t = new Task(doc.getId(),(String)doc.get("message"),
                                            (boolean)doc.get("done"));
                                    int ind = l.find(t);
                                    if(ind >= 0) l.remove(ind);
                                    l.add(t);
                                }else{
                                    int ind = l.find(new Task(doc.getId(),"",false));
                                    if(ind != -1) l.remove(ind);
                                }
                            }

                        }
                    }
                });*/
    all.get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(
                            @NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        QuerySnapshot res = task.getResult();
                        if(task.isSuccessful()&&res!=null){
                            ArrayList<Task> ret = new ArrayList<Task>() ;
                            for(QueryDocumentSnapshot taskData:res){
                                if(taskData != null) {
                                    Task t = new Task(taskData.getId(),
                                            (String) taskData.get("message"),
                                            (Boolean) taskData.get("done"));
                                    ret.add(t);
                                }
                            }
                            loadList(ret);
                        }else{
                            Toast.makeText(ListActivity.this,"Cannot fetch data",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public void addnew(View view) {
        Intent toNewItem = new Intent(this, NewItemInput.class) ;
        startActivity(toNewItem);
    }
}
