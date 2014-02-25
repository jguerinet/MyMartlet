package ca.mcgill.mymcgill.activity.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.DrawerItem;
import ca.mcgill.mymcgill.util.ApplicationClass;

/**
 * Author: Shabbir
 * Date: 24/02/14, 11:46 PM
 */
public class DrawerAdapter extends BaseAdapter {
    private Context mContext;
    private List<DrawerItem> mDrawerItems;

    public DrawerAdapter(Context context){
        this.mContext = context;
        this.mDrawerItems = new ArrayList<DrawerItem>();
        generateDrawerItems();
    }

    //This will generate the drawer items
    private void generateDrawerItems(){
        //Schedule
        mDrawerItems.add(new DrawerItem(mContext.getResources().getString(R.string.title_schedule),
                mContext.getResources().getString(R.string.icon_schedule)));

        //Transcript
        mDrawerItems.add(new DrawerItem(mContext.getResources().getString(R.string.title_transcript),
                mContext.getResources().getString(R.string.icon_transcript)));

        //Email
        mDrawerItems.add(new DrawerItem(mContext.getResources().getString(R.string.title_inbox),
                mContext.getResources().getString(R.string.icon_email)));

        //Ebill
        mDrawerItems.add(new DrawerItem(mContext.getResources().getString(R.string.title_activity_ebill),
                mContext.getResources().getString(R.string.icon_ebill)));

        //Desktop
        mDrawerItems.add(new DrawerItem(mContext.getResources().getString(R.string.title_desktop),
                mContext.getResources().getString(R.string.icon_desktop)));

        //Settings
        mDrawerItems.add(new DrawerItem(mContext.getResources().getString(R.string.main_settings),
                mContext.getResources().getString(R.string.icon_settings)));

        //Logout
        mDrawerItems.add(new DrawerItem(mContext.getResources().getString(R.string.main_logout),
                mContext.getResources().getString(R.string.icon_logout)));
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        return view;
    }
}
