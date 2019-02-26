package com.anindyabrata.elotask;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<Task> checked, unchecked ;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Adapter is divided into two ArrayLists of Tasks. This is done so that Tasks that have been
     * crossed out are displayed on top of the other Tasks. The adapter abstraction allows the two
     * ArrayLists to seem concatenated.
     */
    TaskAdapter() {
        checked = new ArrayList<Task>();
        unchecked = new ArrayList<Task>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Removes all Tasks in view
     */
    public void clear(){
        checked.clear();
        unchecked.clear();
    }

    /**
     * Adds tasks to the proper ArrayList
     * @param t The Task object to be inserted into the view
     */
    public void add(Task t){
        if(t.isDone()) checked.add(t);
        else unchecked.add(t);
    }

    /**
     * Returns the Task object at given adapter position
     * @param i Adapter position corresponding to object
     * @return Task object corresponding to Adapter Position
     */
    public Task get(int i){
        if(i < checked.size()) return checked.get(i);
        else return unchecked.get(i-checked.size()) ;
    }

    /**
     * Removes Task object corresponding to given adapter position
     * @param i Adapter position corresponding to object
     */
    public void remove(int i){
        if(i < checked.size()) checked.remove(i);
        else unchecked.remove(i-checked.size());
    }

    /**
     * Iterates over checked then unchecked items and returns adapter position of given Task where
     * Task id and Object id are equal
     * @param t Task object with target search id
     * @return Adapter position of object with same id as t, returns -1 if not found
     */
    public int find(Task t){
        for(int i = 0 ; i < checked.size(); ++i){
            if(t.equals(checked.get(i))) return i;
        }
        for(int i = 0 ; i < unchecked.size(); ++i){
            if(t.equals(unchecked.get(i))) return i + checked.size();
        }
        return -1;
    }

    /**
     * This is called whenever a checkbox is toggled. Repositions the toggled Task as necessary.
     * Updates the corresponding Task on FireStore as well.
     * @param isChecked New value of checkbox after being toggled
     * @param i Adapter position corresponding to toggled checkbox's view
     */
    public void checkChange(boolean isChecked, int i){
        Task t = get(i);
        // Stop onCheckedListener from being triggered on initialization
        if(t.isDone()==isChecked) return ;
        t.setDone(isChecked);
        fireUpdate(t);
        remove(i);
        add(t);
        notifyItemMoved(i,find(t));
    }

    /**
     * Called when any item on list is Long pressed. Deletes all Tasks marked as done from list and
     * from firestore.
     */
    public void deleteChecked() {
        // Loops over each item to delete as deleting multiple documents from firestore at once is
        // not supported.
        for(int i = 0 ; i < checked.size(); ++i) fireDelete(checked.get(i));
        checked.clear();
        notifyDataSetChanged();
    }

    /**
     * Updates a single Task on Firestore
     * @param t Task to be updated
     */
    public void fireUpdate(Task t){
        db.document("users/"+mAuth.getUid()+"/tasks/"+t.getId()).set(t.toMap());
    }

    /**
     * Deletes a single Task on Firestore
     * @param t Task to be deleted
     */
    public void fireDelete(Task t){
        db.document("users/"+mAuth.getUid()+"/tasks/"+t.getId()).delete();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_single_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.getCheckBox().setChecked(get(i).isDone());
        viewHolder.getEditText().setText(get(i).getMessage());
    }

    public int getCheckedCount(){ return checked.size(); }

    @Override
    public int getItemCount() {
        return checked.size() + unchecked.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        private final EditText editText;
        private final CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            editText = (EditText)itemView.findViewById(R.id.singleItemEditText);
            checkBox = (CheckBox)itemView.findViewById(R.id.singleItemCheckBox);
            // Updates based on EditText after editing is done
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        // These three lines ensure that the cursor is not unnecessarily visible
                        editText.setFocusable(false);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);

                        // Update message of Task only if changed
                        int i = getAdapterPosition();
                        Task t = get(i);
                        if(!editText.getText().toString().equals(t.getMessage())){
                            t.setMessage(editText.getText().toString());
                            fireUpdate(t);
                        }
                    }
                }
            });
            // Action taken when a checkBox is toggled
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkChange(isChecked, getAdapterPosition());

                    // Strikeout text when checked
                    if(isChecked) {
                        editText.setPaintFlags(
                                editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        editText.setTextColor(Color.GRAY);
                    }
                    // Remove Strikeout when not checked (This is necessary because views are
                    // recycled)
                    else {
                        editText.setPaintFlags(
                                editText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        editText.setTextColor(Color.BLACK);
                    }
                }
            });
            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(checkBox.isChecked()){
                        deleteChecked();
                        return true;
                    }
                    return false;
                }
            };
            editText.setOnLongClickListener(longClickListener);
            checkBox.setOnLongClickListener(longClickListener);
        }

        public TextView getEditText() {
            return editText;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}
