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

package ca.appvelopers.mcgillmobile.ui.dialog.list;

import android.content.Context;
import android.util.Pair;

import com.guerinet.utils.dialog.ListDialogInterface;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.util.dagger.prefs.CheckerPreference;


/**
 * Displays the available frequencies the checkers can run at
 * @author Julien Guerinet
 * @since 2.4.0
 */
@SuppressWarnings("ResourceType")
public abstract class CheckerFrequenciesAdapter implements ListDialogInterface {
    /**
     * {@link CheckerPreference} instance this is for
     */
    private final CheckerPreference checkerPref;
    /**
     * List of frequencies
     */
    private final List<Pair<String, String>> frequencies;

    /**
     * Default Constructor
     *
     * @param context     App context
     * @param checkerPref {@link CheckerPreference} instance this is for
     */
    protected CheckerFrequenciesAdapter(Context context, CheckerPreference checkerPref) {
        this.checkerPref = checkerPref;
        frequencies = new ArrayList<>();
        addFrequency(context, CheckerPreference.NEVER);
        addFrequency(context, CheckerPreference.WEEKLY);
        addFrequency(context, CheckerPreference.DAILY);
        addFrequency(context, CheckerPreference.TWELVE_HOURS);
        addFrequency(context, CheckerPreference.SIX_HOURS);
        addFrequency(context, CheckerPreference.HOURLY);
    }

    @Override
    public int getCurrentChoice() {
        for (int i = 0; i < frequencies.size(); i ++) {
            if (frequencies.get(i).first.equals(checkerPref.get())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public CharSequence[] getChoices() {
        CharSequence[] titles = new CharSequence[frequencies.size()];
        for (int i = 0; i < frequencies.size(); i ++) {
            titles[i] = frequencies.get(i).second;
        }
        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
        onFrequencySelected(frequencies.get(position).first);
    }

    /**
     * @param context   App context
     * @param frequency {@link CheckerPreference.Frequency} to add
     */
    private void addFrequency(Context context, @CheckerPreference.Frequency String frequency) {
        frequencies.add(new Pair<>(frequency,
                CheckerPreference.getFrequencyString(context, frequency)));
    }

    /**
     * Called when a homepage has been selected
     *
     * @param homepage The selected {@link CheckerPreference.Frequency}
     */
    public abstract void onFrequencySelected(@CheckerPreference.Frequency String homepage);
}
