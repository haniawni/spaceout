package com.spaceout;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.TextView;

import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCards;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.DeckOfCardsManager;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.ListCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.SimpleTextCard;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;

    private String ipAddress;

	private DeckOfCardsManager toqManager;
	private RemoteDeckOfCards toqDeck;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView subtitles = (TextView) findViewById(R.id.subtitles);
            String spokenText = intent.getStringExtra("text");
            subtitles.append(spokenText + "\n");

			String[] messageText = new String[1];
			messageText[0] = "Message Text";
			ListCard listCard = toqDeck.getListCard();
			SimpleTextCard newCard = new SimpleTextCard(
				""+listCard.size(),
				"Header Text",
				5000,
				"Title Text",
				messageText
			);
			listCard.add(newCard);
            try {
                toqManager.updateDeckOfCards(toqDeck);
            } catch (Exception ex) {
                Log.d("SPACEOUT", ex.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


		// initialize the Toq display for our app
		toqManager = DeckOfCardsManager.getInstance(this);
		toqDeck = new RemoteDeckOfCards(this);
        try {
            toqManager.installDeckOfCards(toqDeck);
        } catch (Exception ex) {
            Log.d("SPACEOUT", ex.getMessage());
        }
    }

    @Override
    protected void onResume() {
        // start the speech recognition service
        Intent serviceIntent = new Intent(this, SpeechToTextService.class);
        startService(serviceIntent);

        // trigger detected speech to append to the subtitles
        IntentFilter filter = new IntentFilter();
        filter.addAction(SpeechToTextService.SPEECH_DETECTED);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        stopService(new Intent(this, SpeechToTextService.class));

        super.onPause();
    }

    public void setNetwork(View view) {
        Intent networkIntent = new Intent(this, NetworkActivity.class);
        networkIntent.putExtra("ipAddress", this.ipAddress);
        startActivityForResult(networkIntent, REQUEST_CODE);
    }

    public void clearSubtitles(View view) {
        if (this.ipAddress != null) {
            TextView subtitles = (TextView) findViewById(R.id.subtitles);
            subtitles.setText("IP address: " + this.ipAddress + "\n\n---\n\n");
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, NeuralAlertnessService.class));
        stopService(new Intent(this, SpeechToTextService.class));

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

                clearSubtitles(null);
            }
        }
    }

}
