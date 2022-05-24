// Zahraa Abbas S00050748
package com.project.sentimentanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowActivity extends AppCompatActivity {

    TextView txt2 , scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        txt2 = (TextView) findViewById(R.id.text1);
        scoreText = (TextView) findViewById(R.id.score);

        ImageView img= (ImageView) findViewById(R.id.emoji);

        String id = getIntent().getStringExtra("sentiment");
        String score = getIntent().getStringExtra("score");

        txt2.setText(id);
        scoreText.setText("Accuracy:  "+score+"%");
        if(id.equals("joy"))
        {
            img.setImageResource(R.drawable.joy);
        }
        else  if(id.equals("sadness"))
        {
            img.setImageResource(R.drawable.sadness);
        }
        else if(id.equals("fear"))
        {
            img.setImageResource(R.drawable.fear);
        }
        else if(id.equals("anger"))
        {
            img.setImageResource(R.drawable.anger);
        }
        else if(id.equals("analytical"))
        {
            img.setImageResource(R.drawable.analytical);
        }
        else if(id.equals("confident"))
        {
            img.setImageResource(R.drawable.confident);
        }
        else if(id.equals("tentative"))
        {
            img.setImageResource(R.drawable.tentative);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShowActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void BackActivity(View view) {
        onBackPressed();
    }
}