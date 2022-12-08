package com.example.gpsdistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    EditText latitude;
    EditText longitude;
    String urlWebService, urlWebServicebase;
    HttpURLConnection co;
    URL url;
    InputStream inputStream = null;
    BufferedReader br;
    JSONArray jsonArray;
    TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = (EditText) findViewById(R.id.ET_Lat);
        longitude = (EditText) findViewById(R.id.ET_Long);
        urlWebServicebase ="http://192.168.43.182/villesdefrance/accessville.php";
        results = (TextView) findViewById(R.id.TV_Results);
    }
    public void Search(View view){
        //quand il n'y a qu'un seul thread multitache
        //policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        urlWebService = urlWebServicebase + "?lat=" + latitude.getText() + "&long=" + longitude.getText();
        results.setText("les communes suivantes sont les plus proches : ");
        AsyncTasks asyncTasks = new AsyncTasks();
        asyncTasks.execute();
    }
    private String getServerDataJSON(String urlWebService){
        String str = "";
        String line;

        try {
            // échange http avec le serveur
            url  = new URL(urlWebService);
            co = (HttpURLConnection)url.openConnection();
            co.connect();
            inputStream = co.getInputStream(); // réponse HTTP

            // exploitation/analyse de la réponse
            br = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = br.readLine()) !=null) {
                str+= line + "\n"; // concatenation
            }
        } catch (Exception exception){
            Log.e("log_tag", "Error during data reading :" + exception.toString());
        }

        try {
            jsonArray = new JSONArray(str);
            str = "\n";
            for (int i = 0; i<jsonArray.length(); i++ ) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                str+= " - " + jsonObject.getString("nomVille") + "\n";
            }
        } catch (JSONException exept){
            Log.e("log_tag", "Erreur pdt ana data" + exept.toString());
        }
        return str;
    }

    private class AsyncTasks extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return (getServerDataJSON(urlWebService));
        }

        @Override
        protected void onPostExecute(String res) {
            results.append(res);
        }

    }
}