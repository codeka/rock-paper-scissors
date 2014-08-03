Rock Paper Scissors
===================

A simple game for Android Wearables, Rock Paper Scissors is a two-player rock-paper-scissors clone. You start the game on your phone, and the main screen pops up on the watch. After waiting for another player to join (they can be anywhere in the world), the game begins. Choose your weapon: rock; paper; or scissors, in the couple of seconds you have available, and see whether you win!

![Screenshot](http://lh4.ggpht.com/bw3YkrLLrMIeHiEv7XxCaiF4_Y4158x4eIGcv9nLJR7cdP0TuIcPG_YS2c63-rg7sJOSkYPG2IZUee8C4EQUAQM=s280 "Screenshot")

The code is pretty horrible, a lot of state is duplicated between the watch and the phone that probably doesn't need to be. Also, the server is not as elegant as I originally had it, since I want to avoid the cost of running an App Engine backend 24/7, so I have to resort to polling.

Still, Not bad for a version 0.1!
