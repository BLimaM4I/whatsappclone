package com.m4i.manutencao.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.model.Conversation;
import com.m4i.manutencao.whatsappclone.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class TalksAdapter extends RecyclerView.Adapter<TalksAdapter.MyViewHolder> {

    private final List<Conversation> conversations;
    private final Context context;

    public TalksAdapter(List<Conversation> list, Context c) {
        this.conversations = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Conversation conversation = conversations.get(position);
        holder.lastMessage.setText(conversation.getLastMessage());

        User user = conversation.getUserLastMessage();
        holder.name.setText(user.getName());

        if (user.getPhoto() != null) {
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.standard_photo_24);
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView photo;
        TextView name, lastMessage;

        public MyViewHolder(View itemView) {

            super(itemView);
            photo = itemView.findViewById(R.id.adapter_contacts_ivImage);
            name = itemView.findViewById(R.id.adapter_contacts_ivTitle);
            lastMessage = itemView.findViewById(R.id.adapter_contacts_ivSubTitle);


        }
    }
}
