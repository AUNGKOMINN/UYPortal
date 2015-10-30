package uycs.uyportal.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import uycs.uyportal.R;
import uycs.uyportal.util.CheckConnection;
import uycs.uyportal.util.FontCache;
import uycs.uyportal.util.ParseConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AungKo on 10/17/2015.
 */
public class Login_Activity extends AppCompatActivity {

    @Bind(R.id.username_input) TextInputLayout _username;
    @Bind(R.id.password_input) TextInputLayout _password;
    @Bind(R.id.login_text) TextView _loginText;
    @Bind(R.id.username_lbl) TextView _usrname_lbl;
    @Bind(R.id.pwd_lbl) TextView _pwd_lbl;
    @Bind(R.id.forget_pwd) TextView _forget_pwd;
    @Bind(R.id.btn_login)   Button _login;
    boolean usernameOK, pwdOK;
    String userS;
    ProgressDialog pd;
    int loginCount = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);

        setFont();
        _login.setEnabled(false);
        usernameOK = pwdOK  = false;

        setValidation(_username.getEditText(), _password.getEditText());

        _login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!netConnectionCheck()) {
                    Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_LONG).show();
                } else
                    parseLogIn();
            }
        });

        _forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwdResetDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Login_Activity.this,WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    private void parseLogIn(){
        pd = new ProgressDialog(Login_Activity.this);
        pd.setMessage("Logging in ...");
        pd.setCancelable(false);
        pd.show();
        _login.setEnabled(false);

        String username = _username.getEditText().getText().toString();
        String password = _password.getEditText().getText().toString();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (pd.isShowing()) pd.dismiss();
                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ParseConstants.KEY_USER_LOGGED = true;
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    if (e.getCode() == 101) {
                        loginCount++;
                        Toast.makeText(getApplicationContext(), "username and password are not correct",
                                Toast.LENGTH_SHORT).show();
                    } else if (e.getCode() == 100)
                        Toast.makeText(getApplicationContext(), "Connection failed, Try again",
                                Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Oop, Something went wrong!",
                                Toast.LENGTH_SHORT).show();
                    if (loginCount == 5 || loginCount == 8 || loginCount == 12) {
                        Toast.makeText(getApplicationContext(), "Forget your password?, You can reset it",
                                Toast.LENGTH_SHORT).show();
                        pwdResetDialog();
                    }
                }
            }
        });
    }

    private void pwdResetDialog(){
        final AlertDialog builder = new AlertDialog.Builder(this,R.style.AlertDialog).create();

        final View pwd_reset_dialog = View.inflate(this, R.layout.pwd_reset_dialog, null);
        final EditText emailinput = (EditText) pwd_reset_dialog.findViewById(R.id.reset_pwd_dialog_et);
        final TextInputLayout outer = (TextInputLayout) pwd_reset_dialog.findViewById(R.id.reset_pwd_dialog_input);
        setCustomTypeFace((TextView) pwd_reset_dialog.findViewById(R.id.pwd_reset_msg),"Regular");
        setCustomTypeFace((TextView) pwd_reset_dialog.findViewById(R.id.pwd_reset_title), "Regular");
        setCustomTypeFace(emailinput, "Regular");

        emailinput.setHint("Enter your email");

        TextView title = new TextView(this);
        title.setText("Password Reset");
        title.setTextSize(20);
        title.setPadding(35, 2, 0, 10);

        setCustomTypeFace(title, "Regular");

        builder.setView(pwd_reset_dialog);
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = emailinput.getText().toString();
                if (!netConnectionCheck()) {
                    Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_LONG).show();
                } else {
                    if (builder.isShowing()) builder.dismiss();
                    pwd_reset(email);
                }
            }
        });


        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.dismiss();
            }
        });
        builder.show();
        emailinput.addTextChangedListener(new TextValidator(emailinput) {
            @Override
            public void validate(EditText et) {
                if (et.getText().toString().isEmpty()) {
                    builder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et.getText().toString()).matches()) {
                    builder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else{
                    builder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }


            }
        });
        builder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void pwd_reset(final String email){
        pd = new ProgressDialog(Login_Activity.this);
        pd.setMessage("Password Reset Request ...");
        pd.setCancelable(false);
        pd.show();
        _login.setEnabled(false);
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                if (pd.isShowing()) pd.dismiss();
                _login.setEnabled(true);
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Now check your email "+email, Toast.LENGTH_LONG).show();
                } else {
                    if(e.getCode() == 205){
                        Toast.makeText(getApplicationContext(), "User with "+ email +" is  not found", Toast.LENGTH_LONG).show();
                    }else if(e.getCode() == 100)
                        Toast.makeText(getApplicationContext(), "Connection failed, Try again",
                                Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Oops, Something went wrong!",
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFont(){
        setCustomTypeFace(_usrname_lbl,"Light");
        setCustomTypeFace(_pwd_lbl,"Light");
        setCustomTypeFace(_loginText,"Light");
        setCustomTypeFace(_forget_pwd,"Light");
        setCustomTypeFace(_login,"Light");
        setCustomTypeFace(_username.getEditText(),"Light");
        setCustomTypeFace(_password.getEditText(),"Light");
    }

    private void setCustomTypeFace(TextView v, String tf){
        v.setTypeface(FontCache.get("fonts/Roboto-"+tf+".ttf", getApplicationContext()));
    }

    private void setValidation(EditText _usernameET, EditText _pwdET){
        _usernameET.addTextChangedListener(new TextValidator(_usernameET) {
            @Override
            public void validate(EditText et) {
                username_validate(et);
            }
        });

        _pwdET.addTextChangedListener(new TextValidator(_pwdET) {
            @Override
            public void validate(EditText et) {
                pwd_validate(et);
            }
        });
    }

    private void username_validate(EditText et){
        String text = et.getText().toString();
        usernameOK = !text.isEmpty();
        bothOK();
    }

    private void pwd_validate(EditText et){
        String text = et.getText().toString();
        pwdOK = !(text.isEmpty() || text.length() < 6);
        bothOK();
    }

    private void bothOK(){
        if(usernameOK && pwdOK){
            _login.setEnabled(true);
        }else{
            _login.setEnabled(false);
        }
    }

    private boolean netConnectionCheck(){
        CheckConnection checkConnection=new CheckConnection(Login_Activity.this);
        return checkConnection.isNetworkAvailable();
    }

    private abstract class TextValidator implements TextWatcher{

        private EditText et;

        public TextValidator (EditText et){
            this.et=et;
        }

        public abstract void validate(EditText et);

        @Override
        public void afterTextChanged(Editable editable) {
            String text = et.getText().toString();
            validate(et);
        }

        @Override
        public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int i, int i1, int i2) {

        }

    }
}
