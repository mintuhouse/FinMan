package org.projectakshara.android.finman;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.R.bool;

public class Bill {
	protected long id;
	protected String	name;
	protected int 	nItems;
	protected long[] itemID;
	protected String[] itemName;
	protected int[] 	itemPrice;
	protected int[]   itemQuantity;
	protected int 	total;
	protected String imgPath;
	protected String dateTime;
	protected boolean saved;
	
	Bill(){
		saved = false;
	}
	
	Bill(String mname, int mnItems, String[] mitemName, int[] mitemPrice, int[] mitemQuantity, int mtotal, String mimgPath){
		name	= mname;
		nItems	= mnItems;
		itemName=(String[]) mitemName.clone();
		itemPrice=(int[]) mitemPrice.clone();
		itemQuantity=(int[]) mitemQuantity.clone();
		total	= mtotal;
		imgPath = mimgPath;
		saved 	= false;
	}

	private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	//Date today = Calendar.getInstance().getTime();    
	 
	 public long getId(){
		 return id;
	 }
	
	 public void setId(long id){
		 this.id=id;
	 }
	 
	 public String getName(){
		 return name;
	 }
	 
	 public void setName(String name){
		 this.name = name;
	 }
	 
	 public int getNItems(){
		 return nItems;
	 }
	 
	 public void setNItems(int nItems){
		 this.nItems = nItems;
	 }
	 
	 public int getTotal(){
		 return total;
	 }
	
	 public void setTotal(int total){
		 this.total = total;
	 }
	 

	 public String getImgPath(){
		 return imgPath;
	 }
	
	 public void setImgPath(String imgPath){
		 this.imgPath = imgPath;
	 }
	 
	 public String dateToString(Date date){
		 return df.format(date);
	 }
	 
	 public Date getDateTime(){
		 Date dt;
		 try{
			 dt = (Date) df.parse(this.dateTime);
		 }catch(java.text.ParseException e){
			 dt = Calendar.getInstance().getTime();// Returning today's date
		 }
		 return dt;
	 }
	 
	 public void setDate(Date datetime){
		 this.dateTime = df.format(datetime);		 
	 }
	 
	 public void setDate(String datetime){
		 this.dateTime = datetime;
	 }
	 
	 // Will be used by the ArrayAdapter in the ListView
	 @Override
	 public String toString() {
	     return Long.toString(id) + name;
	 }
	

	public long getItemID(int i){
		return itemID[i];
	}
	
	public void setItemID(int id, int i){
		this.itemID[i] = id;
	}
	
	public String getItemName(int i){
		return itemName[i];
	}
	
	public void setItemName(String itemName, int i){
		this.itemName[i] = itemName;
	}
	
	public int getPrice(int i){
		return itemPrice[i];
	}
	
	public void setPrice(int price, int i){
		this.itemPrice[i] = price;
	}
	
	public int getQuantity(int i){
		return itemQuantity[i];		
	}
	
	public void setQuantity(int quantity, int i){
		this.itemQuantity[i] = quantity;
	}
		
	//Will be used by the ArrayAdapter in the ListView
	/*@Override
	public String toString() {
	     return Integer.toString(billID)+Integer.toString(id) + name;
	}*/
	
	public boolean getSaved(){
		return saved;
	}
	 
	public void setSaved(boolean saved){
		this.saved = saved;
	}
}
