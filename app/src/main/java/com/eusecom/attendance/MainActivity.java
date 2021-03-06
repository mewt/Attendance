/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eusecom.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.eusecom.attendance.models.Attendance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;

import static com.squareup.picasso.NetworkPolicy.NO_CACHE;
import static java.lang.System.out;

public class MainActivity extends ActionBarActivity {

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    private String[] navMenuTitles;
    int ICONS[] = {R.drawable.ic_image_edit,
            R.drawable.ic_image_edit, R.drawable.ic_image_edit,
            R.drawable.ic_image_edit, R.drawable.ic_image_edit,
            R.drawable.ic_image_edit };

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = "EuroSecom";
    String EMAIL = "edcom@edcom.sk";
    int PROFILE = R.drawable.add2new;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "MainActivity";
    private TextView mStatusTextView, usicoTextView, mText3;
    FirebaseUser user;
    private ImageView mCompanyImage, mUserImage, mIntoworkImage;
    private ImageButton intowork, outsidework, imglogin, imgnepritomnost;
    private String userIDX = "";
    ValueEventListener connlist;
    DatabaseReference connectedRef;
    AlertDialog dialognoico = null;
    static final int REQUEST_IMAGE_OPENUSER = 1;
    static final int REQUEST_IMAGE_OPENICO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_attendance);

        //Create Folder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            } else {
                //File folder = new File("/storage/emulated/0/eusecom/attendance");
                File folder = new File(Environment.getExternalStorageDirectory().toString()+"/eusecom/attendance");
                if(!folder.exists()) {
                    folder.mkdirs();
                }
            }



        }else{

            File folder = new File(Environment.getExternalStorageDirectory().toString()+"/eusecom/attendance");
            if(!folder.exists()) {
                folder.mkdirs();
            }
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        View backgroundImage = findViewById(R.id.background);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(20);

        mStatusTextView = (TextView) findViewById(R.id.status);
        usicoTextView = (TextView) findViewById(R.id.usico);
        mCompanyImage = (ImageView) findViewById(R.id.mycompany);
        mUserImage = (ImageView) findViewById(R.id.imglogin);
        mIntoworkImage = (ImageView) findViewById(R.id.intowork);

        mText3 = (TextView) findViewById(R.id.text3);





        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);


        if (BuildConfig.RELEASEMODE) {
            String TITLES[] = {navMenuTitles[0], navMenuTitles[1]};
            mAdapter = new MainDrawerAdapter(this, TITLES, ICONS, NAME, EMAIL, PROFILE);
        }else{
            String TITLES2[] = {navMenuTitles[0], navMenuTitles[1], navMenuTitles[2], navMenuTitles[3], navMenuTitles[4], navMenuTitles[5]};
            mAdapter = new MainDrawerAdapter(this, TITLES2, ICONS, NAME, EMAIL, PROFILE);
        }

              // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }


        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userIDX = user.getUid();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userIDX = "";
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener

        invalidateOptionsMenu();

        intowork = (ImageButton) findViewById(R.id.intowork);
        intowork.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (user != null) {


                    if (SettingsActivity.getUsIco(MainActivity.this).equals("0")) {
                        getNoIcoAlert();
                    } else {

                        String usatwx = SettingsActivity.getUsAtw(MainActivity.this);
                        if (usatwx.equals("0")) {

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(getString(R.string.incoming))
                                    .setMessage(getString(R.string.qincoming))
                                    .setPositiveButton(R.string.textyes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {

                                                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    String icox = SettingsActivity.getUsIco(MainActivity.this);
                                                    String oscx = SettingsActivity.getUsOsc(MainActivity.this);
                                                    String usnx = SettingsActivity.getUsname(MainActivity.this);
                                                    Long tsLong = System.currentTimeMillis() / 1000;
                                                    String ts = tsLong.toString();

                                                    writeAttendance(icox, userId, "0", "1", "Incoming work", ts, ts, "0", "0", "0", "0", ts, oscx, usnx);

                                                }
                                            })
                                    .setNegativeButton(R.string.textno,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {
                                                    // ignore, just dismiss

                                                }
                                            })
                                    .show();

                        } else {

                            String texttoast = getString(R.string.atwork);
                            Toast.makeText(MainActivity.this, texttoast, Toast.LENGTH_SHORT).show();

                        }

                    }


                } else {

                    String texttoast = getString(R.string.loginfb);
                    Toast.makeText(MainActivity.this, texttoast, Toast.LENGTH_SHORT).show();
                }

            }
        });

        outsidework = (ImageButton) findViewById(R.id.outsidework);
        outsidework.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (user != null) {

                    if (SettingsActivity.getUsIco(MainActivity.this).equals("0")) {
                        getNoIcoAlert();
                    } else {

                        String usatwx = SettingsActivity.getUsAtw(MainActivity.this);
                        if (usatwx.equals("1")) {

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(getString(R.string.leaving))
                                    .setMessage(getString(R.string.qleaving))
                                    .setPositiveButton(R.string.textyes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {

                                                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    String icox = SettingsActivity.getUsIco(MainActivity.this);
                                                    String oscx = SettingsActivity.getUsOsc(MainActivity.this);
                                                    String usnx = SettingsActivity.getUsname(MainActivity.this);
                                                    Long tsLong = System.currentTimeMillis() / 1000;
                                                    String ts = tsLong.toString();

                                                    writeAttendance(icox, userId, "0", "2", "Leaving work", ts, ts, "0", "0", "0", "0", ts, oscx, usnx);

                                                }
                                            })
                                    .setNegativeButton(R.string.textno,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {
                                                    // ignore, just dismiss

                                                }
                                            })
                                    .show();

                        } else {
                            String texttoast = getString(R.string.outwork);
                            Toast.makeText(MainActivity.this, texttoast, Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {

                    String texttoast = getString(R.string.loginfb);
                    Toast.makeText(MainActivity.this, texttoast, Toast.LENGTH_SHORT).show();

                }
            }
        });

        imglogin = (ImageButton) findViewById(R.id.imglogin);
        imglogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                            Intent i = new Intent(getApplicationContext(), EmailPasswordActivity.class);
                            startActivity(i);

            }
        });

        imgnepritomnost = (ImageButton) findViewById(R.id.imgnepritomnost);
        imgnepritomnost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (user != null) {

                    if (SettingsActivity.getUsIco(MainActivity.this).equals("0")) {
                        getNoIcoAlert();
                    } else {

                        Intent i = new Intent(getApplicationContext(), AbsenceActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("fromact", "0");
                        extras.putString("idemp", "0");
                        i.putExtras(extras);
                        startActivity(i);

                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent i = new Intent(getApplicationContext(), EmailPasswordActivity.class);
                    startActivity(i);
                }

            }
        });


    }//end oncreate

    @Override
    protected void onResume() {
        super.onResume();

        //SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (dialognoico != null && dialognoico.isShowing()) {
                dialognoico.dismiss();
                dialognoico=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // [START basic_write]
    private void writeAttendance(String usico, String usid, String ume, String dmxa, String dmna, String daod, String dado, String dnixa,
                                 String hodxb, String longi, String lati, String datm, String usosc, String usname) {

        String key = mDatabase.child("attendances").push().getKey();
        String gpslat;
        String gpslon;
        GPSTracker mGPS = new GPSTracker(MainActivity.this);
        gpslat = "0";
        gpslon = "0";

        if (mGPS.canGetLocation) {
            mGPS.getLocation();
            gpslat = "" + mGPS.getLatitude();
            gpslon = "" + mGPS.getLongitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            mGPS.showSettingsAlert();
        }
        mGPS.stopUsingGPS();

        Attendance attendance = new Attendance(usico, usid, ume, dmxa, dmna, daod, dado, dnixa, hodxb, gpslon, gpslat, datm, usosc, usname);

        Map<String, Object> attValues = attendance.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/attendances/" + key, attValues);
        childUpdates.put("/company-attendances/" + usico + "/" + key, attValues);
        childUpdates.put("/user-attendances/" + userIDX + "/" + key, attValues);

        mDatabase.updateChildren(childUpdates);

        String usatwx = "0";
        if (dmxa.equals("1")) {
            usatwx = "1";
        } else {
            usatwx = "0";
        }

        mDatabase.child("users").child(userIDX).child("usatw").setValue(usatwx);
        mDatabase.child("users").child(userIDX).child("lati").setValue(gpslat);
        mDatabase.child("users").child(userIDX).child("longi").setValue(gpslon);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("usatw", usatwx).apply();

        editor.commit();

        getCompanyIcon(user);


    }
    // [END basic_write]

    private void getCompanyIcon(FirebaseUser user) {

        if (user != null) {

            String myID = user.getUid();
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String imgUser = baseDir + File.separator + "/eusecom/attendance/photo" + myID + ".png";
            File fileUser = new File(imgUser);
            if(!fileUser.exists()){
                imgUser = baseDir + File.separator + "/eusecom/attendance/photo" + myID + ".jpg";
                fileUser = new File(imgUser);
            }
            Log.d("imgUser  ", imgUser);

            String myICO =  SettingsActivity.getUsIco(MainActivity.this);
            String imgIco = baseDir + File.separator + "/eusecom/attendance/photo" + myICO + ".png";
            File fileIco = new File(imgIco);
            if(!fileIco.exists()){
                imgIco = baseDir + File.separator + "/eusecom/attendance/photo" + myICO + ".jpg";
                fileIco = new File(imgIco);
            }
            Log.d("imgIco  ", imgIco);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if(fileUser.exists()){
                    //Bitmap bmp = BitmapFactory.decodeFile(imgUser);
                    //mUserImage.setImageBitmap(bmp);
                    //Picasso.with(this).invalidate(new File(imgUser));
                    Picasso.with(this).load(new File(imgUser))
                            .resize(800, 600)
                            .centerInside()
                            .into(mUserImage);
                }else{
                    mUserImage.setImageDrawable(getResources().getDrawable(R.drawable.zamestnanec, getApplicationContext().getTheme()));
                }

                if(fileIco.exists()){
                    //Bitmap bmp2 = BitmapFactory.decodeFile(imgIco);
                    //mIntoworkImage.setImageBitmap(bmp2);
                    //.with(this).invalidate(new File(imgIco));
                    Picasso.with(this).load(new File(imgIco))
                            .resize(800, 600)
                            .centerInside()
                            .into(mIntoworkImage);
                }else{
                    mIntoworkImage.setImageDrawable(getResources().getDrawable(R.drawable.add2new, getApplicationContext().getTheme()));
                }


            } else {

                if(fileUser.exists()){
                    //Bitmap bmp = BitmapFactory.decodeFile(imgUser);
                    //mUserImage.setImageBitmap(bmp);
                    //Picasso.with(this).invalidate(new File(imgUser));
                    Picasso.with(this).load(new File(imgUser))
                            .resize(800, 600)
                            .centerInside()
                            .into(mUserImage);
                }else{
                    mUserImage.setImageDrawable(getResources().getDrawable(R.drawable.zamestnanec));
                }

                if(fileIco.exists()){
                    //Bitmap bmp2 = BitmapFactory.decodeFile(imgIco);
                    //mIntoworkImage.setImageBitmap(bmp2);
                    //Picasso.with(this).invalidate(new File(imgIco));
                    Picasso.with(this).load(new File(imgIco))
                            .resize(800, 600)
                            .centerInside()
                            .into(mIntoworkImage);
                }else{
                    mIntoworkImage.setImageDrawable(getResources().getDrawable(R.drawable.add2new));
                }

            }

            String usatwx = SettingsActivity.getUsAtw(this);

            if (usatwx.equals("1")) {

                String baseDir3 = Environment.getExternalStorageDirectory().getAbsolutePath();
                String myICO3 =  SettingsActivity.getUsIco(MainActivity.this);
                String imgIco3 = baseDir3 + File.separator + "/eusecom/attendance/photo" + myICO3 + ".png";
                File fileIco3 = new File(imgIco3);
                if(!fileIco3.exists()){
                    imgIco3 = baseDir + File.separator + "/eusecom/attendance/photo" + myICO3 + ".jpg";
                    fileIco3 = new File(imgIco3);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    if(fileIco3.exists()){
                        //Bitmap bmp3 = BitmapFactory.decodeFile(imgIco3);
                        //mCompanyImage.setImageBitmap(bmp3);
                        //Picasso.with(this).invalidate(new File(imgIco3));
                        Picasso.with(this).load(new File(imgIco3))
                                .resize(800, 600)
                                .centerInside()
                                .into(mCompanyImage);
                    }else{
                        mCompanyImage.setImageDrawable(getResources().getDrawable(R.drawable.add2new, getApplicationContext().getTheme()));
                    }

                } else {

                    if(fileIco.exists()){
                        //Bitmap bmp3 = BitmapFactory.decodeFile(imgIco3);
                        //mCompanyImage.setImageBitmap(bmp3);
                        //Picasso.with(this).invalidate(new File(imgIco3));
                        Picasso.with(this).load(new File(imgIco3))
                                .resize(800, 600)
                                .centerInside()
                                .into(mCompanyImage);
                    }else{
                        mCompanyImage.setImageDrawable(getResources().getDrawable(R.drawable.add2new));
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mCompanyImage.setImageDrawable(getResources().getDrawable(R.drawable.clock, getApplicationContext().getTheme()));
                } else {
                    mCompanyImage.setImageDrawable(getResources().getDrawable(R.drawable.clock));
                }
            }


        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUserImage.setImageDrawable(getResources().getDrawable(R.drawable.login, getApplicationContext().getTheme()));
                mIntoworkImage.setImageDrawable(getResources().getDrawable(R.drawable.intowork, getApplicationContext().getTheme()));
            } else {
                mUserImage.setImageDrawable(getResources().getDrawable(R.drawable.login));
                mIntoworkImage.setImageDrawable(getResources().getDrawable(R.drawable.intowork));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCompanyImage.setImageDrawable(getResources().getDrawable(R.drawable.clock, getApplicationContext().getTheme()));
            } else {
                mCompanyImage.setImageDrawable(getResources().getDrawable(R.drawable.clock));
            }
        }
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {

            String usicox = SettingsActivity.getUsIco(this);
            usicoTextView.setText(getString(R.string.emailpassword_usico_fmt, usicox));
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()));
            mText3.setText(getString(R.string.logout));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imglogin.setImageDrawable(getResources().getDrawable(R.drawable.logout, getApplicationContext().getTheme()));
            } else {
                imglogin.setImageDrawable(getResources().getDrawable(R.drawable.logout));
            }

            getCompanyIcon(user);

        } else {
            mStatusTextView.setText(R.string.signed_out);
            usicoTextView.setText("");
            mText3.setText(getString(R.string.login));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imglogin.setImageDrawable(getResources().getDrawable(R.drawable.login, getApplicationContext().getTheme()));
            } else {
                imglogin.setImageDrawable(getResources().getDrawable(R.drawable.login));
            }

            getCompanyIcon(user);
        }
    }


    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (connlist != null) {
            connectedRef.removeEventListener(connlist);
        }
    }
    // [END on_stop_remove_listener]


    @Override
    public void onBackPressed() {

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (Drawer.isDrawerOpen(GravityCompat.START)) {
            Drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void getNoIcoAlert(){

        dialognoico = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.noico))
                .setMessage(getString(R.string.qico))
                .setPositiveButton(R.string.textyes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                Intent is = new Intent(getApplicationContext(), CompanyChooseActivity.class);
                                startActivity(is);
                                dialognoico.dismiss();

                            }
                        })
                .setNegativeButton(R.string.textno,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // ignore, just dismiss
                                dialognoico.dismiss();

                            }
                        })
                .show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        String ustypex = SettingsActivity.getUsType(this);

        if (user == null) {
            getMenuInflater().inflate(R.menu.options_mainnolog, menu);
        }else {
            if (ustypex.equals("99")) {
                getMenuInflater().inflate(R.menu.options_mainallmenu, menu);
            } else {
                getMenuInflater().inflate(R.menu.options_mainmenu, menu);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {

            Intent is = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(is);
            return true;
        }

        if (id == R.id.setuserimage) {

            String userxx = user.getUid();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.saveuserimage))
                    .setMessage(getString(R.string.saveuserimagemsg, userxx))
                    .setNegativeButton(R.string.textok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // ignore, just dismiss
                                    selectImage(1);

                                }
                            })
                    .show();
            return true;
        }

        if (id == R.id.seticoimage) {

            String usicox = SettingsActivity.getUsIco(this);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.saveicoimage))
                    .setMessage(getString(R.string.saveicoimagemsg, usicox))
                    .setNegativeButton(R.string.textok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // ignore, just dismiss
                                    selectImage(2);

                                }
                            })
                    .show();

            return true;
        }

        if (id == R.id.approve) {

            Intent is = new Intent(getApplicationContext(), ApproveActivity.class);
            startActivity(is);
            return true;
        }

        if (id == R.id.absmysql) {

            String usfir = SettingsActivity.getFir(this);
            int firx = 0;

            try {
                firx = Integer.parseInt(usfir);
            } catch(NumberFormatException nfe) {
                out.println("Could not parse " + nfe);
            }
            if (firx > 0 ) {
                Intent is = new Intent(getApplicationContext(), AbsServerAsActivity.class);
                startActivity(is);
            }else{
                String texttoast = getString(R.string.notconnected);
                Toast.makeText(MainActivity.this, texttoast, Toast.LENGTH_LONG).show();
            }


            return true;
        }

        if (id == R.id.myemployees) {

            Intent is = new Intent(getApplicationContext(), EmployeeMvvmActivity.class);
            startActivity(is);
            return true;
        }

        if (id == R.id.mycompanies) {

            if (SettingsActivity.getUsAdmin(MainActivity.this).equals("1")) {
                Intent is = new Intent(getApplicationContext(), CompaniesMvvmActivity.class);
                startActivity(is);
            } else {
                String texttoast = getString(R.string.allowedcompany);
                Toast.makeText(MainActivity.this, texttoast, Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.choosecompany) {

            Intent is = new Intent(getApplicationContext(), CompanyChooseActivity.class);
            startActivity(is);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectImage(int ximage) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ximage);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_OPENUSER) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);
                //Toast.makeText(MainActivity.this, selectedImagePath, Toast.LENGTH_SHORT).show();
                Log.d("selectedImagePath user", selectedImagePath);
                savefile(selectedImagePath, 1);

            }
            if (requestCode == REQUEST_IMAGE_OPENICO) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);
                //Toast.makeText(MainActivity.this, selectedImagePath, Toast.LENGTH_SHORT).show();
                Log.d("selectedImagePath ico", selectedImagePath);
                savefile(selectedImagePath, 2);

            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            return path;
        }
        cursor.close();
        // this is our fallback here
        return uri.getPath();
    }


    void savefile(String selectedImagePath, int ximage)
    {

        String sourceFilename= selectedImagePath;
        String mextension = selectedImagePath.substring(selectedImagePath.lastIndexOf("."));
        String destinationFilename ="";
        if(ximage == 1 ) {
            String usuidx = SettingsActivity.getUserUid(this);
            destinationFilename = Environment.getExternalStorageDirectory().getPath() + File.separatorChar + "eusecom/attendance/photo" + usuidx + mextension;
            String delfilejpg = Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"eusecom/attendance/photo" + usuidx + ".jpg";
            File filedelj = new File(delfilejpg);
            filedelj.delete();
            Picasso.with(this).invalidate(new File(delfilejpg));
            String delfilepng = Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"eusecom/attendance/photo" + usuidx + ".png";
            File filedelp = new File(delfilepng);
            filedelp.delete();
            Picasso.with(this).invalidate(new File(delfilepng));
        }else{
            String usicox = SettingsActivity.getUsIco(this);
            destinationFilename = Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"eusecom/attendance/photo" + usicox + mextension;
            String delfilejpg = Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"eusecom/attendance/photo" + usicox + ".jpg";
            File filedelj = new File(delfilejpg);
            filedelj.delete();
            Picasso.with(this).invalidate(new File(delfilejpg));
            String delfilepng = Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"eusecom/attendance/photo" + usicox + ".png";
            File filedelp = new File(delfilepng);
            filedelp.delete();
            Picasso.with(this).invalidate(new File(delfilepng));
        }


        Log.d("selectedImagePath ext", mextension);
        Log.d("selectedImagePath sour", sourceFilename);
        Log.d("selectedImagePath dest", destinationFilename);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
                //Log.d("selectedImagePath buf", buf.toString());

            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}