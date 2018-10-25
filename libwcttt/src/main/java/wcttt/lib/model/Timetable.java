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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.Comparator;
import java.util.Objects;

/**
 * Represents a timetable.
 *
 * Instead of manipulating returned references directly, please use the add/
 * remove methods provided by this class to edit the day data. These methods
 * ensure the consistency of the timetable.
 */
@XmlType(propOrder = {"name", "softConstraintPenalty", "days"})
public class Timetable {

	private String name;
	private double softConstraintPenalty = 0.0;
	private final ObservableList<TimetableDay> days =
			FXCollections.observableArrayList();

	public Timetable() {
		this.name = "timetable";
	}

	public Timetable(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Parameter 'name' must not be " +
					"null");
		}
		this.name = name;
	}

	public Timetable(Timetable timetable) {
		if (timetable == null) {
			throw new IllegalArgumentException("Parameter 'timetable' must " +
					"not be null");
		}
		this.name = timetable.name;
		this.softConstraintPenalty = timetable.softConstraintPenalty;
		for (TimetableDay day : timetable.days) {
			try {
				TimetableDay newDay = new TimetableDay(day.getDay());
				for (TimetablePeriod period : day.getPeriods()) {
					TimetablePeriod newPeriod = new TimetablePeriod(
							period.getDay(), period.getTimeSlot());
					for (TimetableAssignment assignment : period.getAssignments()) {
						TimetableAssignment newAssignment =
								new TimetableAssignment(assignment.getSession(),
										assignment.getRoom());
						newPeriod.addAssignment(newAssignment);
					}
					newDay.addPeriod(newPeriod);
				}
				this.addDay(newDay);
			} catch (WctttModelException e) {
				throw new WctttModelFatalException("Implementation error, " +
						"there is a problem with cloning a timetable", e);
			}
		}
	}

	@XmlAttribute(required = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Parameter 'name' must not be " +
					"null");
		}
		this.name = name;
	}

	@XmlAttribute(required = true)
	public double getSoftConstraintPenalty() {
		return softConstraintPenalty;
	}

	public void setSoftConstraintPenalty(double softConstraintPenalty) {
		this.softConstraintPenalty = softConstraintPenalty;
	}

	@XmlElementWrapper(required = true)
	@XmlElement(name = "timetableDay")
	public ObservableList<TimetableDay> getDays() {
		return days;
	}

	private boolean dayExists(TimetableDay day) {
		for (TimetableDay existingDay : days) {
			if (day.getDay() == existingDay.getDay()) {
				return true;
			}
		}
		return false;
	}

	public void addDay(TimetableDay day) throws WctttModelException {
		if (day == null) {
			throw new IllegalArgumentException("Parameter 'day' must not " +
					"be null");
		} else if (dayExists(day)) {
			throw new WctttModelException("Day '" + day + "' is already " +
					"included in the timetable");
		}
		days.add(day);
		days.sort(Comparator.comparingInt(TimetableDay::getDay));
	}

	public boolean removeDay(Timetable day) {
		if (day == null) {
			throw new IllegalArgumentException("Parameter 'day' must not " +
					"be null");
		}
		return days.remove(day);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Timetable timetable = (Timetable) o;
		return Double.compare(
				timetable.softConstraintPenalty, softConstraintPenalty) == 0 &&
				Objects.equals(name, timetable.name) &&
				Objects.equals(days, timetable.days);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, softConstraintPenalty, days);
	}

	@Override
	public String toString() {
		return name;
	}

}
