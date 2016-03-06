package com.example.samps_000.fashionapp;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateAccount extends Activity implements OnServerCallCompleted {

    String CREATE_ACCOUNT_EXT = "/create_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void createClicked(View view) throws JSONException {

        final EditText Name = (EditText) findViewById(R.id.name);
        final EditText Email = (EditText) findViewById(R.id.email);
        final EditText Uname = (EditText) findViewById(R.id.username);
        final EditText Pass = (EditText) findViewById(R.id.password);
        final EditText Cpass = (EditText) findViewById(R.id.confirmPassword);
        final String NameText = Name.getText().toString();
        final String EmailText = Email.getText().toString();
        final String UnameText = Uname.getText().toString();
        final String PassText = Pass.getText().toString();

        if (errors(Name, Email, Uname, Pass, Cpass)) {

            JSONObject jObject = new JSONObject();
            Log.d("values", NameText + EmailText + UnameText + PassText);
            jObject.put("Name", NameText);
            jObject.put("Email", EmailText);
            jObject.put("Uname", UnameText);
            jObject.put("Pass", PassText);

            String text = jObject.toString();

            new ServerCall(CreateAccount.this).execute(text, CREATE_ACCOUNT_EXT, "POST");
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

