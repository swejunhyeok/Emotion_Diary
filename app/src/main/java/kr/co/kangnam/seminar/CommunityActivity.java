package kr.co.kangnam.seminar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aks56 on 2017-11-08.
 */

public class CommunityActivity extends AppCompatActivity {
    String UserID = null;
    String UserName = null;
    ListView list;
    IpAddress ipAddress;

    private ArrayList<String> ids = new ArrayList<String>();
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> texts = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_layout);

        Intent intent = getIntent();
        if(intent != null){
            UserID = intent.getStringExtra("USERID");
        }

        ipAddress = new IpAddress();


        final CustomAdapter adapter = new CustomAdapter(this);
        list = (ListView) findViewById(R.id.CommunityList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ShowCommunityActivity.class);
                int Max = 0;
                if(titles.size() != 0)
                    Max = titles.size() - 1;
                intent.putExtra("USERID", UserID);
                intent.putExtra("COMMUNITYID", ids.get(Max - i));
                intent.putExtra("USERNAME", UserName);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


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
                        UserName = message;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=namereturn&userid=" + UserID);

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
                    JSONArray msg = jObj.getJSONArray("msg");
                    for(int i = 0; i < msg.length(); i++){
                        JSONObject m = msg.getJSONObject(i);
                        ids.add(m.getString("id"));
                        titles.add(m.getString("title"));
                        System.out.println(titles.get(i));
                        texts.add(m.getString("text"));
                        names.add(m.getString("name"));
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=communityreturn");

        Button Inputwrite = (Button)findViewById(R.id.CommunityBtnAdd);
        Inputwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InputCommunityActivity.class);
                intent.putExtra("USERID", UserID);
                intent.putExtra("USERNAME", UserName);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button sentiMental = (Button) findViewById(R.id.CommunityBtnSentiMental);
        sentiMental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private class  CustomAdapter extends ArrayAdapter<String>{
        Context context;
        public CustomAdapter(Context context){
            super(context, R.layout.listitem, titles);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View rowView = inflater.inflate(R.layout.listitem, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.List_Title);
            TextView name = (TextView) rowView.findViewById(R.id.List_Name);

            int Max = 0;
            if(titles.size() != 0)
                Max = titles.size() - 1;
            title.setText(titles.get(Max - position));
            name.setText(names.get(Max - position));
            return rowView;
        }
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
