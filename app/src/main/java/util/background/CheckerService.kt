/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.util.background

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.transcript.Transcript
import timber.log.Timber

/**
 * Runs the required checker tasks
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 2.0.0
 */
/**
 * Default Constructor
 */
class CheckerService : IntentService("CheckerService") {

    /**
     * Here is where the actual logic goes when the alarm is called
     */
    override fun onHandleIntent(intent: Intent?) {
        Timber.i("Service started")

        //TODO
        //		//Check the grades if needed
        //		if(Load.gradeChecker()){
        //			checkGrades();
        //		}
        //
        //		//Check the seats if needed
        //		if(Load.seatChecker()){
        //			checkSeats();
        //		}
    }

    /**
     * Downloads the user's transcript to check for new or changed grades
     */
    private fun checkGrades() {
        //TODO
        //		String html = new DownloaderThread(this, McGillManager.TRANSCRIPT_URL).execute();
        val html: String? = null

        if (html != null) {
            //Get the previous grades
            //TODO
            val oldTranscript: Transcript? = null

            //Parse the new transcript and get it
            //TODO
            //			Parser.parseTranscript(html);
            //TODO
            //			Transcript newTranscript = null;

            //Check if the CGPA has changed, alert the user if it has
            //			if(Math.abs(oldTranscript.getCGPA() - newTranscript.getCGPA()) >= 0.01){
            //TODO
            //				Intent intent = new Intent(this, SplashActivity.class);
            //						.putExtra(Constants.HOMEPAGE, HomepageManager.TRANSCRIPT);
            //TODO Use String here
            //				createNotification(intent, "Your new CGPA is " + newTranscript.getCGPA(),
            //						GRADES_ID);
            //				return;
            //			}

            //Go through the new transcript's semesters
            // TODO
            //			for(Semester semester : newTranscript.getSemesters()){
            //				//Find the equivalent semester on the old transcript
            //				//  Don't use it if there aren't the same amount of courses (during add/drop)
            //				Semester foundSemester = null;
            //				for(Semester oldSemester : oldTranscript.getSemesters()){
            //					if(oldSemester.getCurrentTerm().equals(semester.getCurrentTerm()) &&
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
            //												.putExtra(Constants.TERM, semester.getCurrentTerm());
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
     * Queries minerva to check for new seat openings
     */
    private fun checkSeats() {
        //TODO Do this better: merge with WishlistActivity ? When do we send a notification ?
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
        //                coursesList.add(new TranscriptCourse(wishlistClass.getCurrentTerm(), wishlistClass.getCode(),
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
        //            String registrationUrl = new Connection.SearchURLBuilder(course.getCurrentTerm(), courseSubject)
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
        //            List<Course> updatedClassList = Parser.parseClassResults(course.getCurrentTerm(), classesString);
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
        //						.putExtra(Constants.HOMEPAGE, HomepageManager.WISHLIST)
        //						.putExtra(Constants.TERM, wantedClass.getCurrentTerm());
        //				//show notification
        //				createNotification(intent, "A spot has opened up for the class: " +
        //						wantedClass.getTitle(), SEATS_ID);
        //				return;
        //			}
        //		}
    }

    /**
     * Generates a local notification which will redirect the user to the right portion of the app
     * when clicked
     *
     * @param intent The intent to use when clicked
     * @param message The message to display
     * @param id The notification Id (to update any existing ones)
     */
    private fun createNotification(intent: Intent, message: String, id: Int) {
        //Get the notification manager
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentText(message)
            .setContentIntent(pendingIntent)

        manager.notify(id, builder.build())
    }

    companion object {
        /**
         * The notification Id used for the grade checker notifications
         */
        private val GRADES_ID = 100
        /**
         * The notification Id used for the seat checker notification
         */
        private val SEATS_ID = 200
    }
}
