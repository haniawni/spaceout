spaceout
========

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

Language: Java
