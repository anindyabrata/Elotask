package com.anindyabrata.elotask;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Task[] mData ;

    TaskAdapter(Task[] mData) {
        this.mData = mData;
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
        viewHolder.getCheckBox().setChecked(mData[i].isDone());
        viewHolder.getTextView().setText(mData[i].getMessage());
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO click events here yo
            textView = (TextView)itemView.findViewById(R.id.singleItemTextView);
            checkBox = (CheckBox)itemView.findViewById(R.id.singleItemCheckBox);
        }

        public TextView getTextView() {
            return textView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}
