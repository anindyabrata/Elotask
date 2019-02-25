package com.anindyabrata.elotask;

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

    TaskAdapter() {
        checked = new ArrayList<Task>();
        unchecked = new ArrayList<Task>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void clear(){
        checked.clear();
        unchecked.clear();
    }

    public void add(Task t){
        if(t.isDone()) checked.add(t);
        else unchecked.add(t);
    }

    public Task get(int i){
        if(i < checked.size()) return checked.get(i);
        else return unchecked.get(i-checked.size()) ;
    }

    public void remove(int i){
        if(i < checked.size()) checked.remove(i);
        else unchecked.remove(i-checked.size());
    }

    public int find(Task t){
        for(int i = 0 ; i < checked.size(); ++i){
            if(t.equals(checked.get(i))) return i;
        }
        for(int i = 0 ; i < unchecked.size(); ++i){
            if(t.equals(unchecked.get(i))) return i + checked.size();
        }
        return -1;
    }

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

    public void fireUpdate(Task t){
        db.document("users/"+mAuth.getUid()+"/tasks/"+t.getId()).set(t.toMap());
    }

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

    @Override
    public int getItemCount() {
        return checked.size() + unchecked.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText editText;
        private final CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = (EditText)itemView.findViewById(R.id.singleItemEditText);
            checkBox = (CheckBox)itemView.findViewById(R.id.singleItemCheckBox);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        editText.setFocusable(false);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        int i = getAdapterPosition();
                        Task t = get(i);
                        if(!editText.getText().toString().equals(t.getMessage())){
                            t.setMessage(editText.getText().toString());
                            fireUpdate(t);
                        }
                    }
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkChange(isChecked, getAdapterPosition());
                    if(isChecked) editText.setPaintFlags(
                            editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    else editText.setPaintFlags(
                            editText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            });
        }

        public TextView getEditText() {
            return editText;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}
