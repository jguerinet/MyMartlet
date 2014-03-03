package ca.mcgill.mymcgill.activity.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.DesktopActivity;
import ca.mcgill.mymcgill.activity.LoginActivity;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.activity.SettingsActivity;
import ca.mcgill.mymcgill.activity.ebill.EbillActivity;
import ca.mcgill.mymcgill.activity.inbox.InboxActivity;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;
import ca.mcgill.mymcgill.object.DrawerItem;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Clear;

/**
 * Author: Shabbir
 * Date: 24/02/14, 11:46 PM
 */
public class DrawerAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<DrawerItem> mDrawerItems;
    private int mSelectedPosition;
    private int mUnreadMessages;
    private TextView mUnreadMessagesView;

    //Easy way to keep track of the list order (for the OnClick)
    public static final int SCHEDULE_POSITION = 0;
    public static final int TRANSCRIPT_POSITION = 1;
    public static final int EMAIL_POSITION = 2;
    public static final int EBILL_POSITION = 3;
    public static final int DESKTOP_POSITION = 4;
    public static final int SETTINGS_POSITION = 5;
    public static final int LOGOUT_POSITION = 6;

    public DrawerAdapter(Activity activity, int selectedPosition){
        this.mActivity = activity;
        this.mDrawerItems = new ArrayList<DrawerItem>();
        this.mSelectedPosition = selectedPosition;
        this.mUnreadMessages = 0;
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

        //Email
        mDrawerItems.add(EMAIL_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_inbox),
                mActivity.getResources().getString(R.string.icon_email)));

        //Ebill
        mDrawerItems.add(EBILL_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_ebill),
                mActivity.getResources().getString(R.string.icon_ebill)));

        //Desktop
        mDrawerItems.add(DESKTOP_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_desktop),
                mActivity.getResources().getString(R.string.icon_desktop)));

        //Settings
        mDrawerItems.add(SETTINGS_POSITION, new DrawerItem(mActivity.getResources().getString(R.string.title_settings),
                mActivity.getResources().getString(R.string.icon_settings)));

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
        icon.setTypeface(ApplicationClass.getIconFont());
        icon.setText(currentItem.getIcon());

        TextView title = (TextView)view.findViewById(R.id.drawerItem_title);
        title.setText(currentItem.getTitle());
        
        TextView badge = (TextView)view.findViewById(R.id.drawer_email_count);
        if(position == EMAIL_POSITION){
            mUnreadMessagesView = badge;
            updateUnreadMessages(mUnreadMessages);
        }
        else{
        	badge.setVisibility(View.INVISIBLE);
        }
        
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
                    case EMAIL_POSITION:
                        mActivity.startActivity(new Intent(mActivity, InboxActivity.class));
                        break;
                    case EBILL_POSITION:
                        mActivity.startActivity(new Intent(mActivity, EbillActivity.class));
                        break;
                    case DESKTOP_POSITION:
                        mActivity.startActivity(new Intent(mActivity, DesktopActivity.class));
                        break;
                    case SETTINGS_POSITION:
                        mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                        break;
                    case LOGOUT_POSITION:
                        Clear.clearAllInfo(mActivity);

                        //Go back to LoginActivity
                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                        break;
                }
                mActivity.finish();
            }
        });

        //If it's the selected position, set it's background to red
        if(position == mSelectedPosition){
            view.setBackgroundColor(mActivity.getResources().getColor(R.color.red));
            view.setClickable(false);
        }

        return view;
    }

    public void updateUnreadMessages(int unreadMessages){
        mUnreadMessages = unreadMessages;

        if(mUnreadMessagesView != null){
            if(unreadMessages == 0){
                mUnreadMessagesView.setVisibility(View.INVISIBLE);
            }
            else{
                mUnreadMessagesView.setVisibility(View.VISIBLE);
                mUnreadMessagesView.setText(String.valueOf(mUnreadMessages));
            }
            notifyDataSetChanged();
        }
    }
}
