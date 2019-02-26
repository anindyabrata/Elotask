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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setupDate();
        setupList();
    }

    @Override
    public void onResume(){
        super.onResume();
        retrieveAll();
    }

    /**
     * Initializes TextViews with current date
     */
    private void setupDate() {
        Calendar now = Calendar.getInstance();
        TextView tvDay = (TextView)findViewById(R.id.dayTextView);
        TextView tvDate = (TextView)findViewById(R.id.dateTextView);
        TextView tvMonth = (TextView)findViewById(R.id.monthTextView);
        TextView tvYear = (TextView)findViewById(R.id.yearTextView);

        String[] dayOfWeek = { "", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"
                , "SATURDAY" };
        String[] monthOfYear = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP"
                ,"OCT", "NOV", "DEC" };
        String day = dayOfWeek[now.get(Calendar.DAY_OF_WEEK)];
        String date = String.valueOf(now.get(Calendar.DATE));
        String month = monthOfYear[now.get(Calendar.MONTH)];
        String year = String.valueOf(now.get(Calendar.YEAR));

        tvDay.setText(day);
        tvDate.setText(date);
        tvMonth.setText(month);
        tvYear.setText(year);
    }


    /**
     * Initializes RecycleView with a Layout Manager and an Adapter. Adds swipe functionality.
     */
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

        /*list.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mAdapter.getCheckedCount() > 0){
                    mAdapter.deleteChecked();
                    return true;
                }
                return false;
            }
        });*/
    }

    /**
     * Getter method for RecycleView's adapter
     * @return TaskAdapter assigned to RecycleView
     */
    private TaskAdapter getListAdapter(){
        RecyclerView list = (RecyclerView)findViewById(R.id.itemList);
        return (TaskAdapter) list.getAdapter();
    }

    /**
     * Replace current items in view with given Tasks
     * @param a List of Tasks to populate the view
     */
    private void loadList(ArrayList<Task> a){
        TaskAdapter mAdapter = getListAdapter();
        mAdapter.clear();
        for(Task x:a)mAdapter.add(x);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Takes care of Firestore initializations. Loads initial documents and sets up realtime updates
     */
    private void retrieveAll(){
        // Reference to all Tasks of this user
        CollectionReference all = db.collection("users/"+mAuth.getUid()+"/tasks");

        // Update when Firestore is updated
        /*all.addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                        @Nullable FirebaseFirestoreException e) {
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

        // Download all Tasks of this user and assign to view
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

    // Makes sure that EditTexts don't remain in focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm;
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    /**
     * Called when Floating Action Button is pressed. Opens a new activity to enter new task
     * @param view View for context
     */
    public void addnew(View view) {
        Intent toNewItem = new Intent(this, NewItemInput.class) ;
        startActivity(toNewItem);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAuth.signOut();
        Toast.makeText(ListActivity.this,"Logged Out", Toast.LENGTH_LONG).show();
    }
}
