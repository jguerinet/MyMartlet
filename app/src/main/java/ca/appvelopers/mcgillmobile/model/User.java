/*
 * Copyright 2014-2015 Appvelopers
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
 * @version 2.0
 * @since 1.0.0
 */
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * The user's name
     */
    private String mName;
    /**
     * The user's McGill Id
     */
    private String mId;

    /**
     * Default Constructor
     *
     * @param name The user's name
     * @param id   The user's McGill Id
     */
    public User(String name, String id){
        this.mName = name;
        this.mId = id;
    }

    /* GETTERS */

    /**
     * @return The user's name
     */
    public String getName(){
        return mName;
    }

    /**
     * @return The user's Id
     */
    public String getId(){
        return mId;
    }
}
