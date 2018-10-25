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
 * Represents a session that belongs to the faculty for which the timetable
 * should be generated.
 */
@XmlType(propOrder = {"id", "name", "teacher", "doubleSession",
		"preAssignmentBinding", "students", "roomRequirements"})
public class InternalSession extends Session {

	private int students;
	private RoomFeatures roomRequirements;

	public InternalSession() {
		super();
		this.students = ValidationHelper.STUDENTS_MIN;
		this.roomRequirements = new RoomFeatures();
	}

	public InternalSession(String id, String name, Teacher teacher,
	                       Course course, boolean doubleSession,
	                       Period preAssignment, int students,
	                       RoomFeatures roomRequirements)
			throws WctttModelException {
		super(id, name, teacher, course, doubleSession, preAssignment);
		setStudents(students);
		setRoomRequirements(roomRequirements);
	}

	@XmlAttribute(required = true)
	public int getStudents() {
		return students;
	}

	public void setStudents(int students) throws WctttModelException {
		ValidationHelper.validateStudents(students);
		this.students = students;
	}

	@XmlElement(required = true)
	public RoomFeatures getRoomRequirements() {
		return roomRequirements;
	}

	public void setRoomRequirements(RoomFeatures roomRequirements) {
		if (roomRequirements == null) {
			throw new IllegalArgumentException("Parameter 'roomRequirements' " +
					"must not be null");
		}
		this.roomRequirements = roomRequirements;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		InternalSession that = (InternalSession) o;
		return students == that.students &&
				Objects.equals(roomRequirements, that.roomRequirements);
	}

	@Override
	public int hashCode() {
		return Objects.hash(students, roomRequirements);
	}
}
