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

package wcttt.lib.util;

import wcttt.lib.model.Curriculum;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the conflict information between a session and a session.
 */
public class SessionSessionConflict {

	private List<Curriculum> curricula = new LinkedList<>();
	boolean sessionConflict = false;
	boolean teacherConflict = false;

	public List<Curriculum> getCurricula() {
		return curricula;
	}

	public boolean isSessionConflict() {
		return sessionConflict;
	}

	public void setSessionConflict(boolean sessionConflict) {
		this.sessionConflict = sessionConflict;
	}

	public boolean isTeacherConflict() {
		return teacherConflict;
	}

	public void setTeacherConflict(boolean teacherConflict) {
		this.teacherConflict = teacherConflict;
	}
}
