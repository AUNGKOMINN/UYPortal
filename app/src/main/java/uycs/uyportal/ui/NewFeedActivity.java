package uycs.uyportal.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import uycs.uyportal.R;
import uycs.uyportal.util.CheckConnection;
import uycs.uyportal.util.ColorGenerator;
import uycs.uyportal.util.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AungKo on 10/14/2015.
 */

public class NewFeedActivity extends AppCompatActivity {
    private static final String TAG = "Username" ;
    @Bind(R.id.textView)    TextView _textView;
    @Bind(R.id.usernameImage)    ImageView _nameImage;
    @Bind(R.id.logout_btn)    Button _logout;
    @Bind(R.id.delete_btn) Button _delete;
    @Bind(R.id.allUser) TextView _allUserCount;
    ProgressDialog pd;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGen = ColorGenerator.MATERIAL;
    private List<User> user = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newfeed_activity);
        ButterKnife.bind(this);
        final ParseUser currentUser = ParseUser.getCurrentUser();

        navigatation(currentUser);


    }

    private void getAllParseUser(){
        pd=new ProgressDialog(this);
        pd.setMessage("Retrieving Data");
        pd.show();

        if(!new CheckConnection(getBaseContext()).isNetworkAvailable()){
            getUserOffline();
        }else{
            getUserOnline();
        }

    }

    private void getUserOnline(){
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.addAscendingOrder("FullName");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(pd.isShowing()) pd.dismiss();
                Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_LONG).show();
                mRecyclerView.setAdapter(mAdapter);
                if (e == null) {
                    ParseUser.pinAllInBackground(list);
                    _allUserCount.setText("All user : "+list.size());
                    for(ParseUser parseUser : list) {
                        User mUser = new User();
                        mUser.setmName(parseUser.get("FullName").toString());
                        mUser.setmUsername("UserName : " + parseUser.getUsername());
                        mUser.setmEmail("Email : " + parseUser.getEmail());
                        mUser.setmPwd("Created At : " + parseUser.getCreatedAt().toString());
                        mUser.setmMajor("Major : " + parseUser.get("major").toString());
                        mUser.setmNameImg(mDrawableBuilder.build(String.valueOf(parseUser.get("FullName").toString().charAt(0)).toUpperCase(), mColorGen.getRandomColor()));
                        user.add(mUser);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Error Retrieving", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getUserOffline(){
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.addAscendingOrder("CreatedAt").fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (pd.isShowing()) pd.dismiss();
                Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_LONG).show();
                mRecyclerView.setAdapter(mAdapter);
                if (e == null) {
                    _allUserCount.setText("All user : " + list.size());
                    for (ParseUser parseUser : list) {
                        User mUser = new User();
                        mUser.setmName(parseUser.get("FullName").toString());
                        mUser.setmUsername("UserName : " + parseUser.getUsername());
                        mUser.setmEmail("Email : " + parseUser.getEmail());
                        mUser.setmPwd("Created At : " + parseUser.getCreatedAt().toString());
                        mUser.setmMajor("Major : " + parseUser.get("major").toString());
                        mUser.setmNameImg(mDrawableBuilder.build(String.valueOf(parseUser.get("FullName").toString().charAt(0)).toUpperCase(), mColorGen.getRandomColor()));
                        user.add(mUser);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Error Retrieving offline data", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void navigatation(final ParseUser currentUser){
        if (currentUser == null) {
            navigateToLogin();
        }
        else {
            String name = currentUser.get("FullName").toString();
            mDrawableBuilder = TextDrawable.builder().round();
            _nameImage.setImageDrawable(mDrawableBuilder.build(String.valueOf(name.charAt(0)), mColorGen.getRandomColor()));
            _textView.setText("Hello! " + name);

            getAllParseUser();
            mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new RvAdapter(user);
            mRecyclerView.setAdapter(mAdapter);
            _logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });

            _delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser(currentUser);
                }
            });
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void deleteUser(final ParseUser user){
        if(!new CheckConnection(getBaseContext()).isNetworkAvailable()){
            Toast.makeText(getBaseContext(), "Network not Available!", Toast.LENGTH_SHORT).show();
        }
        else{
            pd = new ProgressDialog(NewFeedActivity.this);
            pd.setMessage("Deleting user...");
            pd.setCancelable(false);
            pd.show();

            user.deleteInBackground(new DeleteCallback() {

                @Override
                public void done(ParseException e) {
                    if (pd.isShowing()) pd.dismiss();
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Delete user Success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                        finish();
                    }
                }
            });
        }

    }

    private void logout(){
        if(!new CheckConnection(getBaseContext()).isNetworkAvailable()){
            Toast.makeText(getBaseContext(), "Network Error!", Toast.LENGTH_SHORT).show();
        }
        else{
            pd=new ProgressDialog(NewFeedActivity.this);
            pd.setMessage("Logging out...");
            pd.setCancelable(false);
            pd.show();
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (pd.isShowing()) pd.dismiss();
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Log out Success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), e.getCode()+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}
