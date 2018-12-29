package com.jash.shepard.bookex;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String DB_PATH;
    private static String DB_NAME = "storybook.db";
    public SQLiteDatabase myDataBase;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
        boolean dbexist = checkdatabase();
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        if (dbexist) {
            opendatabase();
        } else {
            System.out.println("Database doesn't exist");
            try {
                createdatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if (!dbexist) {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkdatabase() {

        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outfilename = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte from input file to output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    public void opendatabase() throws SQLException {
        //Open the database
        String mypath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int chapter_Counter(String select) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book group by " +
                "chapter order by ID ASC", null);
        return cursor.getCount();
    }

    public String chapter_Getter(String select, int row) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book group by " +
                "chapter order by ID ASC", null);
        cursor.moveToPosition(row);
        return cursor.getString(0);
    }

    public int section_counter(String select, String where) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                "chapter='" + where + "' group by " + select + " order by ID ASC", null);
        return cursor.getCount();
    }

    public String section_getter(String select, String where, int row) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                "chapter='" + where + "' group by " + select + " order by ID ASC", null);
        cursor.moveToPosition(row);
        return cursor.getString(0);
    }

    public int page_counter(String select, String where) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                "section='" + where + "' group by " + select, null);
        return cursor.getCount();
    }

    public String page_getter(String select, String where, int page, int row) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                " section='" + where + "' and page='" + page + "' group by " + select + " order by ID ASC", null);
        cursor.moveToPosition(row);
        return cursor.getString(0);
    }

    public int search_in_titles_counter(String select, String query) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                " chapter like '%" + query + "%' or section like '%" + query + "%' group by section order by ID ASC", null);
        return cursor.getCount();
    }

    public String[] search_in_titles_getter(String select, String query, int row) {
        String[] result = new String[2];
        Cursor cursor = myDataBase.rawQuery("select chapter,section from tbl_book where" +
                " chapter like '%" + query + "%' or " +
                " section like '%" + query + "%' group by section order by ID ASC ", null);
        cursor.moveToPosition(row);
        result[0] = cursor.getString(0);
        result[1] = cursor.getString(1);
        return result;
    }

    public int search_in_texts_counter(String select, String query) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                " text like '%" + query + "%' group by text order by ID ASC", null);
        return cursor.getCount();
    }

    public String search_in_texts_getter(String select, String query, int row) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                " text like '%" + query + "%' group by text order by ID ASC", null);
        cursor.moveToPosition(row);
        return cursor.getString(0);
    }


    public void setBookmark(String where, int choice) {
        myDataBase.execSQL("update tbl_book set bookmark='" + choice + "' where" +
                " section='" + where + "'");
    }

    public int checkBookmark(String where){
        Cursor cursor = myDataBase.rawQuery("select bookmark from tbl_book where" +
                " section='"+where+"' group by section",null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int fav_counter() {
        Cursor cursor = myDataBase.rawQuery("select bookmark from tbl_book where " +
                "bookmark=1 group by section order by ID ASC", null);
        return cursor.getCount();
    }

    public String fav_getter(String select, int row) {
        Cursor cursor = myDataBase.rawQuery("select " + select + " from tbl_book where " +
                " bookmark=1 group by section order by ID ASC", null);
        cursor.moveToPosition(row);
        return cursor.getString(0);
    }

    public String getChapterfromSection(String select,String where){
        Cursor cursor = myDataBase.rawQuery("select "+select+" from tbl_book where" +
                " section='"+where+"' group by "+select,null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }
}
