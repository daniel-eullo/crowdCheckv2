package com.example.customchu;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class friendAdapter extends FirebaseRecyclerAdapter<friendModel, friendAdapter.myViewHolder> {

    DatabaseReference DB;
    Integer uidCur = 0;

    public friendAdapter(@NonNull FirebaseRecyclerOptions<friendModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull friendModel model) {
        holder.friendName.setText(model.getName());

        // Example of setting visibility based on model properties
//        if (model.isOnline()) {
//            holder.isOnline.setVisibility(View.VISIBLE);
//            holder.isOffline.setVisibility(View.GONE);
//        } else {
//            holder.isOnline.setVisibility(View.GONE);
//            holder.isOffline.setVisibility(View.VISIBLE);
//        }

        // Example Firebase logic to demonstrate usage, adjust as needed
        DB = FirebaseDatabase.getInstance().getReference();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(holder.itemView.getContext());
        if (account != null){
            DatabaseReference ProfileReference = DB.child("Profiles").child(account.getId());

            ProfileReference.child("uid").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uidCur = task.getResult().getValue(Integer.class);
                    if (uidCur != null) {
                        Log.d("friendAdapter", "uid" + uidCur);
                    }
                } else {
                    Log.e("TAG", "Error getting UID", task.getException());
                }
            });
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        ImageView isOnline, isOffline;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            isOnline = itemView.findViewById(R.id.isOnline);
            isOffline = itemView.findViewById(R.id.isOffline);
        }
    }
}
