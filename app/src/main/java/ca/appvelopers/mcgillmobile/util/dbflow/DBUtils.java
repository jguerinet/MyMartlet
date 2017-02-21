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

package ca.appvelopers.mcgillmobile.util.dbflow;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import java.util.List;

/**
 * Static methods to help with DB management
 * @author Julien Guerinet
 * @since 2.4.0
 */
public class DBUtils {

    /**
     * Completely wipes a DB and adds all of the new objects to it
     *
     * @param context    App context
     * @param dbName     Name of the DB
     * @param type       Type of the class
     * @param newObjects List of the new objects to add to the DB
     * @param <T>        Object type
     */
    public static <T extends BaseModel> void replaceDB(Context context, String dbName,
            Class<T> type, List<T> newObjects) {
        // Delete the old database
        context.deleteDatabase(dbName);

        // Set up the transaction to save all of the models
        FastStoreModelTransaction<? extends BaseModel> newObjectsTransaction =
                FastStoreModelTransaction.saveBuilder(FlowManager.getModelAdapter(type))
                        .addAll(newObjects)
                        .build();

        // Execute the transaction
        FlowManager.getDatabase(dbName)
                .beginTransactionAsync(newObjectsTransaction)
                .build()
                .execute();
    }

    /**
     * Updates the objects in a DB by updating existing objects, removing old objects, and
     *  inserting new ones.
     * Note: this only works with models that have correctly set up the equals() method
     *
     * @param type       Object type
     * @param newObjects List of new objects/objects to update
     * @param dbClass    Class of the DB these will be stored in
     * @param callback   Optional callback to run any update code. If not, save() will be called
     * @param <T>        Object Type
     */
    public static <T extends BaseModel> void updateDB(Class<T> type, List<T> newObjects, 
            Class dbClass, UpdateCallback<T> callback) {
                SQLite
                        .select()
                        .from(type)
                        .async()
                        .queryListResultCallback((transaction, tResult) -> {
                            if (tResult == null) {
                                return;
                            }

                            // Go through the existing objects
                            for (T oldObject : tResult) {
                                // Check if the object still exists in the received objects
                                int index = newObjects.indexOf(oldObject);
                                if (index != -1) {
                                    // Update it
                                    T newObject = newObjects.get(index);

                                    // If there's a callback, use it
                                    if (callback != null) {
                                        callback.update(newObject);
                                    } else {
                                        // If not, call save
                                        newObject.save();
                                    }

                                    // Delete that place from the body since we've dealt with it
                                    newObjects.remove(newObject);
                                } else {
                                    // Delete the old place
                                    oldObject.delete();
                                }
                            }

                            // Save any new objects
                            FastStoreModelTransaction<? extends BaseModel> newObjectsTransaction =
                                    FastStoreModelTransaction.saveBuilder(
                                            FlowManager.getModelAdapter(type))
                                            .addAll(newObjects)
                                            .build();

                            FlowManager.getDatabase(dbClass)
                                .beginTransactionAsync(newObjectsTransaction)
                                .build()
                                .execute();
                    })
                        .execute();
            }
    /**
      * Callback used to run update code whenever a DB is updated
      *
      * @param <T> Object type
      */
    interface UpdateCallback<T extends BaseModel> {
            /**
              * Called when update code needs to be run
              *
              * @param object Object to update
              */
            void update(T object);
    }
}
