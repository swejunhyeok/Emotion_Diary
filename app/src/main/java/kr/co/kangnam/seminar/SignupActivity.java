package kr.co.kangnam.seminar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aks56 on 2017-10-30.
 */

public class SignupActivity extends AppCompatActivity {

    boolean IdCheck = false;
    boolean EmailCheck = false;

    int age = -1;
    IpAddress ipAddress;

    EditText Id;
    EditText name;
    EditText email;
    EditText password;
    EditText passwordCheck;

    String UserID;

    Spinner spinner;

    RadioButton rbMan;
    RadioButton rbWoman;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        ipAddress = new IpAddress();


        List<String> SpData = new ArrayList<>();
        SpData.add("나이 선택");
        for(int i = 1; i < 100; i ++)
            SpData.add(String.valueOf(i));
        spinner = (Spinner)findViewById(R.id.signupSpETAge);

        Id = (EditText)findViewById(R.id.signupETID);
        name = (EditText)findViewById(R.id.signupETName);
        email = (EditText)findViewById(R.id.signupETEmail);
        password = (EditText)findViewById(R.id.signupETPassword);
        passwordCheck = (EditText)findViewById(R.id.signupETPasswordCheck);


        rbMan = (RadioButton) findViewById(R.id.signupRBMan);
        rbWoman = (RadioButton) findViewById(R.id.signupWoman);

        Button CheckId = (Button)findViewById(R.id.signupBTNIdCheck);
        Button CheckEmail = (Button)findViewById(R.id.signupBTNEmailCheck);

        Button Cancle = (Button)findViewById(R.id.signupBtnCancle);
        Button Edit = (Button) findViewById(R.id.signupBtnEdit);
        Button Ok = (Button) findViewById(R.id.signupBtnOK);

        Intent intent = getIntent();
        if(intent != null){
            UserID = intent.getStringExtra("TAG");
            if(!UserID.equals("false")){
                Id.setText(UserID);
                Ok.setText("수정");

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
                                email.setText(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=emailreturn&userid=" + UserID);

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
                                name.setText(message);
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
                            String message = jObj.getString("msg");
                            System.out.println(message);
                            if (!message.equals("false")) {
                                if(message.equals("남성"))
                                    rbMan.setChecked(true);
                                else if(message.equals("여성"))
                                    rbWoman.setChecked(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=sexreturn&userid=" + UserID);

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
                                spinner.setSelection(Integer.parseInt(message.toString()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=agereturn&userid=" + UserID);
            }
        }


        // 이름을 입력하는 자판을 한글로 디폴트 시킨다.
        name.setPrivateImeOptions("defaultInputmode=korean;");

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, SpData);
        spinner.setAdapter(spinnerAdapter);

        // ID 중복확인 버튼
        CheckId.setOnClickListener(new View.OnClickListener() {
            boolean flag;
            @Override
            public void onClick(View view) {
                flag = true;

                if(Id.getText().toString().length() < 4){
                    Toast.makeText(getApplicationContext(), "4자리 이상의 아이디를 사용하십시오.", Toast.LENGTH_SHORT).show();
                    flag = false;
                }else {
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
                                if (message.equals("true")) {
                                    flag = false;
                                }
                                // 중복되는 아이디가 없다면 IDCheck를 트루로 한다
                                // 중복확인이 완료됐다는 뜻이다.
                                if (flag) {
                                    IdCheck = true;
                                    Toast.makeText(getApplicationContext(), "사용가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "중복된 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute("http://" + ipAddress.getIp() + ":8080/myopenapi.jsp?method=useridcheck&userid=" + Id.getText().toString());
                }
            }
        });

        // Email 중복확인 버튼
        CheckEmail.setOnClickListener(new View.OnClickListener() {
            boolean flag;
            @Override
            public void onClick(View view) {
                flag = true;
                // DB에서 검사하기전 이메일 형식이 맞는지 부터 확인한다.
                if(email.getText().toString().indexOf('@') == -1 || email.getText().toString().indexOf('.') == -1){
                    flag = false;
                    Toast.makeText(getApplicationContext(), "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    email.setText("");
                    email.setHint("이메일을 입력하세요.");
                }else {
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
                                if (message.equals("true")) {
                                    flag = false;
                                }

                                // 중복되는 이메일이 없다면 EmailCheck를 true로 한다
                                // 중복확인이 완료됐다는 뜻이다.
                                if (flag) {
                                    EmailCheck = true;
                                    Toast.makeText(getApplicationContext(), "사용가능한 이메일 입니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "중복된 이메일 입니다.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute("http://" + ipAddress.getIp() + ":8080/myopenapi.jsp?method=emailcheck&email=" + email.getText().toString());
                }
            }
        });

        // 취소 버튼
        Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        // 수정 버튼
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Id.setText("");
                name.setText("");
                name.setHint("이름을 입력하세요.");
                spinner.setSelection(0);
                rbMan.setChecked(false);
                rbWoman.setChecked(false);
                email.setText("");
                email.setHint("이메일을 입력하세요.");
                password.setText("");
                password.setHint("비밀번호를 입력하세요.");
                passwordCheck.setText("");
                passwordCheck.setHint("비밀번호를 다시 입력하세요.");
            }
        });

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!IdCheck)
                    Toast.makeText(getApplicationContext(), "아이디 중복확인을 완료하세요.", Toast.LENGTH_SHORT).show();
                else if(!EmailCheck)
                    Toast.makeText(getApplicationContext(), "이메일 중복확인을 완료하세요.", Toast.LENGTH_SHORT).show();
                else if(name.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                else if(age == 0)
                    Toast.makeText(getApplicationContext(), "나이를 선택해주세요.", Toast.LENGTH_SHORT).show();
                else if(!rbWoman.isChecked() && !rbMan.isChecked())
                    Toast.makeText(getApplicationContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                else if(password.getText().toString().length() < 6)
                    Toast.makeText(getApplicationContext(), "6자리 이상의 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                else if(!password.getText().toString().equals(passwordCheck.getText().toString()))
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                else{
                    String sex = null;
                    if(rbMan.isChecked())
                        sex = "남성";
                    else if(rbWoman.isChecked())
                        sex = "여성";
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
                                if(message.equals("ok")){
                                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.execute("http://" + ipAddress.getIp() + ":8080/myopenapi.jsp?method=add&tablename=information&userid=" + Id.getText().toString() +"&email=" + email.getText().toString() + "&pw=" + password.getText().toString() +
                            "&name=" + name.getText().toString() +"&sex=" + sex + "&age=" + String.valueOf(age));

                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), "선택된 아이템 : " + spinner.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                age = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
