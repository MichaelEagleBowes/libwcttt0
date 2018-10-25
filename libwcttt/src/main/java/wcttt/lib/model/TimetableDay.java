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

import static wcttt.lib.model.ValidationHelper.validateDay;

/**
 * Represents a day of a timetable.
 *
 * Instead of manipulating returned references directly, please use the add/
 * remove methods provided by this class to edit the period data. These methods
 * ensure the consistency of the timetable day.
 */
@XmlType(propOrder = {"day", "periods"})
public class TimetableDay {

	private int day;
	private final ObservableList<TimetablePeriod> periods =
			FXCollections.observableArrayList();

	public TimetableDay() {
		this.day = ValidationHelper.PERIOD_DAY_MIN;
	}

	public TimetableDay(int day) throws WctttModelException {
		validateDay(day);
		this.day = day;
	}

	@XmlAttribute(required = true)
	public int getDay() {
		return day;
	}

	public void setDay(int day) throws WctttModelException {
		validateDay(day);
		this.day = day;
	}

	@XmlElementWrapper(required = true)
	@XmlElement(name = "timetablePeriod")
	public ObservableList<TimetablePeriod> getPeriods() {
		return periods;
	}

	private boolean periodExists(TimetablePeriod period) {
		for (TimetablePeriod existingPeriod : periods) {
			if (period.getDay() == existingPeriod.getDay() &&
					period.getTimeSlot() == existingPeriod.getTimeSlot()) {
				return true;
			}
		}
		return false;
	}

	public void addPeriod(TimetablePeriod period) throws WctttModelException {
		if (period == null) {
			throw new IllegalArgumentException("Parameter 'period' must not " +
					"be null");
		} else if (periodExists(period)) {
			throw new WctttModelException("Period '" + period + "' is already" +
					" included in the day");
		}
		periods.add(period);
		periods.sort(Comparator.comparingInt(Period::getTimeSlot));
	}

	public boolean removePeriod(TimetablePeriod period) {
		if (period == null) {
			throw new IllegalArgumentException("Parameter 'period' must not " +
					"be null");
		}
		return periods.remove(period);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TimetableDay that = (TimetableDay) o;
		return day == that.day &&
				Objects.equals(periods, that.periods);
	}

	@Override
	public int hashCode() {
		return Objects.hash(day, periods);
	}
}
