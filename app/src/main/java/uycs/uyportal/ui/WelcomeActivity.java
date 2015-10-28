package uycs.uyportal.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uycs.uyportal.R;
import uycs.uyportal.util.FontCache;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AungKo on 10/17/2015.
 */
public class WelcomeActivity extends AppCompatActivity {

    @Bind(R.id.welcome_text)   TextView _welcome_text;
    @Bind(R.id.welcome_blw_text) TextView _welcome_blw_text;
    @Bind(R.id.signup_link) TextView _signup_link;
    @Bind(R.id.login)    Button _login_btn;

    Typeface tf,tf_regular;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        ButterKnife.bind(this);
        setEachTypeFace();

        _login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, Login_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }
        });

        _signup_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, Signup_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    private void setEachTypeFace(){
        tf = FontCache.get("fonts/Roboto-Light.ttf",getApplicationContext());
        _welcome_text.setTypeface(tf);
        _welcome_blw_text.setTypeface(tf);
        _signup_link.setTypeface(tf);
        _login_btn.setTypeface(tf);
    }

}
