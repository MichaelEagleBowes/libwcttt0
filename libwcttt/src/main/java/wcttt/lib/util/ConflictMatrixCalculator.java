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

import wcttt.lib.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculator for the conflict matrices of a semester.
 */
public class ConflictMatrixCalculator {

	private Semester semester;

	public ConflictMatrixCalculator(Semester semester) {
		this.semester = semester;
	}

	public Map<Session, Map<Session, SessionSessionConflict>> calcSessionSessionConflicts() {
		Map<Session, Map<Session, SessionSessionConflict>> matrix = new HashMap<>();
		for (Course course : semester.getCourses()) {
			for (Session lecture : course.getLectures()) {
				addSessionToSessionSessionMatrix(matrix, lecture);
			}
			for (Session practical : course.getPracticals()) {
				addSessionToSessionSessionMatrix(matrix, practical);
			}
		}
		return matrix;
	}

	private void addSessionToSessionSessionMatrix(Map<Session, Map<Session,
			SessionSessionConflict>> matrix, Session session) {
		matrix.put(session, new HashMap<>());
		for (Course otherCourse : semester.getCourses()) {
			for (Session otherLecture : otherCourse.getLectures()) {
				addSessionSessionConflicts(matrix, session, otherLecture);
			}
			for (Session otherPractical : otherCourse.getPracticals()) {
				addSessionSessionConflicts(matrix, session, otherPractical);
			}
		}
	}

	private void addSessionSessionConflicts(
			Map<Session, Map<Session, SessionSessionConflict>> matrix,
			Session session, Session otherSession) {
		if (otherSession.equals(session)) {
			matrix.get(session).put(otherSession, null);
		} else {
			SessionSessionConflict conflict = new SessionSessionConflict();
			// Check for conflicts based on the curricula:
			for (Curriculum curriculum : semester.getCurricula()) {
				if (curriculum.getCourses().contains(session.getCourse()) &&
						curriculum.getCourses().contains(otherSession.getCourse())) {
					if (session.isLecture() || otherSession.isLecture() ||
							session.getCourse().getPracticals().size() == 1 ||
							otherSession.getCourse().getPracticals().size() == 1) {
						conflict.getCurricula().add(curriculum);
					}
				}
			}
			// Check for conflicts based on the same course:
			if (session.getCourse().equals(otherSession.getCourse())) {
				if (session.isLecture() || otherSession.isLecture()) {
					conflict.setSessionConflict(true);
				}
			}
			// Check for conflicts based on the same teacher:
			if (session.getTeacher().equals(otherSession.getTeacher())) {
				conflict.setTeacherConflict(true);
			}
			matrix.get(session).put(otherSession, conflict);
		}
	}

	public Map<InternalSession, Map<InternalRoom, SessionRoomConflict>> calcSessionRoomConflicts() {
		Map<InternalSession, Map<InternalRoom, SessionRoomConflict>> matrix = new HashMap<>();
		for (Course course : semester.getCourses()) {
			for (Session lecture : course.getLectures()) {
				if (lecture instanceof InternalSession) {
					matrix.put((InternalSession) lecture, new HashMap<>());
					for (InternalRoom room : semester.getInternalRooms()) {
						addSessionRoomConflict(matrix,
								(InternalSession) lecture, room);
					}
				}
			}
			for (Session practical : course.getPracticals()) {
				if (practical instanceof InternalSession) {
					matrix.put((InternalSession) practical, new HashMap<>());
					for (InternalRoom room : semester.getInternalRooms()) {
						addSessionRoomConflict(matrix,
								(InternalSession) practical, room);
					}
				}
			}
		}
		return matrix;
	}

	private void addSessionRoomConflict(Map<InternalSession, Map<InternalRoom,
			SessionRoomConflict>> matrix, InternalSession session, InternalRoom room) {
		SessionRoomConflict conflict = new SessionRoomConflict();
		conflict.setCapacityDeviation(room.getCapacity() - session.getStudents());
		if (session.getRoomRequirements().compareTo(room.getFeatures()) <= 0) {
			conflict.setFullfillsFeatures(true);
		}
		matrix.get(session).put(room, conflict);
	}

	public Map<Teacher, Map<Period, TeacherPeriodConflict>> calcTeacherPeriodConflicts() {
		Map<Teacher, Map<Period, TeacherPeriodConflict>> matrix = new HashMap<>();
		for (Chair chair : semester.getChairs()) {
			for (Teacher teacher : chair.getTeachers()) {
				matrix.put(teacher, new HashMap<>());
				addTeacherPeriodConflicts(matrix, teacher);
			}
		}
		return matrix;
	}

	private void addTeacherPeriodConflicts(
			Map<Teacher, Map<Period, TeacherPeriodConflict>> matrix, Teacher teacher) {
		TeacherPeriodConflict conflict = new TeacherPeriodConflict();
		for (int i = 1; i <= semester.getDaysPerWeek(); i++) {
			for (int j = 1; j <= semester.getTimeSlotsPerDay(); j++) {
				Period period;
				try {
					period = new Period(i, j);
				} catch (WctttModelException e) {
					throw new WctttUtilFatalException("Implementation error, " +
							"period was created with illegal parameters", e);
				}
				if (teacher.getUnfavorablePeriods().contains(period)) {
					conflict.setUnfavorable(true);
				} else if (teacher.getUnavailablePeriods().contains(period)) {
					conflict.setUnavailable(true);
				}
				matrix.get(teacher).put(period, conflict);
			}
		}
	}
}
