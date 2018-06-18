package me.morasquad.switchme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.txusballesteros.SnakeView;

import java.util.ArrayList;
import java.util.List;

public class SwitchActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference DeviceRef;
    private DatabaseReference UserRef;
    private DatabaseReference DataRef;
    private DatabaseReference EventRef;
    private Spinner spinnerParam;
    private Spinner spinnerCompare;
    private CheckBox eventSwitch;
    private Button setEvent;
    private LinearLayout eventLayaout;

    private DatabaseReference SensorRef;
    private TextView DeviceName, DeviceID, Temprature, Humidity;


    String currentUser;

    private SwitchCompat simpleSwitch;
    private TubeSpeedometer tubeSpeedometer,tubeSpeedometer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        eventLayaout = (LinearLayout) findViewById(R.id.event_layout);
        eventSwitch = (CheckBox) findViewById(R.id.events);
        setEvent = (Button) findViewById(R.id.set_event);
        DeviceName = (TextView) findViewById(R.id.switch_device_name);
        DeviceID = (TextView) findViewById(R.id.switch_device_id);
        Temprature = (TextView) findViewById(R.id.tem_text);
        Humidity = (TextView) findViewById(R.id.hum_text);
        spinnerParam = (Spinner) findViewById(R.id.param);
        spinnerParam.setOnItemSelectedListener(this);
        spinnerCompare = (Spinner) findViewById(R.id.compare);
        spinnerCompare.setOnItemSelectedListener(this);

        spinnerParamOnLoad();
        tubeSpeedometer = (TubeSpeedometer) findViewById(R.id.tubeSpeedometer);
        tubeSpeedometer.setMaxSpeed(1024);
        tubeSpeedometer2 = (TubeSpeedometer) findViewById(R.id.tubeSpeedometer2);
        tubeSpeedometer2.setMaxSpeed(1024);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        simpleSwitch = (SwitchCompat) findViewById(R.id.onoff_switch);
        final SnakeView snakeView = (SnakeView)findViewById(R.id.snake);
        snakeView.setMinValue(0);
        snakeView.setMaxValue(1024);

        final SnakeView snakeView1 = (SnakeView)findViewById(R.id.snake1);
        snakeView1.setMinValue(0);
        snakeView1.setMaxValue(1024);


        Intent intent = getIntent();
        String deviceName = intent.getStringExtra("deviceID");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DeviceRef = UserRef.child(currentUser).child("devices").child(deviceName);

        DataRef = FirebaseDatabase.getInstance().getReference().child(deviceName);

        eventSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eventSwitch.isChecked()){
                    eventLayaout.setVisibility(LinearLayout.VISIBLE);
                }else {
                    eventLayaout.setVisibility(LinearLayout.GONE);
                }
            }
        });



      /*    DataRef.addValueEventListener(new ValueEventListener() {
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
*/




        SensorRef = DataRef.child("weather");

        SensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("temprature")){
                        String val = dataSnapshot.child("temprature").getValue().toString();
                        snakeView.addValue(Integer.parseInt(val));
                        tubeSpeedometer.speedTo(Float.parseFloat(val));
                        Temprature.setText(val +"°C");

                    }else {
                        Toast.makeText(SwitchActivity.this, "Device Error!", Toast.LENGTH_SHORT).show();
                    }

                    if(dataSnapshot.hasChild("humidity")){
                        String val = dataSnapshot.child("humidity").getValue().toString();
                        snakeView1.addValue(Integer.parseInt(val));
                        tubeSpeedometer2.speedTo(Float.parseFloat(val));
                        Humidity.setText(val +"°C");
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
                    Toast.makeText(SwitchActivity.this, "Switch Turned ON", Toast.LENGTH_SHORT).show();
                }else {
                    DataRef.child("switch").setValue("0");
                    Toast.makeText(SwitchActivity.this, "Switch Turned OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    private void spinnerParamOnLoad() {

        List<String> parameter = new ArrayList<String>();
        parameter.add("Select Parameter");
        parameter.add("Temperature");
        parameter.add("Humidity");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, parameter);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParam.setAdapter(dataAdapter);

        List<String> compare = new ArrayList<String>();
        compare.add("Select Comparator");
        compare.add("<");
        compare.add(">");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, compare);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompare.setAdapter(dataAdapter2);
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


    @Override
    public void onItemSelected(Spinner parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        String item1 = "";
        String item2 = "";
        if(spinner.getId() == R.id.param)
        {
            item1 = parent.getSelectedItem().toString();

        }
        else if(spinner.getId() == R.id.compare)
        {
            item2 = parent.getSelectedItem().toString();
        }

        Toast.makeText(parent.getContext(), "Parameter: " + item1 + " Mom " + item2, Toast.LENGTH_LONG).show();

    }
}
