package ca.mcgill.mymcgill.activity.courseslist;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseListActivity;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author : Julien
 * Date :  2014-05-26 7:09 PM
 * Shows a list of searchedCourses
 */
public class CoursesListActivity extends BaseListActivity {
    public List<Course> mCourses;
    private ListView mListView;
    private CoursesAdapter mAdapter;
    private boolean mWishlist;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courseslist);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        mWishlist = getIntent().getBooleanExtra(Constants.WISHLIST, true);

        // Views
        mListView = (ListView)findViewById(android.R.id.list);

        //Check if we need to load the wishlist
        if(mWishlist){
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
        wishlist.setText(mWishlist ? getResources().getString(R.string.courses_remove_wishlist) :
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
                else if(mWishlist){
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
                    toastMessage = getResources().getString(R.string.wishlist_add, checkedCourses.size());
                    wishlist.addAll(checkedCourses);

                    //Save the courses to the App context
                    App.setCourseWishlist(wishlist);
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
}