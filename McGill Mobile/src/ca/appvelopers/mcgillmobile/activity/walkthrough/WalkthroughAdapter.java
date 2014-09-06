package ca.appvelopers.mcgillmobile.activity.walkthrough;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * Author : Julien
 */
public class WalkthroughAdapter extends FragmentPagerAdapter {
    public WalkthroughAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        return WalkthroughFragment.createInstance(position);
    }

    @Override
    public int getCount(){
        return 7;
    }
}
