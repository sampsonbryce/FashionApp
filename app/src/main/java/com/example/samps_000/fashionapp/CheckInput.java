package com.example.samps_000.fashionapp;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samps_000 on 2/28/2016.
 */
public class CheckInput {

    public static boolean checkInput(ArrayList<EditText> edit_texts) {

        boolean fieldsFilled = true;

        for (int i = 0; i < edit_texts.size(); i++) {
            if (edit_texts.get(i).getText().toString().matches("")) {
                fieldsFilled = false;
            }
        }
        return fieldsFilled;
    }
}
