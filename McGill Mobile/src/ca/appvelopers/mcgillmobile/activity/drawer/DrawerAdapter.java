package ca.appvelopers.mcgillmobile.activity.drawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.DesktopActivity;
import ca.appvelopers.mcgillmobile.activity.LoginActivity;
import ca.appvelopers.mcgillmobile.activity.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.activity.RegistrationActivity;
import ca.appvelopers.mcgillmobile.activity.ScheduleActivity;
import ca.appvelopers.mcgillmobile.activity.SettingsActivity;
import ca.appvelopers.mcgillmobile.activity.courseslist.CoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.ebill.EbillActivity;
import ca.appvelopers.mcgillmobile.activity.map.MapActivity;
import ca.appvelopers.mcgillmobile.activity.mycourseslist.MyCoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.transcript.TranscriptActivity;
import ca.appvelopers.mcgillmobile.object.DrawerItem;
import ca.appvelopers.mcgillmobile.util.Clear;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;

/**
 * Author: Shabbir
 * Date: 24/02/14, 11:46 PM
 */
public class DrawerAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<DrawerItem> mDrawerItems;
    private int mSelectedPosition;
    private DrawerLayout mDrawerLayout;

    //Easy way to keep track of the list order (for the OnClick)
    public static final int SCHEDULE_POSITION = 0;
    public static final int TRANSCRIPT_POSITION = SCHEDULE_POSITION + 1;
    public static final int MYCOURSES_POSITION = TRANSCRIPT_POSITION + 1;
    public static final int COURSES_POSITION = MYCOURSES_POSITION + 1;
    public static final int WISHLIST_POSITION = COURSES_POSITION + 1;
    public static final int SEARCH_COURSES_POSITION = WISHLIST_POSITION + 1;
    public static final int EBILL_POSITION = SEARCH_COURSES_POSITION + 1;
    public static final int MAP_POSITION = EBILL_POSITION + 1;
    public static final int DESKTOP_POSITION = MAP_POSITION + 1;
    public static final int SETTINGS_POSITION = DESKTOP_POSITION + 1;
    public static final int FACEBOOK_POSITION = SETTINGS_POSITION + 1;
    public static final int TWITTER_POSITION = FACEBOOK_POSITION+ 1;
    public static final int LOGOUT_POSITION = TWITTER_POSITION + 1;

    public DrawerAdapter(Activity activity, DrawerLayout drawerLayout, int selectedPosition){
        this.mActivity = activity;
        this.mDrawerLayout = drawerLayout;
        this.mDrawerItems = new ArrayList<DrawerItem>();
        this.mSelectedPosition = selectedPosition;
        generateDrawerItems();
    }

    //This will generate the drawer items
    private void generateDrawerItems(){
        //Schedule
        mDrawerItems.add(SCHEDULE_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_schedule),
                mActivity.getResources().getString(R.string.icon_schedule)));

        //Transcript
        mDrawerItems.add(TRANSCRIPT_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_transcript),
                mActivity.getResources().getString(R.string.icon_transcript)));

        //MyCourses
        mDrawerItems.add(MYCOURSES_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_mycourses),
                mActivity.getResources().getString(R.string.icon_mycourses)));

        //Courses
        mDrawerItems.add(COURSES_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_courses),
                mActivity.getResources().getString(R.string.icon_courses)));

        //Wishlist
        mDrawerItems.add(WISHLIST_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_wishlist),
                mActivity.getResources().getString(R.string.icon_star)));

        //Search Courses
        mDrawerItems.add(SEARCH_COURSES_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_registration),
                mActivity.getResources().getString(R.string.icon_search)));

        //Ebill
        mDrawerItems.add(EBILL_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_ebill),
                mActivity.getResources().getString(R.string.icon_ebill)));

        //Map
        mDrawerItems.add(MAP_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_map),
                mActivity.getResources().getString(R.string.icon_map)));

        //Desktop
        mDrawerItems.add(DESKTOP_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_desktop),
                mActivity.getResources().getString(R.string.icon_desktop)));

        //Settings
        mDrawerItems.add(SETTINGS_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_settings),
                mActivity.getResources().getString(R.string.icon_settings)));

        //Facebook
        mDrawerItems.add(FACEBOOK_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_facebook),
                mActivity.getResources().getString(R.string.icon_facebook)));

        //Twitter
        mDrawerItems.add(TWITTER_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_twitter),
                mActivity.getResources().getString(R.string.icon_twitter)));

        //Logout
        mDrawerItems.add(LOGOUT_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_logout),
                mActivity.getResources().getString(R.string.icon_logout)));
    }

    @Override
    public int getCount() {
        return mDrawerItems.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return mDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_drawer, null);
        }

        //Quick Check
        assert(view != null);

        //Get the current object
        DrawerItem currentItem = mDrawerItems.get(position);

        //Set the info up
        TextView icon = (TextView)view.findViewById(R.id.drawerItem_icon);
        icon.setTypeface(App.getIconFont());
        icon.setText(currentItem.getIcon());

        TextView title = (TextView)view.findViewById(R.id.drawerItem_title);
        title.setText(currentItem.getTitle());
        
        //OnClick
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(position){
                    case SCHEDULE_POSITION:
                        mActivity.startActivity(new Intent(mActivity, ScheduleActivity.class));
                        break;
                    case TRANSCRIPT_POSITION:
                        mActivity.startActivity(new Intent(mActivity, TranscriptActivity.class));
                        break;
                    case MYCOURSES_POSITION:
                        mActivity.startActivity(new Intent(mActivity, MyCoursesActivity.class));
                        break;
                    case COURSES_POSITION:
                        mActivity.startActivity(new Intent(mActivity, MyCoursesListActivity.class));
                        break;
                    case SEARCH_COURSES_POSITION:
                        mActivity.startActivity(new Intent(mActivity, RegistrationActivity.class));
                        break;
                    case WISHLIST_POSITION:
                        mActivity.startActivity(new Intent(mActivity, CoursesListActivity.class));
                        break;
                    case EBILL_POSITION:
                        mActivity.startActivity(new Intent(mActivity, EbillActivity.class));
                        break;
                    case MAP_POSITION:
                        mActivity.startActivity(new Intent(mActivity, MapActivity.class));
                        break;
                    case DESKTOP_POSITION:
                        mActivity.startActivity(new Intent(mActivity, DesktopActivity.class));
                        break;
                    case SETTINGS_POSITION:
                        mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                        break;
                    case FACEBOOK_POSITION:
                        //TODO
                        break;
                    case TWITTER_POSITION:
                        //TODO;
                        break;
                    case LOGOUT_POSITION:
                        new AlertDialog.Builder(mActivity)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(mActivity.getResources().getString(R.string.logout_dialog_title))
                                .setMessage(mActivity.getResources().getString(R.string.logout_dialog_message))
                                .setPositiveButton(mActivity.getResources().getString(R.string.logout_dialog_positive), new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GoogleAnalytics.sendEvent(mActivity, "Logout", "Clicked", null, null);
                                        Clear.clearAllInfo(mActivity);
                                        //Go back to LoginActivity
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }

                                })
                                .setNegativeButton(mActivity.getResources().getString(R.string.logout_dialog_negative), null)
                                .create()
                                .show();
                        break;
                }
            }
        });

        //If it's the selected position, set its background to red and the text to white
        if(position == mSelectedPosition){
            view.setBackgroundColor(mActivity.getResources().getColor(R.color.red));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            });

            icon.setTextColor(mActivity.getResources().getColor(R.color.white));
            title.setTextColor(mActivity.getResources().getColor(R.color.white));
        }
        //If not, set its background to white and the text to black
        else{
            view.setBackgroundResource(R.drawable.drawerblack_darkredpressed);
            icon.setTextColor(mActivity.getResources().getColorStateList(R.color.black_whitepressed));
            title.setTextColor(mActivity.getResources().getColorStateList(R.color.black_whitepressed));
        }

        return view;
    }
}
