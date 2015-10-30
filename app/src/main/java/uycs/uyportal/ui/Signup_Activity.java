package uycs.uyportal.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import uycs.uyportal.R;
import uycs.uyportal.util.CheckConnection;
import uycs.uyportal.util.FontCache;
import uycs.uyportal.util.ParseConstants;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AungKo on 10/19/2015.
 */
public class Signup_Activity extends AppCompatActivity {

    @Bind(R.id.signup_text) TextView _signup_text;
    @Bind(R.id.btn_back)    Button _back_btn;
    static Button _next_btn;
    static int slideNum = 1;
    public static String [] userData = new String[5];
    public static String username;
    ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        _next_btn =(Button) findViewById(R.id.btn_next);
        ButterKnife.bind(this);

        _signup_text.setTypeface(FontCache.get("fonts/Roboto-Light.ttf", getApplicationContext()));
        _back_btn.setVisibility(View.GONE);
        _next_btn.setEnabled(false);

        if (savedInstanceState == null) {
            changingFragment();
        }

        _next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButtonAction();
            }
        });

        _back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backButtonAction();
            }
        });


    }

    private void nextButtonAction() {
        storeData(userData);
        if (slideNum < 5) {
            slideNum++;
            _next_btn.setEnabled(false);
            changingFragment();
//            Toast.makeText(getApplicationContext(), userData[slideNum - 2], Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), userData.length + "", Toast.LENGTH_LONG).show();
            if (slideNum == 5) _next_btn.setText("Sign Up");
        } else {
            if (!netConnectionCheck()) {
                Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
            } else {
                parseSignUp();
            }
        }
        _back_btn.setVisibility(View.VISIBLE);
    }

    private void backButtonAction(){
        if(slideNum!=1){
            slideNum--;
            changingFragment();
            if(slideNum == 1) _back_btn.setVisibility(View.GONE);
            _next_btn.setText("Next");
        }else{
            Intent intent = new Intent(Signup_Activity.this,WelcomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            finish();
        }
    }



    /// --------- To set key action done on user text input
     /*public EditText.OnEditorActionListener keyListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                    (i == EditorInfo.IME_ACTION_DONE)) {
                         nextButtonAction();
            }
            return false;
        }
    };*/


    private void storeData(String[] userData){
        if(slideNum == 2){
            userData[slideNum - 1] = SignUpMajorFragment._majorList.getText().toString();
        }else{
            userData[slideNum - 1] = Signup_Fragment._textInput.getText().toString();
        }
    }

    private void changingFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(slideNum == 2){
            SignUpMajorFragment smf = new SignUpMajorFragment();
            fragmentTransaction.replace(R.id.SignUpfragmentContainer, smf);
        }else{
            Signup_Fragment sf = Signup_Fragment.newInstance(slideNum);
            fragmentTransaction.replace(R.id.SignUpfragmentContainer, sf);
        }
        fragmentTransaction.commit();
        hideKeyboard();
    }

    @Override
    public void onBackPressed() {
        backButtonAction();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("slideNum", slideNum);
    }

    private boolean netConnectionCheck(){
        CheckConnection checkConnection=new CheckConnection(Signup_Activity.this);
        return checkConnection.isNetworkAvailable();
    }


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void parseSignUp(){
        pd = new ProgressDialog(Signup_Activity.this);
        pd.setMessage("Creating your account ...");
        pd.setCancelable(false);
        pd.show();

        ParseUser user = new ParseUser();

        user.put("FullName", userData[0]);
        user.put("major", userData[1]);
        user.setEmail(userData[2]);
        user.setPassword(userData[3]);
        user.setUsername(userData[4]);
        username=(userData[4]);
        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if(pd.isShowing()) pd.dismiss();
                if (e == null) {
                    Toast.makeText(Signup_Activity.this, "Sign Up Success!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Signup_Activity.this, NewsFeedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ParseConstants.KEY_USER_LOGGED = true;
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                } else {
                    if(e.getCode() == 202)
                    Toast.makeText(getApplicationContext(), "username \""+userData[4]+"\" has already taken",
                            Toast.LENGTH_SHORT).show();
                    else if(e.getCode() == 203)
                        Toast.makeText(getApplicationContext(), userData[2]+" has already taken",
                                Toast.LENGTH_SHORT).show();
                    else if(e.getCode() == 100)
                        Toast.makeText(getApplicationContext(), "Connection failed, Try again",
                                Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Something went wrong, Try again",
                                Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public static class Signup_Fragment extends Fragment {
        TextView _descriptionIn, _detailDesIn, _pwdCount;
        TextInputLayout _outerTI;
        static EditText _textInput;
        int slide_num;
        String mDescription,mDetailDes, mHint;

        public static Signup_Fragment newInstance(int slide_num) {
            Bundle args = new Bundle();
            args.putInt("slideNum", slide_num);
            Signup_Fragment fragment = new Signup_Fragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            slide_num = getArguments() != null ? getArguments().getInt("slideNum") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.signup_fragment, container, false);
            iniView(v);
            setValidation(_textInput);
            if(!_textInput.getText().toString().isEmpty()) _next_btn.setEnabled(true);
            return v;
        }

        private void iniView(View v){
            _descriptionIn =(TextView) v.findViewById(R.id.description_su);
            _detailDesIn = (TextView) v.findViewById(R.id.detail_des_su);
            _outerTI = (TextInputLayout) v.findViewById(R.id.textinput_su);
            _textInput =(EditText) v.findViewById(R.id.userdata_input);
            _pwdCount = (TextView) v.findViewById(R.id.pwdcount);
            getStringMsg(slide_num);
            setTextandFont();
            _textInput.setHint(mHint);
            setInputType();
            setTextforUserdata();
        }

        private void setTextforUserdata(){
            String text = userData[slide_num-1];
            if(text != null){
                _textInput.setText(text);
                _textInput.setSelection(text.length());
            }
        }

        boolean ishide = true;
        private void setInputType(){

            if (slide_num == 3) {
                _textInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            } else if (slide_num == 4) {

                _textInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                _textInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eyeclose, 0);
                _textInput.setOnTouchListener(new View.OnTouchListener() {
                    final int DRIGHT = 2;
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            int rightD = _textInput.getRight() -
                                    _textInput.getCompoundDrawables()[DRIGHT].getBounds().width();
                            if(event.getRawX() >= _textInput.getRight()){
                                if(ishide){
                                    _textInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                    _textInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eyeopen, 0);
                                    ishide = false;
                                }else{
                                    _textInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                    _textInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eyeclose, 0);
                                    ishide = true;
                                }

                            }
                        }
                        return false;
                    }
                });
            }
        }

        private void setTextandFont(){
            _descriptionIn.setText(mDescription);
            _detailDesIn.setText(mDetailDes);
            setFont();
        }

        private void setValidation(EditText et){
            et.addTextChangedListener(new TextValidator(et) {
                @Override
                public void validate(EditText et) {
                    switch (slide_num) {
                        case 1:
                            nameValidate(et);
                            break;
                        case 3:
                            emailValidate(et);
                            break;
                        case 4:
                            passwordValidate(et);
                            break;
                        case 5:
                            usernameValidate(et);
                            break;
                    }
                }
            });
        }

        private void nameValidate(EditText et){
            String text = et.getText().toString();
            if(text.isEmpty()){
                _outerTI.setError("name cannot be empty");
                _next_btn.setEnabled(false);
            }else{
                _outerTI.setError(null);
                _next_btn.setEnabled(true);
            }
        }

        private void emailValidate(EditText et){
            String email = et.getText().toString();
            if(email.isEmpty()){
                _outerTI.setError("email cannot be empty");
                _next_btn.setEnabled(false);
            }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                _outerTI.setError("invalid email format");
                _next_btn.setEnabled(false);
            }else{
                _outerTI.setError(null);
                _next_btn.setEnabled(true);
            }
        }

        private void usernameValidate(EditText et){
            final String USERNAME_PATTERN = "^[a-z0-9_-]{3,20}$";
            Pattern pattern = Pattern.compile(USERNAME_PATTERN);
            String username = et.getText().toString();
            Matcher matcher = pattern.matcher(username);
            if(matcher.matches()){
                _outerTI.setError(null);
                _next_btn.setEnabled(true);
            }else{
                _outerTI.setError("invalid username format");
                _next_btn.setEnabled(false);
            }
        }

        private void passwordValidate(EditText et){
            _pwdCount.setVisibility(View.VISIBLE);
            _pwdCount.setTextColor(Color.parseColor("#bababa"));
            String password = et.getText().toString();
            _pwdCount.setText(Integer.toString(password.length()) + "/20");
            if(password.length()>20){
                _pwdCount.setTextColor(Color.parseColor("#FF0000"));
                _next_btn.setEnabled(false);
            }else if(password.length()<6){
                _next_btn.setEnabled(false);
            }else{
                _next_btn.setEnabled(true);
            }
        }

        private void setFont() {
             _descriptionIn.setTypeface(FontCache.get("fonts/Roboto-Light.ttf",
                     this.getActivity().getApplicationContext()));
            _detailDesIn.setTypeface(FontCache.get("fonts/Roboto-Light.ttf",
                    this.getActivity().getApplicationContext()));
            _textInput.setTypeface(FontCache.get("fonts/Roboto-Light.ttf",
                    this.getActivity().getApplicationContext()));
        }

        private void getStringMsg(int slide_num){
            switch (slide_num){
                case 1:
                    mDescription = getActivity().getString(R.string.sufragment_s1);
                    mDetailDes = getActivity().getString(R.string.sufragdetail_s1);
                    mHint = getActivity().getString(R.string.fullname_lbl);
                    break;
                case 3:
                    mDescription = getActivity().getString(R.string.sufragment_s3);
                    mDetailDes = getActivity().getString(R.string.sufragdetail_s3);
                    mHint = getActivity().getString(R.string.email_lbl);
                    break;
                case 4:
                    mDescription = getActivity().getString(R.string.sufragment_s4);
                    mDetailDes = getActivity().getString(R.string.sufragdetail_s4);
                    mHint = getActivity().getString(R.string.pwd_lbl);
                    break;
                case 5:
                    mDescription = getActivity().getString(R.string.sufragment_s5);
                    mDetailDes = getActivity().getString(R.string.sufragdetail_s5);
                    mHint = getActivity().getString(R.string.username_lbl);
                    break;
            }
        }
    }

    public static class SignUpMajorFragment extends Fragment{

        TextView _description,_detail_des;
        static TextInputLayout _majorInput;
        static EditText _majorList;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.signup_major_fragment, container, false);
            iniView(v);
            setFont();
            if(!_majorList.getText().toString().isEmpty()) _next_btn.setEnabled(true);
            return v;
        }


        private void iniView(View v){
            _description = (TextView) v.findViewById(R.id.description_major_su);
            _detail_des = (TextView) v.findViewById(R.id.detail_des_major_su);
            _majorInput = (TextInputLayout) v.findViewById(R.id.majorinput_su);
            _majorList = (EditText) v.findViewById(R.id.major_et);
            _majorList.setHint("Choose your major");
            majorListPopUpShow();
            setValidation();
            setTextforUserdata();
        }

        private void setTextforUserdata(){
            String text = userData[1];
            if(text != null){
                _majorList.setText(text);
                _majorList.setSelection(text.length());
            }
        }


        private void majorListPopUpShow(){
            final ListPopupWindow majorListPopup;
            final List<String> majorList = Arrays.asList(getResources().getStringArray(R.array.major_list));
            majorListPopup = new ListPopupWindow(getContext());
            majorListPopup.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,majorList));
            majorListPopup.setAnchorView(_majorInput);
            majorListPopup.setModal(true);
            majorListPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    String item = majorList.get(position);
                    _majorList.setText(item);
                    majorListPopup.dismiss();
                }
            });

            _majorList.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    majorListPopup.show();
                    return false;
                }
            });
        }

        private void setFont() {
            _description.setTypeface(FontCache.get("fonts/Roboto-Light.ttf",
                    this.getActivity().getApplicationContext()));
            _detail_des.setTypeface(FontCache.get("fonts/Roboto-Light.ttf",
                    this.getActivity().getApplicationContext()));
            _majorList.setTypeface(FontCache.get("fonts/Roboto-Light.ttf",
                    this.getActivity().getApplicationContext()));
        }

        private void setValidation(){
            _majorList.addTextChangedListener(new TextValidator(_majorList) {
                @Override
                public void validate(EditText et) {
                    if(_majorList.getText().toString().isEmpty()){
                        _next_btn.setEnabled(false);
                    }else{
                        _next_btn.setEnabled(true);
                    }

                }
            });
        }

    }

    private static abstract class TextValidator implements TextWatcher {

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
