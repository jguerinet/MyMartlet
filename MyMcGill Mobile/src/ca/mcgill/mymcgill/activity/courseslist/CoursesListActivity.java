package ca.mcgill.mymcgill.activity.courseslist;

import android.os.Bundle;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseListActivity;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author : Julien
 * Date :  2014-05-26 7:09 PM
 * Shows a list of courses
 */
public class CoursesListActivity extends BaseListActivity {
    public List<Course> mCourses;
    private ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courseslist);
//        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        //Show the user we are working.
//        setProgressBarIndeterminateVisibility(true);

        //Get the list of courses from the intent
        String courseString = getIntent().getStringExtra(Constants.COURSES);

        // Views
        mListView = (ListView)findViewById(android.R.id.list);


        //If it's null, this is the wishlist
        if(courseString == null){
            //TODO Wishlist Code here
            mCourses = ApplicationClass.getCourseWishlist();
            //Load the stored info
            loadInfo();
        }
        //If not, parse it
        else{
            parseCourses(courseString);
        }
    }

    private void loadInfo(){
        CoursesAdapter adapter = new CoursesAdapter(this, mCourses);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();

        //Set up the adapter
        CoursesAdapter adapter = new CoursesAdapter(this, mCourses);
        setListAdapter(adapter);

        //Stop the spinner
//        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    //Parses the HTML retrieved from Minerva and returns a list of courses
    //Only used if this activity is a result of a search, and not for the course wishlist
    private List<Course> parseCourses(String coursesString){
        mCourses = new ArrayList<Course>();

        Document document = Jsoup.parse(coursesString, "UTF-8");

        //Find rows of HTML by class
        Elements dataRows = document.getElementsByClass("dddefault");

        int rowNumber = 0;
        boolean loop = true;

        while (loop) {

            // Create a new course object
            int credits = 99;
            String courseCode = "ERROR";
            String courseTitle = "ERROR";
            String sectionType = "";
            String days = "";
            int crn = 00000;
            String instructor = "";
            String location = "";
            String time = "";
            String dates = "";

            int i = 0;
            while (true) {

                try {
                    // Get the HTML row
                    Element row = dataRows.get(rowNumber);
                    rowNumber++;

                    // End condition: Empty row encountered
                    if (row.toString().contains("&nbsp;") || row.toString().contains("NOTES:")) {
                        break;
                    }

                    switch (i) {
                        // CRN
                        case 1:
                            crn = Integer.parseInt(row.text());
                            break;

                        // Course code
                        case 2:
                            courseCode = row.text();
                            break;
                        case 3:
                            courseCode += " " + row.text();
                            break;

                        // Section type
                        case 5:
                            sectionType = row.text();
                            break;

                        // Number of credits
                        case 6:
                            credits = (int) Double.parseDouble(row.text());
                            break;

                        // Course title
                        case 7:
                            courseTitle = row.text();
                            break;

                        // Days of the week
                        case 8:
                            days = row.text();

                            if (days.equals("TBA")) {
                                time = "TBA";
                                i = 10;
                                rowNumber++;
                            }
                            break;

                        // Time
                        case 9:
                            time = row.text();
                            break;

                        // Instructor
                        case 16:
                            instructor = row.text();
                            break;

                        // Start/end date
                        case 17:
                            dates = row.text();
                            break;

                        // Location
                        case 18:
                            location = row.text();
                            break;
                    }

                    i++;
                }
                catch (IndexOutOfBoundsException e){
                    loop = false;
                    break;
                }
                catch (Exception e) {

                }
            }

            if( !courseCode.equals("ERROR")){

                //Create a new course object and add it to list
                Course newCourse = new Course(credits, courseCode, courseTitle, sectionType, days, crn, instructor, location, time, dates);
                mCourses.add(newCourse);
            }
        }
        return mCourses;
    }
}