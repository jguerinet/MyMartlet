/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.ui.transcript;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.ui.transcript.semester.SemesterActivity;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Populates the list of semesters on the transcript page
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class TranscriptAdapter extends RecyclerView.Adapter<TranscriptAdapter.SemesterHolder> {
    /**
     * The list of semesters
     */
    private List<Semester> mSemesters;

    /**
     * Default Constructor
     *
     * @param semesters The list of semesters
     */
    public TranscriptAdapter(List<Semester> semesters){
        this.mSemesters = semesters;
    }

    @Override
    public SemesterHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new SemesterHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semester, parent, false));
    }

    @Override
    public void onBindViewHolder(SemesterHolder holder, int position){
        holder.bind(mSemesters.get(position));
    }

    @Override
    public int getItemCount(){
        return mSemesters.size();
    }

    class SemesterHolder extends RecyclerView.ViewHolder{
        /**
         * The semester name
         */
        @InjectView(R.id.semester_name)
        TextView mName;
        /**
         * The user's GPA for this semester
         */
        @InjectView(R.id.semester_gpa)
        TextView mGPA;
        /**
         * The chevron (we need to apply the icon typeface
         */
        @InjectView(R.id.chevron)
        TextView mChevron;

        public SemesterHolder(View itemView){
            super(itemView);
            ButterKnife.inject(this, itemView);
            mChevron.setTypeface(App.getIconFont());
        }

        public void bind(final Semester semester){
            final Context context = itemView.getContext();

            mName.setText(semester.getSemesterName());
            mGPA.setText(context.getString(R.string.transcript_termGPA,
                    String.valueOf(semester.getGPA())));

            //OnClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SemesterActivity.class);
                    intent.putExtra(Constants.SEMESTER, semester);
                    context.startActivity(intent);
                }
            });

        }
    }
}
