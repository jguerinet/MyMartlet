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

package ca.appvelopers.mcgillmobile.model.exception;

import java.io.IOException;

/**
 * Exception caused when the user was logged out of Minerva
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class MinervaException extends IOException {

	public MinervaException() {
		super("User is Logged out from Minerva");
	}
}
