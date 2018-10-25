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

package wcttt.lib.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Represents a period of a timetable.
 *
 * Instead of manipulating returned references directly, please use the add/
 * remove methods provided by this class to edit the assignment data. These
 * methods ensure the consistency of the timetable period.
 */
@XmlType(propOrder = {"assignments"})
public class TimetablePeriod extends Period {

	private final ObservableList<TimetableAssignment> assignments =
			FXCollections.observableArrayList();

	public TimetablePeriod() {
		super();
	}

	public TimetablePeriod(int day, int timeSlot) throws WctttModelException {
		super(day, timeSlot);
	}

	@XmlElementWrapper(required = true)
	@XmlElement(name = "timetableAssignment")
	public ObservableList<TimetableAssignment> getAssignments() {
		return assignments;
	}

	private boolean assignmentExists(TimetableAssignment assignment) {
		for (TimetableAssignment existingAss : assignments) {
			if (assignment.equals(existingAss)) {
				return true;
			}
		}
		return false;
	}

	public void addAssignment(TimetableAssignment assignment) throws
			WctttModelException {
		if (assignment == null) {
			throw new IllegalArgumentException("Parameter 'assignment' must " +
					"not be null");
		} else if (assignmentExists(assignment)) {
			throw new WctttModelException("Assignment '" + assignment + "' is" +
					" already included in this period");
		}
		assignments.add(assignment);
	}

	public boolean removeAssignment(TimetableAssignment assignment) {
		if (assignment == null) {
			throw new IllegalArgumentException("Parameter 'assignment' must " +
					"not be null");
		}
		return assignments.remove(assignment);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		TimetablePeriod that = (TimetablePeriod) o;
		return Objects.equals(assignments, that.assignments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), assignments);
	}
}
