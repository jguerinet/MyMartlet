/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.threeten.bp.LocalDate;

import java.util.List;

import ca.appvelopers.mcgillmobile.model.Course;

/**
 * Displays the user's schedule for a given day
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.Holder> {
    private LocalDate date;
    private List<Course> courses;

    /**
     * Default Constructor
     *
     * @param date Date to represent
     */
    public DayAdapter(LocalDate date, List<Course> courses) {
        this.date = date;
        this.courses = courses;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
