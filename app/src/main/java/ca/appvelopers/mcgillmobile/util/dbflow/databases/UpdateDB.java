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

package ca.appvelopers.mcgillmobile.util.dbflow.databases;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Database that holds a list of updates that comprise the user's update history
 * @author Julien Guerinet
 * @since 3.0.0
 */
@Database(name = UpdateDB.NAME, version =  UpdateDB.VERSION)
public class UpdateDB {
    public static final String NAME = "Updates";
    static final int VERSION = 1;
}
