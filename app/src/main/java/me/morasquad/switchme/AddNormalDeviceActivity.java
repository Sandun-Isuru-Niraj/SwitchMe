package me.morasquad.switchme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddNormalDeviceActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private Button AddDeviceButton;
    private EditText DeviceID;
    private EditText DeviceName;
    private DatabaseReference UserRef, DeviceRef;
    private FirebaseAuth mAuth;

    private String deviceID, deviceName, currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_normal_device);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DeviceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("devices");

        loadingBar = new ProgressDialog(this);

        DeviceID = (EditText) findViewById(R.id.normal_deviceID);
        DeviceName = (EditText) findViewById(R.id.normal_device_name);

        mToolbar = (Toolbar) findViewById(R.id.add_device_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("SwitchMe | Add new Device");

        AddDeviceButton = (Button) findViewById(R.id.nrmal_add_device);

        AddDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDevice();
            }
        });



    }

    private void AddDevice() {
        deviceID = DeviceID.getText().toString();
        deviceName = DeviceName.getText().toString();

        if(TextUtils.isEmpty(deviceID)){
            Toast.makeText(this, "You need to add Device ID", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(deviceName)){
            Toast.makeText(this, "You Need to add Device Name", Toast.LENGTH_SHORT).show();
        }else {
            HashMap deviceMap = new HashMap();
            deviceMap.put("deviceId", deviceID);
            deviceMap.put("deviceName", deviceName);

            DeviceRef.child(deviceID).updateChildren(deviceMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        loadingBar.dismiss();
                        Intent main = new Intent(AddNormalDeviceActivity.this, MainActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main);
                        Toast.makeText(AddNormalDeviceActivity.this, "Devices Added.", Toast.LENGTH_SHORT).show();
                    }else {
                        loadingBar.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(AddNormalDeviceActivity.this, "Error! - "+message, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id  = item.getItemId();

        if(id == android.R.id.home){

            Intent home = new Intent(AddNormalDeviceActivity.this, MainActivity.class);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
