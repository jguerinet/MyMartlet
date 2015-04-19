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
import android.util.Log;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.exception.NoInternetException;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Base class for all of the user info downloaders.
 * @author Julien Guerinet
 * @version 2.0
 * @since 2.0
 */
public abstract class InfoDownloader extends Thread {
	/**
	 * True if the info downloader was successful, false otherwise
	 */
	private boolean mSuccess = true;

	/* GETTERS */

	/**
	 * @return True if the thread was successful, false otherwise
	 */
	public boolean success(){
		return this.mSuccess;
	}

	/* HELPERS */

	/**
	 * Makes a request at the given URL and returns the response body if no errors occur
	 * @param tag      The tag to use for any eventual errors
	 * @param url      The URL
	 * @param activity The activity if we need to show any error dialogs
	 * @return The response body in String format
	 */
	protected String get(String tag, String url, final Activity activity){
		try{
			//Make the request
			return Connection.getInstance().get(url);
		} catch(MinervaLoggedOutException e){
			mSuccess = false;
			//TODO Broadcast this
		} catch(Exception e){
			mSuccess = false;
			final boolean noInternet = e instanceof NoInternetException;
			Log.e(tag, noInternet ? "No Internet" : "IOException", e);

			//Show an error message if possible
			if(activity != null){
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run(){
						DialogHelper.showNeutralAlertDialog(activity,
								activity.getString(R.string.error),
								noInternet ? activity.getString(R.string.error_no_internet) :
										activity.getString(R.string.error_other));
					}
				});
			}
		}
		return null;
	}

}
