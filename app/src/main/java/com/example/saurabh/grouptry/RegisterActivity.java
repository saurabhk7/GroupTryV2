package com.example.saurabh.grouptry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by saurabh on 4/12/16.
 */

public class RegisterActivity extends AppCompatActivity{


    private EditText nameField;
    private Button signUpBtn;
    private Firebase mRootRef;
    private EditText emailField;
    private EditText passField;
    private Button signoutBtn;
    private ListView mlistView;
    private ArrayList<String> mUsernames = new ArrayList<>();

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // mRootRef=new Firebase("https://chatapp1-1fab2.firebaseio.com/Users");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserData");


        mProgress = new ProgressDialog(this);

        nameField = (EditText) findViewById(R.id.nameField);
        signUpBtn = (Button) findViewById(R.id.SignUpButton);
        emailField = (EditText) findViewById(R.id.emailField);
        //signoutBtn = (Button) findViewById(R.id.SignOutBtn);
        passField = (EditText) findViewById(R.id.passwordField);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegister();

            }
        });

    }
    private void startRegister()
    {

        final String name=nameField.getText().toString();
        String email=emailField.getText().toString();
        String password=passField.getText().toString();

        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
        {
            mProgress.setMessage("Signing Up ...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {

                        String UserId =mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(UserId);

                        current_user_db.child("name").setValue(name);
                        current_user_db.child("profilepicture").setValue("Default");

                        mProgress.dismiss();

                        Intent mainAppIntent = new Intent(RegisterActivity.this , MainActivity.class);
                        mainAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainAppIntent);

                    }
                }
            });
        }

    }
}
