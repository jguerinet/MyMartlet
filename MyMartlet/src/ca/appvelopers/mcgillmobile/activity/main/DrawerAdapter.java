package ca.appvelopers.mcgillmobile.activity.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.DrawerItem;

/**
 * Author: Shabbir
 * Date: 24/02/14, 11:46 PM
 */
public class DrawerAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<DrawerItem> mDrawerItems;

    public DrawerAdapter(Activity activity){
        this.mActivity = activity;
        this.mDrawerItems = new ArrayList<DrawerItem>();
        this.mDrawerItems = Arrays.asList(DrawerItem.values());
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
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            view = inflater.inflate(R.layout.item_drawer, null);
        }

        //Get the current object
        DrawerItem currentItem = mDrawerItems.get(position);

        //Set the info up
        TextView icon = (TextView)view.findViewById(R.id.drawerItem_icon);
        icon.setTypeface(App.getIconFont());
        icon.setText(currentItem.getIcon(mActivity));

        TextView title = (TextView)view.findViewById(R.id.drawerItem_title);
        title.setText(currentItem.getTitle(mActivity));

        return view;
    }
}
