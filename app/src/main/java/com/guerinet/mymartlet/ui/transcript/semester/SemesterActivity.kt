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

package com.guerinet.mymartlet.ui.transcript.semester

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Semester_Table
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.extensions.errorToast
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_semester.*
import timber.log.Timber

/**
 * Displays information about a semester from the user's transcript
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SemesterActivity : BaseActivity() {

    private lateinit var adapter: SemesterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semester)
        setUpToolbar()
        ga.sendScreen("Transcript - Semester")

        // Try finding the semester
        val semester = SQLite.select()
                .from(Semester::class)
                .where(Semester_Table.id.eq(intent.getIntExtra(Constants.ID, -1)))
                .querySingle()

        if (semester == null) {
            errorToast()
            Timber.e(IllegalArgumentException("Semester was null"))
            finish()
            return
        }

        // Set the title as this current semester
        title = semester.getSemesterName(this)

        // Set the info up
        degreeName.text = semester.bachelor
        program.text = semester.program
        gpa.text = getString(R.string.transcript_termGPA, semester.gpa.toString())
        credits.text = getString(R.string.semester_termCredits, semester.credits.toString())
        fullTime.setText(if (semester.isFullTime)
            R.string.semester_fullTime
        else
            R.string.semester_partTime)

        // Set up the courses list
        adapter = SemesterAdapter(semester.id)
        list.apply {
            list.layoutManager = LinearLayoutManager(this@SemesterActivity)
            list.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.update()
    }
}
