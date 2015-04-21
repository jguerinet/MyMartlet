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

package ca.appvelopers.mcgillmobile.background;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.TranscriptCourse;
import ca.appvelopers.mcgillmobile.thread.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.ui.main.MainActivity;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;

/**
 * Author: Shabbir
 * Date: 20/08/14, 5:42 PM
 * This class defines the tasks that will run in the background
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class WebFetcherService extends IntentService {

	protected NotificationManager mNotificationManager;
	protected int NOTIFICATION_ID_GRADES = 1720;
	protected int NOTIFICATION_ID_CLASSES= 1722;
	public WebFetcherService() {
		super("SchedulingService");
		
	}

	/**
	 * Here is where the actual logic goes when the alarm is called
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.v("Background: ","service started");
		//compare grades in transcript
		CheckGrade();
		
		//check seats int the background
		//TODO: FIX check seats method
		CheckSeats();
		
		//release the wake lock of the phone
		AlarmReceiver.completeWakefulIntent(intent);
	}
	
	/**
	 * This method queries the transcript to check for new or changed grades
	 */
	protected void CheckGrade(){
		final TranscriptDownloader downloader = new TranscriptDownloader(this, false);
		downloader.start();

		//get previous grades
		Transcript oldTranscript = App.getTranscript();

		synchronized(downloader){
			try{
				downloader.wait();
			} catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		//compare old vs new for grade changes
		if(downloader.success()){
			Transcript newTranscript = App.getTranscript();
			CompareTranscripts(oldTranscript,newTranscript);
		}
	}
	
	/**
	 * This method compares multiple aspects of the transcript and alerts the user
	 * @param oldTrans
	 * @param newTrans
	 */
    //TODO Rework this with new activity/fragment structure
	protected void CompareTranscripts(Transcript oldTrans, Transcript newTrans){
		
		//check for error
		if(oldTrans == null || newTrans == null){
			return;
		}
		
		//check if cgpa is changed
		if(Math.abs(oldTrans.getCgpa() - newTrans.getCgpa()) >= 0.01){
			LocalToast("Your new CGPA is "+newTrans.getCgpa(), MainActivity.class,NOTIFICATION_ID_GRADES);
			return;
		}
		
		//error check semesters
		if(oldTrans.getSemesters()==null||newTrans.getSemesters()==null)
			return;
		
		int oldIndex = 0;
		int newIndex = 0;
		int oldSize = oldTrans.getSemesters().size();
		int newSize = newTrans.getSemesters().size();
		
		//start first semester
		Semester oldSem = oldTrans.getSemesters().get(oldIndex);
		Semester newSem = newTrans.getSemesters().get(newIndex);
		
		while(oldIndex < oldSize && newIndex < newSize){
			oldSem = oldTrans.getSemesters().get(oldIndex);
			newSem = newTrans.getSemesters().get(newIndex);
			if(oldSem == null || newSem==null ||!oldSem.getTerm().equals(newSem.getTerm()) || oldSem.getCourses().size() != newSem.getCourses().size()){
				//don't compare different semesters OR just after add / drop
				return;
			}
			
			int oldCourseIndex=0;
			int newCourseIndex=0;
			int oldCourseSize= oldSem.getCourses().size();
			int newCourseSize= newSem.getCourses().size();
			TranscriptCourse oldCourse = oldSem.getCourses().get(oldCourseIndex);
			TranscriptCourse newCourse = newSem.getCourses().get(newCourseIndex);
			
			while(oldCourseIndex<oldCourseSize && newCourseIndex  < newCourseSize){
				oldCourse = oldSem.getCourses().get(oldCourseIndex);
				newCourse = newSem.getCourses().get(newCourseIndex);
				if(oldCourse == null || newCourse==null){
					return; //a parsing error occured
				}
				
				//check if grades have changed
				if(!oldCourse.getUserGrade().equals(newCourse.getUserGrade())){
					LocalToast("Your Grades are updated", MainActivity.class,NOTIFICATION_ID_GRADES+1);
					return; //we only need one notification so return after this
				}
				
				++oldCourseIndex;
				++newCourseIndex;					
			}
			
			
			++oldIndex;
			++newIndex;
		}


		
	}
	

	
	/**
	 * This method queries minerva to check for new seat openings
	 */
    //TODO Rework this with new activity/fragment structure
	protected void CheckSeats(){
		List<Course> wishlistClasses = App.getClassWishlist();
		
		//refresh wishlist
		//Sort ClassItems into Courses
        List<TranscriptCourse> coursesList = new ArrayList<TranscriptCourse>();
        for(Course wishlistClass : wishlistClasses){

            boolean courseExists = false;
            //Check if course exists in list
            for(TranscriptCourse addedCourse : coursesList){
                if(addedCourse.getCourseCode().equals(wishlistClass.getCode())){
                    courseExists = true;
                }
            }
            //Add course if it has not already been added
            if(!courseExists){
                coursesList.add(new TranscriptCourse(wishlistClass.getTerm(), wishlistClass.getTitle(),
                        wishlistClass.getCode(), wishlistClass.getCredits(), "N/A", "N/A"));
            }
        }

        //For each course, obtain its Minerva registration page
        for(TranscriptCourse course : coursesList){

            //Get the course registration URL
            String courseCode[] = course.getCourseCode().split(" ");
            String courseSubject = "";
            String courseNumber = "";

            //Check that the course code has been split successfully
            if(courseCode.length > 1){
                courseSubject = courseCode[0];
                courseNumber = courseCode[1];
            } else{
                //TODO: Return indication of failure
                return;
            }

            String registrationUrl = new Connection.SearchURLBuilder(course.getTerm(), courseSubject)
		            .courseNumber(courseNumber)
		            .build();

            //error check the url
            if(registrationUrl==null){
            	continue;
            }

	        String classesString = null;
	        try{
		        classesString = Connection.getInstance().get(registrationUrl);
	        } catch(Exception e){
		        //TODO
		        e.printStackTrace();
	        }

	        //TODO: Figure out a way to parse only some course sections instead of re-parsing all course sections for a given Course
            //This parses all ClassItems for a given course
            List<Course> updatedClassList = Parser.parseClassResults(course.getTerm(), classesString);

            //Update the course object with an updated class size
            for(Course updatedClass : updatedClassList){

                for(Course wishlistClass : wishlistClasses){

                    if(wishlistClass.getCRN() == updatedClass.getCRN()){
                        wishlistClass.setDays(updatedClass.getDays());
                        wishlistClass.setStartTime(updatedClass.getRoundedStartTime());
                        wishlistClass.setEndTime(updatedClass.getRoundedEndTime());
                        wishlistClass.setDates(updatedClass.getDateString());
                        wishlistClass.setInstructor(updatedClass.getInstructor());
                        wishlistClass.setLocation(updatedClass.getLocation());
                        wishlistClass.setSeatsRemaining(updatedClass.getSeatsRemaining());
                        wishlistClass.setWaitlistRemaining(updatedClass.getWaitlistRemaining());
                    }
                }
            }
        }
		
		//check if any classes have open spots
		for(Course wantedClass : wishlistClasses){
			if(wantedClass.getSeatsRemaining()>0){
				//show notification
				LocalToast("A spot has opened up for the class: "+wantedClass.getTitle(), MainActivity.class,NOTIFICATION_ID_CLASSES);
				return;
			}
		}
	}

	/**
	 * This method generates a local toast which will open up to an activity determined by cls
	 * @param message
	 * @param cls
	 */
	protected void LocalToast(String message,Class<?> cls, int ID){
		NotificationManager mNotificationManager = (NotificationManager)
	               this.getSystemService(Context.NOTIFICATION_SERVICE);
	    
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	            new Intent(this, cls), 0);

	        NotificationCompat.Builder mBuilder =
	                new NotificationCompat.Builder(this)
	        .setSmallIcon(R.mipmap.ic_launcher)
	        .setContentTitle(getString(R.string.app_name))
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(message))
	        .setContentText(message);

	        mBuilder.setContentIntent(contentIntent);
	        mNotificationManager.notify(ID, mBuilder.build());
	}
}
