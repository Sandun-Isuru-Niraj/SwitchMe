package me.morasquad.switchme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView deviceList;
    private Toolbar mToolbar;
    private Context mContect = MainActivity.this;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUsername;
    private ImageButton AddNewDeviceButton;

    private FirebaseAuth mAuth;
    private DatabaseReference DeviceRef;
    private DatabaseReference DataRef;
    private DatabaseReference UserRef;


    String currentUser;
    public FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SwitchMe | Home");


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        DeviceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("devices");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        AddNewDeviceButton = (ImageButton) findViewById(R.id.add_new_post_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        deviceList = (RecyclerView) findViewById(R.id.all_devices);
        deviceList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        deviceList.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUsername = (TextView) navView.findViewById(R.id.nav_user_full_name);

        UserRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("name")){
                        String fullname = dataSnapshot.child("name").getValue().toString();
                        NavProfileUsername.setText(fullname);
                    }

                    if(dataSnapshot.hasChild("profileimage")){
                        String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(NavProfileImage);
                    }else {
                        Toast.makeText(MainActivity.this, "Profile Image Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        AddNewDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUsertoPostActivity();
            }
        });

        DisplayAllDevices();

    }

    private void SendUsertoPostActivity() {

        Intent addDevice = new Intent(MainActivity.this, AddNormalDeviceActivity.class);
        startActivity(addDevice);
    }

    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()){

            case R.id.home :
                break;

            case R.id.add_device:
                Intent addDevice = new Intent(MainActivity.this, AddNormalDeviceActivity.class);
                startActivity(addDevice);
                break;


            case R.id.nav_logout:

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mAuth.signOut();
                                SendUsertoLoginActivity();
                                Toast.makeText(MainActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContect);
                builder.setMessage("Are you need to Logout?")
                        .setNegativeButton("No", dialogClickListener)
                        .setPositiveButton("Yes", dialogClickListener).show();

                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DisplayAllDevices(){

       firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Devices, DeviceViewHolder>(

                Devices.class,
                R.layout.all_device,
                DeviceViewHolder.class,
                DeviceRef
        ) {
            @Override
            protected void populateViewHolder(final DeviceViewHolder viewHolder, final Devices model, final int position) {

                viewHolder.setDeviceName(model.getDeviceName());
                viewHolder.setDeviceID(model.getDeviceId());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                        String device = dbRef.getKey();

                        Intent switch1 = new Intent(MainActivity.this, SwitchActivity.class);
                        switch1.putExtra("deviceID", device);
                        startActivity(switch1);

                    }
                });

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        DatabaseReference dbRef = firebaseRecyclerAdapter.getRef(position);
                        final String device = dbRef.getKey();

                        PopupMenu popup = new PopupMenu(MainActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {



                                switch (menuItem.getItemId()){

                                    case R.id.delete_switch:

                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int choice) {
                                                switch (choice) {
                                                    case DialogInterface.BUTTON_POSITIVE:

                                                        DeviceRef.child(device).removeValue();
                                                        Toast.makeText(mContect, "Device Removed!", Toast.LENGTH_SHORT).show();

                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContect);
                                        builder.setMessage("Remove this Device?")
                                                .setPositiveButton("Yes", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();


                                }
                                return true;
                            }
                        });
                        popup.show();
                        return false;
                    }
                });

            }


        };

        deviceList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder{

        View mView;



        public DeviceViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setDeviceName(String deviceName){
            TextView DeviceName = (TextView) mView.findViewById(R.id.device_name);
            DeviceName.setText(deviceName);
        }

        public void setDeviceID(String deviceId){
            TextView DeviceID = (TextView) mView.findViewById(R.id.device_id);
            DeviceID.setText(deviceId);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUsers = mAuth.getCurrentUser();

        if(currentUsers == null){
            SendUsertoLoginActivity();
        }else {
            CheckUserExistance();
        }
    }

    private void SendUsertoLoginActivity() {

        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    private void CheckUserExistance(){

        final String current_user_id = mAuth.getCurrentUser().getUid();

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id)){
                    SendUsertoAddDeviceActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUsertoAddDeviceActivity() {

        Intent add = new Intent(MainActivity.this, AddDeviceActivity.class);
        add.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(add);
        finish();
    }
}
