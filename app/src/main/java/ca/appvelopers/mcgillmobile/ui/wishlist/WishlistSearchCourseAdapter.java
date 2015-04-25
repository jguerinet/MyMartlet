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

package ca.appvelopers.mcgillmobile.ui.wishlist;

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
import ca.appvelopers.mcgillmobile.model.Term;

public class WishlistSearchCourseAdapter
        extends RecyclerView.Adapter<WishlistSearchCourseAdapter.CourseHolder> {
    private Context mContext;
    private List<Course> mCourses;
    private List<Course> mCheckedCourses;

    public WishlistSearchCourseAdapter(Context context, Term term, List<Course> classItems){
        this.mContext = context;
        this.mCourses = new ArrayList<>();
        this.mCheckedCourses = new ArrayList<>();

        //Add only the courses for this term
        for(Course classItem : classItems){
            if(classItem.getTerm().equals(term)){
                mCourses.add(classItem);
            }
        }
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new CourseHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false));
    }

    @Override
    public void onBindViewHolder(CourseHolder holder, int position){
        holder.bind(mCourses.get(position));
    }

    @Override
    public int getItemCount(){
        return mCourses.size();
    }

    /**
     * @return True if there are no courses to display, false otherwise
     */
    public boolean isEmpty(){
        return mCourses.isEmpty();
    }

    /**
     * @return The list of checked classes
     */
    public List<Course> getCheckedCourses(){
        return mCheckedCourses;
    }

    class CourseHolder extends RecyclerView.ViewHolder {
        /**
         * The course code
         */
        @InjectView(R.id.course_code)
        TextView mCode;
        /**
         * The course credits
         */
        @InjectView(R.id.course_credits)
        TextView mCredits;
        /**
         * The course title
         */
        @InjectView(R.id.course_title)
        TextView mTitle;
        /**
         * The course spots
         */
        @InjectView(R.id.course_spots)
        TextView mSpots;
        /**
         * The course type
         */
        @InjectView(R.id.course_type)
        TextView mType;
        /**
         * The number of remaining waitlist spots
         */
        @InjectView(R.id.course_waitlist)
        TextView mWaitlistRemaining;
        /**
         * The course days
         */
        @InjectView(R.id.course_days)
        TextView mDays;
        /**
         * The course hours
         */
        @InjectView(R.id.course_hours)
        TextView mHours;
        /**
         * The course dates
         */
        @InjectView(R.id.course_dates)
        TextView mDates;
        /**
         * The checkbox to (un)select the course for various operations
         */
        @InjectView(R.id.course_checkbox)
        CheckBox mCheckbox;

        public CourseHolder(View itemView){
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(Course course){
            mCode.setText(course.getCode());
            mCredits.setText(mContext.getString(R.string.course_credits, course.getCredits()));
            mTitle.setText(course.getTitle());
            mSpots.setVisibility(View.VISIBLE);
            mSpots.setText(mContext.getString(R.string.registration_spots,
                    course.getSeatsRemaining()));
            mType.setText(course.getType());
            mWaitlistRemaining.setVisibility(View.VISIBLE);
            mWaitlistRemaining.setText(mContext.getString(R.string.registration_waitlist,
                    course.getWaitlistRemaining()));
            mDays.setText(Day.getDayStrings(course.getDays()));
            mHours.setText(course.getTimeString());
            mDates.setText(course.getDateString());

            mCheckbox.setVisibility(View.VISIBLE);
            //Remove any other listeners
            mCheckbox.setOnCheckedChangeListener(null);
            //Initial state
            mCheckbox.setChecked(mCheckedCourses.contains(course));
            mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
}
