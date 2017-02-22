/*
 * Copyright 2014-2017 Julien Guerinet
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guerinet.utils.RecyclerViewBaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.ui.transcript.semester.SemesterActivity;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Populates the list of semesters on the transcript page
 * @author Julien Guerinet
 * @since 1.0.0
 */
class TranscriptAdapter extends RecyclerViewBaseAdapter {
    /**
     * List of semesters
     */
    private List<Semester> mSemesters;

    /**
     * Default Constructor
     *
     * @param semesters List of semesters
     */
    TranscriptAdapter(List<Semester> semesters) {
        super(null);
        mSemesters = semesters;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SemesterHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semester, parent, false));
    }

    @Override
    public int getItemCount() {
        return mSemesters.size();
    }

    class SemesterHolder extends BaseHolder {
        /**
         * The semester name
         */
        @BindView(R.id.semester_name)
        TextView mName;
        /**
         * The user's GPA for this semester
         */
        @BindView(R.id.semester_gpa)
        TextView mGPA;

        SemesterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            final Semester semester = mSemesters.get(position);
            final Context context = itemView.getContext();

            mName.setText(semester.getSemesterName(context));
            mGPA.setText(context.getString(R.string.transcript_termGPA,
                    String.valueOf(semester.getGPA())));

            //OnClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SemesterActivity.class)
                            .putExtra(Constants.SEMESTER, semester);
                    context.startActivity(intent);
                }
            });

        }
    }
}
