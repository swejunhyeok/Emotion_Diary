package kr.co.kangnam.seminar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aks56 on 2017-11-04.
 */

public class InputCalendarActivity extends AppCompatActivity {
    String UserID, Year, Month, Date, Tag;
    IpAddress ipAddress;
    EditText write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputcalendar_layout);

        ipAddress = new IpAddress();

        write = (EditText)findViewById(R.id.InputETWrite);
        write.setPrivateImeOptions("defaultInputmode=korean;");

        Intent intent = getIntent();
        if(intent!=null) {
            Year = intent.getStringExtra("Year");
            Month = intent.getStringExtra("Month");
            Date = intent.getStringExtra("Date");
            Tag = intent.getStringExtra("Tag");
            UserID = intent.getStringExtra("ID");
        }

        if(Tag.equals("True")){
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
                        if (!message.equals("false")) {
                            write.setText(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=textreturn&userid=" + UserID + "&year=" + Year + "&month=" + Month + "&day=" + Date);
        }

        Button save = (Button)findViewById(R.id.InputBtnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Tag.equals("False")) {
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
                                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=add&tablename=calendar&userid=" + UserID + "&text=" + write.getText().toString() + "&year=" + Year + "&month=" + Month + "&day=" + Date);
                } else if (Tag.equals("True")) {
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
                                    final String emotion = null;
                                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=textupdate&userid=" + UserID + "&text=" + write.getText().toString() + "&year=" + Year + "&month=" + Month + "&day=" + Date);
                }
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
