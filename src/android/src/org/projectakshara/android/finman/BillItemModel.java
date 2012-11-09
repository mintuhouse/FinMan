package org.projectakshara.android.finman;

public class BillItemModel {
	private int id;
	private String name;
	private int price;
	private int quantity;
	private int billID;
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getPrice(){
		return price;
	}
	
	public void setPrice(int price){
		this.price = price;
	}
	
	public int getQuantity(){
		return quantity;		
	}
	
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
	
	public int getBillID(){
		return billID;
	}
	
	public void set(int billID){
		this.billID = billID;
	}
	
	//Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
	     return Integer.toString(billID)+Integer.toString(id) + name;
    }
}
