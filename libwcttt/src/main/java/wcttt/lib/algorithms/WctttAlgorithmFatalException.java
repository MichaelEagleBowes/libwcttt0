/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * libwcttt comprises the data model, a binder (parser + emitter) to store the
 * data as XML files, the implementations of the algorithms as well as
 * functionality to calculate conflicts and their violations.
 *
 * Copyright (C) 2018 Nicolas Gross
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package wcttt.lib.algorithms;

/**
 * Thrown if an error occurs in the algorithm that is caused by a faulty
 * implementation.
 */
public class WctttAlgorithmFatalException extends RuntimeException {

	public WctttAlgorithmFatalException() {
		super();
	}

	public WctttAlgorithmFatalException(String message) {
		super(message);
	}

	public WctttAlgorithmFatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public WctttAlgorithmFatalException(Throwable cause) {
		super(cause);
	}

	protected WctttAlgorithmFatalException(String message, Throwable cause, boolean
			enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
