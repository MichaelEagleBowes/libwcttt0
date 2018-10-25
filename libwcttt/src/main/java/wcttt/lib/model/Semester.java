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

import javafx.collections.ObservableList;

import java.util.List;

/**
 * Represents the data of one semester.
 *
 * Instead of manipulating returned references directly, please use the add/
 * remove/update methods provided by this interface to edit the data of the
 * semester. These methods ensure the consistency of the semester.
 */
public interface Semester {

	/**
	 * Getter for the name of a semester.
	 *
	 * @return the name of the semester.
	 */
	String getName();

	/**
	 * Setter for the name of a semester.
	 *
	 * @param name the new name of the semester, must not be null.
	 */
	void setName(String name);

	/**
	 * Getter for the number of days per week of a semester.
	 *
	 * @return the number of days per week of the semester.
	 */
	int getDaysPerWeek();

	/**
	 * Setter for the number of days per week of a semester.
	 *
	 * @param daysPerWeek the new number of days per week, must be >=
	 *                    {@value ValidationHelper#DAYS_PER_WEEK_MIN}
	 *                    and <= {@value ValidationHelper#DAYS_PER_WEEK_MAX}.
	 * @throws WctttModelException if {@code daysPerWeek} is outside the given
	 *                             range.
	 */
	void setDaysPerWeek(int daysPerWeek) throws WctttModelException;

	/**
	 * Getter for the number of time slots per day of a semester.
	 *
	 * @return the number of time slots per day of the semester
	 */
	int getTimeSlotsPerDay();

	/**
	 * Setter for the number of time slots per day of a semester.
	 *
	 * @param timeSlotsPerDay the new number of time slots per day, must be >=
	 *                        {@value ValidationHelper#TIME_SLOTS_PER_DAY_MIN}
	 *                        and <= {@value ValidationHelper#TIME_SLOTS_PER_DAY_MAX}.
	 * @throws WctttModelException if {@code timeSlotsPerDay} is outside the
	 *                             given range.
	 */
	void setTimeSlotsPerDay(int timeSlotsPerDay) throws
			WctttModelException;

	/**
	 * Getter for the maximum number of daily lectures per curriculum of a
	 * semester.
	 *
	 * @return the maximum number of daily lectures per curriculum.
	 */
	int getMaxDailyLecturesPerCur();

	/**
	 * Setter for the maximum number of daily lectures per curriculum of a
	 * semester.
	 *
	 * @param maxDailyLecturesPerCur the new maximum number of daily lectures
	 *                               per curriculum, must be >=
	 *                               {@value ValidationHelper#MIN_DAILY_LECTURES_PER_CUR_MIN}.
	 * @throws WctttModelException if {@code maxDailyLecturesPerCur} is outside
	 *                             the given range.
	 */
	void setMaxDailyLecturesPerCur(int maxDailyLecturesPerCur) throws
			WctttModelException;

	/**
	 * Getter for the soft constraint weightings of a semester.
	 *
	 * @return the soft constraint weightings of the semester.
	 */
	ConstraintWeightings getConstrWeightings();

	/**
	 * Setter for the soft constraint weightings of a semester.
	 *
	 * @param constrWeightings the new soft constraint weightings for the
	 *                         semester, must not be null.
	 */
	void setConstrWeightings(ConstraintWeightings constrWeightings);

	/**
	 * Getter for the chairs of a semester. Do not manipulate the returned
	 * references.
	 *
	 * @return the list of the chairs of the semester.
	 */
	ObservableList<Chair> getChairs();

	/**
	 * Getter for the internal rooms of a semester. Do not manipulate the
	 * returned references.
	 *
	 * @return the list of the rooms of the semester.
	 */
	ObservableList<InternalRoom> getInternalRooms();

	/**
	 * Getter for the external rooms of a semester. Do not manipulate the
	 * returned references.
	 *
	 * @return the list of the rooms of the semester.
	 */
	ObservableList<ExternalRoom> getExternalRooms();

	/**
	 * Getter for the courses of a semester. Do not manipulate the returned
	 * references.
	 *
	 * @return the list of the courses of the semester.
	 */
	ObservableList<Course> getCourses();

	/**
	 * Getter for the curricula of a semester. Do not manipulate the returned
	 * references.
	 *
	 * @return the list of the curricula of the semester.
	 */
	ObservableList<Curriculum> getCurricula();

	/**
	 * Getter for the timetables of a semester. Do not manipulate the returned
	 * references.
	 *
	 * @return the list of the timetables of the semester.
	 */
	ObservableList<Timetable> getTimetables();

	/**
	 * Adds a chair to the semester. The list of timetables must be empty.
	 *
	 * @param chair the new chair.
	 * @throws WctttModelException if the timetable list is not empty or the ids
	 * used in the chair are already used in the semester.
	 */
	void addChair(Chair chair) throws WctttModelException;

	/**
	 * Removes a chair from the semester. The list of timetables must be empty.
	 *
	 * @param chair the chair that should be removed.
	 * @return {@code true} if the chair existed, otherwise {@code false}.
	 * @throws WctttModelException if the timetable list is not empty, the chair
	 * still has teachers or the chair is still referenced by a course.
	 */
	boolean removeChair(Chair chair) throws WctttModelException;

	/**
	 * Updates the id of a chair.
	 *
	 * @param chair the chair whose id should be updated.
	 * @param id the new id.
	 * @throws WctttModelException if the chair is not assigned to the semester
	 * or the id is already used.
	 */
	void updateChairId(Chair chair, String id) throws
			WctttModelException;

	/**
	 * Updates the data of a chair.
	 *
	 * @param chair the chair that should be updated.
	 * @param name the new name.
	 * @param abbreviation the new abbreviation.
	 * @throws WctttModelException if the chair is not assigned to the semester.
	 */
	void updateChairData(Chair chair, String name, String abbreviation)
			throws WctttModelException;

	/**
	 * Adds a teacher to a chair. The list of timetables must be empty.
	 *
	 * @param teacher the new teacher.
	 * @param chair the chair to which the teacher should be added.
	 * @throws WctttModelException if the chair is not assigned to the semester
	 * or the id of the teacher is already used.
	 */
	void addTeacherToChair(Teacher teacher, Chair chair) throws
					WctttModelException;

	/**
	 * Removes a teacher from a chair. The list of timetables must be empty.
	 *
	 * @param teacher the teacher that should be removed.
	 * @param chair the chair from which the teacher should be removed.
	 * @return {@code true} if the teacher existed, otherwise {@code false}.
	 * @throws WctttModelException if the timetable list is not empty or the
	 * teacher is still referenced in a course.
	 */
	boolean removeTeacherFromChair(Teacher teacher, Chair chair) throws
			WctttModelException;

	/**
	 * Updates the id of a teacher.
	 *
	 * @param teacher the teacher whose id should be updated.
	 * @param id the new id.
	 * @throws WctttModelException if the teacher is not assigned to the
	 * semester or the id is already used.
	 */
	void updateTeacherId(Teacher teacher, Chair chair, String id) throws
					WctttModelException;

	/**
	 * Updates the data of a teacher. The list of timetables must be empty if
	 * other data are changed besides the name.
	 *
	 * @param teacher the teacher whose data should be updated.
	 * @param name the new name.
	 * @param unfavorablePeriods the new unfavorable periods.
	 * @param unavailablePeriods the new unavailable periods.
	 * @throws WctttModelException if the teacher is not assigned to the
	 * semester, the timetable list should be empty but is not, the unavailable
	 * periods are conflicting with pre-assignments involving the teacher or a
	 * period appears in unfavorable as well as unavailable periods.
	 */
	void updateTeacherData(Teacher teacher, String name,
	                       List<Period> unfavorablePeriods,
	                       List<Period> unavailablePeriods)
			throws WctttModelException;

	/**
	 * Adds an internal room to the semester. The list of timetables must be
	 * empty.
	 *
	 * @param room the new internal room.
	 * @throws WctttModelException if the timetable list is not empty or the id
	 * of the room is already in use.
	 */
	void addInternalRoom(InternalRoom room) throws WctttModelException;

	/**
	 * Adds an external room to the semester. The list of timetables must be
	 * empty.
	 *
	 * @param room the new external room.
	 * @throws WctttModelException if the timetable list is not empty or the id
	 * of the room is already in use.
	 */
	void addExternalRoom(ExternalRoom room) throws WctttModelException;

	/**
	 * Removes an internal room. The list of timetables must be empty.
	 *
	 * @param room the internal room that should be removed.
	 * @return {@code true} if the room existed, otherwise {@code false}.
	 * @throws WctttModelException if the timetable list is not empty.
	 */
	boolean removeInternalRoom(InternalRoom room) throws WctttModelException;

	/**
	 * Removes an external room. The list of timetables must be empty.
	 *
	 * @param room the external room that should be removed.
	 * @return {@code true} if the room existed, otherwise {@code false}.
	 * @throws WctttModelException if the timetable list is not empty.
	 */
	boolean removeExternalRoom(ExternalRoom room) throws WctttModelException;

	/**
	 * Updates the id of a room.
	 *
	 * @param room the room whose id should be updated.
	 * @param id the new id.
	 * @throws WctttModelException if the room is not assigned to the semester
	 * or the id is already used.
	 */
	void updateRoomId(Room room, String id) throws WctttModelException;

	/**
	 * Updates the data of an internal room. The list of timetables must be
	 * empty if other data are changed besides the name.
	 *
	 * @param room the internal room that should be updated.
	 * @param name the new name.
	 * @param capacity the new capacity.
	 * @param features the new room features.
	 * @throws WctttModelException if the room is not assigned to the semester,
	 * the timetable list should be empty but is not or the capacity was <
	 * {@value ValidationHelper#ROOM_CAPACITY_MIN}.
	 */
	void updateInternalRoomData(InternalRoom room, String name, int capacity,
	                           RoomFeatures features) throws WctttModelException;

	/**
	 * Updates the data of an external room.
	 *
	 * @param room the external room that should be updated.
	 * @param name the new name.
	 * @throws WctttModelException if the room is not assigned to the semester.
	 */
	void updateExternalRoomData(ExternalRoom room, String name)
			throws WctttModelException;

	/**
	 * Adds a course to the semester. The list of timetables must be empty.
	 *
	 * @param course the new course.
	 * @throws WctttModelException if the timetable list is not empty, there are
	 * no chairs added to the semester, the chair is nor assigned to the
	 * semester or if an id in the course is already used.
	 */
	void addCourse(Course course) throws WctttModelException;

	/**
	 * Removes a course from the semester. The list of timetables must be empty.
	 *
	 * @param course the course that should be removed from the semester.
	 * @return {@code true} if the course existed, otherwise {@code false}.
	 * @throws WctttModelException if the timetable list is not empty or the
	 * course is still assigned to a curriculum.
	 */
	boolean removeCourse(Course course) throws WctttModelException;

	/**
	 * Updates the id of a course.
	 *
	 * @param course the course whose id should be updated.
	 * @param id the new id.
	 * @throws WctttModelException if the course is not assigned to the semester
	 * or the id is already used.
	 */
	void updateCourseId(Course course, String id) throws
			WctttModelException;

	/**
	 * Updates the data of a course. The list of timetables must be empty if
	 * other data are changed besides the name or the abbreviation.
	 *
	 * @param course the course whose data should be updated.
	 * @param name the new name.
	 * @param abbreviation the new abbreviation.
	 * @param chair the new chair.
	 * @param courseLevel the new course level.
	 * @param minNumberOfDays the new minimum number of days the lectures of the
	 *                           course must at least be spread over.
	 * @throws WctttModelException the course is not assigned to the semester,
	 * the timetable list should be empty but is not or 'minNumberOfDays' is <
	 * {@value ValidationHelper#MIN_NUM_OF_DAYS_MIN}.
	 */
	void updateCourseData(Course course, String name, String abbreviation,
	                      Chair chair, CourseLevel courseLevel,
	                      int minNumberOfDays) throws WctttModelException;

	/**
	 * Adds a lecture to a course. The list of timetables must be empty.
	 *
	 * @param lecture the new lecture.
	 * @param course the course to which the lecture should be added.
	 * @throws WctttModelException if the course is not assigned to the semester,
	 * there are no teachers added to the semester or the id of the lecture is
	 * already in use.
	 */
	void addCourseLecture(Session lecture, Course course) throws
					WctttModelException;

	/**
	 * Removes a lecture from a course. The list of timetables must be empty.
	 *
	 * @param lecture the lecture that should be removed.
	 * @return {@code true} if the lecture existed, otherwise {@code false}.
	 * @throws WctttModelException if the course of the lecture is not assigned
	 * to the semester or the timetable list is not empty,
	 */
	boolean removeCourseLecture(Session lecture) throws WctttModelException;

	/**
	 * Adds a practical to a course. The list of timetables must be empty.
	 *
	 * @param practical the new practical.
	 * @param course the course to which the practical should be added.
	 * @throws WctttModelException if the course is not assigned to the semester,
	 * there are no teachers added to the semester or the id of the practical is
	 * already in use.
	 */
	void addCoursePractical(Session practical, Course course) throws
					WctttModelException;

	/**
	 * Removes a practical from a course. The list of timetables must be empty.
	 *
	 * @param practical the practical that should be removed.
	 * @return {@code true} if the practical existed, otherwise {@code false}.
	 * @throws WctttModelException if the course of the practical is not
	 * assigned to the semester or the timetable list is not empty,
	 */
	boolean removeCoursePractical(Session practical) throws WctttModelException;

	/**
	 * Updates the id of a session.
	 *
	 * @param session the session whose id should be updated.
	 * @param id the new id.
	 * @throws WctttModelException if the session is not assigned to the
	 * semester or the id is already used.
	 */
	void updateCourseSessionId(Session session, Course course, String id)
					throws WctttModelException;

	/**
	 * Updates the data of an internal session. The list of timetables must be
	 * empty if other data are changed besides the name.
	 *
	 * @param session the internal session that should be updated.
	 * @param name the new name.
	 * @param teacher the new teacher.
	 * @param doubleSession the new double session indicator.
	 * @param preAssignment the new pre-assignment, can be {@code null}.
	 * @param students the new number of students.
	 * @param roomRequirements the new room requirements.
	 * @throws WctttModelException if referenced entities are not assigned to
	 * the semester, the timetable list should be empty but is not,
	 * pre-assignment and double session data are conflicting or pre-assignment
	 * is conflicting with teacher's unavailable periods.
	 */
	void updateInternalSessionData(InternalSession session, String name,
	                               Teacher teacher, boolean doubleSession,
	                               Period preAssignment, int students,
	                               RoomFeatures roomRequirements)
			throws WctttModelException;

	/**
	 * Updates the data of an external session. The list of timetables must be
	 * empty if other data are changed besides the name.
	 *
	 * @param session the external session that should be updated.
	 * @param name the new name.
	 * @param teacher the new teacher.
	 * @param doubleSession the new double session indicator.
	 * @param preAssignment the new pre-assignment.
	 * @param room the new external room.
	 * @throws WctttModelException if referenced entities are not assigned to
	 * the semester, the timetable list should be empty but is not,
	 * pre-assignment and double session data are conflicting or pre-assignment
	 * is conflicting with teacher's unavailable periods.
	 */
	void updateExternalSessionData(ExternalSession session, String name,
	                               Teacher teacher, boolean doubleSession,
	                               Period preAssignment, ExternalRoom room)
			throws WctttModelException;

	/**
	 * Adds a curriculum to the semester. The list of timetables must be empty.
	 *
	 * @param curriculum the new curriculum.
	 * @throws WctttModelException if the timetable list is not empty, the id of
	 * the curriculum is already used or a course of the curriculum is not
	 * assigned to the semester.
	 */
	void addCurriculum(Curriculum curriculum) throws WctttModelException;

	/**
	 * Removes a curriculum from the semester. The list of timetables must be
	 * empty.
	 *
	 * @param curriculum the new curriculum that should be removed.
	 * @return {@code true} if the curriculum existed, otherwise {@code false}.
	 * @throws WctttModelException if the timetable list is not empty.
	 */
	boolean removeCurriculum(Curriculum curriculum) throws WctttModelException;

	/**
	 * Updates the id of a curriculum.
	 *
	 * @param curriculum the curriculum whose id should be updated.
	 * @param id the new id.
	 * @throws WctttModelException if the curriculum is not assigned to the
	 * semester or the id is already used.
	 */
	void updateCurriculumId(Curriculum curriculum, String id) throws
			WctttModelException;

	/**
	 * Updates the data of a curriculum. The list of timetables must be empty if
	 * other data are changed besides the name.
	 *
	 * @param curriculum the curriculum that should be updated.
	 * @param name the new name.
	 * @param courses the new courses.
	 * @throws WctttModelException if the curriculum is not assigned to the
	 * semester or a course of the curriculum is not assigned to the semester.
	 */
	void updateCurriculumData(Curriculum curriculum, String name,
	                          List<Course> courses) throws WctttModelException;

	/**
	 * Adds a timetable to the semester.
	 *
	 * @param timetable the new timetable.
	 * @throws WctttModelException if the name of the timetable is already used.
	 */
	void addTimetable(Timetable timetable) throws WctttModelException;

	/**
	 * Removes a timetable from the semester.
	 *
	 * @param timetable the timetable that should be removed.
	 * @return {@code true} if the timetable existed, otherwise {@code false}.
	 */
	boolean removeTimetable(Timetable timetable);

	/**
	 * Updates the name of a timetable.
	 *
	 * @param timetable the timetable whose name should be updated.
	 * @param name the new name.
	 * @throws WctttModelException if the timetable is not assigned to the
	 * semester or the name is already used.
	 */
	void updateTimetableName(Timetable timetable, String name)
			throws WctttModelException;
}
