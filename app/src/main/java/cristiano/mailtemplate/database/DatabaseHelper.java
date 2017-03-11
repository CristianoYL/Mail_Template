package cristiano.mailtemplate.database;

/**
 * Created by Cristiano on 2016/4/27.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MailTemplate.db";
    public static final String TEMPLATES_TABLE = "Templates";
    public static final int TEMPLATES_TABLE_COLUMNS = 3;
    public static final String COL_1 = "Name";
    public static final String COL_2 = "Subject";
    public static final String COL_3 = "Content";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TEMPLATES_TABLE + " (Name TEXT PRIMARY KEY,Subject TEXT,Content TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TEMPLATES_TABLE);
        onCreate(db);
    }
    public boolean insert(String tableName, String[] columnValue){
        if(tableName.equals(DatabaseHelper.TEMPLATES_TABLE) && columnValue.length == DatabaseHelper.TEMPLATES_TABLE_COLUMNS){ // TEMPLATES_TABLE has 3 columns
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_1,columnValue[0]);
            values.put(DatabaseHelper.COL_2,columnValue[1]);
            values.put(DatabaseHelper.COL_3,columnValue[2]);
            if (db.insert(TEMPLATES_TABLE, null, values) == -1){
                return false;
            } else {
                return true;
            }
        } else{
            return false;
        }
    }
    public Cursor select(String templateName){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+ TEMPLATES_TABLE +" where Name = ?",new String[]{templateName});

    }
    public Cursor selectAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+ TEMPLATES_TABLE,null);

    }
    public Integer delete(String templateName){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TEMPLATES_TABLE, "Name = ?",new String[]{templateName});
    }
    public boolean update(String[] columnValue){
        if(columnValue.length == DatabaseHelper.TEMPLATES_TABLE_COLUMNS){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_1,columnValue[0]);
            values.put(DatabaseHelper.COL_2,columnValue[1]);
            values.put(DatabaseHelper.COL_3,columnValue[2]);
            db.update(TEMPLATES_TABLE, values, "Name = ?",new String[]{columnValue[0]});
            return true;
        } else {
            return false;
        }

    }
}

