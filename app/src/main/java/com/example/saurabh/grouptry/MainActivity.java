package com.example.saurabh.grouptry;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saurabh.grouptry.Group.GroupSelectionList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private Button mLogout;
    private Button mCreateEvent;
    private TextView mUserNameTextView;
    public String GroupName;
    public  String UserID;
    private ListView EventList;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String > list_of_activities = new ArrayList<>();
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference userRef;
    FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserNameTextView=(TextView)findViewById(R.id.UserNameTextView);


        mLogout=(Button)findViewById(R.id.LogOutBtn);
        mCreateEvent=(Button)findViewById(R.id.CreateGroupBtn);
        mAuth = FirebaseAuth.getInstance();


        EventList =(ListView)findViewById(R.id.RecentEventsListView);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_activities);
        EventList.setAdapter(arrayAdapter);

        UserID= mAuth.getCurrentUser().getUid();
        /***************************************/
  ;



        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
               Intent SignOutIntent= new Intent(MainActivity.this, SignInActivity.class);
                SignOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(SignOutIntent);

            }
        });

        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_new_event();
            }
        });


        check_new_notif();



    }

    private void check_new_notif() {


        DatabaseReference userListRef = mDatabase.child("UserData");
        userRef = userListRef.child(UserID);
        final DatabaseReference NotifRef = userRef.child("Notifications");

        NotifRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               final String group_key= dataSnapshot.getKey();
                final String group_name = (String) dataSnapshot.getValue();
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("You have a new event :"+group_name);
                NotifRef.child(group_key).setValue(null);



                builder.setPositiveButton("INTERESTED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        DatabaseReference childRef=userRef.child("Groups");
                        childRef.child(group_key).setValue(group_name);
                        showEvents();



                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void create_new_event()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("ENTER EVENT NAME :");

        final EditText input_event_name =new EditText(this);

        builder.setView(input_event_name);

        builder.setPositiveButton("ADD MEMBERS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GroupName=input_event_name.getText().toString();

               UserID= mAuth.getCurrentUser().getUid();

                Intent AddMembersIntent = new Intent(MainActivity.this , GroupSelectionList.class);
                AddMembersIntent.putExtra("group_name" , GroupName);
                AddMembersIntent.putExtra("user_id" , UserID);
                startActivity(AddMembersIntent);

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();



    }

    public void showEvents()
    {
        DatabaseReference userListRef = mDatabase.child("UserData");
        userRef = userListRef.child(UserID);
        DatabaseReference GroupRef = userRef.child("Groups");
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                

                Set<String> set = new HashSet<String>();
                Iterator i= dataSnapshot.getChildren().iterator();
                while (i.hasNext())
                {
                    set.add((String)(((DataSnapshot)i.next()).getValue()));

                }

                list_of_activities.clear();
                list_of_activities.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        showEvents();
    }
}
