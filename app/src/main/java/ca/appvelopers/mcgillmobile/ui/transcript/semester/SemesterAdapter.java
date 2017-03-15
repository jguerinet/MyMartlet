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

package ca.appvelopers.mcgillmobile.ui.transcript.semester;

import android.content.Context;
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
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse;
import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse_Table;

/**
 * Populates the courses list in SemesterActivity
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SemesterAdapter extends RecyclerViewBaseAdapter {
    /**
     * {@link Term} of the semester we are currently looking at
     */
    private final Term term;
    /**
     * List of courses
     */
    private final List<TranscriptCourse> courses;

    /**
     * Default Constructor
     *
     * @param term {@link Term} of the {@link Semester} we are looking at
     */
    SemesterAdapter(Term term) {
        super(null);
        this.term = term;
        this.courses = new ArrayList<>();
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transcript_course, parent, false));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    @Override
    public void update() {
        courses.clear();

        // Get all of the courses from the DB
        SQLite.select()
                .from(TranscriptCourse.class)
                .where(TranscriptCourse_Table.term.eq(term))
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    if (tResult == null) {
                        return;
                    }
                    courses.addAll(tResult);
                    notifyDataSetChanged();
                })
                .execute();
    }

    class CourseHolder extends BaseHolder {
        /**
         * Course Code
         */
        @BindView(R.id.course_code)
        TextView code;
        /**
         * User grade
         */
        @BindView(R.id.course_grade)
        TextView grade;
        /**
         * Course title
         */
        @BindView(R.id.course_title)
        TextView title;
        /**
         * Course credits
         */
        @BindView(R.id.course_credits)
        TextView credits;
        /**
         * Course average grade
         */
        @BindView(R.id.course_average)
        TextView averageGrade;

        CourseHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(int position) {
            TranscriptCourse course = courses.get(position);
            Context context = itemView.getContext();

            code.setText(course.getCourseCode());
            grade.setText(course.getUserGrade());
            title.setText(course.getCourseTitle());
            credits.setText(context.getString(R.string.course_credits,
                    String.valueOf(course.getCredits())));

            // Don't display average if it does not exist
            if (!course.getAverageGrade().equals("")) {
                averageGrade.setText(context.getString(R.string.course_average,
                        course.getAverageGrade()));
            }
        }
    }
}
