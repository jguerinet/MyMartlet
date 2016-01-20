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

package ca.appvelopers.mcgillmobile.ui.dialog.list;

/**
 * Helper class for creating list dialogs. Must be implemented by subclasses
 * @author Julien Guerinet
 * @since 2.0.0
 */
public interface DialogListAdapter {

    /**
     * @return The position of the current choice
     */
    int getCurrentChoice();

    /**
     * @return The list of Strings to display in the dialog
     */
    CharSequence[] getTitles();

    /**
     * Called when the user has chosen an option
     *
     * @param position The position of the selected choice
     */
    void onChoiceSelected(int position);
}
