package kr.co.kangnam.seminar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //DB 관련 변수들 선언
    String dbName = "Logindb.db";
    int dbVersion = 1;
    DBHelper dbHelper;
    SQLiteDatabase db;
    String sql, data;
    IpAddress ipAddress;

    // 뒤로 가기 버튼 눌린 시간 측정
    private long lastTimeBackPressed;

    //XML UI
    EditText id;
    EditText password;

    CheckBox cbid;
    CheckBox cblogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //DBHelper 객체 생성
        dbHelper = new DBHelper(this, dbName, null, dbVersion);

        ipAddress = new IpAddress();

        // UI 매칭
        id = (EditText)findViewById(R.id.mainETId);
        cbid = (CheckBox)findViewById(R.id.mainCBId);
        cblogin = (CheckBox)findViewById(R.id.mainCBLogin);

        //내부DB를 통해 아이디 기억 여부, 자동 로그인 여부 확인
        db = dbHelper.getReadableDatabase();
        sql = "SELECT * FROM Login;";
        Cursor c = db.rawQuery(sql, null);
        try {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if(c.getInt(3) == 1){
                        // 아이디 기억이 체크되어 있었다는 뜻
                        id.setText(c.getString(1).toString());
                        cbid.setChecked(true);
                    }else if(c.getInt(3) == 2 || c.getInt(3) == 3) {
                        // 자동 로그인이 체크되어 있었다는 뜻
                        finish();
                        Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
                        startActivity(intent);
                    }
                }
            }else{
                // DB에 아직 아무것도 안들어가있으면 의미 없는 값들을 첫번째로 등록해준다.
                db = dbHelper.getWritableDatabase();
                sql = String.format("INSERT INTO Login VALUES(NULL,'%s','%s','%d');",
                        "", "", 0);
                db.execSQL(sql);
            }
        }finally {
            c.close();
        }

        password = (EditText)findViewById(R.id.mainETPassword);

        Button ok = (Button)findViewById(R.id.mainBtnOk);
        ImageButton signup = (ImageButton)findViewById(R.id.mainBtnSignup);

        //ID 기억하기 체크박스값이 변할때 호출된다.
        cbid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!id.getText().toString().equals("")) {
                    if (b) {
                        //ID 기억하기가 체크된다면 DB에 있는 첫번째 값들을 수정해준다.
                        db = dbHelper.getWritableDatabase();
                        sql = "UPDATE Login SET id='" + id.getText().toString() + "',tag='" + 1 + "' WHERE _id=" + 1 + ";";
                        db.execSQL(sql);
                    } else {
                        //ID 기억하기가 체크 해제된다면 DB에 있는 첫번째 값들을 수정해준다.
                        db = dbHelper.getWritableDatabase();
                        sql = "UPDATE Login SET tag='" + 0 + "' WHERE _id=" + 1 + ";";
                        db.execSQL(sql);
                    }
                } else{ // 만약 ID EditText창에 아무것도 입력되지 않은 상태에서 ID 기억하기 체크박스를 체크한다면 이런 토스트 메시지를 띄어준다.
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    cbid.setChecked(false);
                }
            }
        });

        //로그인하기 버튼을 클릭하면
        ok.setOnClickListener(new View.OnClickListener() {
            boolean IDFlag;
            boolean PWFlag;
            @Override
            public void onClick(View view) {
                IDFlag = false;
                PWFlag = false;

                new AsyncTask<String, Void, String>(){
                    @Override
                    protected String doInBackground(String... strings) {
                        System.out.println("url : " + strings[0]);
                        URL Url = null;
                        String line = null;
                        try{
                            Url = new URL(strings[0]);
                            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                            conn.setRequestMethod("GET");
                            InputStream is = conn.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                            line = reader.readLine();
                            System.out.println(line);
                        }
                        catch (Exception e){
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
                            if(message.equals("true")){
                                IDFlag = true;
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        new AsyncTask<String, Void, String>(){
                            @Override
                            protected String doInBackground(String... strings) {
                                System.out.println("url : " + strings[0]);
                                URL Url = null;
                                String line = null;
                                try{
                                    Url = new URL(strings[0]);
                                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                    conn.setRequestMethod("GET");
                                    InputStream is = conn.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                                    line = reader.readLine();
                                    System.out.println(line);
                                }
                                catch (Exception e){
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
                                    if(message.equals("true")){
                                        PWFlag = true;
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                if(IDFlag && PWFlag){ // 입력된 아이디와 비밀번호가 DB에 있는 것과 일치한다면
                                    // 자동 로그인 체크 여부
                                    if(cblogin.isChecked()) {
                                        int tag = 0;
                                        db = dbHelper.getReadableDatabase();
                                        sql = "SELECT * FROM Login;";
                                        Cursor c = db.rawQuery(sql, null);
                                        try {
                                            if (c.getCount() > 0) {
                                                while (c.moveToNext()) {
                                                    tag = c.getInt(3);
                                                }
                                            }
                                        }finally {
                                            c.close();
                                        }

                                        tag += 2;

                                        db = dbHelper.getWritableDatabase();
                                        sql = "UPDATE Login SET id='" + id.getText().toString() + "',pw='" + password.getText().toString() + "',tag='" + tag + "' WHERE _id=" + 1 + ";";
                                        db.execSQL(sql);
                                    }else if(!cblogin.isChecked() && !cbid.isChecked()){
                                        db = dbHelper.getWritableDatabase();
                                        sql = "UPDATE Login SET id='" + id.getText().toString() + "',tag='" +  0 + "' WHERE _id=" + 1 + ";";
                                        db.execSQL(sql);
                                    }
                                    finish();
                                    Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }else {
                                    Toast.makeText(getApplicationContext(), "아이디 또는 패스워드를 확인하세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=pwcheck&pw=" + password.getText().toString());
                    }
                }.execute("http://" + ipAddress.ip +":8080/myopenapi.jsp?method=useridcheck&userid=" + id.getText().toString());
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                intent.putExtra("TAG", "false");
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    //뒤로가기를 두번 누르면 꺼지는 설정
    public void onBackPressed(){
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}
