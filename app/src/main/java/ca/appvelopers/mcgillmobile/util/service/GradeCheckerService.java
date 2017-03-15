/*
 * Copyright 2014-2017 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.util.service;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.guerinet.utils.Utils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse;
import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse_Table;
import ca.appvelopers.mcgillmobile.ui.SplashActivity;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.TranscriptDB;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.util.retrofit.TranscriptConverter;
import timber.log.Timber;

/**
 * Checks if the user has any new grades
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class GradeCheckerService extends IntentService {
    /**
     * {@link NotificationManager} instance
     */
    @Inject
    NotificationManager notificationManager;
    /**
     * {@link McGillService} instance
     */
    @Inject
    McGillService mcGillService;

	/**
	 * Default Constructor
	 */
	public GradeCheckerService() {
		super("GradeCheckerService");
	}

    @Override
    public void onCreate() {
        super.onCreate();
        App.component(this).inject(this);
    }

    /**
	 * Here is where the actual logic goes when the alarm is called
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
        // If we aren't connected to the internet, don't continue
        if (!Utils.isConnected(this)) {
            return;
        }

        // Get the old transcript
        Transcript oldTranscript = SQLite.select()
                .from(Transcript.class)
                .querySingle();

        // If there was no transcript, don't continue
        if (oldTranscript == null) {
            return;
        }

        // Download the user's transcript
        TranscriptConverter.TranscriptResponse transcriptResponse;
        try {
            transcriptResponse = mcGillService.transcript().execute().body();
        } catch (IOException e) {
            Timber.e(e, "Error downloading user transcript when checking for grades");
            return;
        }

        // If there was no response, don't continue
        if (transcriptResponse == null) {
            return;
        }

        // Go through the new transcript's courses
        for (TranscriptCourse course : transcriptResponse.courses) {
            // Find the equivalent course
            TranscriptCourse oldCourse = SQLite.select()
                    .from(TranscriptCourse.class)
                    .where(TranscriptCourse_Table.id.eq(course.getId()))
                    .querySingle();

            // If there is no equivalent, continue
            if (oldCourse == null) {
                continue;
            }

            if (!course.getUserGrade().equals(oldCourse.getUserGrade())) {
                // Open the app to the transcript page
                Intent notificationIntent = new Intent(this, SplashActivity.class)
                        .putExtra(Constants.ID, HomepageManager.TRANSCRIPT);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);
                // TODO String
                String message = "You have a new grade for " + course.getCourseCode();

                // Build and send the notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(message)
                        .setContentIntent(pendingIntent);

                // TODO Try to find a way to use different request codes for the different courses
                notificationManager.notify(100, builder.build());
            }
		}

		// Save th transcript
        TranscriptDB.saveTranscript(this, transcriptResponse);
	}
}
