package org.projectakshara.android.finman;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteBillHelper extends SQLiteOpenHelper {
	private static final String TAG = "MySQLiteBill";
	
	  public static final String TABLE_BILLS = "bills";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_NAME = "name";
	  public static final String COLUMN_NITEMS = "nItems";
	  public static final String COLUMN_TOTAL = "total";
	  public static final String COLUMN_IMGPATH = "imgPath";
	  public static final String COLUMN_DATETIME = "datetime";

	  private static final String DATABASE_NAME = "bills.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table " + TABLE_BILLS + "(" 
			  + COLUMN_ID + " integer primary key autoincrement, " 
			  + COLUMN_NAME + " text not null, "
			  + COLUMN_NITEMS + " integer not null, "
			  + COLUMN_TOTAL + " integer not null, "
			  + COLUMN_IMGPATH + " text not null, "
			  + COLUMN_DATETIME + " text not null "
			  +");";

	  public MySQLiteBillHelper(Context context) {
		  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		  database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteBillHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
	    onCreate(db);
	  }


}
