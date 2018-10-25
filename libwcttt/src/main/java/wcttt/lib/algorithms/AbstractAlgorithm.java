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

import wcttt.lib.model.Semester;
import wcttt.lib.model.Timetable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract class that can be used as a starting point to implement an algorithm.
 * It already provides basic functionality for the cancellation of the algorithm.
 */
public abstract class AbstractAlgorithm implements Algorithm {

	private final Semester semester;
	private final AtomicBoolean isCancelled = new AtomicBoolean(false);

	/**
	 * Initializion of the class.
	 *
	 * @param semester the semester that should be used to create a new timetable.
	 */
	public AbstractAlgorithm(Semester semester) {
		if (semester == null) {
			throw new IllegalArgumentException("Parameter 'semester' must not" +
					" be null");
		}
		this.semester = semester;
	}

	/**
	 * Getter for the assigned semester.
	 *
	 * @return the semester that is used to create new timetables.
	 */
	protected Semester getSemester() {
		return semester;
	}

	/**
	 * Runs the actual algorithm.
	 *
	 * @param isCancelled indicates whether the algorithm was cancelled and
	 *                       should periodically be checked by the algorithm.
	 * @return the newly created timetable.
	 * @throws WctttAlgorithmException if an error occurred in the algorithm.
	 */
	protected abstract Timetable runAlgorithm(AtomicBoolean isCancelled)
			throws WctttAlgorithmException;

	@Override
	public Timetable generate() throws WctttAlgorithmException {
		isCancelled.set(false);
		return runAlgorithm(isCancelled);
	}

	@Override
	public void cancel() {
		isCancelled.set(true);
	}

	@Override
	public String toString() {
		return getName();
	}
}
