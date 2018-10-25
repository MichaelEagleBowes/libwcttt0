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

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a teacher.
 *
 * Instead of manipulating returned references directly, please use the add/
 * remove methods provided by this class to edit the period data. These methods
 * ensure the consistency of the teacher.
 */
@XmlType(propOrder = {"id", "name", "unfavorablePeriods", "unavailablePeriods"})
public class Teacher {

	private String id;
	private String name;
	private final List<Period> unfavorablePeriods = new LinkedList<>();
	private final List<Period> unavailablePeriods = new LinkedList<>();

	public Teacher() {
		this.id = "teacher";
		this.name = "teacher";
	}

	public Teacher(String id, String name) {
		if (id == null || name == null) {
			throw new IllegalArgumentException("Parameters 'id' and 'name'" +
					"must not be null");
		}
		this.id = id;
		this.name = name;
	}

	@XmlAttribute(required = true)
	@XmlID
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id == null) {
			throw new IllegalArgumentException("Parameter 'id' must not be " +
					"null");
		}
		this.id = id;
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

	@XmlElementWrapper(required = false)
	@XmlElement(name = "unfavorable")
	public List<Period> getUnfavorablePeriods() {
		return unfavorablePeriods;
	}

	@XmlElementWrapper(required = false)
	@XmlElement(name = "unavailable")
	public List<Period> getUnavailablePeriods() {
		return unavailablePeriods;
	}

	private boolean unavailPeriodExists(Period period) {
		for (Period unavailable : unavailablePeriods) {
			if (period.equals(unavailable)) {
				return true;
			}
		}
		return false;
	}

	private boolean unfavorPeriodExists(Period period) {
		for (Period unfavorable : unfavorablePeriods) {
			if (period.equals(unfavorable)) {
				return true;
			}
		}
		return false;
	}

	private void addPeriod(Period period, boolean isUnfavorable) throws
			WctttModelException {
		if (period == null) {
			throw new IllegalArgumentException("Parameter 'period' must not " +
					"be null");
		} else if (unfavorPeriodExists(period)) {
			throw new WctttModelException("Period '" + period + "' is " +
					"already included in unfavorable periods");
		} else if (unavailPeriodExists(period)) {
			throw new WctttModelException("Period '" + period + "' is " +
					"already included in unavailable periods");
		}
		if (isUnfavorable) {
			unfavorablePeriods.add(period);
		} else {
			unavailablePeriods.add(period);
		}
	}

	public void addUnfavorablePeriod(Period period) throws WctttModelException {
		addPeriod(period, true);
	}

	public void addUnavailablePeriod(Period period) throws WctttModelException {
		addPeriod(period, false);
	}

	public boolean removeUnfavorablePeriod(Period period) {
		if (period == null) {
			throw new IllegalArgumentException("Parameter 'period' must not " +
					"be null");
		}
		return unfavorablePeriods.remove(period);
	}

	public boolean removeUnavailablePeriod(Period period) {
		if (period == null) {
			throw new IllegalArgumentException("Parameter 'period' must not " +
					"be null");
		}
		return unavailablePeriods.remove(period);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Teacher teacher = (Teacher) o;
		return Objects.equals(id, teacher.id) &&
				Objects.equals(name, teacher.name) &&
				Objects.equals(unfavorablePeriods, teacher.unfavorablePeriods) &&
				Objects.equals(unavailablePeriods, teacher.unavailablePeriods);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, unfavorablePeriods, unavailablePeriods);
	}

	@Override
	public String toString() {
		return name;
	}

}
