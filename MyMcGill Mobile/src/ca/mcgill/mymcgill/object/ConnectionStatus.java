package ca.mcgill.mymcgill.object;

import android.content.Context;

import ca.mcgill.mymcgill.R;

/**
 * Author: Julien
 * Date: 09/02/14, 3:02 PM
 */
public enum ConnectionStatus {
    CONNECTION_OK,
    CONNECTION_WRONG_INFO,
    CONNECTION_OTHER,
    CONNECTION_NO_INTERNET,
    CONNECTION_MINERVA_LOGOUT,
    CONNECTION_AUTHENTICATING,
    CONNECTION_FIRSTACCESS;

    public String getErrorString(Context context){
        switch(this){
            case CONNECTION_OK:
                return null;
            case CONNECTION_WRONG_INFO:
                return context.getResources().getString(R.string.login_error_wrong_data);
            case CONNECTION_NO_INTERNET:
                return context.getResources().getString(R.string.login_error_no_internet);
            default:
                return context.getResources().getString(R.string.login_error_other);
        }
    }
}
