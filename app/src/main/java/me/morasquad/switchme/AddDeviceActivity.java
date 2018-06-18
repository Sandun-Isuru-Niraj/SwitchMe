package me.morasquad.switchme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.rey.material.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDeviceActivity extends AppCompatActivity {

    private EditText Username, DeviceID, DeviceName;
    private Button SaveInfoButton;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    private DatabaseReference DeviceRef;
    private ProgressDialog loadingbar;
    private StorageReference UserProfileImageRef;

    String current_user_id;
    final static int Gallery_pick = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images");

        loadingbar = new ProgressDialog(this);

        Username = (EditText) findViewById(R.id.setup_username);
        DeviceID = (EditText) findViewById(R.id.setup_primaryAddress);
        DeviceName = (EditText) findViewById(R.id.setup_device_name);
        SaveInfoButton = (Button) findViewById(R.id.setup_save);
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_image);

        SaveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetup();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, Gallery_pick);

            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);

                    }else {
                        Toast.makeText(AddDeviceActivity.this, "Please Select Profile Image First!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null){
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingbar.setTitle("Profile Image...");
                loadingbar.setMessage("Please wait, We are updating your Profile Image!");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                StorageReference filepath = UserProfileImageRef.child(current_user_id + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddDeviceActivity.this, "Your Image Uploaded!", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            UserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                loadingbar.dismiss();
                                                Intent setup = new Intent(AddDeviceActivity.this, AddDeviceActivity.class);
                                                startActivity(setup);
                                                Toast.makeText(AddDeviceActivity.this, "Profile Image Uploaded to Cloud!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                loadingbar.dismiss();
                                                String message = task.getException().getMessage();
                                                Toast.makeText(AddDeviceActivity.this, "Error! "+message, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }
                    }
                });
            }else {
                loadingbar.dismiss();
                Toast.makeText(this, "Error! Image Can't Croped!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void SaveAccountSetup() {

        String username = Username.getText().toString();
        String deviceAdd  = DeviceID.getText().toString();
        String deviceNme = DeviceName.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please Enter a Username!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(deviceAdd)){
            Toast.makeText(this, "Please Enter a Full Name!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(deviceNme)){
            Toast.makeText(this, "Please Enter a Country!", Toast.LENGTH_SHORT).show();
        }else {
            loadingbar.setTitle("Saving...");
            loadingbar.setMessage("Please wait, We are updating your details!");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("deviceId",deviceAdd);
            userMap.put("deviceName",deviceNme);

            UserRef.child("name").setValue(username);

            DeviceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id).child("devices").child(deviceAdd);


            DeviceRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){

                        loadingbar.dismiss();
                        Intent main = new Intent(AddDeviceActivity.this, MainActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main);
                        finish();
                        Toast.makeText(AddDeviceActivity.this, "Your Account is Created!", Toast.LENGTH_LONG).show();
                    }else {
                        loadingbar.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(AddDeviceActivity.this, "Error! "+ message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
