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

package com.guerinet.mymartlet.ui.wishlist;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.CourseResult;
import com.guerinet.mymartlet.model.CourseResult_Table;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.util.DayUtils;
import com.guerinet.utils.RecyclerViewBaseAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Displays the list of courses in the user's wish list or after a course search
 * @author Julien Guerinet
 * @since 1.0.0
 */
class WishlistAdapter extends RecyclerViewBaseAdapter {
    /**
     * List of {@link CourseResult}s currently being shown
     */
    private final List<CourseResult> courses;
    /**
     * List of checked {@link CourseResult}s
     */
    private final List<CourseResult> checkedCourses;
    /**
     * Empty view if there are no courses to show
     */
    private final TextView emptyView;

    /**
     * Default Constructor
     */
    WishlistAdapter(TextView emptyView) {
        super(emptyView);
        this.emptyView = emptyView;
        courses = new ArrayList<>();
        checkedCourses = new ArrayList<>();
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    /**
     * @param courses List of {@link CourseResult}s to display
     */
    public void update(List<CourseResult> courses) {
        this.courses.clear();
        this.courses.addAll(courses);
        showEmptyView(courses.isEmpty());
    }

    /**
     * @param term Term the wishlist courses should be in, null if no wishlist semesters available
     */
    public void update(@Nullable Term term) {
        courses.clear();

        if (term == null) {
            // Hide all of the main content and show explanatory text if the term is null
            emptyView.setText(R.string.registration_no_semesters);
            showEmptyView(true);
            notifyDataSetChanged();
            return;
        }

        // Get the wishlist
        SQLite.select()
                .from(CourseResult.class)
                .where(CourseResult_Table.term.eq(term))
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    if (tResult == null) {
                        tResult = new ArrayList<>();
                    }
                    // Add the courses and update the view
                    courses.addAll(tResult);
                    showEmptyView(courses.isEmpty());
                    notifyDataSetChanged();
                })
                .execute();
    }

    /**
     * @return The list of checked {@link CourseResult}s
     */
    List<CourseResult> getCheckedCourses() {
        return checkedCourses;
    }

    class CourseHolder extends BaseHolder {
        /**
         * Course code
         */
        @BindView(R.id.course_code)
        TextView code;
        /**
         * Course credits
         */
        @BindView(R.id.course_credits)
        TextView credits;
        /**
         * Course title
         */
        @BindView(R.id.course_title)
        TextView title;
        /**
         * Course spots
         */
        @BindView(R.id.course_spots)
        TextView spots;
        /**
         * Course type
         */
        @BindView(R.id.course_type)
        TextView type;
        /**
         * Number of remaining waitlist spots
         */
        @BindView(R.id.course_waitlist)
        TextView waitlistRemaining;
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
         * Course dates
         */
        @BindView(R.id.course_dates)
        TextView dates;
        /**
         * Checkbox to (un)select the course for various operations
         */
        @BindView(R.id.course_checkbox)
        CheckBox checkBox;

        CourseHolder(View itemView) {
            super(itemView);
        }

        public void bind(int position) {
            CourseResult course = courses.get(position);
            Context context = itemView.getContext();

            code.setText(course.getCode());
            credits.setText(context.getString(R.string.course_credits, String.valueOf(
                    course.getCredits())));
            title.setText(course.getTitle());
            spots.setVisibility(View.VISIBLE);
            spots.setText(context.getString(R.string.registration_spots, String.valueOf(
                    course.getSeatsRemaining())));
            type.setText(course.getType());
            waitlistRemaining.setVisibility(View.VISIBLE);
            waitlistRemaining.setText(context.getString(R.string.registration_waitlist,
                    String.valueOf(course.getWaitlistRemaining())));
            days.setText(DayUtils.getDayStrings(course.getDays()));
            hours.setText(course.getTimeString());
            dates.setText(course.getDateString());

            checkBox.setVisibility(View.VISIBLE);

            // Remove any other listeners
            checkBox.setOnCheckedChangeListener(null);
            // Initial state
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
}
