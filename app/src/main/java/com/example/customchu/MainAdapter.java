package com.example.customchu;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel,MainAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull MainModel model) {
        holder.username.setText(model.getUsername());
        holder.account_id.setText(model.getAccount_id());
        holder.rating.setText(String.valueOf(model.getRating()));
        holder.userFeedback.setText(model.getUserFeedback());
        holder.date.setText(model.getDate());
        holder.ticketNumber.setText(model.getTicketNumber()); // Set the ticket number
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView username, account_id,rating,userFeedback, date;
        TextView ticketNumber; // Add the ticket number TextView

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            username = itemView.findViewById(R.id.username);
            account_id = itemView.findViewById(R.id.account_id);
            rating = itemView.findViewById(R.id.rating);
            userFeedback = itemView.findViewById(R.id.userFeedback);
            ticketNumber = itemView.findViewById(R.id.ticketNumber); // Initialize the ticket number TextView
        }
    }
}
