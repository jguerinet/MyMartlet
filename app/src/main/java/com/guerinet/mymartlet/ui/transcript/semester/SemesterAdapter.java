/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.ui.transcript.semester;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.Semester;
import com.guerinet.mymartlet.model.transcript.TranscriptCourse;
import com.guerinet.mymartlet.model.transcript.TranscriptCourse_Table;
import com.guerinet.suitcase.ui.BaseRecyclerViewAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Populates the courses list in SemesterActivity
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SemesterAdapter extends BaseRecyclerViewAdapter {
    /**
     * Id of the semester we are currently looking at
     */
    private final int semesterId;
    /**
     * List of courses
     */
    private final List<TranscriptCourse> courses;

    /**
     * Default Constructor
     *
     * @param semesterId Id of the {@link Semester} we are currently looking at
     */
    SemesterAdapter(int semesterId) {
        super(null);
        this.semesterId = semesterId;
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

    public void update() {
        courses.clear();

        // Get all of the courses from the DB
        SQLite.select()
                .from(TranscriptCourse.class)
                .where(TranscriptCourse_Table.semesterId.eq(semesterId))
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
        @BindView(R.id.code)
        TextView code;
        /**
         * User grade
         */
        @BindView(R.id.course_grade)
        TextView grade;
        /**
         * Course title
         */
        @BindView(R.id.title)
        TextView title;
        /**
         * Course credits
         */
        @BindView(R.id.credits)
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
