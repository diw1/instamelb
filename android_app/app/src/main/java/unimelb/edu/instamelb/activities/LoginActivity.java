package unimelb.edu.instamelb.activities;

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
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unimelb.edu.instamelb.database.DatabaseHandler;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private DatabaseHandler db;
    private HashMap user;

    @InjectView(R.id.input_email)
    EditText _usernameText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        db= new DatabaseHandler(getApplicationContext());

        user = db.getUserDetails();
        //Log.d("USER:", (String) user.get("uid"));
        if (user.get("uid")!=null){
            finish();
        }
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login(View v) {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);
        NetAsync(v);
        // TODO: Implement your own authentication logic here.
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            _usernameText.setError("at least 4 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessLogin extends AsyncTask<String,String,String> {
        String user, password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            user = _usernameText.getText().toString();
            password = _passwordText.getText().toString();

        }

        @Override
        protected String doInBackground(String... args) {
            String result = "";
            try {
                APIRequest request = new APIRequest(user, password);
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                params.add(new BasicNameValuePair("users", "self"));
                result = request.createRequest("GET", "/", params);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String string) {
            if (!string.isEmpty()) {
                try {
                    JSONObject object = new JSONObject(string);
                    if (object.getString("user_id") != null) {

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        db.resetTables();
                        db.addUser(object.getString("email"), object.getString("username"), password, object.getString("user_id"));
                        /**
                         *If JSON array details are stored in SQlite it launches the User Panel.
                         **/
                        finish();
//                    Intent home = new Intent(getApplicationContext(), ActivityMain.class);
//                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    pDialog.dismiss();
//                    startActivity(home);
                        /**
                         * Close Login Screen
                         **/
                    } else {
                        _loginButton.setEnabled(true);
                        Toast.makeText(getBaseContext(), "Incorrect username/password", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                _loginButton.setEnabled(true);
                Toast.makeText(getBaseContext(), "Incorrect username/password", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void NetAsync(View view) {
        new ProcessLogin().execute();
    }

}
