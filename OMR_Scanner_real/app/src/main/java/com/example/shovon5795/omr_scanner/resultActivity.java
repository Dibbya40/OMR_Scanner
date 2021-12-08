package com.example.shovon5795.omr_scanner;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.telephony.SmsManager;


public class resultActivity extends AppCompatActivity {
    TextView mainText, studentText,result;
    Button button1, bt2;
    int correct=0;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        button1=(Button)findViewById(R.id.bt1);
        bt2=(Button)findViewById(R.id.bt2);
        mainText = (TextView) findViewById(R.id.main);
        studentText = (TextView) findViewById(R.id.student);
        result = (TextView) findViewById(R.id.result);
        Bundle bundle = getIntent().getExtras();
        //String message = bundle.getString("Result");

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        String main =sharedPreferences.getString("main","");

        main=main.replace("]","");
        main=main.replace("[","");

        List<String> mainList = new ArrayList<String>(Arrays.asList(main.split(",")));

        String mainShow="";
        for(int i=0;i<mainList.size();i++){
            if(mainList.get(i).trim().equals("-1")){
                mainShow+=(i+1)+" : "+" Not filled "+"    ";
            }
            else mainShow+=(i+1)+" : "+mainList.get(i)+"    ";
        }

        String stu =sharedPreferences.getString("student","");

        stu=stu.replace("]","");
        stu=stu.replace("[","");

        List<String> stuList = new ArrayList<String>(Arrays.asList(stu.split(",")));


        String stuShow="";
        for(int i=0;i<stuList.size();i++){
            if(stuList.get(i).trim().equals("-1")){
                stuShow+=(i+1)+" : "+" Not filled "+"    ";
            }
            else
            stuShow+=(i+1)+" : "+stuList.get(i)+"    ";
        }


        for(int i=0;i<stuList.size();i++){
            if(!mainList.get(i).trim().equals("-1") && !stuList.get(i).trim().equals("-1") && mainList.get(i).equals(stuList.get(i))){
                correct++;
                //Toast.makeText(this, Integer.toString(i+1)+" correct ", Toast.LENGTH_SHORT).show();
            }

            //else Toast.makeText(this, Integer.toString(i+1)+" incorrect ", Toast.LENGTH_SHORT).show();
        }


        mainText.setText(mainShow);
        studentText.setText(stuShow);

        result.setText("Obtained Marks: "+correct);



       // StringBuilder builder = new StringBuilder();

        //textview2.setText(message);
   /*     button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(resultActivity.this, MainActivity2.class);
                startActivity(intent);



            }
        });
        */
       /* Bundle bundle1 = getIntent().getExtras();
        String message1 = bundle1.getString("Result");
        textview3.setText(message1);
        */

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   String s="Obtained Marks";
                   Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:+8801676892449"));
                   smsIntent.putExtra("sms_body","Obtained Marks: "+Integer.toString(correct));
                   startActivity(smsIntent);


            }
        });



       button1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(resultActivity.this, MainActivity.class);
               //intent.putExtra("Result", Arrays.toString(answer));
               intent.putExtra("type","Student OMR");
               SharedPreferences.Editor editor = sharedPreferences.edit();
               editor.remove("student");
               editor.commit();
               startActivity(intent);
           }
       });




            // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }



}

