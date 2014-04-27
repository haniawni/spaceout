spaceout
========

Spaceout is a experimental project created during Hackendo 2014 focusing on
wearable tech. This project uses an [Emotiv
EPOC](http://emotiv.com/epoc/features.php) to detect when a user is
daydreaming and an Android phone to constantly record ambient audio. If the
user is spacing out, an alert is flashed on the user's phone and the recorded
audio is streamed to a [Wit](https://wit.ai/) device for audio-to-text
translation. This text is then displayed to the user on their phone (or even
better, on a Google Glass-style heads-up display) so the user can catch up on
what they missed while daydreaming.

Currently a laptop or other general computer is required to run the code
polling the Emotiv headset, but when Emotiv releases an Android version of
their API then the polling code can be merged into the Android side of this
project and the laptop requirement eliminated.


## Laptop-side
Poll Emotive hardware continuously over serial interface (Java) for mental state.
If space-out state is detected, store to internal variable and wait for next request from Android side to respond with message indicating the space-out state has occurred.

Language: Java

### Directions
```
cd neural
mvn install
java -jar target/spaceout-1.0-SNAPSHOT.jar
```

**Note:** Use `DEBUG=1` Java system property (-D) to use with the EmoComposer software.


## Android-side
Continously record audio, keeping a buffer of the last n seconds of audio,
where n is somewhere around 10 s. Continuously poll Laptop-side, checking if a space-out state has occurred.
When space-out has occurred, send audio buffer to wit.ai. Flash screen chartreuse and display text
received back from wit.ai to the screen. Continue translation in this manner until user hits OK button or Wit.AI returns no text translated.

