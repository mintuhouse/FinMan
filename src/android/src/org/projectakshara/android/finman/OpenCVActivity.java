package org.projectakshara.android.finman;

import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/** Activity class implements LoaderCallbackInterface to handle OpenCV initialization status **/
public class OpenCVActivity extends Activity {
	private static final String TAG = "OpenCV::Activity";
	private static final String IMAGE_PATH = "imagepath";
	
	static final String KEY_ITEM = "item"; // parent node
    static final String KEY_ITEMNAME = "itemname";
    static final String KEY_ITEMPRICE = "itemprice";
	
	private String path;
	//private Bill bill;

	protected String	name;     
	protected int 	nItems;			//-- Updated with UI Changes
	protected String[] itemName;
	protected int[] 	itemPrice;
	protected int 	total;
	protected String imgPath;
	
	ArrayList< HashMap<String,String> > items;
	BillItemAdapter adapter;
	Activity a;
	
	private native int processBill(String path);

	private BaseLoaderCallback  mOpenCVCallBack = new BaseLoaderCallback(this) {
    	@Override
    	public void onManagerConnected(int status) {
    		switch (status) {
				case LoaderCallbackInterface.SUCCESS:
				{
					System.loadLibrary("jni_part");
					// Create and set View
					processBill(path);
					//Log.v(TAG, "TEST "+name+" "+nItems+" "+imgPath+" "+total+" "+itemName[1]+" "+itemPrice[1]);
					
					EditText mEditTextName = (EditText) findViewById(R.id.newbill_name);			
					mEditTextName.setText(name);
					
					items = new ArrayList< HashMap<String,String> >();
					ListView itemsListView = (ListView) findViewById( R.id.newbill_items );
					for (int i = 0; i < nItems; i++) {
						HashMap<String,String> map = new HashMap<String, String>();
						map.put(KEY_ITEMNAME, itemName[i]);
						map.put(KEY_ITEMPRICE, Integer.toString(itemPrice[i]));
						items.add(map);
					}
					
					adapter = new BillItemAdapter(a, items);
					itemsListView.setAdapter(adapter);
					
					OnClickListener mSaveListener = new OnClickListener() {
					    public void onClick(View v) {
							adapter.notifyDataSetChanged(); //TODO: Not Neccessary
					    	BillsDataSource adapter1 = new BillsDataSource(a);
					    	adapter1.open();
					    	Bill bill = new Bill();
					    	EditText mEditTextName = (EditText) findViewById(R.id.newbill_name);
					    	String mname = mEditTextName.getText().toString().trim();
					    	int mnItems = nItems;
					    	String[] mitemName 	= new String[mnItems];
					    	int[] mitemPrice 	= new int[mnItems];
					    	int[] mitemQuantity	= new int[mnItems];
					    	int mtotal = 0;
					    	for(int i = 0;i < mnItems; i++){
						    	HashMap<String,String> map = items.get(i);
						    	mitemName[i] 	= map.get(KEY_ITEMNAME);
								mitemPrice[i]	= Integer.parseInt(map.get(KEY_ITEMPRICE));
								mtotal+=mitemPrice[i];
					    	}
					    	String mimgPath = imgPath;
					    	adapter1.addBill(mname, mnItems , mitemName, mitemPrice,  mitemQuantity, mtotal, mimgPath);
					    	adapter1.close();
					    	
					    	Intent intent = new Intent(a, ListBillsActivity.class);
					    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					    	startActivity(intent);
					    }
					}; 
					
					OnClickListener mAddListener = new OnClickListener(){
						public void onClick(View v){
							nItems++;
							HashMap<String,String> map = new HashMap<String, String>();
							map.put(KEY_ITEMNAME, "");
							map.put(KEY_ITEMPRICE, Integer.toString(0));
							items.add(map);
							adapter.notifyDataSetChanged();
						}
					};
					
					Button saveButton = (Button) findViewById(R.id.newbill_save);
					saveButton.setOnClickListener(mSaveListener);	
					
					Button addButton  = (Button) findViewById(R.id.newbill_add);
					addButton.setOnClickListener(mAddListener);
									
				} break;
				default:
				{
					super.onManagerConnected(status);
				} break;
			}
    	}

	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	
		path = this.getIntent().getStringExtra(IMAGE_PATH);

		setContentView(R.layout.activity_opencv);
		a = this;
		
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
        {
        	Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_opencv, menu);
        return true;
    }
    /*
    static {
            if (!OpenCVLoader.initDebug()) {
            	Log.e(TAG, "Cannot initialize to OpenCV Manager");
                // Handle initialization error
            }else{
            	System.loadLibrary("jni_part");
            }
        }
    */
    
    public void removeItemOnClickHandler(View v){
    	/*int position = (Integer)v.getTag();
    	Log.v("OpenCVActivity","Removing item at "+ Integer.toString(position));
    	items.remove(position);*/
    	items.remove((HashMap<String,String>)v.getTag());
    	nItems--;
    	adapter.notifyDataSetChanged();
    }
    
    public void changeItem(int position, String newItemName, String newItemPrice, String newItemQuantity){
    	Log.v("OpenCVActivity",newItemName+" is the new value of item at "+Integer.toString(position));
    	HashMap<String,String> map = items.get(position);
		map.put(KEY_ITEMNAME, newItemName);
		map.put(KEY_ITEMPRICE, newItemPrice);
		items.set(position, map);
    	//adapter.notifyDataSetChanged();		
    }
    
}
