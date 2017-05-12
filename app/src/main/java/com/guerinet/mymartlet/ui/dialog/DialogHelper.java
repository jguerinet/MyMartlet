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

package com.guerinet.mymartlet.ui.dialog;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.guerinet.mymartlet.R;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

/**
 * Helper methods that create dialogs for various situations
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class DialogHelper {

    /**
     * Displays a toast with a generic error message
     *
     * @param context App context
     */
    public static void error(Context context) {
        Utils.toast(context, R.string.error_other);
    }

    /**
     * Shows an error {@link AlertDialog} with one button
     *
     * @param context   App context
     * @param messageId String Id of the error description
     */
    public static void error(Context context, @StringRes int messageId) {
        DialogUtils.neutral(context, R.string.error, messageId);
    }

    /**
     * Shows an error {@link AlertDialog} with one button
     *
     * @param context App context
     * @param message Message String
     */
    public static void error(Context context, String message) {
        DialogUtils.neutral(context, R.string.error, message);
    }
}