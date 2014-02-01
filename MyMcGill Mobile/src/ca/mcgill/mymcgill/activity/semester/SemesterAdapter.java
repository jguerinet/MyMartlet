package ca.mcgill.mymcgill.activity.semester;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * List Adapter that will populate the courses list in SemesterActivity
 * Author: Julien
 * Date: 01/02/14, 10:27 AM
 */
public class SemesterAdapter extends BaseAdapter {
    private Context mContext;

    public SemesterAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
