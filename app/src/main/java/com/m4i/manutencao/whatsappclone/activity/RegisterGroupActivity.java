package com.m4i.manutencao.whatsappclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.adapter.SelectedGroupAdapter;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.helper.RecyclerItemClickListener;
import com.m4i.manutencao.whatsappclone.model.Group;
import com.m4i.manutencao.whatsappclone.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterGroupActivity extends AppCompatActivity {

    private static final int PHOTO_GALLERY_SELECTED = 200;

    private final List<User> listSelectedMembers = new ArrayList<>();
    private TextView tvGroupName;
    private SelectedGroupAdapter selectedGroupAdapter;
    private RecyclerView rvSelectedMembers;
    private CircleImageView civGroupImage;
    private StorageReference storageReference;
    private Group group;
    private FloatingActionButton fabSaveGroup;
    private EditText etGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.register_group_activity_toolbar);
        toolbar.setTitle("New Group");
        toolbar.setSubtitle("Define the name");
        setSupportActionBar(toolbar);

        //Initial configs
        tvGroupName = findViewById(R.id.content_register_group_tvTotalParticipants);
        rvSelectedMembers = findViewById(R.id.content_register_group_rvGroupsMembers);
        civGroupImage = findViewById(R.id.content_register_group_civGroupImage);
        storageReference = FirebaseConfiguration.getFirebaseStorage();
        fabSaveGroup = findViewById(R.id.register_group_activity_fab);
        etGroupName = findViewById(R.id.content_register_group_etGroupName);
        group = new Group();

        //Config OnClick Listener
        civGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, PHOTO_GALLERY_SELECTED);
                }
            }
        });

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

        //Config floating action button

        fabSaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = etGroupName.getText().toString();
                //Add to the list the user that is currently logged to the app
                listSelectedMembers.add(FirebaseUserAccess.getDataFromLoggedUser());
                group.setMembers(listSelectedMembers);
                group.setName(groupName);
                group.save();

                Intent i = new Intent(RegisterGroupActivity.this, ChatActivity.class);
                i.putExtra("groupChat", group);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                Uri imageSelectedPath = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageSelectedPath);

                //Recover image from Firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageData = baos.toByteArray();

                //save image in Firebase
                final StorageReference imageRef = storageReference
                        .child("images")
                        .child("groups")
                        .child(group.getId() + ".jpeg");

                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterGroupActivity.this,
                                "Error uploading image",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(RegisterGroupActivity.this,
                                "Sucess on uploading image",
                                Toast.LENGTH_SHORT).show();

                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String url = task.getResult().toString();
                                group.setPhoto(url);
                            }
                        });
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (image != null) {
                civGroupImage.setImageBitmap(image);
            }

        }
    }
}