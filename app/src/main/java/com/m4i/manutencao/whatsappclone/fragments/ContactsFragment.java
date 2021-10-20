package com.m4i.manutencao.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.activity.ChatActivity;
import com.m4i.manutencao.whatsappclone.adapter.ContactsAdapter;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.helper.RecyclerItemClickListener;
import com.m4i.manutencao.whatsappclone.model.User;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private final ArrayList<User> contactsList = new ArrayList<>();
    private FirebaseUser actualUser;
    private DatabaseReference usersRef;
    private ContactsAdapter contactsAdapter;
    private RecyclerView rvContactsLists;
    private ValueEventListener valueEventListenerContacts;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerContacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        //initial configuration of recycler view
        rvContactsLists = view.findViewById(R.id.fragment_contacts_recyclerView);

        //Adapter config
        contactsAdapter = new ContactsAdapter(contactsList, getActivity());
        usersRef = FirebaseConfiguration.getFirebaseDatabase().child("users");
        actualUser = FirebaseUserAccess.getActualUser();

        //recyclerView config
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        rvContactsLists.setLayoutManager(layoutManager);
        rvContactsLists.setHasFixedSize(true);
        rvContactsLists.setAdapter(contactsAdapter);

        //config recyclerview click event
        rvContactsLists.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                rvContactsLists,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User userSelected = contactsList.get(position);
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra("contactsChat", userSelected);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        return view;
    }

    public void recoverContacts() {

        valueEventListenerContacts = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    User user = data.getValue(User.class);

                    String emailActualUSer = actualUser.getEmail();
                    //If it is the current user logged to the app it shouldn't appear on contacts list
                    if (!emailActualUSer.equals(user.getEmail())) {
                        contactsList.add(user);
                    }
                }
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
