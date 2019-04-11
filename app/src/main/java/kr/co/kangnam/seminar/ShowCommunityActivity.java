package kr.co.kangnam.seminar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aks56 on 2017-11-08.
 */

public class ShowCommunityActivity extends AppCompatActivity {
    String CommunityID = null;
    String UserName = null;
    String UserId = null;
    ListView list;
    IpAddress ipAddress;
    TextView title;
    TextView text;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcommunity_layout);

        ipAddress = new IpAddress();

        title = (TextView)findViewById(R.id.ShowCommunityTVTitle);
        text = (TextView)findViewById(R.id.ShowCommunityTVWrite);
        name = (TextView)findViewById(R.id.ShowCommunityTVName);

        Intent intent = getIntent();
        if (intent != null) {
            UserId = intent.getStringExtra("USERID");
            CommunityID = intent.getStringExtra("COMMUNITYID");
            UserName = intent.getStringExtra("USERNAME");
        }

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
                        title.append(jObj.getString("title"));
                        text.append(jObj.getString("text"));
                        name.append(jObj.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=communitytextreturn&id=" + CommunityID);
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
        intent.putExtra("USERID", UserId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
