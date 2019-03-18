package com.example.ayush.finalapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class RateandReview extends AppCompatActivity implements Serializable {
    RatingBar ratenegotiator;
    float rateval;
    EditText review;
    Button submitrate;
    TextView ratetext;
    String nego_id;
    DatabaseReference databaseReference, mroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rateand_review);
        ratenegotiator = (RatingBar)findViewById(R.id.ratenego);
        ratetext= (TextView)findViewById(R.id.ratetextview) ;
        review = (EditText)findViewById(R.id.reviewnego);

        ratenegotiator.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateval=rating;
//                Toast.makeText(MainActivity.this,"Rating" + String.valueOf(rating),Toast.LENGTH_SHORT).show();
                ratetext.setText(String.valueOf(rateval));

            }
        });

        // contentreview = review.getText().toString();


        submitrate=(Button)findViewById(R.id.submitreview);
        submitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submitrate.setText(review.getText().toString()  );
                //Toast.makeText(RateandReview.this,"Rating" + String.valueOf(rateval),Toast.LENGTH_LONG).show();
                ratetext.setText(String.valueOf(rateval));
                Log.v ("whats the rating", String.valueOf (rateval));
                nego_id = (String)getIntent ().getSerializableExtra ("nego_user");
                Log.v ("nego_user",nego_id);
                try {
                    mroot = FirebaseDatabase.getInstance ().getReference ().child ("Negotiator").child (nego_id);
                    mroot.addValueEventListener (new ValueEventListener () {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            NegotiatorDetails details = new NegotiatorDetails ();
                            details = dataSnapshot.getValue (NegotiatorDetails.class);
//                            Log.v ("ratings", details.getRatings ());
//                            details.setRatings (String.valueOf (rateval));
//                            mroot.child ("ratings").setValue (details.getRatings ());
//                           // mroot.child ("count").setValue (details.getCount ()+1);
//                            Log.v ("addition",details.getCount ()+1);
                            // infinite loop check here
//                            details.setCount (details.getCount () +1);
//                            mroot.child ("count").setValue (details.getCount ());
                           startActivity (new Intent (RateandReview.this,ShopperHomepage.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }catch (NullPointerException e)
                {

                }

            }
        });
    }
}
