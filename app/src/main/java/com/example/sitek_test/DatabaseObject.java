package com.example.sitek_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseObject
{
	private static DatabaseHelper dbHelper = new DatabaseHelper();
	private static SQLiteDatabase db = null;

	private final static int APP_DB_VER = 0;
	private static String INT_PATH = "";

	public static void init(Context ctx) {

		INT_PATH = "/data/data/"+ctx.getPackageName()+"/databases/";
		dbHelper.initDB(ctx, constanta.rawDbName, INT_PATH, constanta.dbName, APP_DB_VER);

	}

	public static boolean open() {

		if (isOpen()) return false;

		db = dbHelper.open(db, INT_PATH, constanta.dbName);

		if (db!=null) return true;
		else return false;

	}

	public static void close()
	{
		db = dbHelper.close(db);
	}

	public static boolean isOpen()
	{
		return dbHelper.isOpen(db);
	}

	public static Cursor getAuthTableCursor(String uid) {

		String query = "SELECT * FROM 'logger' WHERE uid=" +'"'+ uid+'"';

		open();

		Cursor crs = null;

		if (db==null) return null;

		try {
			crs = db.rawQuery(query, null);
		}
		catch (SQLiteException se) { }
		catch (IllegalStateException ie) { }

		return crs;
	}

	public static void writeSessionToDb(utils.RootAuth auth, String uid) {

		open();

		if (db == null) return;

		try {

			db.beginTransaction();

				ContentValues cv = new ContentValues();
				cv.put("uid", uid);
				cv.put("response", auth.authentication.Response);
				cv.put("continue_work", auth.authentication.ContinueWork);
				cv.put("photohash", auth.authentication.PhotoHash);
				cv.put("current_date", auth.authentication.CurrentDate);
				cv.put("query_date_ms", System.currentTimeMillis());

				db.insert("logger",null,cv);

			db.setTransactionSuccessful();
			db.endTransaction();
		}
		catch (SQLiteException se) { Log.e("102500", "inser exceptionn "+ se.toString()); }
		catch (IllegalStateException e) {  }

	}

}
