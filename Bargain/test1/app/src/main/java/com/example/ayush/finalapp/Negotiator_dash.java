package com.example.ayush.finalapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OneSignal;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class Negotiator_dash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;

    private DatabaseReference fdb;
    FirebaseAuth fba;
    static String nego_name;
    FirebaseUser muser;
    FirebaseUser user;
    ImageView mwallet, nfaq, mcommunity, mhome;
    Fragment fragment = null;
    FirebaseStorage firebaseStorage;
    StorageReference photo_storage;
    AlertDialog.Builder builder2;
    public static Context contextOfApplication;
    CircleImageView i1;
SessionManagment sessionManagment;
    // for user name
    FirebaseDatabase firebaseDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negotiator_dash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.applogo1);
        setSupportActionBar(toolbar);
        fba = FirebaseAuth.getInstance ();

        // ferencing storage refernce

       // photo_storage = FirebaseStorage.getInstance ().getReference ().child ("Negotiator_profile_image");

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        //
        OneSignal.sendTag("USER_ID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
        fragmentTransaction.replace (R.id.content_frame, new HomeNegoFrag(), "Homefrag");
        fragmentTransaction.commit ();

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        muser=fba.getCurrentUser ();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mwallet = (ImageView) findViewById (R.id.wallet);//creating the buttons
//        nfaq = (ImageView) findViewById(R.id.faq); //faqfragbutton
        mcommunity = (ImageView) findViewById (R.id.communityimage);
        mhome=(ImageView)findViewById(R.id.nego_home);



        // calling wallet page using fragments
        mwallet.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
                fragmentTransaction.replace(R.id.content_frame,new NegotiatorWallet ());
                fragmentTransaction.addToBackStack("wallet");
                fragmentTransaction.commit ();

            }
        });

        // calling community page using fragments
//        mcommunity.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
//                fragmentTransaction.replace (R.id.content_frame, new CommunityFragment ());
//                fragmentTransaction.addToBackStack ("community");
//                fragmentTransaction.commit ();
//
//            }
//        });

        mhome.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
                fragmentTransaction.replace (R.id.content_frame, new HomeNegoFrag(), "Homefrag");
                fragmentTransaction.commit ();

            }
        });

//        // calling wallet page using fragments
//        nfaq.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
//                fragmentTransaction.replace(R.id.content_frame,new FAQ ());
//                fragmentTransaction.addToBackStack("faq");
//                fragmentTransaction.commit ();
//
//            }
//        });

        //// added new content here
        final TextView textView = (TextView) findViewById (R.id.shopper_name);
        fdb = FirebaseDatabase.getInstance ().getReference ();
        DatabaseReference shopper = fdb.child ("Negotiator").child (muser.getUid ());
//########################
        try {
            shopper.addValueEventListener (new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    NegotiatorDetails profile = dataSnapshot.getValue (NegotiatorDetails.class);
                    assert profile != null;
                    try {
                        String key = profile.getFirstname () + " " + profile.getLastname ();

                        NavigationView navigationView = (NavigationView) findViewById (R.id.nav_view);
                        View headerView = navigationView.getHeaderView (0);
                        TextView navUsername = (TextView) headerView.findViewById (R.id.nego_name);
                        navUsername.setText (key);
                        final TextView user_email = (TextView) headerView.findViewById (R.id.nego_email);
                        user_email.setText (profile.getEmail ());

                        i1 = (CircleImageView) headerView.findViewById (R.id.image_nego);
                        fetch ();

                 //   photo_storage = FirebaseStorage.getInstance ().getReference ().child ("Negotiator_profile_image");
//                    try {
//
//                        final String location = muser.getUid () + "." + "jpg";
//                        Log.v ("userid", muser.getUid ());
//                        photo_storage.child (location).getDownloadUrl ().addOnSuccessListener (new OnSuccessListener <Uri> () {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                String imageURL = uri.toString ();
//                                Glide.with (getApplicationContext ()).load (imageURL).into (i1);
//                                Log.v ("its done", imageURL);
//                            }
//                        }).addOnFailureListener (new OnFailureListener () {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                String location2 = muser.getUid () + "." + "null";
//                                photo_storage.child (location2).getDownloadUrl ().addOnSuccessListener (new OnSuccessListener <Uri> () {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        String imageurl2 = uri.toString ();
//                                        Glide.with (getApplicationContext ()).load (imageurl2).into (i1);
//                                    }
//                                }).addOnFailureListener (new OnFailureListener () {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        //Toast.makeText (Negotiator_dash.this,e.getMessage (),Toast.LENGTH_LONG).show ();
//                                    }
//                                });
//
//                                // Handle any errors
//                                // Toast.makeText (Negotiator_dash.this, exception.getMessage (), Toast.LENGTH_LONG).show ();
//                            }
//                        });
//
//                    }catch (NullPointerException e)
//                    {}
                    } catch (NullPointerException e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e)
        {}




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.negotiator_dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chatbox) {//R.id.action_setting

            //open chat fragment
//            Intent intent = new Intent (ShopperHomepage.this,ChatMain.class);
//            startActivity (intent);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
            fragmentTransaction.replace (R.id.content_frame, new ChatFragmentNego ());
            fragmentTransaction.addToBackStack ("chatfrag");
            fragmentTransaction.commit ();
        }


        return super.onOptionsItemSelected (item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //  TextView fullname=(TextView)findViewById(R.id.nav_drawer_username);
        //fullname.setText(user.getDisplayName());
        // user.getEmail();
        //  user.getDisplayName();
        // Fragment fragment = null;

        int id = item.getItemId ();

        if (id == R.id.nav_logout) {

            // Handle the camera action
            sessionManagment = new SessionManagment (getApplicationContext ());

            fba.signOut ();
            sessionManagment.logoutUser ();

            fba.signOut ();
            finish ();
            startActivity (new Intent (Negotiator_dash.this, WelcomePage.class));

        }
          else if (id == R.id.nav_profile) {
            Intent intent = new Intent (Negotiator_dash.this, NegotiatorProfileActivity.class);
            startActivity (intent);

        }

        else if (id == R.id.nav_settings) {
            Intent intent = new Intent (Negotiator_dash.this, SettingsActivity.class);
            startActivity (intent);

        }
            else if (id == R.id.nav_faq) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
            fragmentTransaction.replace (R.id.content_frame, new FAQ ());
            fragmentTransaction.addToBackStack ("faq");
            fragmentTransaction.commit ();

        } else if (id == R.id.nav_cs) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager ().beginTransaction ();
            fragmentTransaction.replace (R.id.content_frame, new CustomerServiceFrag ());
            fragmentTransaction.addToBackStack ("faq");
            fragmentTransaction.commit ();


        }
        /////////
        else if (id == R.id.nav_rate) {


            builder2 = new AlertDialog.Builder (Negotiator_dash.this);
            builder2.setTitle("Rate App");
            builder2.setMessage("Show us some love!!");

            LinearLayout linearLayout = new LinearLayout(this);
            final RatingBar rating = new RatingBar(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            rating.setLayoutParams(lp);
            rating.setNumStars(5);
            rating.setStepSize(1);
            linearLayout.addView(rating);
            linearLayout.setGravity(Gravity.CENTER);

//            rating.setNumStars(5);
            builder2.setIcon(android.R.drawable.star_on);
            builder2.setView(linearLayout);

            builder2.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    int rate_val =rating.getProgress();
                    Log.v("RAAAAA",String.valueOf(rating.getProgress()));
                    if (rate_val>=4)
                        Toast.makeText(Negotiator_dash.this,"Thank you for your Support!",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Negotiator_dash.this,"Thank you! Please provide feedback to help us improve the service",Toast.LENGTH_SHORT).show();
                    // here you can add functions
                }
            });
            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                    // here you can add functions
                }
            });

            builder2.create();
            builder2.show();  //<-- See This!


        }
        /////////
     else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";//insert app link here
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bargaining App");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));



        }


        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        drawer.closeDrawer (GravityCompat.START);
        return true;
    }
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    public void fetch()
    {
        try {

            final StorageReference photo_storage = FirebaseStorage.getInstance ().getReference ().child ("Negotiator_profile_image");

            String location = muser.getUid () + "." + "jpg";
            final String location2 = muser.getUid () + "."+"null";


            Log.v("manas",photo_storage.getPath().toString());
            photo_storage.child (location).getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = uri.toString ();
                    Glide.with (getApplicationContext ()).load (imageURL).into (i1);
                }
            }).addOnFailureListener (new OnFailureListener () {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    photo_storage.child (location2).getDownloadUrl ().addOnSuccessListener (new OnSuccessListener <Uri> () {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageurl2 =uri.toString ();
                            Log.v ("url link",imageurl2);
                            Glide.with (getApplicationContext ()).load (imageurl2).into (i1);
                        }
                    }).addOnFailureListener (new OnFailureListener () {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText (ShopperHomepage.this,e.getMessage (),Toast.LENGTH_LONG).show ();
                        }
                    });
                    //Toast.makeText (ShopperHomepage.this, exception.getMessage (), Toast.LENGTH_LONG).show ();
                }
            });
        }catch (NullPointerException e)
        {

        }
    }


}
