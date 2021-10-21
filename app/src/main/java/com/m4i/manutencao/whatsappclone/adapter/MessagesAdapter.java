//
// Author: Bruno Lima
// Company: M4I
// 20/10/2021 at 15:30
//

package com.m4i.manutencao.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.model.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private static final int TYPE_SENDER = 0;
    private static final int TYPE_RECEIVER = 1;
    private final List<Message> messages;
    private final Context context;

    public MessagesAdapter(List<Message> list, Context c) {
        this.messages = list;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if (viewType == TYPE_SENDER) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_sender, parent, false);
        } else if (viewType == TYPE_RECEIVER) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_receiver, parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messages.get(position);
        String msg = message.getMessage();
        String photo = message.getPhoto();

        if (photo != null) {
            Uri url = Uri.parse(photo);
            Glide.with(context).load(url).into(holder.photo);
            //Hide text
            holder.message.setVisibility(View.GONE);
        } else {
            holder.message.setText(msg);
            //Hide photo
            holder.photo.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        String idUser = FirebaseUserAccess.getUserId();
        if (idUser.equals(msg.getIdUser())) {
            return TYPE_SENDER;
        }
        return TYPE_RECEIVER;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        ImageView photo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.adapter_messages_tvMessageTxt);
            photo = itemView.findViewById(R.id.adapter_messages_ivPhoto);
        }
    }
}
