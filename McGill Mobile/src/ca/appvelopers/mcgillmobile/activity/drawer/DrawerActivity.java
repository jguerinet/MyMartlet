package ca.appvelopers.mcgillmobile.activity.drawer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.DesktopActivity;
import ca.appvelopers.mcgillmobile.activity.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.activity.RegistrationActivity;
import ca.appvelopers.mcgillmobile.activity.SettingsActivity;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.activity.courseslist.CoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.ebill.EbillActivity;
import ca.appvelopers.mcgillmobile.activity.mycourseslist.MyCoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.transcript.TranscriptActivity;

public class DrawerActivity extends BaseActivity {

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        assert (getActionBar() != null);

        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setFocusableInTouchMode(false);

        //Set up the drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0);
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Set up the adapter
        DrawerAdapter drawerAdapter;
        if(this instanceof TranscriptActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.TRANSCRIPT_POSITION);
        }
        else if(this instanceof RegistrationActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.SEARCH_COURSES_POSITION);
        }
        else if(this instanceof EbillActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.EBILL_POSITION);
        }
        else if(this instanceof DesktopActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.DESKTOP_POSITION);
        }
        else if(this instanceof SettingsActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.SETTINGS_POSITION);
        }
        else if(this instanceof MyCoursesActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.MYCOURSES_POSITION);
        }
        else if(this instanceof CoursesListActivity){
            //Wishlist
            if(((CoursesListActivity)this).wishlist){
                drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.WISHLIST_POSITION);
            }
            //Course search
            else{
                drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.SEARCH_COURSES_POSITION);
            }
        }
        else if(this instanceof MyCoursesListActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.COURSES_POSITION);
        }
        else{
            Log.e("Drawer Adapter", "not well initialized");
            drawerAdapter = new DrawerAdapter(this, drawerLayout, -1);
        }

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(drawerAdapter);
    }

    @Override
    public void onBackPressed(){
        //Open the menu if it is not open
        if(!drawerLayout.isDrawerOpen(drawerList)){
            drawerLayout.openDrawer(drawerList);
        }
        //If it is open, ask the user if he wants to exit
        else{
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.drawer_exit))
                    .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DrawerActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
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
