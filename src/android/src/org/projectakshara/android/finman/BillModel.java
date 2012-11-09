package org.projectakshara.android.finman;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BillModel {
	 private int id;
	 private String name;
	 private int nItems;
	 private int total;
	 private String datetime;	 

	 private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	//Date today = Calendar.getInstance().getTime();    
	 
	 public int getId(){
		 return id;
	 }
	
	 public void setId(int id){
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
	 
	 public String dateToString(Date date){
		 return df.format(date);
	 }
	 
	 public Date getDateTime(){
		 Date dt;
		 try{
			 dt = (Date) df.parse(this.datetime);
		 }catch(java.text.ParseException e){
			 dt = Calendar.getInstance().getTime();// Returning today's date
		 }
		 return dt;
	 }
	 
	 public void setDate(Date datetime){
		 this.datetime = df.format(datetime);		 
	 }
	 
	 // Will be used by the ArrayAdapter in the ListView
	 @Override
	 public String toString() {
	     return Integer.toString(id) + name;
     }
}
