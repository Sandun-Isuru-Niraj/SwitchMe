package me.morasquad.switchme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SwitchActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference DeviceRef;
    private DatabaseReference UserRef;
    private DatabaseReference DataRef;
    private TextView DeviceName, DeviceID;


    String currentUser;

    private Switch simpleSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        DeviceName = (TextView) findViewById(R.id.switch_device_name);
        DeviceID = (TextView) findViewById(R.id.switch_device_id);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        simpleSwitch = (Switch) findViewById(R.id.onoff_switch);


        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("deviceID");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DeviceRef = UserRef.child(currentUser).child("devices").child(deviceName);

        DataRef = FirebaseDatabase.getInstance().getReference().child(deviceName);
        DataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("switch")){
                        if(dataSnapshot.child("switch").getValue().toString().equals("1")){
                            simpleSwitch.setChecked(true);
                        }else {
                            simpleSwitch.setChecked(false);
                        }

                    }else {
                        Toast.makeText(SwitchActivity.this, "Device Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DeviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("deviceId")){
                        DeviceID.setText(dataSnapshot.child("deviceId").getValue().toString());
                    }

                    if(dataSnapshot.hasChild("deviceName")){
                        DeviceName.setText(dataSnapshot.child("deviceName").getValue().toString());
                    }else {
                        Toast.makeText(SwitchActivity.this, "Error has occured!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("SwitchMe | "+deviceName);

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    DataRef.child("switch").setValue("1");
                    Toast.makeText(SwitchActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                }else {
                    DataRef.child("switch").setValue("0");
                    Toast.makeText(SwitchActivity.this, "Not", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id  = item.getItemId();

        if(id == android.R.id.home){
            SendUsertoMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUsertoMainActivity() {

        Intent home = new Intent(SwitchActivity.this, MainActivity.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();

    }


}
