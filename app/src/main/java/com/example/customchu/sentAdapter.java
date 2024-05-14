package com.example.customchu;

import android.app.Dialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class sentAdapter extends FirebaseRecyclerAdapter<sentModel, sentAdapter.myViewHolder> {
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
    Dialog  dialog_current_sent;
    Button confirmBtn, cancelBtn;
    TextView userRequest;
    public sentAdapter(@NonNull FirebaseRecyclerOptions<sentModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull sentModel model) {
        holder.recName.setText(model.getReceiver_name());
        holder.recUid.setText(String.valueOf(model.getReceiver_uid()));

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

        dialog_current_sent = new Dialog(holder.itemView.getContext());
        dialog_current_sent.setContentView(R.layout.dialog_current_sent);
        dialog_current_sent.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_current_sent.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.dialogbox_qr_bg));
        dialog_current_sent.setCancelable(false);

        cancelBtn = dialog_current_sent.findViewById(R.id.cancelBtn);
        confirmBtn = dialog_current_sent.findViewById(R.id.confirmBtn);
        userRequest = dialog_current_sent.findViewById(R.id.userRequest);

        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sentAdapter","decline working" + uidCur);
                DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(uidCur.toString())
                        .child("currentRequest")
                        .child(getRef(position).getKey())
                        .child("receiver_name");

                senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sender_name = dataSnapshot.getValue(String.class);
                        if (sender_name != null) {
                            userRequest.setText("Remove " + sender_name + " from your sent request?");
                            dialog_current_sent.show();

                            cancelBtn.setOnClickListener(view -> {
                                dialog_current_sent.dismiss();
                            });

                            confirmBtn.setOnClickListener(view -> {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(uidCur.toString())
                                        .child("currentRequest")
                                        .child(getRef(position).getKey())
                                        .removeValue();

                                dialog_current_sent.dismiss();
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
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currentrequest_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView recName, recUid;
        ImageView declineBtn;

        public myViewHolder(@NonNull View itemView){
            super(itemView);

            recName = (TextView)itemView.findViewById(R.id.recName);
            recUid = (TextView)itemView.findViewById(R.id.recUid);

            declineBtn = (ImageView)itemView.findViewById(R.id.declineBtn);
        }
    }
}
