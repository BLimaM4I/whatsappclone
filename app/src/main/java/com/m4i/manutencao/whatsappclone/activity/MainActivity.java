package com.m4i.manutencao.whatsappclone.activity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.fragments.ContactsFragment;
import com.m4i.manutencao.whatsappclone.fragments.TalksFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase Authentication
        fbAuth = FirebaseConfiguration.getFirebaseAuth();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.MainToolbar);
        toolbar.setTitle("Whatsapp");
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.Exit:
                logOffUser();
                finish();
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
}
