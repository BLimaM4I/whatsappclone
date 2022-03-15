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
import com.m4i.manutencao.whatsappclone.activity.GroupActivity;
import com.m4i.manutencao.whatsappclone.adapter.ContactsAdapter;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.helper.RecyclerItemClickListener;
import com.m4i.manutencao.whatsappclone.model.User;

import java.util.ArrayList;
import java.util.List;

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

                                List<User> listUsersUpdated = contactsAdapter.getContacts();

                                User userSelected = listUsersUpdated.get(position);

                                boolean header = userSelected.getEmail().isEmpty();

                                if (header) {
                                    Intent i = new Intent(getActivity(), GroupActivity.class);
                                    startActivity(i);

                                } else {
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("contactsChat", userSelected);
                                    startActivity(i);
                                }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        addNewGroupOption();

        return view;
    }

    private void addNewGroupOption() {
        //For a group we use an empty email field
        User groupItem = new User();
        groupItem.setName("New Group");
        groupItem.setEmail("");
        contactsList.add(groupItem);
    }

    public void recoverContacts() {

        valueEventListenerContacts = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cleanContactsList();

                for (DataSnapshot data : snapshot.getChildren()) {

                    User user = data.getValue(User.class);

                    String emailActualUser = actualUser.getEmail();
                    //If it is the current user logged to the app it shouldn't appear on contacts list
                    if (!emailActualUser.equals(user.getEmail())) {
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

    public void cleanContactsList() {
        contactsList.clear();
        addNewGroupOption();

    }

    public void searchContacts(String text) {

        List<User> listContactsFiltered = new ArrayList<>();

        for (User user : contactsList) {
            String name = user.getName().toLowerCase();
            if (name.contains(text)) {
                listContactsFiltered.add(user);
            }
        }

        contactsAdapter = new ContactsAdapter(listContactsFiltered, getActivity());
        rvContactsLists.setAdapter(contactsAdapter);
        contactsAdapter.notifyDataSetChanged();

    }

    public void recoverAllContacts() {
        contactsAdapter = new ContactsAdapter(contactsList, getActivity());
        rvContactsLists.setAdapter(contactsAdapter);
        contactsAdapter.notifyDataSetChanged();
    }

}