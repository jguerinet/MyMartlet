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

package ca.appvelopers.mcgillmobile.util.background;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.TranscriptCourse;
import ca.appvelopers.mcgillmobile.ui.SplashActivity;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Runs the required checker tasks
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 2.0
 */
public class CheckerService extends IntentService {
	private static final String TAG = "CheckerService";
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
	public CheckerService() {
		super(TAG);
	}

	/**
	 * Here is where the actual logic goes when the alarm is called
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG,"Service started");

		//Check the grades if needed
		if(Load.gradeChecker()){
			checkGrades();
		}
		
		//Check the seats if needed
		if(Load.seatChecker()){
			checkSeats();
		}
	}
	
	/**
	 * Downloads the user's transcript to check for new or changed grades
	 */
	private void checkGrades(){
		String html = new DownloaderThread(this, TAG, Connection.TRANSCRIPT_URL).execute();

		if(html != null){
			//Get the previous grades
			Transcript oldTranscript = App.getTranscript();

			//Parse the new transcript and get it
			Parser.parseTranscript(html);
			Transcript newTranscript = App.getTranscript();

			//Check if the CGPA has changed, alert the user if it has
			if(Math.abs(oldTranscript.getCgpa() - newTranscript.getCgpa()) >= 0.01){
				Intent intent = new Intent(this, SplashActivity.class)
						.putExtra(Constants.HOMEPAGE, Homepage.TRANSCRIPT);
				//TODO Use String here
				createNotification(intent, "Your new CGPA is " + newTranscript.getCgpa(),
						GRADES_ID);
				return;
			}

			//Go through the new transcript's semesters
			for(Semester semester : newTranscript.getSemesters()){
				//Find the equivalent semester on the old transcript
				//  Don't use it if there aren't the same amount of courses (during add/drop)
				Semester foundSemester = null;
				for(Semester oldSemester : oldTranscript.getSemesters()){
					if(oldSemester.getTerm().equals(semester.getTerm()) &&
							oldSemester.getCourses().size() == semester.getCourses().size()){
						foundSemester = oldSemester;
						//Go through the new semester's courses
						for(TranscriptCourse course : semester.getCourses()){
							//Find an equivalent in the old semester
							TranscriptCourse foundCourse = null;
							for(TranscriptCourse oldCourse : oldSemester.getCourses()){
								if(course.getCourseCode().equals(oldCourse.getCourseCode())){
									foundCourse = oldCourse;
									//Alert the user if the grade has changed
									if(!course.getUserGrade().equals(oldCourse.getUserGrade())){
										Intent intent = new Intent(this, SplashActivity.class)
												.putExtra(Constants.HOMEPAGE, Homepage.TRANSCRIPT)
												.putExtra(Constants.TERM, semester.getTerm());
										//TODO Use a String
										createNotification(intent, "Your Grades are updated",
												GRADES_ID);
									}
									break;
								}
							}
							//If the course has been found, we can remove it from the old transcript
							if(foundCourse != null){
								oldSemester.getCourses().remove(foundCourse);
							}
						}
						break;
					}
				}
				//If the semester has been found, we can remove it from the old transcript
				if(foundSemester != null){
					oldTranscript.getSemesters().remove(foundSemester);
				}
			}
		}
	}
	
	/**
	 * Queries minerva to check for new seat openings
	 */
	private void checkSeats(){
		//TODO Do this better: merge with WishlistFragment ? When do we send a notification ?
//		List<Course> wishlistClasses = App.getWishlist();
//
//		//Refresh
//		//Sort ClassItems into Courses
//        List<TranscriptCourse> coursesList = new ArrayList<>();
//        for(Course wishlistClass : wishlistClasses){
//
//            boolean courseExists = false;
//            //Check if course exists in list
//            for(TranscriptCourse addedCourse : coursesList){
//                if(addedCourse.getCourseCode().equals(wishlistClass.getCode())){
//                    courseExists = true;
//                }
//            }
//            //Add course if it has not already been added
//            if(!courseExists){
//                coursesList.add(new TranscriptCourse(wishlistClass.getTerm(), wishlistClass.getCode(),
//                        wishlistClass.getTitle(), wishlistClass.getCredits(), "N/A", "N/A"));
//            }
//        }
//
//        //For each course, obtain its Minerva registration page
//        for(TranscriptCourse course : coursesList){
//
//            //Get the course registration URL
//            String courseCode[] = course.getCourseCode().split(" ");
//            String courseSubject = "";
//            String courseNumber = "";
//
//            //Check that the course code has been split successfully
//            if(courseCode.length > 1){
//                courseSubject = courseCode[0];
//                courseNumber = courseCode[1];
//            } else{
//                //TODO: Return indication of failure
//                return;
//            }
//
//            String registrationUrl = new Connection.SearchURLBuilder(course.getTerm(), courseSubject)
//		            .courseNumber(courseNumber)
//		            .build();
//
//            //error check the url
//            if(registrationUrl==null){
//            	continue;
//            }
//
//	        String classesString = null;
//	        try{
//		        classesString = Connection.getInstance().get(registrationUrl);
//	        } catch(Exception e){
//		        //TODO
//		        e.printStackTrace();
//	        }
//
//	        //TODO: Figure out a way to parse only some course sections instead of re-parsing all course sections for a given Course
//            //This parses all ClassItems for a given course
//            List<Course> updatedClassList = Parser.parseClassResults(course.getTerm(), classesString);
//
//            //Update the course object with an updated class size
//            for(Course updatedClass : updatedClassList){
//
//                for(Course wishlistClass : wishlistClasses){
//
//                    if(wishlistClass.getCRN() == updatedClass.getCRN()){
//                        wishlistClass.setDays(updatedClass.getDays());
//                        wishlistClass.setStartTime(updatedClass.getRoundedStartTime());
//                        wishlistClass.setEndTime(updatedClass.getRoundedEndTime());
//                        wishlistClass.setDates(updatedClass.getDateString());
//                        wishlistClass.setInstructor(updatedClass.getInstructor());
//                        wishlistClass.setLocation(updatedClass.getLocation());
//                        wishlistClass.setSeatsRemaining(updatedClass.getSeatsRemaining());
//                        wishlistClass.setWaitlistRemaining(updatedClass.getWaitlistRemaining());
//                    }
//                }
//            }
//        }
//
//		//check if any classes have open spots
//		for(Course wantedClass : wishlistClasses){
//			if(wantedClass.getSeatsRemaining()>0){
//				Intent intent = new Intent(this, SplashActivity.class)
//						.putExtra(Constants.HOMEPAGE, Homepage.WISHLIST)
//						.putExtra(Constants.TERM, wantedClass.getTerm());
//				//show notification
//				createNotification(intent, "A spot has opened up for the class: " +
//						wantedClass.getTitle(), SEATS_ID);
//				return;
//			}
//		}
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
