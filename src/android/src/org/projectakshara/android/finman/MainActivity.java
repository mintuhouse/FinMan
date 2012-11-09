package org.projectakshara.android.finman;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Uri imageStorageURI;
	private static String ALBUM_NAME="FinMan";

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final String IMAGE_PATH = "imagepath";
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private String mCurrentPhotoPath;
	
	/** Photo album for this application */
	private static String getAlbumName() {
		return ALBUM_NAME;
	}
	
	/** Create a file Uri for saving an image */
	private Uri getOutputMediaFileUri(int type){
		File mediaURI = getOutputMediaFile(type);
		if(mediaURI!=null){
			mCurrentPhotoPath = mediaURI.getAbsolutePath();		
			return Uri.fromFile(mediaURI);
		}else{
			return null;
		}
	}
	
	/** Create a File for saving an image  */
	private File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		String albumName = getAlbumName();
		File mediaStorageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
		    mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
		              Environment.DIRECTORY_PICTURES), albumName);	    
		    // Create the storage directory if it does not exist
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            Log.d(albumName, "failed to create directory");
		            return null;
		        }
		    }

		    // Create a media file name
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		    File mediaFile;
		    if (type == MEDIA_TYPE_IMAGE){
		        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
			    return mediaFile;
		    } else {
		        return null;
		    }
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.message_memorycard_unmounted)
			       .setTitle(R.string.title_memorycard_unmounted)
			       .setCancelable(true);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		return null;
	}
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(View.VISIBLE);
	}
	
	private void setNotification(String str){
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.billShotView);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search_transaction);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        // Configure the search info and add any event listeners
        searchMenuItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_billshot:
            	if (isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
	            	// create Intent to take a picture and return control to the calling application
	                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                imageStorageURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
	                if(imageStorageURI!=null){
	                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageStorageURI); // set the image file name
	
	                // start the image capture Intent
	                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	                /*
	                Intent intent_camera = new Intent(this, CameraActivity.class);
	                intent_camera.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                startActivity(intent_camera);*/
	                return true;
		            /*case R.id.menu_search_transaction:
		            	Intent intent_searchable = new Intent(this, SearchableActivity.class);
		            	intent_searchable.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            	startActivity(intent_searchable);
		            	return true;
		            */	
	                }else{
	                	Log.d("MemoryCard", "Not Available");
	            		return super.onOptionsItemSelected(item);
	            	}  
            	}else{
            		Log.d("Camera", "Not Available");
            		return super.onOptionsItemSelected(item);
            	}
            case R.id.menu_listbills:
            	Intent intent_billlist = new Intent(this, ListBillsActivity.class);
                intent_billlist.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_billlist);
            	return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to imageStorageURI specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                          mCurrentPhotoPath, Toast.LENGTH_LONG).show();
                setPic();
                galleryAddPic();
                Intent intent_opencv = new Intent(this, OpenCVActivity.class);
                intent_opencv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_opencv.putExtra(IMAGE_PATH, mCurrentPhotoPath);
                startActivity(intent_opencv);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }
    

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(IMAGE_PATH, mCurrentPhotoPath);
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mCurrentPhotoPath = savedInstanceState.getString(IMAGE_PATH);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(
				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
	}

    
    /**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

}
