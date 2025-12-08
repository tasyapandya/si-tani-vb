package com.farmerapp.utils;

import com.farmerapp.models.TodoItem;
import com.farmerapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirebaseHelper {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public static void loginUser(String email, String password, OnCompleteListener<Task<FirebaseUser>> onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        onCompleteListener.onComplete(task);
                    } else {
                        onCompleteListener.onComplete(task);
                    }
                });
    }

    public static void registerUser(String email, String password, OnCompleteListener<Task<FirebaseUser>> onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        onCompleteListener.onComplete(task);
                    } else {
                        onCompleteListener.onComplete(task);
                    }
                });
    }

    public static void logoutUser() {
        mAuth.signOut();
    }

    public static void saveUser(User user, OnCompleteListener<Void> onCompleteListener) {
        db.collection(Constants.USERS_COLLECTION)
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getUser(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        db.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void saveTodoItem(TodoItem todoItem, OnCompleteListener<Void> onCompleteListener) {
        db.collection(Constants.TODOS_COLLECTION)
                .document(todoItem.getId())
                .set(todoItem)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void updateTodoItem(TodoItem todoItem, OnCompleteListener<Void> onCompleteListener) {
        db.collection(Constants.TODOS_COLLECTION)
                .document(todoItem.getId())
                .set(todoItem)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void deleteTodoItem(String todoId, OnCompleteListener<Void> onCompleteListener) {
        db.collection(Constants.TODOS_COLLECTION)
                .document(todoId)
                .delete()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getUserTodos(String userId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        db.collection(Constants.TODOS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }
}