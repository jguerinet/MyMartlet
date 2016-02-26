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

package ca.appvelopers.mcgillmobile.util.manager;

import android.content.Context;

import com.guerinet.utils.StorageUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.Transcript;

/**
 * Entirely manages the {@link Transcript} lifecycle
 * @author Julien Guerinet
 * @since 2.2.0
 */
@Singleton
public class TranscriptManager {
    /**
     * {@link Context} instance
     */
    private final Context context;
    /**
     * {@link Transcript} instance
     */
    private Transcript transcript;

    /**
     * Default Injectable Constructor
     *
     * @param context App Context
     */
    @Inject
    protected TranscriptManager(Context context) {
        this.context = context;
        //Do not lazy load the transcript because it can be null, load it upfront
        transcript = (Transcript) StorageUtils.loadObject(context, "transcript", "Transcript");
    }

    /**
     * @return {@link Transcript} instance
     */
    public synchronized Transcript get() {
        return transcript;
    }

    /**
     * @param transcript {@link Transcript} instance to save
     */
    public synchronized void set(Transcript transcript) {
        //Don't save a null transcript
        if (transcript == null) {
            return;
        }
        //Set the local instance
        this.transcript = transcript;
        //Save it to internal storage
        StorageUtils.saveObject(context, transcript, "transcript", "Transcript");
    }

    /**
     * Clears the stored {@link Transcript}
     */
    public synchronized void clear() {
        //Clear both the local instance and the stored one
        transcript = null;
        context.deleteFile("transcript");
    }
}
