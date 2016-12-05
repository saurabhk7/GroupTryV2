package com.example.saurabh.grouptry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {


    private Toolbar msignInToolbar;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mGetStartedBtn;
    private ProgressDialog mProgress;
    //private SignInButton mGoogleBtn;
   /* private  static final int RC_SIGN_IN = 1;
    private static final String TAG = "SignInActivity";
    private GoogleApiClient mGoogleApiClient;*/

    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mProgress = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("UserData");
        mDatabaseUsers.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mEmailField=(EditText)findViewById(R.id.emailField);
        mPasswordField=(EditText)findViewById(R.id.passwordField);
        mSignInBtn=(Button)findViewById(R.id.SignInBtn);
        mGetStartedBtn=(Button)findViewById(R.id.getStartedRegister);

        mAuthListener =new FirebaseAuth.AuthStateListener()
        {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Intent mainappIntent = new Intent(SignInActivity.this , MainActivity.class);
                    mainappIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainappIntent);

                }
              /* else if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(SignInActivity.this , GetStartedActivity.class));
                }*/
            }
        };

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSignIn();
            }
        });

        mGetStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this , RegisterActivity.class));

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn()
    {
        String email = mEmailField.getText().toString();
        String password=mPasswordField.getText().toString();

        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password))
        {
            Toast.makeText(SignInActivity.this , "Fields are empty!" , Toast.LENGTH_LONG).show();

        }
        else
        {
            mProgress.setMessage("Signing In ...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful())
                    {
                        mProgress.dismiss();
                        Toast.makeText(SignInActivity.this , "Please input correct Username and Password" , Toast.LENGTH_LONG).show();
                    }
                    else if(task.isSuccessful())
                    {
                        mProgress.dismiss();
                        checkUserExist();
                        Intent mainappIntent = new Intent(SignInActivity.this , MainActivity.class);
                        mainappIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainappIntent);
                    }


                }
            });
        }



    }

    private  void checkUserExist()
    {
        final String UserId = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UserId))
                {

                }
                else
                {
                    Toast.makeText(SignInActivity.this , "Setup new Account" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
