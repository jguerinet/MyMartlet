package ca.mcgill.mymcgill.activity.courseslist;

import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseListActivity;
import ca.mcgill.mymcgill.object.Course;
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
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courseslist);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        // Views
        mListView = (ListView)findViewById(android.R.id.list);

        //Check if we need to load the wishlist
        if(getIntent().getBooleanExtra(Constants.WISHLIST, false)){
            //TODO Wishlist Code here
            mCourses = App.getCourseWishlist();
        }
        //If not, parse it
        else{
            mCourses = Constants.courses;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        loadInfo();
    }

    private void loadInfo(){
        CoursesAdapter adapter = new CoursesAdapter(this, mCourses);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}