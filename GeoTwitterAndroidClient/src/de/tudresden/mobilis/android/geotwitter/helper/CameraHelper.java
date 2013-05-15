package de.tudresden.mobilis.android.geotwitter.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

/**
 * 
 * @author Marian Seliuchenko
 *
 */

public class CameraHelper {
	
	String albumName = "GeoTwitter";
	String mCurrentPhotoPath = null;
	
	public Intent prepareTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    File f = null;
		try {
			f = createImageFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("CameraHANDLER", "File preparing crashed!");
			e.printStackTrace();
		}
	    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
	    return takePictureIntent;
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size()>0)
        	return true;
        return false;
    }
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = 
	        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "GeoTwitter_ "+ timeStamp + "_";
	    
	    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"");
	    File image = File.createTempFile(imageFileName,".JPEG",storageDir);
	    mCurrentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	
	public Bitmap setPic(ImageView mImageView) {
	    // Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();
	  
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	  
	    // Determine how much to scale down the image
	   // int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	    int scaleFactor = 8;
	  
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    
	    return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	}
	
	public static String convertBitmapToString(Bitmap bitmap){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		String result = Base64.encodeToString(byteArray, Base64.DEFAULT);
		return result;
	}
	
	public static Bitmap convertStringToBitmap(String string){
		byte[] byteArray1;
		byteArray1 = Base64.decode(string, Base64.DEFAULT);
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.length);
		return bmp;
	}
	
	
	
	

}
