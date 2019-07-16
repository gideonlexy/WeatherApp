package com.stardev.soigolexy.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText  = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);

    }

    public void getWeather(View view) {

        editText.getText();
        String result = "";

        DownloadTask task  = new DownloadTask();
        try {
            String encodeCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            result = task.execute("https://openweathermap.org/data/2.5/weather?q="+ encodeCityName +"&appid=b6907d289e10d714a6e88b30761fae22").get();
            InputMethodManager mngr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mngr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't Downlaod the Weather", Toast.LENGTH_LONG).show();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data !=-1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't get the Weather", Toast.LENGTH_LONG).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                String details = jsonObject.getString("main");
                Log.i("Weather Content",weatherInfo);


                JSONArray array = new JSONArray(weatherInfo);
                String message = "";
                for (int i=0; i<array.length(); i++) {
                    JSONObject jsonObject1 = array.getJSONObject(i);

                    String main = jsonObject1.getString("main");
                    String description = jsonObject1.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message += main + ":" + description + "\r\n";
                    }
                }

                    if (!message.equals("")){
                        resultTextView.setText(message);
                    }else {
                        Toast.makeText(getApplicationContext(), "Couldn't get the Weather", Toast.LENGTH_LONG).show();
                    }



            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't get the Weather", Toast.LENGTH_LONG).show();
            }

        }
    }

}
