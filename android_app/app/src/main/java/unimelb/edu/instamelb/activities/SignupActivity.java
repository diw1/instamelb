package unimelb.edu.instamelb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unimelb.edu.instamelb.database.DatabaseHandler;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name)
    EditText _nameText;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(v);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup(View view) {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }
        _signupButton.setEnabled(false);
        NetAsync(view);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            _nameText.setError("at least 4 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private class ProcessRegister extends AsyncTask<String,String,String> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String email,password,name;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            name = _nameText.getText().toString();
            email = _emailText.getText().toString();
            password = _passwordText.getText().toString();
            pDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Base);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Creating Account...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String result="";
            try {
                APIRequest request = new APIRequest();
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                params.add(new BasicNameValuePair("username", name));
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("password", password));
                result = request.createRequest("POST", "/register", params);

            }catch (Exception e){
                e.printStackTrace();
            }
                return result;
        }

        @Override
        protected void onPostExecute(String string) {
            /**
             * Checks for success message.
             **/
            try {
                JSONObject object = new JSONObject(string);
                Boolean registered=object.getBoolean("registered");
                if (registered) {
                    pDialog.setTitle("Getting Data");
                    pDialog.setMessage("Loading Info");
                    Toast.makeText(getBaseContext(), "Successfully Registered", Toast.LENGTH_LONG).show();
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    /**
                     * Removes all the previous data in the SQlite database
                     **/

                    db.resetTables();
                    object=object.getJSONObject("user");
                    db.addUser(object.getString("email"),object.getString("username"),password,object.getString("user_id"));
                    /**
                     * Stores registered data in SQlite Database
                     * Launch Registered screen
                     **/
                    pDialog.dismiss();
                    _signupButton.setEnabled(true);
                    setResult(RESULT_OK, null);
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    finish();
                }

                else{
                    pDialog.dismiss();
                    _signupButton.setEnabled(true);
                    String error=object.getString("error");
                    Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
    public void NetAsync(View view){
        new ProcessRegister().execute();
    }
}