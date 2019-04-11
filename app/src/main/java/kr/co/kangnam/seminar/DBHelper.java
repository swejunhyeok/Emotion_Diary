package kr.co.kangnam.seminar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aks56 on 2017-10-30.
 */

public class DBHelper extends SQLiteOpenHelper {
    // 생성자 - database 파일을 생성합니다.
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    //DB 처음 만들때 한번만 호출 -테이블을 생성합니다.
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Login (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "id TEXT, pw TEXT, tag INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS Login");
        onCreate(db);
    }
}
