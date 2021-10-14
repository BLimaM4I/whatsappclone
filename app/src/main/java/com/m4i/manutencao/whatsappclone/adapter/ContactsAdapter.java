//
// Author: Bruno Lima
// Company: M4I
// 11/10/2021 at 17:48
//

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
import com.m4i.manutencao.whatsappclone.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private final List<User> contacts;
    private final Context context;

    public ContactsAdapter(List<User> contactsList, Context c) {
        this.contacts = contactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = contacts.get(position);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        if (user.getPhoto() != null) {
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.standard_photo_24);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView photo;
        TextView name, email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.adapter_contacts_ivPhoto);
            name = itemView.findViewById(R.id.adapter_contacts_ivName);
            email = itemView.findViewById(R.id.adapter_contacts_ivEmail);

        }
    }
}
