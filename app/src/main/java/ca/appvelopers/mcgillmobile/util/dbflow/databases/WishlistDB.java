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

import ca.appvelopers.mcgillmobile.model.CourseResult;

/**
 * Database that holds a list of {@link CourseResult}s that comprise the user's wishlist
 * @author Julien Guerinet
 * @since 2.4.0
 */
@Database(name = WishlistDB.NAME, version = WishlistDB.VERSION)
public class WishlistDB {
    public static final String NAME = "Wishlist";
    public static final String FULL_NAME = NAME + ".db";
    static final int VERSION = 1;
}
