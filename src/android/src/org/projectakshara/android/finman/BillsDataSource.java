package org.projectakshara.android.finman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BillsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteBillHelper dbHelper;
	private String[] allColumns = { 
			MySQLiteBillHelper.COLUMN_ID,
			MySQLiteBillHelper.COLUMN_NAME,
			MySQLiteBillHelper.COLUMN_NITEMS,
			MySQLiteBillHelper.COLUMN_TOTAL,
			MySQLiteBillHelper.COLUMN_IMGPATH,
			MySQLiteBillHelper.COLUMN_DATETIME			
	};
	
	public BillsDataSource(Context context) {
	    dbHelper = new MySQLiteBillHelper(context);
	}
	
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}

	public void close() {
	    dbHelper.close();
	}
	
	public Bill addBill(String mname, int mnItems, String[] mitemName, int[] mitemPrice, int[] mitemQuantity, int mtotal, String mimgPath){
		Bill bill = new Bill(mname, mnItems, mitemName, mitemPrice,  mitemQuantity, mtotal, mimgPath);
		String name = bill.name;
		int nItems 	= bill.nItems;
		String[] itemName 	= bill.itemName;
		int[] itemPrice		= bill.itemPrice;
		int[] itemQuantity 	= bill.itemQuantity;
		int total	= bill.total;
		String imgPath 	= bill.imgPath;
		Bill mBill = new Bill();
		String datetime = mBill.dateToString(Calendar.getInstance().getTime());
		ContentValues values = new ContentValues();
		values.put(MySQLiteBillHelper.COLUMN_NAME, name);
		values.put(MySQLiteBillHelper.COLUMN_NITEMS, nItems);
		values.put(MySQLiteBillHelper.COLUMN_TOTAL, total);
		values.put(MySQLiteBillHelper.COLUMN_IMGPATH, imgPath);
		values.put(MySQLiteBillHelper.COLUMN_DATETIME, datetime);
		long billInsertId = database.insert(MySQLiteBillHelper.TABLE_BILLS, null, values);
		Cursor cursor = database.query(MySQLiteBillHelper.TABLE_BILLS, 
										allColumns, 
										MySQLiteBillHelper.COLUMN_ID + " = " + billInsertId,
										null,null,null,null);
		cursor.moveToFirst();
		bill.setId(billInsertId);
		bill.setSaved(true);
		cursor.close();
		return bill;		
	}
	
	public void deleteBill(Bill bill){
		long id = bill.getId();
		database.delete(MySQLiteBillHelper.TABLE_BILLS, MySQLiteBillHelper.COLUMN_ID + " = " + id, null);
	}
	
	public List<Bill> getAllBills(){
		List<Bill> bills  = new ArrayList<Bill>();
		/*
		Cursor ti = database.rawQuery("PRAGMA table_info("+MySQLiteBillHelper.TABLE_BILLS+")", null);
		if ( ti.moveToFirst() ) {
		    do {
		        Log.v("BillsDataSource","col: " + ti.getString(1));
		    } while (ti.moveToNext());
		}
		*/
		Cursor cursor = database.query(MySQLiteBillHelper.TABLE_BILLS,
				allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		int i=0;
		while (!cursor.isAfterLast()) {
			  Bill bill = cursorToBill(cursor);
			  bills.add(bill);
			  cursor.moveToNext();
			  i++;
	    }
		cursor.close();
		return bills;
	}

	private Bill cursorToBill(Cursor cursor) {
	    Bill bill = new Bill();
	    bill.setId(cursor.getLong(0));
	    bill.setName(cursor.getString(1));
	    bill.setNItems(cursor.getInt(2));
	    bill.setTotal(cursor.getInt(3));
	    bill.setImgPath(cursor.getString(4));
	    bill.setDate(cursor.getString(5));
		bill.setSaved(true);
	    return bill;
	}
		
}
