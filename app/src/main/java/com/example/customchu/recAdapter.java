package com.example.customchu;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class recAdapter extends FirebaseRecyclerAdapter<recModel, recAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DatabaseReference DB;
    Integer uidCur = 0;
    public recAdapter(@NonNull FirebaseRecyclerOptions<recModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull recAdapter.myViewHolder holder, int position, @NonNull recModel model) {
        holder.recName.setText(model.getSender_name());
        holder.recUid.setText(String.valueOf(model.getSender_uid()));

        DB = FirebaseDatabase.getInstance().getReference();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(holder.itemView.getContext(), gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(holder.itemView.getContext());
        if (account != null){
            DatabaseReference ProfileReference = DB.child("Profiles").child(account.getId());

            ProfileReference.child("uid").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uidCur = task.getResult().getValue(Integer.class);
                    if (uidCur != null) {
                        Log.d("recAdapter", "uid" + uidCur);
                    }
                } else {
                    Log.e("TAG", "Error getting UID", task.getException());
                }
            });
        }

        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recAdapter", "decline btn working" + uidCur);
                FirebaseDatabase.getInstance().getReference().child("users").child(uidCur.toString()).child("friendRequests")
                        .child(getRef(position).getKey()).removeValue();
            }
        });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recAdapter", "accept btn working" + uidCur);
            }
        });
    }

    @NonNull
    @Override
    public recAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendrequest_item, parent, false);
        return new recAdapter.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView recName, recUid;
        ImageView declineBtn, acceptBtn;

        public myViewHolder(@NonNull View itemView){
            super(itemView);

            recName = (TextView)itemView.findViewById(R.id.recName);
            recUid = (TextView)itemView.findViewById(R.id.recUid);
            declineBtn = (ImageView)itemView.findViewById(R.id.declineBtn);
            acceptBtn = (ImageButton)itemView.findViewById(R.id.acceptBtn);
        }
    }
}
