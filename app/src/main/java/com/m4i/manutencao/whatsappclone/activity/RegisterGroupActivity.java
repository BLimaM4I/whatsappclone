package com.m4i.manutencao.whatsappclone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.adapter.SelectedGroupAdapter;
import com.m4i.manutencao.whatsappclone.helper.RecyclerItemClickListener;
import com.m4i.manutencao.whatsappclone.model.User;

import java.util.ArrayList;
import java.util.List;

public class RegisterGroupActivity extends AppCompatActivity {

    private final List<User> listSelectedMembers = new ArrayList<>();
    private Toolbar toolbar;
    private TextView tvGroupName;
    private SelectedGroupAdapter selectedGroupAdapter;
    private RecyclerView rvSelectedMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);

        //Toolbar
        toolbar = findViewById(R.id.register_group_activity_toolbar);
        toolbar.setTitle("New Group");
        toolbar.setSubtitle("Define the name");
        setSupportActionBar(toolbar);

        //Textview
        tvGroupName = findViewById(R.id.content_register_tvTotalParticipants);
        rvSelectedMembers = findViewById(R.id.content_register_rvGroupsMembers);

        FloatingActionButton fab = findViewById(R.id.register_group_activity_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recover the list of members received by intent
        if (getIntent().getExtras() != null) {
            List<User> membersSelected = (List<User>) getIntent().getExtras().getSerializable("members");
            listSelectedMembers.addAll(membersSelected);

            //TexView test to see if we are receiving the members by intent
            tvGroupName.setText("Participants: " + listSelectedMembers.size());
        }

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

    }
}