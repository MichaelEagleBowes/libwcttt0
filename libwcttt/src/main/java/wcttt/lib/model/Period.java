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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Represents a period (day + time slot).
 */
@XmlSeeAlso(TimetablePeriod.class)
@XmlType(propOrder = {"day", "timeSlot"})
public class Period implements Comparable<Period> {

	public static final String[] WEEK_DAY_NAMES = {"Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	public static final String[] TIME_SLOT_NAMES = {"8:15 - 9:45",
			"10:15 - 11:45", "12:15 - 13:45", "14:15 - 15:45", "16:15 - 17:45",
			"18:15 - 19:45", "20:15 - 21:45"};

	private int day;
	private int timeSlot;

	public Period() {
		this.day = ValidationHelper.PERIOD_DAY_MIN;
		this.timeSlot = ValidationHelper.PERIOD_TIME_SLOT_MIN;
	}

	public Period(int day, int timeSlot) throws WctttModelException {
		ValidationHelper.validateDay(day);
		ValidationHelper.validateTimeSlot(day);
		this.day = day;
		this.timeSlot = timeSlot;
	}

	@XmlAttribute(required = true)
	public int getDay() {
		return day;
	}

	public void setDay(int day) throws WctttModelException {
		ValidationHelper.validateDay(day);
		this.day = day;
	}

	@XmlAttribute(required = true)
	public int getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(int timeslot) throws WctttModelException {
		ValidationHelper.validateTimeSlot(day);
		this.timeSlot = timeslot;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Period period = (Period) o;
		return day == period.day &&
				timeSlot == period.timeSlot;
	}

	@Override
	public int hashCode() {
		return Objects.hash(day, timeSlot);
	}

	@Override
	public String toString() {
		return WEEK_DAY_NAMES[day - 1] + ", " + TIME_SLOT_NAMES[timeSlot - 1];
	}

	@Override
	public int compareTo(Period o) {
		if (this.day > o.day) {
			return 1;
		} else if (this.day < o.day) {
			return -1;
		} else {
			return Integer.compare(this.timeSlot, o.timeSlot);
		}
	}
}
