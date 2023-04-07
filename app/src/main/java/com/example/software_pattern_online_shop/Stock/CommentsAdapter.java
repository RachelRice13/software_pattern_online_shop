package com.example.software_pattern_online_shop.Stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.software_pattern_online_shop.Model.Comment;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ExampleViewHolder> {
    private List<Comment> commentList;
    private Context context;

    public CommentsAdapter (List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView username, comment;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.comment_username);
            comment = itemView.findViewById(R.id.comment_message);
            cardView = itemView.findViewById(R.id.comment_card_view);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_row_layout, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.username.setText(comment.getUserName());
        holder.comment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}