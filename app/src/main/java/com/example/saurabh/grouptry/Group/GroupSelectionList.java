package com.example.saurabh.grouptry.Group;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saurabh.grouptry.MainActivity;
import com.example.saurabh.grouptry.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GroupSelectionList extends AppCompatActivity {

    private ListView mUserSelectionList;
    private String group_name , user_id;
    private Button mCreateGroupBtn;
    private ArrayList<String> list_of_users = new ArrayList<String>();
    private ArrayList<String> selected_users = new ArrayList<String>();

    private int count=0;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> adapter;
   // private  String[] users;

    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserData");

    private DatabaseReference group_main = FirebaseDatabase.getInstance().getReference().child("GroupData");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selection_list);

        mUserSelectionList=(ListView)findViewById(R.id.UserSelectionListView);
        mUserSelectionList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mCreateGroupBtn=(Button)findViewById(R.id.CreateGroupBtn);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_users);
        mUserSelectionList.setAdapter(arrayAdapter);

        group_name = getIntent().getExtras().get("group_name").toString();
        user_id = getIntent().getExtras().get("user_id").toString();



        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i= dataSnapshot.getChildren().iterator();
                while (i.hasNext())
                {
                    String temp_key =dataSnapshot.getKey();
                    if(temp_key!=user_id)
                    {
                        set.add(((DataSnapshot)i.next()).getKey());
                    }



                }

                list_of_users.clear();
                list_of_users.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ArrayAdapter<String>(this , R.layout.rowlayout,R.id.check);
        //mUserSelectionList.setAdapter(adapter);
        mUserSelectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_user =((TextView)view).getText().toString();
                if(selected_users.contains(selected_user))
                {


                    selected_users.remove(selected_user);
                    ((TextView)view).setTextColor(Color.BLACK);
                    count--;



                }
                else
                {
                    selected_users.add(selected_user);
                    ((TextView) view).setTextColor(Color.BLUE);
                    count++;

                }
            }
        });



        mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                group_name = getIntent().getExtras().get("group_name").toString();
                user_id = getIntent().getExtras().get("user_id").toString();

                final DatabaseReference newGroup = group_main.push();
                newGroup.child("Event Name").setValue(group_name);
                newGroup.child("Group Creater UID").setValue(user_id);
                newGroup.child("Pending Requests").setValue(selected_users);

                sendRequests(selected_users , newGroup.getKey());


                acceptGroup(newGroup.getKey() , group_name);
                Intent HomeIntent = new Intent(GroupSelectionList.this , MainActivity.class);
                startActivity(HomeIntent);



/*
                Map<String ,Object> map = new HashMap<String, Object>();
               // map.put()*/

            }
        });

    }

    public void sendRequests(ArrayList<String> list , String group_key)
    {

        for(int i=0;i<list.size();i++)
        {
            String req_uid =list.get(i);

            DatabaseReference uidRef = userRef.child(req_uid);
            DatabaseReference notifRef = uidRef.child("Notifications");
            notifRef.child(group_key).setValue(group_name);


        }



    }

    public void acceptGroup(String group_key , String group_name)
    {
       DatabaseReference groupRef =  userRef.child(user_id);
        DatabaseReference childRef = groupRef.child("Groups");
        childRef.child(group_key).setValue(group_name);


    }

}

