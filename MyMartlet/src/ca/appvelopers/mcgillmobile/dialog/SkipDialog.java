package ca.appvelopers.mcgillmobile.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;

/**
 * Author: Julien Guerinet
 * Date: 2014-09-23 9:45 AM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class SkipDialog{
    private AlertDialog mDialog;
    private Activity mActivity;
    private boolean mSkip;

    public SkipDialog(Activity activity){
        mActivity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        mSkip = true;

        View checkboxLayout = View.inflate(mActivity, R.layout.dialog_checkbox, null);
        final CheckBox doNotShow = (CheckBox) checkboxLayout.findViewById(R.id.skip);


        builder.setCancelable(false);
        builder.setView(checkboxLayout);
        builder.setTitle(mActivity.getString(R.string.warning));
        builder.setMessage(mActivity.getString(R.string.skip_loading));
        builder.setPositiveButton(mActivity.getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Save the do not show option
                Save.saveLoadingDoNotShow(mActivity, doNotShow.isChecked());

                mSkip = true;

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(mActivity.getString(android.R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Save the do not show option
                Save.saveLoadingDoNotShow(mActivity, doNotShow.isChecked());

                mSkip = false;

                dialog.dismiss();
            }
        });
        mDialog = builder.create();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener){
        mDialog.setOnDismissListener(onDismissListener);
    }

    public boolean show(){
        if(!Load.loadLoadingDoNotShow(mActivity)){
           mDialog.show();
            return true;
        }
        return false;
    }

    public boolean skip(){
        return mSkip;
    }
}