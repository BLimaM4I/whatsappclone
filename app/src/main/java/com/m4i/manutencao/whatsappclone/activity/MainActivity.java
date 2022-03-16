package com.m4i.manutencao.whatsappclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.fragments.ContactsFragment;
import com.m4i.manutencao.whatsappclone.fragments.TalksFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private MaterialSearchView materialSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase Authentication
        fbAuth = FirebaseConfiguration.getFirebaseAuth();


        //Toolbar
        Toolbar toolbar = findViewById(R.id.MainToolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar); //To support previous Android versions

        //Tabs configurations
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Talks", TalksFragment.class)
                        .add("Contacts", ContactsFragment.class)
                        .create()
        );
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        //Search View
        materialSearchView = findViewById(R.id.materialSearchMain);

        //Listener para search view
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                TalksFragment talksFragment = (TalksFragment) adapter.getPage(0);
                talksFragment.recoverAllConversation();
            }
        });

        //Listener for query
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@NonNull String query) {
//                Log.d("evento", "OnQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(@NonNull String newText) {
                //position: 0 -> TalksFragment position: 1 -> ContactsFragment
                //Checks if it is the conversations or contacts fragment
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        TalksFragment talksFragment = (TalksFragment) adapter.getPage(0);
                        if (!newText.isEmpty()) {
                            talksFragment.searchConversations(newText.toLowerCase());
                        } else {
                            talksFragment.recoverAllConversation();
                        }
                        break;
                    case 1:
                        ContactsFragment contactsFragment = (ContactsFragment) adapter.getPage(1);
                        if (!newText.isEmpty()) {
                            contactsFragment.searchContacts(newText.toLowerCase());
                        } else {
                            contactsFragment.recoverAllContacts();
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //Config search button
        MenuItem item = menu.findItem(R.id.menuSearch);
        materialSearchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_exit:
                logOffUser();
                finish();
                break;
            case R.id.menu_settings:
                openSettingsActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOffUser() {
        try {
            fbAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

}