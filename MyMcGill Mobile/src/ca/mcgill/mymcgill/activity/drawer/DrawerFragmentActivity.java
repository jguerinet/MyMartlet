package ca.mcgill.mymcgill.activity.drawer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Inbox;
import ca.mcgill.mymcgill.util.ApplicationClass;

public class DrawerFragmentActivity extends FragmentActivity{

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public DrawerAdapter mDrawerAdapter;
    private ActionBarDrawerToggle drawerToggle;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        showDrawer(true);
    }

    public void showDrawer(boolean showDrawer){
        assert (getActionBar() != null);

        if(showDrawer){
            // R.id.drawer_layout should be in every activity with exactly the same id.
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            //Set up the drawer toggle
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0);

            drawerLayout.setDrawerListener(drawerToggle);

            //Make Sure the Adapter is not null
            if(mDrawerAdapter == null){
                mDrawerAdapter = new DrawerAdapter(this, -1);
                Log.e("Drawer", "Drawer Adapter was null");
            }

            //Show the number of unread emails initially stored on the phone
            final Inbox inbox = ApplicationClass.getInbox();
            if(inbox != null){
                mDrawerAdapter.updateUnreadMessages(inbox.getNumNewEmails());
                //Update the number of emails in a separate thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        inbox.retrieveEmail();
                        DrawerFragmentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDrawerAdapter.updateUnreadMessages(inbox.getNumNewEmails());
                            }
                        });
                    }
                }).start();
            }

            drawerList = (ListView) findViewById(R.id.left_drawer);
            drawerList.setAdapter(mDrawerAdapter);
        }

        drawerToggle.setDrawerIndicatorEnabled(showDrawer);
        getActionBar().setDisplayHomeAsUpEnabled(showDrawer);
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
