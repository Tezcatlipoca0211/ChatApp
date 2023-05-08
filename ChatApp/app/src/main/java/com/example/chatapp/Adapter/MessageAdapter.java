package com.example.chatapp.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.ViewHolder {
    public CircleImageView civAvatarUserOne;
    public TextView tvSmsUserOne, tvTimeMessageUserOne, tvSmsUserTwo, tvTimeMessageUserTwo;
    public MessageAdapter(@NonNull View itemView) {
        super(itemView);
        civAvatarUserOne =  itemView.findViewById(R.id.civAvatarUserOne);
        tvSmsUserOne =  itemView.findViewById(R.id.tvSmsUserOne);
        tvTimeMessageUserOne =  itemView.findViewById(R.id.tvTimeMessageUserOne);
        tvSmsUserTwo =  itemView.findViewById(R.id.tvSmsUserTwo);
        tvTimeMessageUserTwo =  itemView.findViewById(R.id.tvTimeMessageUserTwo);
    }
}
