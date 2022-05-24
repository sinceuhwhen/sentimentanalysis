// Tala Bin Hussein S00049918
package com.project.sentimentanalysis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // initialise variables
    TextView intro ;
    LottieAnimationView animationViewloader ;
    EditText textEntry;
    CardView btn1;
    View view;
    String sentiment;
    double score=0.0f;
    boolean check = true;

    // initialise variables for temperature and humidity sensors
    private SensorManager mSensorManager;
    private Sensor mHumiditySensor;
    private Sensor mTemperatureSensor;
    private boolean isHumiditySensorPresent;
    private boolean isTemperatureSensorPresent;
    private float temperature = 0;
    private float mLastKnownRelativeHumidity = 0;
    String temperatureS = "41";
    String humidityS = "13";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // binding the widgets with XML
        intro =  (TextView) findViewById(R.id.intro);
        textEntry =  (EditText) findViewById(R.id.textEntry);
        animationViewloader = (LottieAnimationView) findViewById(R.id.animationViewloader);
        btn1 = (CardView) findViewById(R.id.analyseButton);
        view = (View) findViewById(R.id.view_id);


        // Create an instance for Sensors and
        // then check whether the current mobile has temperature and humidity sensors or not
        // if the sensors do not exist, status will be set to NA ( Not Available )
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            mHumiditySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

            isHumiditySensorPresent = true;
        }
        else {
            humidityS="NA";

            isHumiditySensorPresent = false;
        }

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            mTemperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);


            isTemperatureSensorPresent = true;
        } else {
            temperatureS="NA";
            isTemperatureSensorPresent = false;
        }

    }


    // onClick function which will work when the analyse button is pressed
    public void AnalyseData(View view) {

        if(!textEntry.equals("")) {
            textEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
            btn1.setVisibility(View.GONE);
            view.setVisibility(View.GONE);

            animationViewloader.setVisibility(View.VISIBLE);

            System.out.println("logging to the console that the button pressed for the test" + textEntry.getText());
            intro.setText("Display at UI the sentiment to be checked for" + textEntry.getText());

            AskWatsonTask task = new AskWatsonTask();
            task.execute(new String[]{});
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Enter text......",Toast.LENGTH_SHORT).show();

        }
    }




    // this function can analyse the sentence and get a response in the json array
    // from the IBM tone analyser Service
    // "tones": [
    //    {
    //    "score": 0.913043,
    //      "tone_id": "id",
    //     "tone_name": "name"
    //   },
    // if the service failed to analyse the sentence
    // they will return the empty string and App will show the Toast (Try Again Robot is confused)

    private class AskWatsonTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... TextToAnalyse) {
            System.out.println(textEntry.getText());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    intro.setText("Analysing the text...");
                }
            });
            Authenticator authenticator = new IamAuthenticator("qOQvdMRiNw5sn9dNH6h0aJXrpRi0nMdzLzhzJYtKvdTs");
            ToneAnalyzer service = new ToneAnalyzer("2017-09-21", authenticator);

            ToneOptions toneOptions = new ToneOptions.Builder().text(String.valueOf(textEntry.getText())).build();
            ToneAnalysis tone = service.tone(toneOptions).execute().getResult();
            System.out.println(tone.getDocumentTone());


            sentiment = "Test Sentiment";
            //System.out.println(tone);


            try {

                // In response there was a json array
                // {
                //  "tones": [
                //    {
                //     "score": 0.913043,
                //      "tone_id": "fear",
                //     "tone_name": "Fear"
                //   },
                //    {
                //       "score": 0.984352,
                //      "tone_id": "tentative",
                //      "tone_name": "Tentative"
                //    }
                //  ]
                // the app can show the index which has the highest score
                // in the above json array tone id tentative has high score

                int  i=0 , id =0;
                double prescore =0;

                for(;i<tone.getDocumentTone().getTones().size();i++) {
                    if(prescore<tone.getDocumentTone().getTones().get(i).getScore() ) {
                        score = tone.getDocumentTone().getTones().get(i).getScore();
                        prescore =score;
                        id = i;
                        score = score * 100;
                    }
                }
                return tone.getDocumentTone().getTones().get(id).getToneId().toString();
            } catch (Exception exception) {

                check= false;
                //   Toast.makeText(getApplicationContext(),"Try Again, Robot is confused",Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            // intro.setText("The message sentiment is "+ result);
            if(check ) {
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("sentiment", result );
                intent.putExtra("score",  String.format("%.2f", score));


                startActivity(intent);
                finish();
            }

            else
            {
                btn1.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);

                animationViewloader.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Try Again, Robot is confused",Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isHumiditySensorPresent) {
            mSensorManager.registerListener(this, mHumiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(isTemperatureSensorPresent) {
            mSensorManager.registerListener(this, mTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isHumiditySensorPresent || isTemperatureSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }




    // when the temperature and humidity of surrounding changes this function can update the values
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY) {
            //   mRelativeHumidityValue.setText("Relative Humidity in % is " + event.values[0]);
            mLastKnownRelativeHumidity = event.values[0];
            humidityS=String.valueOf(mLastKnownRelativeHumidity)+"%";
        } else if(event.sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE) {
            if(mLastKnownRelativeHumidity !=0) {
                temperature = event.values[0];
                temperatureS=String.valueOf(temperature)+"\u2103";

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // In this function, dialogue will pop up from thr bottom and show the humidity and temperature of the surroundings
    public void ShowDialogBox(View view) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_sheet);

        ImageButton closeDialog = (ImageButton) bottomSheetDialog.findViewById(R.id.close_dialog_sheet);

        TextView temperatureText = (TextView) bottomSheetDialog.findViewById(R.id.temperatureText);
        TextView humidityText = (TextView) bottomSheetDialog.findViewById(R.id.humdityText);

        temperatureText.setText(temperatureS);
        humidityText.setText(humidityS);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.show();
    }


}
