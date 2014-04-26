spaceout
========

Laptop
--
Poll Emotive hardware for mental state. If space-out state is detected, notify
the Android segment.

Android
--
Continously record audio, keeping a buffer of the last n seconds of audio,
where n is somewhere around 10 s. When a signal is received from the laptop
segment, send audio buffer to wit.ai. Flash screen chartreuse and display text
received back from wit.ai to the screen.

