/*
 *    Created by BLimaM4I
 *    Date: 07/03/2022
 */

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

public class SelectedGroupAdapter extends RecyclerView.Adapter<SelectedGroupAdapter.MyViewHolder> {

    private final List<User> selectedContacts;
    private final Context context;

    public SelectedGroupAdapter(List<User> contactsList, Context c) {
        this.selectedContacts = contactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public SelectedGroupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_group_selected, parent, false);
        return new SelectedGroupAdapter.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedGroupAdapter.MyViewHolder holder, int position) {
        User user = selectedContacts.get(position);

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
        return selectedContacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView photo;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.adapter_group_selected_ivImage);
            name = itemView.findViewById(R.id.adapter_group_selected_ivTitle);

        }
    }
}