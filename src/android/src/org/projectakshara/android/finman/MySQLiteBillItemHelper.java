package org.projectakshara.android.finman;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteBillItemHelper extends SQLiteOpenHelper {
	private static final String TAG = "MySQLiteBillItem";

	  public static final String TABLE_BILLITEMS = "billitems";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_NAME = "name";
	  public static final String COLUMN_PRICE = "price";
	  public static final String COLUMN_QUANTITY = "quantity";
	  public static final String COLUMN_BILLID = "billID";

	  private static final String DATABASE_NAME = "billitems.db";
	  private static final int DATABASE_VERSION = 1;

	  
	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table " + TABLE_BILLITEMS + "(" 
			  + COLUMN_ID + " integer primary key autoincrement, " 
			  + COLUMN_NAME + " text not null, "
			  + COLUMN_PRICE + " integer not null, "
			  + COLUMN_QUANTITY + " integer not null, "
			  + COLUMN_BILLID + " integer not null, "
			  + " FOREIGN KEY ("+ COLUMN_BILLID +") REFERENCES "
			  	+ MySQLiteBillHelper.TABLE_BILLS+" ("+ MySQLiteBillHelper.COLUMN_ID +")"
			  +");";

	  public MySQLiteBillItemHelper(Context context) {
		  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		  Log.d(TAG,"DB_CREATE");
		  database.execSQL(DATABASE_CREATE);
		  Log.d(TAG,"DB_CREATED");
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteBillHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLITEMS);
	    onCreate(db);
	  }


}
