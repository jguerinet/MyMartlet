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
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.ui.SplashActivity;
import timber.log.Timber;

/**
 * Runs the required checker tasks
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class GradeCheckerService extends IntentService {
	/**
	 * The notification Id used for the grade checker notifications
	 */
	private static final int GRADES_ID = 100;
	/**
	 * The notification Id used for the seat checker notification
	 */
	private static final int SEATS_ID = 200;

	/**
	 * Default Constructor
	 */
	public GradeCheckerService() {
		super("GradeCheckerService");
	}

	/**
	 * Here is where the actual logic goes when the alarm is called
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Timber.i("Service started");

        //TODO
//		String html = new DownloaderThread(this, McGillManager.TRANSCRIPT_URL).execute();
        String html = null;

		if(html != null){
			//Get the previous grades
            //TODO
			Transcript oldTranscript = null;

			//Parse the new transcript and get it
            //TODO
//			Parser.parseTranscript(html);
            //TODO
			Transcript newTranscript = null;

			//Check if the CGPA has changed, alert the user if it has
			if(Math.abs(oldTranscript.getCGPA() - newTranscript.getCGPA()) >= 0.01){
                //TODO
				Intent anIntent = new Intent(this, SplashActivity.class);
//						.putExtra(Constants.HOMEPAGE, HomepageManager.TRANSCRIPT);
				//TODO Use String here
				createNotification(anIntent, "Your new CGPA is " + newTranscript.getCGPA(),
						GRADES_ID);
				return;
			}

			//Go through the new transcript's semesters
            // TODO
//			for(Semester semester : newTranscript.getSemesters()){
//				//Find the equivalent semester on the old transcript
//				//  Don't use it if there aren't the same amount of courses (during add/drop)
//				Semester foundSemester = null;
//				for(Semester oldSemester : oldTranscript.getSemesters()){
//					if(oldSemester.getTerm().equals(semester.getTerm()) &&
//							oldSemester.getCourses().size() == semester.getCourses().size()){
//						foundSemester = oldSemester;
//						//Go through the new semester's courses
//						for(TranscriptCourse course : semester.getCourses()){
//							//Find an equivalent in the old semester
//							TranscriptCourse foundCourse = null;
//							for(TranscriptCourse oldCourse : oldSemester.getCourses()){
//								if(course.getCourseCode().equals(oldCourse.getCourseCode())){
//									foundCourse = oldCourse;
//									//Alert the user if the grade has changed
//									if(!course.getUserGrade().equals(oldCourse.getUserGrade())){
//                                        //TODO
//										Intent intent = new Intent(this, SplashActivity.class)
////												.putExtra(Constants.HOMEPAGE, HomepageManager.TRANSCRIPT)
//												.putExtra(Constants.TERM, semester.getTerm());
//										//TODO Use a String
//										createNotification(intent, "Your Grades are updated",
//												GRADES_ID);
//									}
//									break;
//								}
//							}
//							//If the course has been found, we can remove it from the old transcript
//							if(foundCourse != null){
//								oldSemester.getCourses().remove(foundCourse);
//							}
//						}
//						break;
//					}
//				}
//				//If the semester has been found, we can remove it from the old transcript
//				if(foundSemester != null){
//					oldTranscript.getSemesters().remove(foundSemester);
//				}
//			}
		}
	}

	/**
	 * Generates a local notification which will redirect the user to the right portion of the app
	 *  when clicked
	 *
	 * @param intent  The intent to use when clicked
	 * @param message The message to display
	 * @param id      The notification Id (to update any existing ones)
	 */
	private void createNotification(Intent intent, String message, int id){
		//Get the notification manager
		NotificationManager manager =
				(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
		        .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
		        .setContentText(message)
		        .setContentIntent(pendingIntent);

        manager.notify(id, builder.build());
	}
}
