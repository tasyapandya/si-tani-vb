package com.sitani.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import com.sitani.R;
import com.sitani.adapters.TodoAdapter;
import com.sitani.models.TodoItem;
import com.sitani.notifications.WeatherNotificationService;
import com.sitani.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends AppCompatActivity implements TodoAdapter.OnTodoClickListener {

    private MaterialToolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView todoRecyclerView;
    private TextView emptyStateText;
    private ProgressBar progressBar;
    private FloatingActionButton addTaskFab;
    private View emptyStateLayout;

    private TodoAdapter todoAdapter;
    private List<TodoItem> todoList;
    private WeatherNotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        setupNotificationService();
        loadTodos();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        todoRecyclerView = findViewById(R.id.todoRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        addTaskFab = findViewById(R.id.addTaskFab);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        todoList = new ArrayList<>();
        todoAdapter = new TodoAdapter(this);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoRecyclerView.setAdapter(todoAdapter);
    }

    private void setupClickListeners() {
        addTaskFab.setOnClickListener(v -> showAddTaskDialog());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTodos();
        });
    }

    private void setupNotificationService() {
        notificationService = new WeatherNotificationService(this);
    }

    @SuppressLint("MissingPermission")
    private void loadTodos() {
        showProgress(true);

        FirebaseUser currentUser = FirebaseHelper.getCurrentUser();
        if (currentUser != null) {
            FirebaseHelper.getUserTodos(currentUser.getUid(), task -> {
                showProgress(false);
                swipeRefreshLayout.setRefreshing(false);

                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        todoList.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            TodoItem todoItem = document.toObject(TodoItem.class);
                            if (todoItem != null) {
                                todoList.add(todoItem);
                            }
                        }
                        todoAdapter.setTodoItems(todoList);
                        updateEmptyState();
                        checkPendingTasks();
                    }
                } else {
                    Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateEmptyState() {
        if (todoList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            todoRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            todoRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void checkPendingTasks() {
        int pendingTasks = 0;
        for (TodoItem item : todoList) {
            if (!item.isCompleted()) {
                pendingTasks++;
            }
        }

        if (pendingTasks > 0) {
            notificationService.showTodoNotification(
                "Task Reminder",
                "You have pending tasks to complete",
                pendingTasks
            );
        }
    }

    private void showAddTaskDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        TextInputLayout titleInputLayout = dialogView.findViewById(R.id.titleInputLayout);
        TextInputLayout descriptionInputLayout = dialogView.findViewById(R.id.descriptionInputLayout);
        TextInputEditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        MaterialButton addButton = dialogView.findViewById(R.id.addButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(dialogView)
                .setCancelable(false)
                .create();

        addButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (title.isEmpty()) {
                titleInputLayout.setError("Please enter a task title");
                return;
            }

            addTask(title, description);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void addTask(String title, String description) {
        showProgress(true);

        FirebaseUser currentUser = FirebaseHelper.getCurrentUser();
        if (currentUser != null) {
            String todoId = java.util.UUID.randomUUID().toString();
            TodoItem todoItem = new TodoItem(todoId, title, description, currentUser.getUid());

            FirebaseHelper.saveTodoItem(todoItem, task -> {
                showProgress(false);
                if (task.isSuccessful()) {
                    Toast.makeText(this, getString(R.string.task_added), Toast.LENGTH_SHORT).show();
                    loadTodos();
                } else {
                    Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateTask(TodoItem todoItem) {
        showProgress(true);

        FirebaseHelper.updateTodoItem(todoItem, task -> {
            showProgress(false);
            if (task.isSuccessful()) {
                Toast.makeText(this, getString(R.string.task_updated), Toast.LENGTH_SHORT).show();
                loadTodos();
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTask(TodoItem todoItem) {
        showProgress(true);

        FirebaseHelper.deleteTodoItem(todoItem.getId(), task -> {
            showProgress(false);
            if (task.isSuccessful()) {
                Toast.makeText(this, getString(R.string.task_deleted), Toast.LENGTH_SHORT).show();
                loadTodos();
            } else {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        addTaskFab.setEnabled(!show);
    }

    // TodoAdapter.OnTodoClickListener implementation
    @Override
    public void onTodoClick(TodoItem todoItem) {
        // Handle task click - could show task details
    }

    @Override
    public void onTodoEdit(TodoItem todoItem) {
        showEditTaskDialog(todoItem);
    }

    @Override
    public void onTodoDelete(TodoItem todoItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask(todoItem))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onTodoStatusChanged(TodoItem todoItem, boolean isCompleted) {
        todoItem.setCompleted(isCompleted);
        updateTask(todoItem);
    }

    private void showEditTaskDialog(TodoItem todoItem) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        TextInputLayout titleInputLayout = dialogView.findViewById(R.id.titleInputLayout);
        TextInputLayout descriptionInputLayout = dialogView.findViewById(R.id.descriptionInputLayout);
        TextInputEditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        MaterialButton addButton = dialogView.findViewById(R.id.addButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Pre-fill with existing data
        titleEditText.setText(todoItem.getTitle());
        descriptionEditText.setText(todoItem.getDescription());
        addButton.setText("Update");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setCancelable(false)
                .create();

        addButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (title.isEmpty()) {
                titleInputLayout.setError("Please enter a task title");
                return;
            }

            todoItem.setTitle(title);
            todoItem.setDescription(description);
            updateTask(todoItem);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}