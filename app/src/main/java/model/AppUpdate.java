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

package com.guerinet.mymartlet.model;

import com.guerinet.mymartlet.util.dbflow.databases.UpdateDB;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.threeten.bp.ZonedDateTime;

/**
 * An update to the app
 * @author Julien Guerinet
 * @since 3.0.0
 */
@Table(database = UpdateDB.class, allFields = true)
public class AppUpdate extends BaseModel {
    /**
     * Unique Id
     */
    @PrimaryKey(autoincrement = true)
    int id;
    /**
     * App version we are updating to
     */
    String version;
    /**
     * DateTime this update is happening at
     */
    ZonedDateTime timestamp;

    /**
     * DB Constructor
     */
    AppUpdate() {}

    /**
     * Default Constructor
     *
     * @param version   App version we are updating to
     * @param timestamp DateTime this update is happening at
     */
    public AppUpdate(String version, ZonedDateTime timestamp) {
        this.version = version;
        this.timestamp = timestamp;
    }

    /* GETTERS */

    /**
     * @return App version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return Update timestamp
     */
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
