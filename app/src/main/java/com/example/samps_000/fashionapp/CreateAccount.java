package com.example.samps_000.fashionapp;

import android.app.Activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.models.RegisterParams;
import com.stormpath.sdk.models.StormpathError;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_LONG;


public class CreateAccount extends Activity implements OnServerCallCompleted {

    String ADD_USER_EXT = "/add_user";
    String SERVER_URL = "http://ec2-52-33-232-213.us-west-2.compute.amazonaws.com:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        // Initialize Stormpath
        StormpathConfiguration stormpathConfiguration = new StormpathConfiguration.Builder()
                .baseUrl(SERVER_URL)
                .build();
        Stormpath.init(this, stormpathConfiguration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If not logged in, show stormpath activity.
        /*
        if (Stormpath.accessToken() == null) {
            startActivity(new Intent(this, StormpathLoginActivity.class));
        } else {
            getNotes();
        }
        */
    }

    public void loginLinkClicked(View view) {
        Intent i = new Intent(CreateAccount.this, Login.class);
        startActivity(i);
    }

    public void createClicked(View view) throws JSONException {

        final EditText firstName = (EditText) findViewById(R.id.firstName);
        final EditText lastName = (EditText) findViewById(R.id.lastName);
        final EditText Email = (EditText) findViewById(R.id.email);
        final EditText Pass = (EditText) findViewById(R.id.password);
        final EditText Cpass = (EditText) findViewById(R.id.confirmPassword);
        final String firstNameText = firstName.getText().toString();
        final String lastNameText = lastName.getText().toString();
        final String emailText = Email.getText().toString();
        final String passText = Pass.getText().toString();

        if (errors(firstName, lastName, Email, Pass, Cpass)) {

            JSONObject jObject = new JSONObject();
            Log.d("values", firstNameText  + lastNameText + emailText +  passText);
            jObject.put("Email", emailText);

            String text = jObject.toString();

            //new ServerCall(CreateAccount.this).execute(text, CREATE_ACCOUNT_EXT, "POST");
            RegisterParams registerParams = new RegisterParams(firstNameText, lastNameText, emailText, passText);
            Stormpath.register(registerParams, new StormpathCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent i = new Intent(CreateAccount.this, Feed.class);
                    startActivity(i);
                }

                @Override
                public void onFailure(StormpathError error) {
                    Log.d("STORMPATH ERROR", error.message());
                    Toast.makeText(CreateAccount.this, error.message(), LENGTH_LONG).show();
                }
            });

            new ServerCall(CreateAccount.this).execute(text, ADD_USER_EXT, "POST");
        }
    }

    private boolean errors(EditText name, EditText email, EditText uname, EditText pass, EditText cpass) {
        ArrayList<EditText> edit_texts = new ArrayList<>();
        edit_texts.add(name);
        edit_texts.add(email);
        edit_texts.add(uname);
        edit_texts.add(pass);
        edit_texts.add(cpass);

        if (!CheckInput.checkInput(edit_texts)) {
            Toast.makeText(CreateAccount.this, "Incomplete Information", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pass.getText().toString().equals(cpass.getText().toString()))

        {
            Log.d("PASSWORDS", "p1: " + pass.getText().toString() + " pass2: " + cpass.getText().toString() + "|");
            Toast.makeText(CreateAccount.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void PerformResponse(int status) {
        if (status != 200) {
            Toast.makeText(CreateAccount.this, R.string.failed_create_account_toast, Toast.LENGTH_SHORT).show();
            Log.d("Status Code: ", Integer.toString(status));
        } else {
            Toast.makeText(CreateAccount.this, R.string.success_create_account_toast, Toast.LENGTH_SHORT).show();

        }
    }
}

