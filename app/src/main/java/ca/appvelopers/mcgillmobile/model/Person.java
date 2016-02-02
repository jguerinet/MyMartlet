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

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.io.Serializable;

/**
 * One person for the About page
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Person's name
	 */
	private @StringRes int name;
	/**
	 * Person's picture
	 */
	private @DrawableRes int pictureId;
	/**
	 * Person's role
	 */
	private @StringRes int role;
	/**
	 * A short description about the person
	 */
	private @StringRes int description;
	/**
	 * Person's email
	 */
	private @StringRes int email;
	/**
	 * URL to the person's LinkedIn
	 */
	private @StringRes int linkedin;

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
	public Person(@StringRes int name, @DrawableRes int pictureId, @StringRes int role,
            @StringRes int description, @StringRes int email, @StringRes int linkedin) {
		this.name = name;
		this.pictureId = pictureId;
		this.role = role;
		this.description = description;
		this.email = email;
		this.linkedin = linkedin;
	}

	/* GETTERS */

	/**
	 * @return Person's name
	 */
	public @StringRes int getName() {
		return name;
	}

	/**
	 * @return Person's picture Id
	 */
	public @DrawableRes int getPictureId() {
		return pictureId;
	}

	/**
	 * @return Person's role
	 */
	public @StringRes int getRole() {
		return role;
	}

	/**
	 * @return A short description about the person
	 */
	public @StringRes int getDescription() {
		return description;
	}

	/**
	 * @return Person's email
	 */
	public @StringRes int getEmail() {
		return email;
	}

	/**
	 * @return The URL to the person's LinkedIn
	 */
	public @StringRes int getLinkedIn() {
		return linkedin;
	}
}
