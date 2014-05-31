package ca.mcgill.mymcgill.activity.courseslist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.ChangeSemesterActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author : Julien
 * Date :  2014-05-26 7:09 PM
 * Shows a list of searchedCourses
 */
public class CoursesListActivity extends DrawerActivity {
    public boolean wishlist;

    private List<Course> mCourses;
    private ListView mListView;
    private CoursesAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_courseslist);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        wishlist = getIntent().getBooleanExtra(Constants.WISHLIST, true);

        super.onCreate(savedInstanceState);

        // Views
        mListView = (ListView)findViewById(R.id.courses_list);
        mListView.setEmptyView(findViewById(R.id.courses_empty));

        //Check if we need to load the wishlist
        if(wishlist){
            mCourses = App.getCourseWishlist();
        }
        //If not, get the searched courses
        else{
            mCourses = Constants.searchedCourses;
        }

        //Register button
        TextView register = (TextView)findViewById(R.id.course_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Registration code here
                Toast.makeText(CoursesListActivity.this, "Feature not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        //Add/Remove to/from Wishlist Button
        TextView wishlist = (TextView)findViewById(R.id.course_wishlist);
        wishlist.setText(this.wishlist ? getResources().getString(R.string.courses_remove_wishlist) :
            getResources().getString(R.string.courses_add_wishlist));
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the checked list of courses from the adapter
                List<Course> checkedCourses = mAdapter.getCheckedCourses();

                String toastMessage;

                //If there are none, display error message
                if(checkedCourses.isEmpty()){
                    toastMessage = getResources().getString(R.string.wishlist_error_empty);
                }
                //If we are in the wishlist, this button is to remove a course
                else if(CoursesListActivity.this.wishlist){
                    toastMessage = getResources().getString(R.string.wishlist_remove, checkedCourses.size());
                    mCourses.removeAll(checkedCourses);

                    //Save the courses to the App context
                    App.setCourseWishlist(mCourses);

                    //Reload the adapter
                    loadInfo();

                }
                //If not, it's to add a course to the wishlist
                else{
                    //Get the wishlist courses
                    List<Course> wishlist = App.getCourseWishlist();

                    //Only add it if it's not already part of the wishlist
                    int coursesAdded = 0;
                    for(Course course : checkedCourses){
                        if(!wishlist.contains(course)){
                            wishlist.add(course);
                            coursesAdded ++;
                        }
                    }

                    //Save the courses to the App context
                    App.setCourseWishlist(wishlist);

                    toastMessage = getResources().getString(R.string.wishlist_add, coursesAdded);
                }

                //Visual feedback of what was just done
                Toast.makeText(CoursesListActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    private void loadInfo(){
        mAdapter = new CoursesAdapter(this, mCourses);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    // JDAlfaro
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.refresh, menu);

        // change semester menu item
        menu.add(Menu.NONE, Constants.MENU_ITEM_CHANGE_SEMESTER, Menu.NONE, R.string.schedule_change_semester);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Opens the context menu
            case Constants.MENU_ITEM_CHANGE_SEMESTER:
                Intent intent = new Intent(this, ChangeListActivity.class);
                startActivity(intent);
                //startActivityForResult(intent, CHANGE_SEMESTER_CODE);
                return true;
            case R.id.action_refresh:
                //Start thread to retrieve schedule
                //new ScheduleGetter(mCurrentSemester.getURL()).execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CHANGE_SEMESTER_CODE){
            if(resultCode == RESULT_OK){
                //mCurrentSemester = ((Semester)data.getSerializableExtra(Constants.SEMESTER));

                //Quick Check
                //assert (mCurrentSemester != null);

               // new ScheduleGetter(mCurrentSemester.getURL()).execute();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/
}