package com.example.customchu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class LogAdapter extends FirebaseRecyclerAdapter<LogModel,LogAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LogAdapter(@NonNull FirebaseRecyclerOptions<LogModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull LogModel model) {
        holder.date_and_time.setText(model.getDate_and_time());
        holder.account_id.setText(model.getAccount_id());
        holder.in.setText(Boolean.toString(model.getIn()));
        holder.out.setText(Boolean.toString(model.getOut()));
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log,parent,false);
        return new myViewHolder(view);
    }
    class myViewHolder extends RecyclerView.ViewHolder{
        TextView account_id, date_and_time,in,out;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            date_and_time = itemView.findViewById(R.id.date_and_time);
            account_id = itemView.findViewById(R.id.account_id);
            in = itemView.findViewById(R.id.in);
            out = itemView.findViewById(R.id.out);
        }
    }
}
