package ca.mcgill.mymcgill.util;

import android.content.Context;

import ca.mcgill.mymcgill.R;

/**
 * Author: Julien
 * Date: 22/01/14, 8:09 PM
 * This package will hold the logic for logging someone in to MyMcGill
 */
public class Connection {

    public static int connect(Context context, String username, String password){
        String fullUsername = username + context.getResources().getString(R.string.login_email);

        //TODO Put logic to connect right here

        return Constants.CONNECTION_OK;
    }
}
