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

package ca.appvelopers.mcgillmobile.ui.courses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Day;

/**
 * The adapter used for the list of courses
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseHolder> {
    /**
     * The application context
     */
    private Context mContext;
    /**
     * The list of courses
     */
    private List<Course> mCourses;
    /**
     * The list of checked courses
     */
    private List<Course> mCheckedCourses;
    /**
     * True if the user can unregister from these courses, false otherwise
     */
    private boolean mCanUnregister;

    /**
     * Default Constructor
     *
     * @param context       The app context
     * @param courses       The list of courses
     * @param canUnregister True if the user can unregister from these courses, false otherwise
     */
    public CoursesAdapter(Context context, List<Course> courses, boolean canUnregister){
        this.mContext = context;
        this.mCourses = courses;
        this.mCheckedCourses = new ArrayList<>();
        this.mCanUnregister = canUnregister;
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        return new CourseHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_course, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(CourseHolder courseHolder, int i){
        courseHolder.bindCourse(mCourses.get(i));
    }

    @Override
    public int getItemCount(){
        return mCourses.size();
    }

    /**
     * @return The list of checked classes
     */
    public List<Course> getCheckedCourses(){
        return this.mCheckedCourses;
    }

    class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        /**
         * The course code
         */
        @InjectView(R.id.course_code)
        private TextView mCode;
        /**
         * The course title
         */
        @InjectView(R.id.course_title)
        private TextView mTitle;
        /**
         * The course type
         */
        @InjectView(R.id.course_type)
        private TextView mType;
        /**
         * The course credits
         */
        @InjectView(R.id.course_credits)
        private TextView mCredits;
        /**
         * The course days
         */
        @InjectView(R.id.course_days)
        private TextView mDays;
        /**
         * The course hours
         */
        @InjectView(R.id.course_hours)
        private TextView mHours;
        /**
         * The course unregistration check box
         */
        @InjectView(R.id.course_checkbox)
        private CheckBox mCheckBox;

        /**
         * Default Constructor
         *
         * @param itemView The item view
         */
        public CourseHolder(View itemView){
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        /**
         * Binds the views to the given course
         *
         * @param course The course
         */
        public void bindCourse(final Course course){
            mCode.setText(course.getCode());
            mTitle.setText(course.getTitle());
            mType.setText(course.getType());
            mCredits.setText(mContext.getString(R.string.course_credits, course.getCredits()));
            mDays.setText(Day.getDayStrings(course.getDays()));
            mHours.setText(course.getTimeString());

            //Show the check box if the user can unregister
            mCheckBox.setVisibility(mCanUnregister ? View.VISIBLE : View.GONE);
            //Only set the view selectable if the user can unregister
            itemView.setClickable(mCanUnregister);
            if(mCanUnregister){
                //Remove any other listeners
                mCheckBox.setOnCheckedChangeListener(null);
                mCheckBox.setChecked(mCheckedCourses.contains(course));
                mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked){
                        //If it becomes checked, add it to the list. If not, remove it
                        if(checked){
                            mCheckedCourses.add(course);
                        }
                        else{
                            mCheckedCourses.remove(course);
                        }
                    }
                });
            }
        }

        @Override
        public void onClick(View v){
            mCheckBox.setChecked(!mCheckBox.isChecked());
        }
    }
}
