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

package com.guerinet.mymartlet.util.dbflow;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

import java.util.List;

import androidx.annotation.Nullable;
import timber.log.Timber;

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
     * @param callback   Optional callback called when a transaction is finished
     * @param <T>        Object type
     */
    public static <T extends BaseModel> void replaceDB(Context context, String dbName,
            Class<T> type, List<T> newObjects, @Nullable Callback callback) {
        // Delete the old database
        FlowManager.getDatabase(dbName).reset(context);

        // Set up the transaction to save all of the models
        ProcessModelTransaction<T> newObjectsTransaction = new ProcessModelTransaction
                .Builder<T>((tModel, wrapper) -> {
            if (tModel != null) {
                tModel.save();
            }
        })
                .addAll(newObjects)
                .build();

        // Execute the transaction
        FlowManager.getDatabase(dbName)
                .beginTransactionAsync(newObjectsTransaction)
                .success(transaction -> {
                    if (callback != null) {
                        callback.onFinish();
                    }
                })
                .error((transaction, error) -> {
                    Timber.e(error);
                    if (callback != null) {
                        callback.onFinish();
                    }
                })
                .build()
                .execute();
    }

    /**
     * Updates the objects in a DB by updating existing objects, removing old objects, and
     *  inserting new ones.
     * Note: this only works with models that have correctly set up the equals() method
     *
     * @param type           Object type
     * @param newObjects     List of new objects/objects to update
     * @param condition      Optional condition to run when searching
     * @param dbClass        Class of the DB these will be stored in
     * @param updateCallback Optional callback to run any update code. If not, save() will be called
     * @param callback       Optional callback to call after update is finished
     * @param <T>            Object Type
     */
    public static <T extends BaseModel> void updateDB(Class<T> type, List<T> newObjects,
            @Nullable SQLOperator condition, Class dbClass, UpdateCallback<T> updateCallback,
            @Nullable Callback callback) {
        From<T> select = SQLite.select()
                .from(type);

        BaseModelQueriable<T> query;
        if (condition != null) {
            // Add the conditions if necessary
            query = select.where(condition);
        } else {
            query = select.where();
        }

        query.async()
                .queryListResultCallback((transaction, tResult) -> {
                    // Go through the existing objects
                    for (T oldObject : tResult) {
                        // Check if the object still exists in the received objects
                        int index = newObjects.indexOf(oldObject);
                        if (index != -1) {
                            // Update it
                            T newObject = newObjects.get(index);

                            // If there's a callback, use it
                            if (updateCallback != null) {
                                updateCallback.update(newObject, oldObject);
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

                    // Set up the transaction to save all of the models
                    ProcessModelTransaction<T> newObjectsTransaction = new ProcessModelTransaction
                            .Builder<T>((tModel, wrapper) -> {
                        if (tModel != null) {
                            tModel.save();
                        }
                    })
                            .addAll(newObjects)
                            .build();

                    FlowManager.getDatabase(dbClass)
                            .executeTransaction(newObjectsTransaction);

                    if (callback != null) {
                        callback.onFinish();
                    }
            })
                .execute();
    }

    /**
     * Callback used when a transaction is finished
     */
    public interface Callback {
        /**
         * Called when a transaction is finished
         */
        void onFinish();
    }

    /**
     * Callback used to run update code whenever a DB is updated
     *
     * @param <T> Object type
     */
    public interface UpdateCallback<T extends BaseModel> {
        /**
         * Called when update code needs to be run
         *
         * @param object    Object to update
         * @param oldObject Object we are updating from
         */
        void update(T object, T oldObject);
    }
}
