In order to set the app up and running, please follow these steps:
1) Download and install Android Studio from: https://developer.android.com/studio/
2) Download the code of the "MQTT app" from Github. 
The link is: https://github.com/Kerelos/MQTT-Android-application
3) Open Android Studio and run the app. The app can run on both the Android Emulator or a mobile phone connected through USB.
4) For connecting and installing the app on a mobile phone through USB, you need to enable the Developer Options. For more info, please check this link: https://www.youtube.com/watch?v=p2oHD-06YcM

Assumptions:
1) Each time a user connects, a random client ID is generated through the app.
2) When a message arrives to a phone, I added a feature of making the phone vibrate and also make a notification sound.
3) I used these 2 .jar files:
org.eclipse.paho.android.service-1.0.2.jar
org.eclipse.paho.client.mqttv3-1.0.2.jar
