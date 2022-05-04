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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.ibm.cloud.sdk.core.http.ServiceCallback;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.tone_analyzer.v3.model.ToneOptions;
import com.ibm.watson.tone_analyzer.v3.model.ToneScore;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    TextView txt1 ;
    LottieAnimationView animationViewloader ;
    EditText edtTxt1;
    CardView btn1;
    View view;
    String sentiment;
    double score=0.0f;
    boolean check = true;

    private SensorManager mSensorManager;
    private Sensor mHumiditySensor;
    private Sensor mTemperatureSensor;
    private boolean isHumiditySensorPresent;
    private boolean isTemperatureSensorPresent;
    private float temperature = 0;
    private float mLastKnownRelativeHumidity = 0;
    String temperatureS = "NR";
    String humidityS = "NR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txt1 =  (TextView) findViewById(R.id.txt1);
        edtTxt1 =  (EditText) findViewById(R.id.edttxt1);
        animationViewloader = (LottieAnimationView) findViewById(R.id.animationViewloader);
        btn1 = (CardView) findViewById(R.id.analyize_btn);
        view = (View) findViewById(R.id.view_id);


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


    public void AnalyizeData(View view) {

        if(!edtTxt1.equals("")) {
            edtTxt1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            btn1.setVisibility(View.GONE);
            view.setVisibility(View.GONE);

            animationViewloader.setVisibility(View.VISIBLE);

            System.out.println("logging to the console that the button pressed for the test" + edtTxt1.getText());
            txt1.setText("Display at UI the sentiment to be checked for" + edtTxt1.getText());

            AskWatsonTask task = new AskWatsonTask();
            task.execute(new String[]{});
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Enter text......",Toast.LENGTH_SHORT).show();

        }
    }




    private class AskWatsonTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... TextToAnalyse) {
            System.out.println(edtTxt1.getText());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt1.setText("Analyizing the text......");
                }
            });
            Authenticator authenticator = new IamAuthenticator("qOQvdMRiNw5sn9dNH6h0aJXrpRi0nMdzLzhzJYtKvdTs");
            ToneAnalyzer service = new ToneAnalyzer("2017-09-21", authenticator);

            ToneOptions toneOptions = new ToneOptions.Builder().text(String.valueOf(edtTxt1.getText())).build();
            ToneAnalysis tone = service.tone(toneOptions).execute().getResult();
            System.out.println(tone.getDocumentTone());


            sentiment = "Test Sentiment";
            //System.out.println(tone);


            try {

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
             //   Toast.makeText(getApplicationContext(),"Try Again Robot is confuse",Toast.LENGTH_SHORT).show();
                return null;
            }

        }

       @Override
       protected void onPostExecute(String result) {
         // txt1.setText("The message sentiment is "+ result);
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
               Toast.makeText(getApplicationContext(),"Try Again Robot is confuse",Toast.LENGTH_SHORT).show();
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




    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY) {
         //   mRelativeHumidityValue.setText("Relative Humidity in % is " + event.values[0]);
            mLastKnownRelativeHumidity = event.values[0];
            humidityS=String.valueOf(mLastKnownRelativeHumidity);
        } else if(event.sensor.getType()==Sensor.TYPE_AMBIENT_TEMPERATURE) {
            if(mLastKnownRelativeHumidity !=0) {
                temperature = event.values[0];
                temperatureS=String.valueOf(temperature);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void ShowDialogBox(View view) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_sheet);

        ImageButton closeDialog = (ImageButton) bottomSheetDialog.findViewById(R.id.close_dialog_sheet);

        TextView temperatureText = (TextView) bottomSheetDialog.findViewById(R.id.temperature_text);
        TextView humidityText = (TextView) bottomSheetDialog.findViewById(R.id.humdity_text);

        temperatureText.setText(temperatureS="\u2103");
        humidityText.setText(humidityS+"%");

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.show();
    }


}
