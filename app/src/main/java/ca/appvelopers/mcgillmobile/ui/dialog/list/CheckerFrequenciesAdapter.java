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
     * @param checkerPref {@link CheckerPreference} instance this is for
     */
    protected CheckerFrequenciesAdapter(CheckerPreference checkerPref) {
        this.checkerPref = checkerPref;
        frequencies = new ArrayList<>();
        addFrequency(CheckerPreference.NEVER);
        addFrequency(CheckerPreference.WEEKLY);
        addFrequency(CheckerPreference.DAILY);
        addFrequency(CheckerPreference.TWELVE_HOURS);
        addFrequency(CheckerPreference.SIX_HOURS);
        addFrequency(CheckerPreference.HOURLY);
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
     * @param frequency {@link CheckerPreference.Frequency} to add
     */
    private void addFrequency(@CheckerPreference.Frequency String frequency) {
        String title;
        // TODO Strings
        switch (frequency) {
            case CheckerPreference.NEVER:
                title = "Never";
                break;
            case CheckerPreference.WEEKLY:
                title = "Weekly";
                break;
            case CheckerPreference.DAILY:
                title = "Daily";
                break;
            case CheckerPreference.TWELVE_HOURS:
                title = "Every 12 hours";
                break;
            case CheckerPreference.SIX_HOURS:
                title = "Every 6 hours";
                break;
            case CheckerPreference.HOURLY:
                title = "Hourly";
                break;
            default:
                throw new IllegalArgumentException("Unknown frequency: " + frequency);
        }
        frequencies.add(new Pair<>(frequency, title));
    }

    /**
     * Called when a homepage has been selected
     *
     * @param homepage The selected {@link CheckerPreference.Frequency}
     */
    public abstract void onFrequencySelected(@CheckerPreference.Frequency String homepage);
}
