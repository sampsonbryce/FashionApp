package com.example.samps_000.fashionapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.models.StormpathError;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by samps_000 on 1/15/2016.
 */
public class Login extends Activity implements OnServerCallCompleted {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText password = (EditText) findViewById(R.id.loginPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        Log.d("BASE URL", this.getString(R.string.SERVER_URL));
        if (!Stormpath.isInitialized()) {
            StormpathConfiguration stormpathConfiguration = new StormpathConfiguration.Builder()
                    .baseUrl(this.getString(R.string.SERVER_URL))
                    .build();
            Stormpath.init(this, stormpathConfiguration);
        }

    }

    public void createLinkClicked(View view) {
        Intent i = new Intent(Login.this, CreateAccount.class);
        startActivity(i);
    }

    public void loginClicked(View view) throws JSONException, NoSuchAlgorithmException {

        EditText Email = (EditText) findViewById(R.id.loginEmail);
        EditText Pass = (EditText) findViewById(R.id.loginPassword);

        String EmailText = Email.getText().toString();
        String PassText = Pass.getText().toString();

        JSONObject jObject = new JSONObject();
        jObject.put("Email", EmailText);

        String text = jObject.toString();
        Log.d("checks", "text"+text);

        ArrayList<EditText> edit_texts = new ArrayList<>();

        edit_texts.add(Email);
        edit_texts.add(Pass);


        if (errors(edit_texts)) {
            Stormpath.login(EmailText, PassText, new StormpathCallback<Void>() {

                @Override
                public void onSuccess(Void aVoid) {
                    Intent i = new Intent(Login.this, Feed.class);
                    startActivity(i);
                }

                @Override
                public void onFailure(StormpathError error) {
                    Log.d("STORMPATH ERROR", error.message());
                    Toast.makeText(Login.this, error.message(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void PerformResponse(int status) {
        if (status != 200) {
            Toast.makeText(Login.this, R.string.failed_login_toast, Toast.LENGTH_SHORT).show();
            Log.d("Status Code: ", Integer.toString(status));
        } else {
            Intent i = new Intent(Login.this, Feed.class);
            startActivity(i);
        }
    }

    private boolean errors(ArrayList edit_texts) {
        if (!CheckInput.checkInput(edit_texts)) {
            Toast.makeText(Login.this, R.string.incomplete_info_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
