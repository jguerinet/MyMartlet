/*
 * Copyright 2014-2016 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.util.thread;

import android.app.Activity;
import android.content.Context;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.model.exception.NoInternetException;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Connection;
import timber.log.Timber;

/**
 * Downloads the information at a given URL, shows any errors if necessary and present
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class DownloaderThread extends Thread {
	/**
	 * The calling activity (to show eventual errors)
	 */
	private Activity mActivity;
	/**
	 * The URL to query
	 */
	private String mURL;
	/**
	 * The body response, in String format
	 */
	private String mResult;
	/**
	 * The callback to use, if any
	 */
	private Callback mCallback;

	/**
	 * Default Constructor
	 *
	 * @param context    The app context
	 * @param url        The URl to make the request to
	 */
	public DownloaderThread(Context context, String url){
		//Make sure that the context is an activity before setting it (Service calls this too)
		this.mActivity = context instanceof Activity ? (Activity)context : null;
		this.mURL = url;
	}

	@Override
	public void run() {
		synchronized(this){
			mResult = null;
			try{
				mResult = Connection.getInstance().get(mURL);
			} catch(MinervaException e){
				//TODO Broadcast this
			} catch(Exception e){
				final boolean noInternet = e instanceof NoInternetException;
				Timber.e(e, noInternet ? "No Internet" : "IOException");

				//Show an error message if possible
				if(mActivity != null){
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run(){
							DialogHelper.neutral(mActivity, R.string.error,
									noInternet ? R.string.error_no_internet : R.string.error_other);
						}
					});
				}
			} finally {
				//Call the callback if necessary
				if(mCallback != null){
					mCallback.onDownloadFinished(mResult);
				}
			}
			notify();
		}
	}

	/* HELPERS */

	/**
	 * Executes the thread synchronously
	 *
	 * @return The response body in String format
	 */
	public String execute(){
		start();

		synchronized(this){
			try{
				wait();
			} catch(InterruptedException ignored){}
		}

		return this.mResult;
	}

	/**
	 * Executes the thread asynchronously and calls the callback when it is finished
	 *
	 * @param callback The callback to use after execution
	 */
	public void execute(Callback callback){
		this.mCallback = callback;
		start();
	}

	/**
	 * The callback to use when the thread has finished executing
	 */
	public static abstract class Callback {
		/**
		 * Method that is called when the thread has finished
		 *
		 * @param result The result of the downloader thread
		 */
		public abstract void onDownloadFinished(String result);
	}
}
