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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Represents a session that belongs to a different faculty. Every external
 * session must have a pre-assignment and and external room.
 */
@XmlType(propOrder = {"id", "name", "teacher", "doubleSession",
		"preAssignmentBinding", "room"})
public class ExternalSession extends Session {

	private ExternalRoom room;

	public ExternalSession() {
		super();
		setRoom(new ExternalRoom());
	}

	public ExternalSession(String id, String name, Teacher teacher,
	                       Course course, boolean doubleSession,
	                       Period preAssignment, ExternalRoom room)
			throws WctttModelException {
		super(id, name, teacher, course, doubleSession, preAssignment);
		setRoom(room);
	}

	@XmlElement(required = true)
	@XmlIDREF
	public ExternalRoom getRoom() {
		return room;
	}

	public void setRoom(ExternalRoom room) {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not be " +
					"null");
		}
		this.room = room;
	}

	@Override
	public void setPreAssignment(Period preAssignment)
			throws WctttModelException {
		if (preAssignment == null) {
			throw new WctttModelException("Every external session must have a" +
					" pre-assignment");
		}
		super.setPreAssignment(preAssignment);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ExternalSession session = (ExternalSession) o;
		return Objects.equals(room, session.room);
	}

	@Override
	public int hashCode() {
		return Objects.hash(room);
	}
}
