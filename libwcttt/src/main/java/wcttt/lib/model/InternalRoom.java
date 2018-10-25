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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Represents a room that belongs to the faculty for which the timetable should
 * be generated.
 */
@XmlType(propOrder = {"id", "name", "capacity", "features"})
public class InternalRoom extends Room {

	private int capacity;
	private RoomFeatures features;

	public InternalRoom() {
		super();
		this.capacity = ValidationHelper.ROOM_CAPACITY_MIN;
		this.features = new RoomFeatures();
	}

	public InternalRoom(String id, String name, int capacity,
	                    RoomFeatures features) throws WctttModelException {
		super(id, name);
		ValidationHelper.validateRoomCapacity(capacity);
		this.capacity = capacity;
		this.features = features;
	}

	@XmlAttribute(required = true)
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) throws WctttModelException {
		ValidationHelper.validateRoomCapacity(capacity);
		this.capacity = capacity;
	}

	@XmlElement(required = true)
	public RoomFeatures getFeatures() {
		return features;
	}

	public void setFeatures(RoomFeatures features) {
		if (features == null) {
			throw new IllegalArgumentException("Parameter 'features' must not" +
					" be null");
		}
		this.features = features;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		InternalRoom that = (InternalRoom) o;
		return capacity == that.capacity &&
				Objects.equals(features, that.features);
	}

	@Override
	public int hashCode() {
		return Objects.hash(capacity, features);
	}
}
