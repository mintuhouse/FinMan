package org.projectakshara.android.finman;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BillAdapter extends BaseAdapter {

	private Activity activity;
    private List<Bill> data;
    private static LayoutInflater inflater=null;

    public BillAdapter(Activity a, List<Bill> d) {
        activity = a;
        data=d;
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
	
	public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.billlist_row, null);
        
        TextView billid			= (TextView) vi.findViewById(R.id.billid);
        ImageView list_image  	= (ImageView) vi.findViewById(R.id.list_image);
        TextView billname  		= (TextView) vi.findViewById(R.id.billname);
        TextView billdate  		= (TextView) vi.findViewById(R.id.billdate);
        TextView billtotal  	= (TextView) vi.findViewById(R.id.billtotal);
        ImageView billviewdetails  	= (ImageView) vi.findViewById(R.id.billviewdetails);
        Bill bill = new Bill();
        bill = data.get(position);
        //ImageView list_image
        billid.setText(Long.toString(bill.getId()));
        billname.setText(bill.getName());
        billdate.setText(bill.getDateTime());
        billtotal.setText(Integer.toString(bill.getTotal()));
        //ImageView billviewdetails
        
		return vi;
	}

}
