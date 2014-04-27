package com.spaceout;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class NetworkActivity extends Activity {
    private EditText lastFocused = null;

    final int[] ids = new int[] {
        R.id.ip_0,
        R.id.ip_1,
        R.id.ip_2,
        R.id.ip_3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network);

        final Button okButton = (Button) findViewById(R.id.ip_ok);
        okButton.setEnabled(false);

        for (int id : ids) {
            EditText editText = (EditText) findViewById(id);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    okButton.setEnabled(validateInputs());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    public boolean validateInputs() {
        for (int id : ids) {
            EditText editText = (EditText) findViewById(id);
            String valueString = editText.getText().toString();
            if (valueString.isEmpty())
                return false;

            int val = Integer.parseInt(valueString);
            if (val < 0 || val > 255) {
                return false;
            }
        }
        return true;
    }

    public void confirm(View view) {
        String ipAddress = TextUtils.join(".", new String[] {
            ((EditText) findViewById(ids[0])).getText().toString(),
            ((EditText) findViewById(ids[1])).getText().toString(),
            ((EditText) findViewById(ids[2])).getText().toString(),
            ((EditText) findViewById(ids[3])).getText().toString(),
        });

        // pass ip address to client http service
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ipAddress", ipAddress);
        setResult(RESULT_OK, returnIntent);
        this.finish();
    }

    public void cancel(View view) {
        this.finish();
    }

}

