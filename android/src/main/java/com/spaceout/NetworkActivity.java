package com.spaceout;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

public class NetworkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network);
    }

    public void confirm(View view) {
        // TODO -- check for inputs all being between 0 and 255
        this.finish();
    }

    public void cancel(View view) {
        this.finish();
    }

}

