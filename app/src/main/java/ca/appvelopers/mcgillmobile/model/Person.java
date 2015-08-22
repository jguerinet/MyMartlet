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
 * One person for the About page
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 2.0
 */
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Person's name
	 */
	private String mName;
	/**
	 * Person's picture
	 */
	private int mPictureId;
	/**
	 * Person's role
	 */
	private String mRole;
	/**
	 * A short description about the person
	 */
	private String mDescription;
	/**
	 * Person's email
	 */
	private String mEmail;
	/**
	 * URL to the person's LinkedIn
	 */
	private String mLinkedin;

	/**
	 * Default Constructor
	 *
	 * @param name        Person's name
	 * @param pictureId   Person's picture
	 * @param role        Person's role
	 * @param description A short description about the person
	 * @param email       Person's email
	 * @param linkedin    URL to the person's LinkedIn
	 */
	public Person(String name, int pictureId, String role, String description,
	              String email, String linkedin){
		this.mName = name;
		this.mPictureId = pictureId;
		this.mRole = role;
		this.mDescription = description;
		this.mEmail = email;
		this.mLinkedin = linkedin;
	}

	/* GETTERS */

	/**
	 * @return Person's name
	 */
	public String getName(){
		return this.mName;
	}

	/**
	 * @return Person's picture Id
	 */
	public int getPictureId(){
		return this.mPictureId;
	}

	/**
	 * @return Person's role
	 */
	public String getRole(){
		return this.mRole;
	}

	/**
	 * @return A short description about the person
	 */
	public String getDescription(){
		return this.mDescription;
	}

	/**
	 * @return Person's email
	 */
	public String getEmail(){
		return this.mEmail;
	}

	/**
	 * @return The URL to the person's LinkedIn
	 */
	public String getLinkedIn(){
		return this.mLinkedin;
	}
}
