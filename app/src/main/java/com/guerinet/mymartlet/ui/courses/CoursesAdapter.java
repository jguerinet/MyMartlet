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

package com.guerinet.mymartlet.ui.courses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.Course;
import com.guerinet.mymartlet.model.Course_Table;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.util.DayUtils;
import com.guerinet.utils.RecyclerViewBaseAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Adapter used for the list of {@link Course}s
 * @author Julien Guerinet
 * @since 1.0.0
 */
class CoursesAdapter extends RecyclerViewBaseAdapter {
    /**
     * List of {@link Course}s to show
     */
    private final List<Course> courses;
    /**
     * List of checked {@link Course}s
     */
    private final List<Course> checkedCourses;
    /**
     * True if the user can unregister from these courses, false otherwise
     */
    private boolean canUnregister;

    /**
     * Default Constructor
     *
     * @param emptyView     View to show if there are no {@link Course}s
     */
    CoursesAdapter(TextView emptyView) {
        super(emptyView);
        courses = new ArrayList<>();
        checkedCourses = new ArrayList<>();
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CourseHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_course, viewGroup, false));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void update(Term term, boolean canUnregister) {
        this.canUnregister = canUnregister;
        courses.clear();
        checkedCourses.clear();

        // Get the courses asynchronously
        SQLite.select()
                .from(Course.class)
                .where(Course_Table.term.eq(term))
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    if (tResult == null) {
                        tResult = new ArrayList<>();
                    }
                    courses.addAll(tResult);
                    showEmptyView(courses.isEmpty());
                    notifyDataSetChanged();
                })
                .execute();
    }

    /**
     * @return The list of checked {@link Course}s
     */
    List<Course> getCheckedCourses() {
        return checkedCourses;
    }

    class CourseHolder extends BaseHolder implements View.OnClickListener {
        /**
         * Course code
         */
        @BindView(R.id.course_code)
        TextView code;
        /**
         * Course title
         */
        @BindView(R.id.course_title)
        TextView title;
        /**
         * Course type
         */
        @BindView(R.id.course_type)
        TextView type;
        /**
         * Course credits
         */
        @BindView(R.id.course_credits)
        TextView credits;
        /**
         * Course days
         */
        @BindView(R.id.course_days)
        TextView days;
        /**
         * Course hours
         */
        @BindView(R.id.course_hours)
        TextView hours;
        /**
         * Course unregistration check box
         */
        @BindView(R.id.course_checkbox)
        CheckBox checkBox;

        CourseHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Course course = courses.get(position);
            code.setText(course.getCode());
            title.setText(course.getTitle());
            type.setText(course.getType());
            credits.setText(itemView.getContext().getString(R.string.course_credits,
                    String.valueOf(course.getCredits())));
            days.setText(DayUtils.getDayStrings(course.getDays()));
            hours.setText(course.getTimeString());

            // Show the check box if the user can unregister
            checkBox.setVisibility(canUnregister ? View.VISIBLE : View.GONE);
            // Only set the view selectable if the user can unregister
            itemView.setClickable(canUnregister);
            if (canUnregister) {
                // Remove any other listeners
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(checkedCourses.contains(course));
                checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                    // If it becomes checked, add it to the list. If not, remove it
                    if (checked) {
                        checkedCourses.add(course);
                    } else {
                        checkedCourses.remove(course);
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            checkBox.setChecked(!checkBox.isChecked());
        }
    }
}