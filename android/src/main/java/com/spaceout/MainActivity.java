package com.spaceout;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 1;

    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void setNetwork(View view) {
        Intent networkIntent = new Intent(this, NetworkActivity.class);
        networkIntent.putExtra("ipAddress", this.ipAddress);
        startActivityForResult(networkIntent, REQUEST_CODE);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, NeuralAlertnessService.class));

        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                this.ipAddress = data.getStringExtra("ipAddress");
                Log.d("SPACEOUT", "ip address of server set to " + this.ipAddress);

                // restart the service with the new ip address
                stopService(new Intent(this, NeuralAlertnessService.class));
                Intent serviceIntent = new Intent(this, NeuralAlertnessService.class);
                serviceIntent.putExtra("ipAddress", this.ipAddress);
                startService(serviceIntent);

                TextView subtitles = (TextView) findViewById(R.id.subtitles);
                subtitles.setText("IP address: " + ipAddress + "\n\n---\n\n");
            }
        }
    }

}
