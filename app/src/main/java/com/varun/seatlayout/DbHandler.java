package com.varun.seatlayout;

import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;


public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "usersdb";
    private static final String TABLE_Users = "userdetails";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PN = "phoneNumber";
    private static final String KEY_SID = "seatId";
    private static final String KEY_STATUS = "status";
    public DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PN + " TEXT,"
                + KEY_SID + " TEXT,"
                + KEY_STATUS + " TEXT"+ ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
//        SQLiteDatabase db = this.getWritableDatabase();
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Users);
        // Create tables again
        onCreate(db);
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    // Adding new User Details
    void insertUserDetails(String name, String phoneNumber, String seatId, String status){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_NAME, name);
        cValues.put(KEY_PN, phoneNumber);
        cValues.put(KEY_SID, seatId);
        cValues.put(KEY_STATUS, status);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_Users,null, cValues);
        db.close();
    }
    // Get User Details
    public ArrayList<HashMap<String, String>> GetUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, phoneNumber, seatId, status FROM "+ TABLE_Users;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.put("phoneNumber",cursor.getString(cursor.getColumnIndex(KEY_PN)));
            user.put("seatId",cursor.getString(cursor.getColumnIndex(KEY_SID)));
            user.put("status",cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
            userList.add(user);
        }
        cursor.close();
        return  userList;
    }

    public String getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {KEY_ID,KEY_NAME,KEY_PN,KEY_SID};
        Cursor cursor =db.query(TABLE_Users,columns,null,null,null,null,null);
//        Cursor cursor = db.rawQuery("SELECT * FROM " + myDbHelper.TABLE_NAME + "", null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int cid =cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String name =cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String phoneNumber =cursor.getString(cursor.getColumnIndex(KEY_PN));
            String seatId =cursor.getString(cursor.getColumnIndex(KEY_SID));
//            String status =cursor.getString(cursor.getColumnIndex(KEY_STATUS));
            buffer.append(cid+ "   " + name + "   " + phoneNumber +"    " + seatId +"\n");
        }
        cursor.close();
        return buffer.toString();
    }

    public String getPhoneNumbers()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {KEY_PN};
        Cursor cursor =db.query(TABLE_Users,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String phoneNumber =cursor.getString(cursor.getColumnIndex(KEY_PN));
            buffer.append(phoneNumber+"\n");
        }
        cursor.close();
        return buffer.toString();
    }

    public String searchByPN(String phoneNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
//        String[] whereArgs ={phoneNumber};
//        int count =db.delete(TABLE_Users ,KEY_NAME+" = ?",whereArgs);
//        Cursor TuplePointer = db.rawQuery("select * from " + TABLE_Users + " where "+KEY_NAME+" = ?", whereArgs );
        String query = "SELECT name, phoneNumber, seatId, status FROM "+ TABLE_Users + " WHERE phoneNumber = '" + phoneNumber + "'";
        Cursor cursor = db.rawQuery(query,null);
//        cursor.moveToFirst();
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String name1 =cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String phoneNumber1 =cursor.getString(cursor.getColumnIndex(KEY_PN));
            String seatId1 =cursor.getString(cursor.getColumnIndex(KEY_SID));
            String status =cursor.getString(cursor.getColumnIndex(KEY_STATUS));
            buffer.append(name1 + "   " + phoneNumber1 +"   " + seatId1 +"  " + status+ "\n");
        }
        cursor.close();
        return buffer.toString();
    }

    public String seatIdFromPN(String phoneNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT seatId FROM "+ TABLE_Users + " WHERE phoneNumber = '" + phoneNumber + "'";
        Cursor cursor = db.rawQuery(query,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String seatId1 =cursor.getString(cursor.getColumnIndex(KEY_SID));
            buffer.append(seatId1 +"\n");
        }
        cursor.close();
        return buffer.toString();
    }

    public String statusFromSeatId(String seatId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT status FROM "+ TABLE_Users + " WHERE seatId = '" + seatId + "'";
        Cursor cursor = db.rawQuery(query,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String status =cursor.getString(cursor.getColumnIndex(KEY_STATUS));
            buffer.append(status +"\n");
        }
        cursor.close();
        return buffer.toString();
    }

    public String getGreenSeatId()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT seatId FROM "+ TABLE_Users + " WHERE status = 'green'";
        Cursor cursor = db.rawQuery(query,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String seatId =cursor.getString(cursor.getColumnIndex(KEY_SID));
            buffer.append(seatId +" ");
        }
        cursor.close();
        return buffer.toString();
    }

    // Get User Details based on userid
    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, location, designation FROM "+ TABLE_Users;
        Cursor cursor = db.query(TABLE_Users, new String[]{KEY_NAME, KEY_PN, KEY_SID}, KEY_ID+ "=?",new String[]{String.valueOf(userid)},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.put("phoneNumber",cursor.getString(cursor.getColumnIndex(KEY_PN)));
            user.put("seatId",cursor.getString(cursor.getColumnIndex(KEY_SID)));
            userList.add(user);
        }
        cursor.close();
        return  userList;
    }
    // Delete User Details
    public void DeleteUser(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users, KEY_ID+" = ?",new String[]{String.valueOf(userid)});
        db.close();
    }

    public String getNameById(int userId){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT name FROM "+ TABLE_Users + " WHERE seatId = '" + userId + "'";
        Cursor cursor = db.rawQuery(query,null);
//        while (cursor.moveToNext())
//        {
//            String name1 =cursor.getString(cursor.getColumnIndex(KEY_NAME));
//            buffer.append(name1 +"\n");
//        }
        cursor.moveToFirst();
        String name1 =cursor.getString(cursor.getColumnIndex(KEY_NAME));
        cursor.close();
        return name1;
    }

    public void DeleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_Users);
        db.close();
    }
    // Update User Details
    public int UpdateUserDetails(String phoneNumber, String seatId, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_PN, phoneNumber);
        cVals.put(KEY_SID, seatId);
        int count = db.update(TABLE_Users, cVals, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        return  count;
    }

    // Update User Status
    public int UpdateUserStatus(String status, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_STATUS, status);
        int count = db.update(TABLE_Users, cVals, KEY_SID+" = ?",new String[]{String.valueOf(id)});
        return  count;
    }
}
