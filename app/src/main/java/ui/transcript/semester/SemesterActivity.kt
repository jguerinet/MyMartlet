/*
 * Copyright 2014-2022 Julien Guerinet
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
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.extensions.assertNotNull
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.mymartlet.viewmodel.SemesterViewModel
import com.guerinet.suitcase.lifecycle.observe
import com.guerinet.suitcase.log.TimberTag
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Displays information about a semester from the user's transcript
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SemesterActivity : BaseActivity(), TimberTag {

    override val tag: String = "SemesterActivity"

    private val semesterViewModel by viewModel<SemesterViewModel>()

    private val adapter: SemesterAdapter by lazy { SemesterAdapter() }

    private val list by getView<RecyclerView>(android.R.id.list)
    private val degreeName by getView<TextView>(R.id.degreeName)
    private val program by getView<TextView>(R.id.program)
    private val gpa by getView<TextView>(R.id.gpa)
    private val credits by getView<TextView>(R.id.credits)
    private val fullTime by getView<TextView>(R.id.fullTime)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_semester)
        setUpToolbar()

        val semesterId = intent.getIntExtra(Constants.ID, -1)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        observe(semesterViewModel.getSemester(semesterId)) {
            // Make sure it's not null before continuing
            assertNotNull(it, "Semester")?.also { semester ->
                // Set the title as this current semester
                title = semester.getName(this)

                degreeName.text = semester.bachelor
                program.text = semester.program
                gpa.text = getString(R.string.transcript_termGPA, semester.gpa.toString())
                credits.text = getString(R.string.semester_termCredits, semester.credits.toString())
                fullTime.setText(
                    if (semester.isFullTime) {
                        R.string.semester_fullTime
                    } else {
                        R.string.semester_partTime
                    }
                )
            }
        }

        observe(semesterViewModel.getTranscriptCourses(semesterId)) {
            adapter.update(it)
        }
    }
}
