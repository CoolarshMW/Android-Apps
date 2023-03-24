package com.example.sms.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sms.R;
import com.example.sms.Adapter.UserAdapter;
import com.example.sms.ModelClass.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    ArrayList<Users> usersArrayList;
    ImageView img_logOut,img_setting;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        isInternetOn();

        auth = FirebaseAuth.getInstance();
        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        img_logOut = findViewById(R.id.img_logOut);
        img_setting = findViewById(R.id.img_setting);

        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user");
        usersArrayList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(HomeActivity.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        if(auth.getCurrentUser()==null)
        {
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }

        img_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(HomeActivity.this,R.style.Dialoge);
                dialog.setContentView(R.layout.dialog_layout);

                TextView yesBtn,noBtn;

                yesBtn = dialog.findViewById(R.id.yesBtn);
                noBtn = dialog.findViewById(R.id.noBtn);

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    }
                });

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

       img_setting.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(HomeActivity.this,SettingActivity.class));
           }
       });

    }

    public void onBackPressed()
    {
        if(doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }

        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        doubleBackToExitPressedOnce = true;
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            Toast.makeText(this, "Connected to Internet", Toast.LENGTH_LONG).show();
            return true;


        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}