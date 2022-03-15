package com.m4i.manutencao.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.activity.ChatActivity;
import com.m4i.manutencao.whatsappclone.adapter.TalksAdapter;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.helper.RecyclerItemClickListener;
import com.m4i.manutencao.whatsappclone.model.Conversation;

import java.util.ArrayList;
import java.util.List;

public class TalksFragment extends Fragment {

    private final List<Conversation> listConversations = new ArrayList<>();
    private RecyclerView recyclerViewTalks;
    private TalksAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversationsRef;
    private ChildEventListener childEventListenerConversations;

    public TalksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_talks, container, false);
        recyclerViewTalks = view.findViewById(R.id.fragment_talks_recyclerView);

        //Adapter configuration
        adapter = new TalksAdapter(listConversations, getActivity());

        //Recyclerview configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTalks.setLayoutManager(layoutManager);
        recyclerViewTalks.setHasFixedSize(true);
        recyclerViewTalks.setAdapter(adapter);

        //Config Click Event on item
        recyclerViewTalks.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewTalks,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Conversation selectedConversation = listConversations.get(position);

                                if (selectedConversation.getIsGroup().equals("true")) {
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("groupChat", selectedConversation.getGroup());
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("contactsChat", selectedConversation.getUserLastMessage());
                                    startActivity(i);
                                }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        //Config Conversations ref
        String userIdentifier = FirebaseUserAccess.getUserId();
        database = FirebaseConfiguration.getFirebaseDatabase();
        conversationsRef = database.child("conversations")
                .child(userIdentifier);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        receiveUpdatedConversationsFirebase();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationsRef.removeEventListener(childEventListenerConversations);
    }

    public void searchConversations(String text) {
//        Log.d("evento", text);
        List<Conversation> listConversationsFiltered = new ArrayList<>();

        for (Conversation conversation : listConversations) {

            if (conversation.getIsGroup().equals("false")) {
                String name = conversation.getUserLastMessage().getName().toLowerCase();
                String lastMessage = conversation.getLastMessage().toLowerCase();

                if (name.contains(text) || lastMessage.contains(text)) {
                    listConversationsFiltered.add(conversation);
                }
            } else {
                String name = conversation.getGroup().getName().toLowerCase();
                String lastMessage = conversation.getLastMessage().toLowerCase();

                if (name.contains(text) || lastMessage.contains(text)) {
                    listConversationsFiltered.add(conversation);
                }
            }
        }

        adapter = new TalksAdapter(listConversationsFiltered, getActivity());
        recyclerViewTalks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void recoverAllConversation() {
        adapter = new TalksAdapter(listConversations, getActivity());
        recyclerViewTalks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void receiveUpdatedConversationsFirebase() {


        childEventListenerConversations = conversationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                //Recover conversation
                Conversation conversation = snapshot.getValue(Conversation.class);
                listConversations.add(conversation);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
