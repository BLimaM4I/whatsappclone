package com.m4i.manutencao.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.adapter.TalksAdapter;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.model.Conversation;

import java.util.ArrayList;
import java.util.List;

public class TalksFragment extends Fragment {

    private final List<Conversation> listConversations = new ArrayList<>();
    private RecyclerView recyclerViewTalks;
    private TalksAdapter adapter;

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

        return view;
    }

    public void recoverConversations() {
        String userIdentifier = FirebaseConfiguration.get
    }
}
