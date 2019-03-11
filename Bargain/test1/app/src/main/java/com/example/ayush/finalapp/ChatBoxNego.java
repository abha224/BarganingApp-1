package com.example.ayush.finalapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.L;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.ayush.finalapp.NegotiatorProfileAdapter.n;

public class ChatBoxNego extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView mDisplayDate;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;
    EditText place;
    EditText time;
    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText messagebox;
    Button displayMeet;
    ImageButton sendButton;
    int i,alpha;

    MeetDetails meetDetails;
    TextView placeText,dateText,timeText,noMeet;
    TextView placeText1,dateText1,timeText1;
    AlertDialog.Builder builder2;

    final static int Left = 1;
    final static int Right = 2;

    String User;
    String[] Reciever;
    String meet_date;
    static String empty="does not exist";
    static String ChatRoom = "manas";
    ChatBoxNegoAdapter adapter;
    static List<Message> chats = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_nego);
        InitializeFields();
        displayMeet=findViewById(R.id.Button_display_meet);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser ();
        databaseReference=FirebaseDatabase.getInstance ().getReference ();
        Intent intent = getIntent();
        User = intent.getStringExtra("User");
        Reciever = intent.getStringArrayExtra("Reciever");
        int Num = intent.getIntExtra("Number",0);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(Reciever[0]);

        setTitle(Reciever[0]);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //adapter = new ChatBoxAdapter(chats,getApplicationContext(),User,Reciever[1]);
        //recyclerView.setAdapter(adapter);

        if(User.compareTo(Reciever[1])>0)
            ChatRoom = User + Reciever[1];
        else
            ChatRoom = Reciever[1] + User;

        ReadMessages();
        displayMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                builder2 = new AlertDialog.Builder (ChatBoxNego.this);
                LayoutInflater inflater = ChatBoxNego.this.getLayoutInflater ();
                final View v1= inflater.inflate(R.layout.meet_nego_frag,null);
                builder2.setView (v1);

                dateText=v1.findViewById(R.id.meet_date_edit);
                placeText=v1.findViewById(R.id.meet_place_edit);
                timeText=v1.findViewById(R.id.meet_time_edit);
                dateText1=v1.findViewById(R.id.meet_date);
                placeText1=v1.findViewById(R.id.meet_place);
                noMeet=v1.findViewById(R.id.no_meet);
                timeText1=v1.findViewById(R.id.meet_time);
                dateText.setText(empty);
                placeText.setText(empty);
                timeText.setText(empty);
                i=0;
                alpha=0;
                Query query=databaseReference.child("Negotiator").child(firebaseUser.getUid()).child("meet").orderByChild("shopper").equalTo(Reciever[1]).limitToLast(1);

                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.exists())
                        {
                            if(dataSnapshot.child("place").getValue().toString()!=null)
                            {

                                i++;

                            }
                            if(dataSnapshot.child("time").getValue().toString()!=null)
                            {
                                i++;

                            }
                            if(dataSnapshot.child("date").getValue().toString()!=null)
                            {
                                i++;

                            }
                            if(i==3) {

                                meetDetails=dataSnapshot.getValue(MeetDetails.class);
                                if(meetDetails.isAccepted){
                                    Log.v("me inside i==3",String.valueOf(i));
                                    dateText.setVisibility(v1.GONE);
                                    timeText.setVisibility(v1.GONE);
                                    placeText.setVisibility(v1.GONE);
                                    dateText1.setVisibility(v1.GONE);
                                    timeText1.setVisibility(v1.GONE);
                                    placeText1.setVisibility(v1.GONE);
                                    noMeet.setVisibility(v1.VISIBLE);
                                    noMeet.setText("The latest pending meet is already accepted");
                                    builder2.setTitle("Accept or decline the meet");

                                    AlertDialog alert = builder2.create ();
                                    alert.show ();

                                }else {
                                    Log.v("me inside i==3 and else",String.valueOf(i));
                                    placeText.setText(dataSnapshot.child("place").getValue().toString());
                                    timeText.setText(dataSnapshot.child("time").getValue().toString());
                                    dateText.setText(dataSnapshot.child("date").getValue().toString());
                                    alpha=1;
                                    builder2.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            meetDetails.isAccepted=false;
                                            dataSnapshot.getRef().setValue(meetDetails);
                                        }
                                    });
                                    builder2.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            meetDetails.isAccepted=true;
                                            dataSnapshot.getRef().setValue(meetDetails);
                                            /////// function here
                                        }
                                    });
                                 
                                    builder2.setTitle("Accept or decline the meet");

                                    AlertDialog alert = builder2.create ();
                                    alert.show ();
                                }


                                }else{
                                Log.v("me inside i!=3",String.valueOf(i));
                                dateText.setVisibility(v1.GONE);
                                timeText.setVisibility(v1.GONE);
                                placeText.setVisibility(v1.GONE);
                                dateText1.setVisibility(v1.GONE);
                                timeText1.setVisibility(v1.GONE);
                                placeText1.setVisibility(v1.GONE);
                                noMeet.setVisibility(v1.VISIBLE);
                                builder2.setTitle("Accept or decline the meet");

                                AlertDialog alert = builder2.create ();
                                alert.show ();

                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId ();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_meet)
//        {
//            builder2 = new AlertDialog.Builder (ChatBoxNego.this);
//            LayoutInflater inflater = ChatBoxNego.this.getLayoutInflater ();
//            final View v1= inflater.inflate(R.layout.meet_frag,null);
//            builder2.setView (v1);
//
//            builder2.setNegativeButton ("Close", new DialogInterface.OnClickListener () {
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.cancel ();
//                }
//            });
//            mDisplayDate = (TextView) v1.findViewById(R.id.meet_date_edit);
//
//            mDisplayDate.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Calendar cal = Calendar.getInstance();
//                    int year = cal.get(Calendar.YEAR);
//                    int month = cal.get(Calendar.MONTH);
//                    int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                    DatePickerDialog dialog = new DatePickerDialog(
//                            ChatBoxNego.this,
//                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                            mDateSetListener,
//                            year,month,day);
//
//                    dialog.getDatePicker().setMaxDate(new Date().getTime());
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    dialog.show();
//                }
//
//            });
//
//            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                    month = month + 1;
//
//                    String date = day + "/" + month + "/" + year;
//                    mDisplayDate.setText(date);
//                    meet_date=mDisplayDate.getText ().toString ().trim ();
//                }
//            };
//            builder2.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    place=(EditText)v1.findViewById(R.id.meet_place_edit);
//                    time=(EditText)v1.findViewById(R.id.meet_time_edit);
//                    meetDetails=new MeetDetails(firebaseUser.getUid(),place.getText().toString(),meet_date,time.getText().toString(),Reciever[1],false);
//                    databaseReference.child("Shopper").child(firebaseUser.getUid()).child("meet").push().setValue(meetDetails);
//                    databaseReference.child("Negotiator").child(Reciever[1]).child("meet").push().setValue(meetDetails);
//                }
//            });
//            builder2.setTitle("Enter Details for meet");
//            builder2.setCancelable (false);
//            AlertDialog alert = builder2.create ();
//            alert.show ();
//        }
//        return true;
//    }
    private void ReadMessages() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chats.clear();
                if(dataSnapshot.child(ChatRoom).exists())
                {
                    //Toast.makeText(ChatActivity.this, "here", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot snapshot : dataSnapshot.child(ChatRoom).getChildren()) {

                        Message message = snapshot.getValue(Message.class);
                        while (message.message == null) ;

                        // Toast.makeText(ChatActivity.this, "AfterWhile", Toast.LENGTH_SHORT).show();
                        chats.add(message);
                        //   Toast.makeText(ChatActivity.this, chats.get(0).message, Toast.LENGTH_SHORT).show();
                    }
                    adapter = new ChatBoxNegoAdapter(chats,getApplicationContext(),User,Reciever[1],ChatBoxNego.this);
                    recyclerView.setAdapter(adapter);

                }
                //else
                //   Toast.makeText(ChatActivity.this, "here1", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("databaseerror",databaseError.getMessage());

            }
        });

    }

    private void InitializeFields() {
        recyclerView = findViewById(R.id.ChatAct_Recycler);
//        toolbar = findViewById(R.id.ChatAct_Toolbar);
        messagebox = findViewById(R.id.ChatAct_message);
        sendButton = findViewById(R.id.ChatAct_Send);
        ChatRoom =null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChatFragmentNego.Opened=0;
    }

    public void SendMessage(View view) {

        String message = messagebox.getText().toString();
        if(!TextUtils.isEmpty(message))
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats/"+ChatRoom);
            String key = reference.push().getKey();
            final Message messages = new Message(message,User,Reciever[1],key);
            reference.child(key).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    messagebox.setText(null);
                }
            });

        }


    }
    void DeleteMessage(int i)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats/"+ChatRoom);
        reference.child(chats.get(i).id).removeValue();
        chats.remove(i);
        adapter = new ChatBoxNegoAdapter(chats,getApplicationContext(),User,Reciever[1],ChatBoxNego.this);
        recyclerView.setAdapter(adapter);

    }
    //

    public  void start_payment()
    {
        startActivity (new Intent (ChatBoxNego.this,NegotiatorWallet.class));
    }


}
