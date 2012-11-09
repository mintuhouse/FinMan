package org.projectakshara.android.finman;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

public class BillItemAdapter extends BaseAdapter {
	
	private Activity activity;
    private ArrayList< HashMap <String, String> > data;
    private static LayoutInflater inflater=null;

    public BillItemAdapter(Activity a, ArrayList< HashMap <String, String> > d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	public int getCount() {
	    return data.size();
	}

	public Object getItem(int position) {
	    return position;
	}

	public long getItemId(int position) {
	    return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.newbill_item, null);
 
        //ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        EditText itemname  = (EditText) vi.findViewById(R.id.newbill_itemName);
        EditText itemprice = (EditText) vi.findViewById(R.id.newbill_itemPrice);        
 
        HashMap<String, String> item = new HashMap<String, String>();
        item = data.get(position);
 
        // Setting all values in listview
        itemname.setText(item.get(OpenCVActivity.KEY_ITEMNAME));
        itemprice.setText(item.get(OpenCVActivity.KEY_ITEMPRICE));
        
        return vi;
    }
	
}

