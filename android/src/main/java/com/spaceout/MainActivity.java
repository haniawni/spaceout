package com.spaceout;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startService(new Intent(this, NeuralAlertnessService.class));
    }

    public void setNetwork(View view) {
        startActivity(new Intent(this, NetworkActivity.class));
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, NeuralAlertnessService.class));

        super.onDestroy();
    }

}
