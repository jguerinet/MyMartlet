package ca.appvelopers.mcgillmobile.activity.walkthrough;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * Author : Julien
 */
public class WalkthroughAdapter extends FragmentPagerAdapter {
    private boolean mEmail;

    public WalkthroughAdapter(FragmentManager fm, boolean email){
        super(fm);
        mEmail = email;
    }

    @Override
    public Fragment getItem(int position){
        return WalkthroughFragment.createInstance(position, mEmail);
    }

    @Override
    public int getCount(){
        return 7;
    }
}
