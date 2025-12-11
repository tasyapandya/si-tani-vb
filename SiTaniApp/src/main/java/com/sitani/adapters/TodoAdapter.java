package com.sitani.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sitani.R;
import com.sitani.models.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoItem> todoItems;
    private OnTodoClickListener listener;

    public interface OnTodoClickListener {
        void onTodoClick(TodoItem todoItem);
        void onTodoEdit(TodoItem todoItem);
        void onTodoDelete(TodoItem todoItem);
        void onTodoStatusChanged(TodoItem todoItem, boolean isCompleted);
    }

    public TodoAdapter(OnTodoClickListener listener) {
        this.todoItems = new ArrayList<>();
        this.listener = listener;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem todoItem = todoItems.get(position);
        holder.bind(todoItem);
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private CheckBox taskCheckBox;
        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageButton editButton;
        private ImageButton deleteButton;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTodoClick(todoItems.get(position));
                }
            });
        }

        public void bind(TodoItem todoItem) {
            taskCheckBox.setChecked(todoItem.isCompleted());
            titleTextView.setText(todoItem.getTitle());
            descriptionTextView.setText(todoItem.getDescription());

            // Update text style based on completion status
            if (todoItem.isCompleted()) {
                titleTextView.setAlpha(0.6f);
                descriptionTextView.setAlpha(0.6f);
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleTextView.setAlpha(1.0f);
                descriptionTextView.setAlpha(1.0f);
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }

            taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTodoStatusChanged(todoItem, isChecked);
                }
            });

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoEdit(todoItem);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoDelete(todoItem);
                }
            });
        }
    }
}