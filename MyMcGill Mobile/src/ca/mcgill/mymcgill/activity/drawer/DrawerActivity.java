package ca.mcgill.mymcgill.activity.drawer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.DesktopActivity;
import ca.mcgill.mymcgill.activity.MyCoursesActivity;
import ca.mcgill.mymcgill.activity.SettingsActivity;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.activity.ebill.EbillActivity;
import ca.mcgill.mymcgill.activity.inbox.InboxActivity;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;

public class DrawerActivity extends BaseActivity {

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerAdapter mDrawerAdapter;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        assert (getActionBar() != null);

        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Set up the drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0);
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Set up the adapter
        if(this instanceof TranscriptActivity){
            mDrawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.TRANSCRIPT_POSITION);
        }
        else if(this instanceof InboxActivity){
            mDrawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.EMAIL_POSITION);
        }
        else if(this instanceof EbillActivity){
            mDrawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.EBILL_POSITION);
        }
        else if(this instanceof DesktopActivity){
            mDrawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.DESKTOP_POSITION);
        }
        else if(this instanceof SettingsActivity){
            mDrawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.SETTINGS_POSITION);
        }
        else if(this instanceof MyCoursesActivity){
            mDrawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.MYCOURSES_POSITION);
        }
        else{
            Log.e("Drawer Adapter", "not well initialized");
            mDrawerAdapter = new DrawerAdapter(this, drawerLayout, -1);
        }

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(mDrawerAdapter);
    }

    public void updateUnreadMessages(){
        if(mDrawerAdapter != null){
            mDrawerAdapter.updateUnreadMessages();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
