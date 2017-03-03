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

package ca.appvelopers.mcgillmobile.ui.courses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.guerinet.utils.RecyclerViewBaseAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Course_Table;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.util.DayUtils;

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
    private final boolean canUnregister;

    /**
     * Default Constructor
     *
     * @param emptyView     View to show if there are no {@link Course}s
     * @param term          {@link Term} we are currently looking at
     * @param canUnregister True if the user can unregister from these courses, false otherwise
     */
    CoursesAdapter(TextView emptyView, Term term, boolean canUnregister) {
        super(emptyView);
        this.canUnregister = canUnregister;
        courses = new ArrayList<>();
        checkedCourses = new ArrayList<>();

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
                    update();
                })
                .execute();
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

    @Override
    public void update() {
        showEmptyView(courses.isEmpty());
        notifyDataSetChanged();
    }

    /**
     * @return The list of checked {@link Course}s
     */
    List<Course> getCheckedCourses() {
        return checkedCourses;
    }

    class CourseHolder extends BaseHolder implements View.OnClickListener {
        /**
         * The course code
         */
        @BindView(R.id.course_code)
        TextView code;
        /**
         * The course title
         */
        @BindView(R.id.course_title)
        TextView title;
        /**
         * The course type
         */
        @BindView(R.id.course_type)
        TextView type;
        /**
         * The course credits
         */
        @BindView(R.id.course_credits)
        TextView credits;
        /**
         * The course days
         */
        @BindView(R.id.course_days)
        TextView days;
        /**
         * The course hours
         */
        @BindView(R.id.course_hours)
        TextView hours;
        /**
         * The course unregistration check box
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
            credits.setText(itemView.getContext().getString(
                    R.string.course_credits, course.getCredits()));
            days.setText(DayUtils.getDayStrings(course.getDays()));
            hours.setText(course.getTimeString());

            //Show the check box if the user can unregister
            checkBox.setVisibility(canUnregister ? View.VISIBLE : View.GONE);
            //Only set the view selectable if the user can unregister
            itemView.setClickable(canUnregister);
            if(canUnregister){
                //Remove any other listeners
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(checkedCourses.contains(course));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked){
                        //If it becomes checked, add it to the list. If not, remove it
                        if(checked){
                            checkedCourses.add(course);
                        }
                        else{
                            checkedCourses.remove(course);
                        }
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
