package me.morasquad.switchme;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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

    private CircleImageView NavProfileImage;
    private TextView NavProfileUsername;
    private ImageButton AddNewDeviceButton;

    private FirebaseAuth mAuth;
    private DatabaseReference DeviceRef;
    private DatabaseReference DataRef;
    private DatabaseReference UserRef;

    String currentUser;

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
    }

    private void UserMenuSelector(MenuItem item) {
    }

    private void DisplayAllDevices(){

        FirebaseRecyclerAdapter<Devices, DeviceViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Devices, DeviceViewHolder>(

                        Devices.class,
                        R.layout.all_device,
                        DeviceViewHolder.class,
                        DeviceRef
                ) {
                    @Override
                    protected void populateViewHolder(DeviceViewHolder viewHolder, Devices model, int position) {

                    }
                };

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

        public void setDeviceID(String deviceID){
            TextView DeviceID = (TextView) mView.findViewById(R.id.device_id);
            DeviceID.setText(deviceID);
        }
    }

}
