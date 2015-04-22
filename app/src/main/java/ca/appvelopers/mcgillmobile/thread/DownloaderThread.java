/*
 * Copyright 2014-2015 Appvelopers
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

package ca.appvelopers.mcgillmobile.thread;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.exception.MinervaException;
import ca.appvelopers.mcgillmobile.exception.NoInternetException;
import ca.appvelopers.mcgillmobile.ui.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Connection;

/**
 * Downloads the information at a given URL, shows any errors if necessary and present
 * @author Julien Guerinet
 * @version 2.0
 * @since 2.0
 */
public class DownloaderThread extends Thread {
	/**
	 * The calling activity (to show eventual errors)
	 */
	private Activity mActivity;
	/**
	 * The tag to use for logging errors
	 */
	private String mTag;
	/**
	 * The URL to query
	 */
	private String mURL;
	/**
	 * True if we should be showing errors, false otherwise
	 */
	private boolean mShowErrors;
	/**
	 * The results of the downloading, null if there was an error
	 */
	private String mResults;

	/**
	 * Default Constructor
	 *
	 * @param context    The app context
	 * @param tag        The tag to use for logging eventual errors
	 * @param url        The URl to make the request to
	 */
	public DownloaderThread(Context context, String tag, String url){
		//Make sure that the context is an activity before setting it (Service calls this too)
		this.mActivity = context instanceof Activity ? (Activity)context : null;
		this.mTag = tag;
		this.mURL = url;
		this.mResults = null;
	}

	@Override
	public void run() {
		synchronized(this){
			try{
				//Make the request
				this.mResults = Connection.getInstance().get(mURL);
			} catch(MinervaException e){
				//TODO Broadcast this
			} catch(Exception e){
				final boolean noInternet = e instanceof NoInternetException;
				Log.e(mTag, noInternet ? "No Internet" : "IOException", e);

				//Show an error message if possible
				if(mActivity != null){
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run(){
							DialogHelper.showNeutralDialog(mActivity,
									mActivity.getString(R.string.error),
									noInternet ? mActivity.getString(R.string.error_no_internet) :
											mActivity.getString(R.string.error_other));
						}
					});
				}
			}
		}
		notify();
	}

	/* HELPERS */

	/**
	 * Synchronously runs the thread until it is finished
	 *
	 * @return The html result
	 */
	public String execute(){
		start();

		synchronized(this){
			try{
				wait();
			} catch(InterruptedException ignored){}
		}

		return this.mResults;
	}
}
