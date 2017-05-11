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

package com.guerinet.mymartlet.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.exception.MinervaException;
import com.guerinet.mymartlet.ui.dialog.DialogHelper;

/**
 * Static help classes
 * @author Julien Guerinet
 * @since 2.4.0
 */
public class Help {

    /**
     * Broadcasts a {@link MinervaException} if necessary
     *
     * @param context App context
     * @param t       Received Throwable
     */
    public static void handleException(Context context, Throwable t) {
        if (t == null) {
            return;
        }

        if (t instanceof MinervaException) {
            // If this is a MinervaException, broadcast it
            LocalBroadcastManager.getInstance(context).sendBroadcast(
                    new Intent(Constants.BROADCAST_MINERVA));
        } else {
            DialogHelper.error(context, R.string.error_other);
        }
    }
}
