package de.tudresden.mobilis.android.geotwitter.engine;

import de.tudresden.mobilis.android.geotwitter.activities.TreasuresListActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;


/**
 * This class is responsible for showing waiting animation while response is being
 * received and processed
 * @author MARCHELLO
 *
 */

public class WaitingAnimation extends AsyncTask<Void, Void, Boolean>{
	
	public WaitingAnimation(Activity activity){
		this.activity = activity;
		progress = new ProgressDialog(activity);

		if(activity instanceof TreasuresListActivity){
			progressLabel = "Registering . . .";
		}
	}
	
	
	public void setProgressBarLabel(String label){
		this.progressLabel = label;
	}

	ProgressDialog progress;
	Activity activity;
	String progressLabel = null;
	
	protected void onPreExecute(){
		this.progress.setMessage(progressLabel);
		this.progress.show();
		
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		while(!this.isCancelled()){	}
		return true;
	}
	
	@Override
	protected void onCancelled(){
		if(this.progress.isShowing()){
			progress.dismiss();
		}
	}
	
	@Override
    protected void onPostExecute(final Boolean success) {
		if(this.progress.isShowing()){
			progress.dismiss();
		}
		
	}
	
	
	
};

