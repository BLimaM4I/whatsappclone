package com.m4i.manutencao.whatsappclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.adapter.ContactsAdapter;
import com.m4i.manutencao.whatsappclone.adapter.SelectedGroupAdapter;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.helper.RecyclerItemClickListener;
import com.m4i.manutencao.whatsappclone.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private final List<User> listMembers = new ArrayList<>();
    private final List<User> listSelectedMembers = new ArrayList<>();
    private RecyclerView rvSelectedMembers, rvMembers;
    private ContactsAdapter contactsAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    private ValueEventListener valueEventListenerMembers;
    private DatabaseReference usersRef;
    private FirebaseUser actualUser;
    private Toolbar toolbar;
    private FloatingActionButton fabNewGroup;

    public void updateMembersToolBar() {
        int totalSelected = listSelectedMembers.size();
        int total = listMembers.size() + listSelectedMembers.size();
        toolbar.setSubtitle(totalSelected + " of " + total + " selected");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //Toolbar
        toolbar = findViewById(R.id.activity_group_toolbar);
        toolbar.setTitle("New Group");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Initial config for recycler view
        rvMembers = findViewById(R.id.content_group_rvMembers);
        rvSelectedMembers = findViewById(R.id.content_group_rvSelectedMembers);
        fabNewGroup = findViewById(R.id.activity_group_fab);

        usersRef = FirebaseConfiguration.getFirebaseDatabase().child("users");
        actualUser = FirebaseUserAccess.getActualUser();

        //Config adapters
        contactsAdapter = new ContactsAdapter(listMembers, getApplicationContext());

        //Config recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvMembers.setLayoutManager(layoutManager);
        rvMembers.setHasFixedSize(true);
        rvMembers.setAdapter(contactsAdapter);

        rvMembers.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                rvMembers,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedUser = listMembers.get(position);

                        //Remove the selected from list
                        listMembers.remove(selectedUser);
                        contactsAdapter.notifyDataSetChanged();

                        //Add the remove user to the list of selected users
                        listSelectedMembers.add(selectedUser);
                        selectedGroupAdapter.notifyDataSetChanged();
                        updateMembersToolBar();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        //Config Recycler View for the selected members
        selectedGroupAdapter = new SelectedGroupAdapter(listSelectedMembers, getApplicationContext());
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        rvSelectedMembers.setLayoutManager(layoutManagerHorizontal);
        rvSelectedMembers.setHasFixedSize(true);
        rvSelectedMembers.setAdapter(selectedGroupAdapter);

        rvSelectedMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rvSelectedMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User selectedUser = listSelectedMembers.get(position);

                                //Remove from list of selected user
                                listSelectedMembers.remove(selectedUser);
                                selectedGroupAdapter.notifyDataSetChanged();

                                //Add it again to the contacts list
                                listMembers.add(selectedUser);
                                contactsAdapter.notifyDataSetChanged();
                                updateMembersToolBar();


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

        fabNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupActivity.this, RegisterGroupActivity.class);
                i.putExtra("members", (Serializable) listSelectedMembers);
                startActivity(i);
            }
        });

    }

    public void recoverContacts() {

        valueEventListenerMembers = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    User user = data.getValue(User.class);

                    String emailActualUser = actualUser.getEmail();
                    //If it is the current user logged to the app it shouldn't appear on contacts list
                    if (!emailActualUser.equals(user.getEmail())) {
                        listMembers.add(user);
                    }
                }
                contactsAdapter.notifyDataSetChanged();
                updateMembersToolBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerMembers);
    }

}