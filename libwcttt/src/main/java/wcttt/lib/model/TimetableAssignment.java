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
 * Represents the assignment of a session to a room in the timetable.
 */
@XmlType(propOrder = {"session", "room"})
public class TimetableAssignment {

	private Session session;
	private Room room;

	public TimetableAssignment() {
		this.session = new InternalSession();
		this.room = new InternalRoom();
	}

	public TimetableAssignment(Session session, Room room) {
		setSession(session);
		setRoom(room);
	}

	@XmlElement(required = true)
	@XmlIDREF
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		if (session == null) {
			throw new IllegalArgumentException("Parameter 'session' must not " +
					"be null");
		}
		this.session = session;
	}

	@XmlElement(required = true)
	@XmlIDREF
	public Room getRoom() {
		return room;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TimetableAssignment that = (TimetableAssignment) o;
		return Objects.equals(session, that.session) &&
				Objects.equals(room, that.room);
	}

	@Override
	public int hashCode() {
		return Objects.hash(session, room);
	}

	public void setRoom(Room room) {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not be " +
					"null");
		}
		this.room = room;
	}

	@Override
	public String toString() {
		return session + " to " + room;
	}
}
