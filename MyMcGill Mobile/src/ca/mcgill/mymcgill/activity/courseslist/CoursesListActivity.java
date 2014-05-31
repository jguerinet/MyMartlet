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

        mWishlist = getIntent().getBooleanExtra(Constants.WISHLIST, false);

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
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the checked list of courses from the adapter
                List<Course> checkedCourses = mAdapter.getCheckedCourses();

                //If there are none, display error message
                if(checkedCourses.isEmpty()){
                    Toast.makeText(CoursesListActivity.this, "You need to select at least one course", Toast.LENGTH_SHORT).show();
                    return;
                }

                //If we are in the wishlist, this button is to remove a course
                if(mWishlist){
                    //TODO Remove a course from wishlist here
                }
                //If not, it's to add a course to the wishlist
                else{
                    //TODO Add a course to wishlist here
                }

                //Reload the adapter
                loadInfo();
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