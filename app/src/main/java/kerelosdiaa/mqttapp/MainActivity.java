package kerelosdiaa.mqttapp;

import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import static kerelosdiaa.mqttapp.R.id.publish;
import static kerelosdiaa.mqttapp.R.id.subscribe;
import static kerelosdiaa.mqttapp.R.id.topic;
import static kerelosdiaa.mqttapp.R.id.unsubscribe;


public class MainActivity extends AppCompatActivity {

    String clientID;
    MqttAndroidClient client;
    Vibrator vibrator;
    Ringtone ringtone;
   DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(),uri);
    }
    public void submit(View view){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        EditText editTextTopic = (EditText) findViewById(topic);
        String topicName = editTextTopic.getText().toString();
        final EditText recv = (EditText) findViewById(R.id.recevMsg);


        if( client.isConnected() && !topicName.equals(""))
        {
            if(radioGroup.getCheckedRadioButtonId() == subscribe)
            {
                subscribe(topicName,recv);
            }
            else if(radioGroup.getCheckedRadioButtonId() == unsubscribe)
            {
              unsubscribe(topicName);
            }
            else if(radioGroup.getCheckedRadioButtonId() == publish)
            {
               publish(topicName);
            }
        }
    }
    public void unsubscribe(String topicName){

        try {
            IMqttToken unsubToken = client.unsubscribe(topicName);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"Unsubscribed successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(MainActivity.this,"Unsubscribed NOT successful",Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
        public void subscribe(final String topicName, final EditText recv){
        try {
            client.subscribe(topicName,1);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Toast.makeText(MainActivity.this,"Connection Lost",Toast.LENGTH_LONG).show();

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    recv.setText(new String(mqttMessage.toString()));
                    vibrator.vibrate(600);
                    ringtone.play();
                  db.insertData(topicName,mqttMessage.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void publish(String topicName){
        Switch switchMsg = (Switch) findViewById(R.id.switchmsg);

        String payload;
          if(switchMsg.isChecked())
            payload = "Hello";
        else
            payload = "Bye";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topicName, message);
            Toast.makeText(MainActivity.this,"message published successfully",Toast.LENGTH_LONG).show();

        } catch (UnsupportedEncodingException | MqttException e) {
            Toast.makeText(MainActivity.this,"message DIDN'T publish successfully",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public void clientConnect(View view){
        clientID = MqttClient.generateClientId();
         client = new MqttAndroidClient(this.getApplicationContext(), "tcp://m11.cloudmqtt.com:12619", clientID);
        MqttConnectOptions options = new MqttConnectOptions();

        options.setUserName("uacksnkb");
        options.setPassword("ciJOnSCD3khG".toCharArray());

        try {
            final IMqttToken token = client.connect(options);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Toast.makeText(MainActivity.this,"connected successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Toast.makeText(MainActivity.this,"failed to connect",Toast.LENGTH_LONG).show();

                }
            });



        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void clientDisconnect(View view){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"disconnected successfully",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(MainActivity.this,"failed to disconnect",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void showHistory(View view){
      Cursor res = db.retrieveData();
        if(res.getCount() == 0){
            showMessage("Error","No data in db");
            return;
        }

            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()){
                buffer.append("ID : " + res.getString(0) + "\n");
                buffer.append("Topic : " + res.getString(1) + "\n");
                buffer.append("Message : " + res.getString(2) + "\n");
            }
        showMessage("History",buffer.toString());
    }
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
