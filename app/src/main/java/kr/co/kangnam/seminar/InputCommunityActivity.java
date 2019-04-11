package kr.co.kangnam.seminar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aks56 on 2017-11-08.
 */

public class InputCommunityActivity extends AppCompatActivity {
    String UserName = null;
    String UserId = null;
    IpAddress ipAddress;

    EditText title;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputcommunity_layout);

        title = (EditText)findViewById(R.id.InputCommunityETTitle);
        text = (EditText)findViewById(R.id.InputCommunityETWrite);

        ipAddress = new IpAddress();

        Intent intent = getIntent();
        if (intent != null) {
            UserId = intent.getStringExtra("USERID");
            UserName = intent.getStringExtra("USERNAME");
        }

        Button save = (Button)findViewById(R.id.InputCommunityBtnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(text.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    new AsyncTask<String, Void, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            System.out.println("url : " + strings[0]);
                            URL Url = null;
                            String line = null;
                            try {
                                Url = new URL(strings[0]);
                                HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                conn.setRequestMethod("GET");
                                InputStream is = conn.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                                line = reader.readLine();
                                System.out.println(line);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return line;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            try {
                                JSONObject jObj = new JSONObject(s);
                                String message = jObj.getString("msg");
                                System.out.println(message);
                                if (message.equals("ok")) {
                                    Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
                                    intent.putExtra("USERID", UserId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=add&tablename=community&title=" + title.getText().toString() + "&text=" + text.getText().toString() + "&name=" + UserName);
                }
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
        intent.putExtra("USERID", UserId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
