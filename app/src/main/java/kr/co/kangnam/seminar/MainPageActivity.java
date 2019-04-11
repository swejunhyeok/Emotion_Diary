package kr.co.kangnam.seminar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by aks56 on 2017-10-30.
 */

public class MainPageActivity extends AppCompatActivity {
    String dbName = "Logindb.db";
    int dbVersion = 1;
    DBHelper dbHelper;
    SQLiteDatabase db;
    String sql, data;
    IpAddress ipAddress;

    private boolean writeFlag = false;
    private boolean FirstClick = false;

    private long lastTimeBackPressed;

    private GridAdapter gridAdapter;
    private GridAdapter2 gridAdapter2;

    //일 저장 할 리스트
    private ArrayList<String> dayList;
    private ArrayList<String> Dayarraylist;

    private ArrayList<String> DBEmotion = new ArrayList<String>();
    private ArrayList<String> DBDay = new ArrayList<String>();

    //그리드뷰
    private GridView gridView;
    private GridView gridView2;

    // 캘런더 변수
    private Calendar mCal;

    String UserID;

    String nowMonth;
    String Year, Month, IntentDate;

    int dayNum;

    TextView tvDate;
    TextView tvDiary;
    Button write;
    Button delete;

    View lastview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage_layout);
        dbHelper = new DBHelper(this, dbName, null, dbVersion);

        ipAddress = new IpAddress();

        write = (Button)findViewById(R.id.mainPageBtnWrite);
        delete = (Button)findViewById(R.id.mainPageBtnDelete);
        tvDiary = (TextView)findViewById(R.id.mainPageTVDiary);
        tvDate = (TextView)findViewById(R.id.MainPageTVDate);
        gridView = (GridView)findViewById(R.id.gridView);
        gridView2 = (GridView)findViewById(R.id.dayGridView);

        Button community = (Button)findViewById(R.id.MainPageBtnCommunity);
        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
                intent.putExtra("USERID", UserID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        db = dbHelper.getReadableDatabase();
        sql = "SELECT * FROM Login;";
        Cursor c = db.rawQuery(sql, null);
        try {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    UserID = c.getString(1);
                }
            }
        }finally {
            c.close();
        }

        //오늘에 날짜를 세팅해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        //연, 월, 일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        nowMonth = curMonthFormat.format(date);

        Year = curYearFormat.format(date);
        Month = curMonthFormat.format(date);

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
                        DBDay.add(m.getString("day"));
                        DBEmotion.add(m.getString("emotion"));
                    }
                    gridAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=calendarreturn&userid=" + UserID + "&year=" + Year + "&month=" + Month);

        // 현재 날짜 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));

        //gridView 요일 표시
        Dayarraylist = new ArrayList<String>();
        dayList = new ArrayList<String>();
        Dayarraylist.add("일");
        Dayarraylist.add("월");
        Dayarraylist.add("화");
        Dayarraylist.add("수");
        Dayarraylist.add("목");
        Dayarraylist.add("금");
        Dayarraylist.add("토");

        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        //1일 - 요일 매칭 시키기 위해 공백 add
        for(int i = 1; i < dayNum; i++){
            dayList.add("");
        }
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);

        gridAdapter2 = new GridAdapter2(getApplicationContext(), Dayarraylist);
        gridView2.setAdapter(gridAdapter2);

        ImageView leftarrow = (ImageView)findViewById(R.id.MainPageIVLeft);
        leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setMonth(date.getMonth() - 1);
                Year = curYearFormat.format(date);
                Month = curMonthFormat.format(date);

                DBEmotion.clear();
                DBDay.clear();
                DBEmotion = new ArrayList<String>();
                DBDay = new ArrayList<String>();

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
                                DBDay.add(m.getString("day"));
                                DBEmotion.add(m.getString("emotion"));
                            }
                            gridAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=calendarreturn&userid=" + UserID + "&year=" + Year + "&month=" + Month);

                dayList.clear();
                dayList = new ArrayList<String>();

                tvDate.setText(curYearFormat.format(date) + "/" + Integer.parseInt(curMonthFormat.format(date)));

                mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
                dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                //1일 - 요일 매칭 시키기 위해 공백 add
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);
            }
        });

        ImageView rightarrow = (ImageView)findViewById(R.id.MainPageIVRight);
        rightarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setMonth(date.getMonth() + 1);

                Year = curYearFormat.format(date);
                Month = curMonthFormat.format(date);
                dayList.clear();
                dayList = new ArrayList<String>();

                DBEmotion.clear();
                DBDay.clear();
                DBEmotion = new ArrayList<String>();
                DBDay = new ArrayList<String>();

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
                                DBDay.add(m.getString("day"));
                                DBEmotion.add(m.getString("emotion"));
                            }
                            gridAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=calendarreturn&userid=" + UserID + "&year=" + Year + "&month=" + Month);

                tvDate.setText(curYearFormat.format(date) + "/" + (Integer.parseInt(curMonthFormat.format(date))));

                mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
                dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                //1일 - 요일 매칭 시키기 위해 공백 add
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);

                gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                gridView.setAdapter(gridAdapter);
            }
        });

        ImageView User = (ImageView)findViewById(R.id.MainPageIVUser);
        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog loginDialog = new Dialog(view.getContext());
                loginDialog.setContentView(R.layout.user_dialog);

                Button edit = (Button)loginDialog.findViewById(R.id.UserDialogBtnEdit);
                Button logout = (Button)loginDialog.findViewById(R.id.UserDialogBtnLogout);
                Button signout = (Button)loginDialog.findViewById(R.id.UserDialogBtnSignOut);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                        intent.putExtra("TAG", UserID);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });

                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

                        tag %= 2;

                        db = dbHelper.getWritableDatabase();
                        sql = "UPDATE Login SET tag='" + tag +  "' WHERE _id=" + 1 + ";";
                        db.execSQL(sql);
                        finish();
                        Intent intent = new Intent(MainPageActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });

                signout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                                        db = dbHelper.getWritableDatabase();
                                        sql = "UPDATE Login SET tag='" + 0 +  "' WHERE _id=" + 1 + ";";
                                        db.execSQL(sql);
                                        finish();
                                        Intent intent = new Intent(MainPageActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=useriddelete&userid=" + UserID);
                    }
                });

                loginDialog.show();
            }
        });

        lastview = null;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            boolean fflag = true;
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                FirstClick = true;
                writeFlag = false;
                IntentDate = dayList.get(i);

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
                            TextView textView = view.findViewById(R.id.tv_item_gridview);
                            textView.setTextColor(Color.BLACK);
                            if(lastview != null && lastview != view){
                                TextView lasttextview = lastview.findViewById(R.id.tv_item_gridview);
                                lasttextview.setTextColor(Color.WHITE);
                            }
                            lastview = view;
                            if (!message.equals("false")) {
                                writeFlag = true;
                                write.setText("수정");
                                tvDiary.setText(message);
                            }
                            if(!writeFlag) {
                                write.setText("글쓰기");
                                tvDiary.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=textreturn&userid=" + UserID + "&year=" + Year + "&month=" + Month + "&day=" + IntentDate);
            }
        });

        write.setOnClickListener(new View.OnClickListener() {
            boolean fflase = false;
            @Override
            public void onClick(View view) {
                if (FirstClick) {
                    Intent intent = new Intent(getApplicationContext(), InputCalendarActivity.class);
                    intent.putExtra("Year", Year);
                    intent.putExtra("Month", Month);
                    intent.putExtra("Date", IntentDate);
                    if (writeFlag)
                        intent.putExtra("Tag", "True");
                    else
                        intent.putExtra("Tag", "False");
                    intent.putExtra("ID", UserID);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }else{
                    Toast.makeText(getApplicationContext(), "날짜를 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            boolean fflag = false;
            @Override
            public void onClick(View view) {
                if(FirstClick){
                    if (writeFlag) {
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
                                        write.setText("글쓰기");
                                        tvDiary.setText("");
                                    }
                                    DBEmotion.clear();
                                    DBDay.clear();
                                    DBEmotion = new ArrayList<String>();
                                    DBDay = new ArrayList<String>();

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
                                                    DBDay.add(m.getString("day"));
                                                    DBEmotion.add(m.getString("emotion"));
                                                }
                                                gridAdapter.notifyDataSetChanged();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=calendarreturn&userid=" + UserID + "&year=" + Year + "&month=" + Month);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute("http://" + ipAddress.ip + ":8080/myopenapi.jsp?method=textdelete&userid=" + UserID + "&year=" + Year + "&month=" + Month + "&day=" + IntentDate);
                    }else{
                        Toast.makeText(getApplicationContext(), "삭제 할 내용이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "날짜를 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //해당 월에 표시할 일 수 구함
    private void setCalendarDate(int month){
        mCal.set(Calendar.MONTH, month - 1);

        for(int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
            dayList.add("" + (i + 1));
        }
    }

    private class GridAdapter extends BaseAdapter{
        private final List<String> list;
        private final LayoutInflater inflater;
        ViewHolder holder = null;
        Integer today;

        public  GridAdapter(Context context, List<String> list){
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount(){
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();
                holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_item_gridview);
                holder.ivItemGridView = (ImageView)convertView.findViewById(R.id.iv_item_gridview);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.tvItemGridView.setText("" + getItem(position));
            //해당 날짜 텍스트 컬러,배경 변경
            mCal = Calendar.getInstance();


            holder.ivItemGridView.setImageResource(0);

            for(int i = 0 ; i < DBDay.size(); i++) {
                if(DBDay.get(i).equals(getItem(position))) {
                    if (DBEmotion.get(i).equals("1"))
                        holder.ivItemGridView.setImageResource(R.drawable.verybad);
                    else if (DBEmotion.get(i).equals("2"))
                        holder.ivItemGridView.setImageResource(R.drawable.bad);
                    else if (DBEmotion.get(i).equals("3"))
                        holder.ivItemGridView.setImageResource(R.drawable.soso);
                    else if (DBEmotion.get(i).equals("4"))
                        holder.ivItemGridView.setImageResource(R.drawable.happy);
                    else if (DBEmotion.get(i).equals("5"))
                        holder.ivItemGridView.setImageResource(R.drawable.veryhappy);
                }
            }

            //오늘 day 가져옴
            today = mCal.get(Calendar.DAY_OF_MONTH);

            return convertView;
        }
    }

    private class GridAdapter2 extends BaseAdapter{
        private final List<String> list;
        private final LayoutInflater inflater;

        public  GridAdapter2(Context context, List<String> list){
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount(){
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder = null;

            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_calendar_daygridview, parent, false);
                holder = new ViewHolder();
                holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_item_daygridview);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.tvItemGridView.setText("" + getItem(position));
            //해당 날짜 텍스트 컬러,배경 변경

            if("일".equals(getItem(position))){
                holder.tvItemGridView.setTextColor(Color.RED);
            }
            if("토".equals(getItem(position))){
                holder.tvItemGridView.setTextColor(Color.BLUE);
            }
            return convertView;
        }
    }

    private class ViewHolder{
        TextView tvItemGridView;
        ImageView ivItemGridView;
    }

    public void onBackPressed(){
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}
