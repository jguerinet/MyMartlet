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
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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
     * List of {@link Semester}s
     */
    private final List<Semester> semesters;

    /**
     * Default Constructor
     */
    TranscriptAdapter() {
        super(null);
        this.semesters = new ArrayList<>();
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SemesterHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semester, parent, false));
    }

    @Override
    public int getItemCount() {
        return semesters.size();
    }

    @Override
    public void update() {
        semesters.clear();
        SQLite.select()
                .from(Semester.class)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    if (tResult == null) {
                        return;
                    }
                    semesters.addAll(tResult);
                    notifyDataSetChanged();
                })
                .execute();
    }

    class SemesterHolder extends BaseHolder {
        /**
         * Semester name
         */
        @BindView(R.id.semester_name)
        TextView name;
        /**
         * User's GPA for this semester
         */
        @BindView(R.id.semester_gpa)
        TextView gpa;

        SemesterHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(int position) {
            Semester semester = semesters.get(position);
            Context context = itemView.getContext();

            name.setText(semester.getSemesterName(context));
            gpa.setText(context.getString(R.string.transcript_termGPA,
                    String.valueOf(semester.getGPA())));

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SemesterActivity.class)
                        .putExtra(Constants.ID, semester.getId());
                context.startActivity(intent);
            });
        }
    }
}
