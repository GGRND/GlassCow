package com.eaaa.glasscow;

import android.os.AsyncTask;
import android.util.Log;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.service.CowService;

public class AsyncCowDataChange extends AsyncTask<Integer, String, Cow>{

	public interface AsyncCowResponse{
		public void asyncCowResponse(Cow cow);
	}
	
	private AsyncCowResponse target;
	private int id;
	
	public AsyncCowDataChange(AsyncCowResponse target, int id){
		this.target = target;
		this.id = id;
	}
	
	@Override
	protected Cow doInBackground(Integer... params) {
		Log.d("GlassCow:Async", "Background");
		return CowService.getInstance().getCow(id);
	}
	
	@Override
	protected void onPostExecute(Cow result) {
		//super.onPostExecute(result);
		target.asyncCowResponse(result);
	}

}
