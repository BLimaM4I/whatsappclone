package com.m4i.manutencao.whatsappclone.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.helper.Permissions;
import com.m4i.manutencao.whatsappclone.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;

public class
SettingsActivity extends AppCompatActivity {

    private static final int CAMERA_SELECTED = 100;
    private static final int PHOTO_GALLERY_SELECTED = 200;
    private final String[] neededPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private CircleImageView circleImageViewProfileImage;
    private EditText etProfileName;
    private ImageView ivChangeName;
    private StorageReference storageReference;
    private String userID;
    private User userLogged;
    private ImageButton imageButtonCamera, getImageButtonPhotoGallery;

    //teste
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Initial settings
        storageReference = FirebaseConfiguration.getFirebaseStorage();
        userID = FirebaseUserAccess.getUserId();
        userLogged = FirebaseUserAccess.getDataFromLoggedUser();

        //Validate permissions
        Permissions.validatePermissions(neededPermissions, this, 1);

        //Image buttons instantiate
        imageButtonCamera = findViewById(R.id.ibCamera);
        getImageButtonPhotoGallery = findViewById(R.id.ibPhotoGallery);

        //Profile Photo
        circleImageViewProfileImage = findViewById(R.id.civProfilePhoto);

        //Name of the profile
        etProfileName = findViewById(R.id.etProfileName);
        ivChangeName = findViewById(R.id.ivChangeName);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.MainToolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        //Show back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recover data from user
        FirebaseUser user = FirebaseUserAccess.getActualUser();
        Uri url = user.getPhotoUrl();

        //User photo
        if (url != null) {
            Glide.with(SettingsActivity.this)
                    .load(url)
                    .into(circleImageViewProfileImage);
        } else {
            circleImageViewProfileImage.setImageResource(R.drawable.standard_photo_24);
        }

        //User name
        etProfileName.setText(user.getDisplayName());

        //Methods for image button clicks
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, CAMERA_SELECTED);
                }

            }
        });

        getImageButtonPhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //comment to shelve
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, PHOTO_GALLERY_SELECTED);
                }

            }
        });

        ivChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etProfileName.getText().toString();
                boolean returnValue = FirebaseUserAccess.updateUserName(name);
                if (returnValue) {

                    userLogged.setName(name);
                    userLogged.update();

                    Toast.makeText(SettingsActivity.this, "Name changed successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;
            try {
                switch (requestCode) {
                    case CAMERA_SELECTED:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case PHOTO_GALLERY_SELECTED:
                        Uri imageSelectedPath = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageSelectedPath);
                        break;

                }
                if (image != null) {
                    circleImageViewProfileImage.setImageBitmap(image);

                    //Recover image from Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //save image in Firebase
                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("userProfilePhoto")
                            .child(userID + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SettingsActivity.this,
                                    "Error uploading image",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SettingsActivity.this,
                                    "Sucess on uploading image",
                                    Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    updateUserPhoto(url);
                                }
                            });
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void updateUserPhoto(Uri url) {
        boolean returnedValue = FirebaseUserAccess.updateUserPhoto(url);
        if (returnedValue) {
            userLogged.setPhoto(url.toString());
            userLogged.update();

            Toast.makeText(this, "Your photo was changed successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                alertValidationPermission();
            }
        }
    }

    private void alertValidationPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions denied!");
        builder.setMessage("To use the app we need to access the camera and disk storage");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }
}
