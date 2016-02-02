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

package ca.appvelopers.mcgillmobile.model;

import java.io.Serializable;

/**
 * The user's basic information
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * User's name
     */
    private String name;
    /**
     * User's McGill Id
     */
    private String id;

    /**
     * Default Constructor
     *
     * @param name User's name
     * @param id   User's McGill Id
     */
    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    /* GETTERS */

    /**
     * @return User's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return User's Id
     */
    public String getId() {
        return id;
    }
}
