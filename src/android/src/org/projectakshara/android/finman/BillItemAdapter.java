package org.projectakshara.android.finman;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

public class BillItemAdapter extends BaseAdapter {
	
	private Activity activity;
    private ArrayList< HashMap <String, String> > data;
    private static LayoutInflater inflater=null;

    public BillItemAdapter(Activity a, ArrayList< HashMap <String, String> > d) {
        activity = a;
        data	 = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	public int getCount() {
	    return data.size();
	}

	public Object getItem(int position) {
	    return data.get(position);
	}

	public long getItemId(int position) {
	    return position;
	}
	
	private static class ViewHolder{
		int position;
		EditText itemname;
		EditText itemprice;
		ImageButton itemdelete;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.newbill_item, null);
        }
        holder = new ViewHolder();
        holder.position = position;
        holder.itemname  = (EditText) vi.findViewById(R.id.newbill_itemName);
        holder.itemprice = (EditText) vi.findViewById(R.id.newbill_itemPrice);
        holder.itemdelete = (ImageButton) vi.findViewById(R.id.newbill_itemdelete);
 
        HashMap<String, String> item = new HashMap<String, String>();
        item = data.get(position);
 
        // Setting all values in listview
        holder.itemname.setText(item.get(OpenCVActivity.KEY_ITEMNAME));
        holder.itemprice.setText(item.get(OpenCVActivity.KEY_ITEMPRICE));
        holder.itemdelete.setTag(item);
        holder.itemname.addTextChangedListener(new textWatcher(holder));
        
        return vi;
    }
	
	private class textWatcher implements TextWatcher {
        private ViewHolder holder;

        private textWatcher(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void afterTextChanged(Editable s) {
        	int position = holder.position;
        	String newname = holder.itemname.getText().toString().trim();
        	String newprice = holder.itemprice.getText().toString().trim();
    		Log.v("BillItemAdapter","Position: "+ Integer.toString(position)+" ItemName: "+ newname+ " ItemPrice: "+ newprice);
    		((OpenCVActivity)activity).changeItem(position, newname, newprice,"");
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    }
	
}
