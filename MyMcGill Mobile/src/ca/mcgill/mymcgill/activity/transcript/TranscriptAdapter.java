package ca.mcgill.mymcgill.activity.transcript;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Transcript;

/**
 * Author: Julien
 * Date: 31/01/14, 6:06 PM
 */
public class TranscriptAdapter extends BaseAdapter {
    private Context mContext;
    private Transcript mTranscript;

    public TranscriptAdapter(Context context, Transcript transcript){
        this.mContext = context;
        this.mTranscript = transcript;
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
    public View getView(int position, View view, ViewGroup parent) {
        //Reuse the view if it's already been used
        if(view == null){
            //Get the inflater
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_transcript_semesterItem, null);
        }
        return null;
    }
}
