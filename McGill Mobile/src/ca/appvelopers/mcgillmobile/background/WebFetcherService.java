package ca.appvelopers.mcgillmobile.background;

import android.app.IntentService;
import android.content.Intent;

public class WebFetcherService extends IntentService {

	public WebFetcherService() {
		super("SchedulingService");
		
	}

	/**
	 * Here is where the actual logic goes when the alarm is called
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		
	}

}
