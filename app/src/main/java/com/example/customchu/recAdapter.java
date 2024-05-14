package com.example.customchu;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    Dialog dialog_confirm_decline, dialog_confirm_request;
    Button confirmDeclineBtn, cancelBtn, cancelAcceptBtn, confirmAcceptBtn;
    TextView userRequest;
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

        dialog_confirm_decline = new Dialog(holder.itemView.getContext());
        dialog_confirm_decline.setContentView(R.layout.dialog_confirm_decline);
        dialog_confirm_decline.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_confirm_decline.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.dialogbox_qr_bg));
        dialog_confirm_decline.setCancelable(false);

        cancelBtn = dialog_confirm_decline.findViewById(R.id.cancelBtn);
        confirmDeclineBtn = dialog_confirm_decline.findViewById(R.id.confirmDeclineBtn);
        userRequest = dialog_confirm_decline.findViewById(R.id.userRequest);

        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recAdapter", "decline btn working" + uidCur);
                DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(uidCur.toString())
                        .child("friendRequests")
                        .child(getRef(position).getKey())
                        .child("sender_name");

                senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sender_name = dataSnapshot.getValue(String.class);
                        if (sender_name != null) {
                            userRequest.setText("Decline request from " + sender_name + "?");
                            dialog_confirm_decline.show();

                            cancelBtn.setOnClickListener(view -> {
                                dialog_confirm_decline.dismiss();
                            });

                            confirmDeclineBtn.setOnClickListener(view -> {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(uidCur.toString())
                                        .child("friendRequests")
                                        .child(getRef(position).getKey())
                                        .removeValue();

                                dialog_confirm_decline.dismiss();
                            });
                        } else {
                            Log.e("recAdapter", "Sender name is null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("recAdapter", "Error getting sender name", databaseError.toException());
                    }
                });
            }
        });

        dialog_confirm_request = new Dialog(holder.itemView.getContext());
        dialog_confirm_request.setContentView(R.layout.dialog_confirm_request);
        dialog_confirm_request.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_confirm_request.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.dialogbox_qr_bg));
        dialog_confirm_request.setCancelable(false);

        cancelAcceptBtn = dialog_confirm_request.findViewById(R.id.cancelAcceptBtn);
        confirmAcceptBtn = dialog_confirm_request.findViewById(R.id.confirmAcceptBtn);
        userRequest = dialog_confirm_request.findViewById(R.id.userRequest);

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recAdapter", "accept btn working" + uidCur);
                DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(uidCur.toString())
                        .child("friendRequests")
                        .child(getRef(position).getKey())
                        .child("sender_name");

                senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sender_name = dataSnapshot.getValue(String.class);
                        if (sender_name != null) {
                            userRequest.setText("Accept request from " + sender_name + "?");
                            dialog_confirm_request.show();

                            cancelAcceptBtn.setOnClickListener(view -> {
                                dialog_confirm_request.dismiss();
                            });

                            confirmAcceptBtn.setOnClickListener(view -> {
                                // Add sender_name to friends node
                                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(uidCur.toString())
                                        .child("friends")
                                        .child(getRef(position).getKey());

                                friendsRef.child("name").setValue(sender_name).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Remove the item from friendRequests node
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("users")
                                                .child(uidCur.toString())
                                                .child("friendRequests")
                                                .child(getRef(position).getKey())
                                                .removeValue();

                                        dialog_confirm_request.dismiss();
                                    } else {
                                        Log.e("recAdapter", "Error adding friend", task.getException());
                                    }
                                });
                            });
                        } else {
                            Log.e("recAdapter", "Sender name is null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("recAdapter", "Error getting sender name", databaseError.toException());
                    }
                });
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
