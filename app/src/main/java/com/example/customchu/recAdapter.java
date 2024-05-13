package com.example.customchu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class recAdapter extends FirebaseRecyclerAdapter<recModel, recAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public recAdapter(@NonNull FirebaseRecyclerOptions<recModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull recAdapter.myViewHolder holder, int position, @NonNull recModel model) {
        holder.recName.setText(model.getSender_name());
        holder.recUid.setText(String.valueOf(model.getSender_uid()));
    }

    @NonNull
    @Override
    public recAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currentrequest_item, parent, false);
        return new recAdapter.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView recName, recUid;

        public myViewHolder(@NonNull View itemView){
            super(itemView);

            recName = (TextView)itemView.findViewById(R.id.recName);
            recUid = (TextView)itemView.findViewById(R.id.recUid);
        }
    }
}
