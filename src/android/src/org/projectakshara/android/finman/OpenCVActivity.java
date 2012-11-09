package org.projectakshara.android.finman;

import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
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
	protected int 	nItems;
	protected String[] itemName;
	protected int[] 	itemPrice;
	protected int 	total;
	protected String imgPath;
	
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
					Log.i(TAG, "OpenCV loaded successfully");
					// Create and set View
					processBill(path);
					Log.v(TAG, "TEST "+name+" "+nItems+" "+imgPath+" "+total+" "+itemName[1]+" "+itemPrice[1]);
					
					EditText mEditTextName = (EditText) findViewById(R.id.newbill_name);			
					mEditTextName.setText(name);
					
					ArrayList< HashMap<String,String> > items = new ArrayList< HashMap<String,String> >();
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
					    						    	
					    }
					}; 
					
					Button saveButton = (Button) findViewById(R.id.newbill_save);
					saveButton.setOnClickListener(mSaveListener);					
									
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
		
        Log.i(TAG, "Trying to load OpenCV library");
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
    
}
