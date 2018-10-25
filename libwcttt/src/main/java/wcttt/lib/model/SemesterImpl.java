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

import wcttt.lib.util.ConstraintViolationsCalculator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a semester.
 */
@XmlRootElement(name = "semester")
@XmlType(propOrder = {"name", "daysPerWeek", "timeSlotsPerDay",
		"maxDailyLecturesPerCur", "constrWeightings", "chairs", "internalRooms",
		"externalRooms", "courses", "curricula", "timetables"})
public class SemesterImpl implements Semester {

	private String name;
	private int daysPerWeek;
	private int timeSlotsPerDay;
	private int maxDailyLecturesPerCur;
	private ConstraintWeightings constrWeightings;
	private final ObservableList<Chair> chairs =
			FXCollections.observableList(new LinkedList<>());
	private final ObservableList<InternalRoom> internalRooms =
			FXCollections.observableList(new LinkedList<>());
	private final ObservableList<ExternalRoom> externalRooms =
			FXCollections.observableList(new LinkedList<>());
	private final ObservableList<Course> courses =
			FXCollections.observableList(new LinkedList<>());
	private final ObservableList<Curriculum> curricula =
			FXCollections.observableList(new LinkedList<>());
	private final ObservableList<Timetable> timetables =
			FXCollections.observableList(new LinkedList<>());

	/**
	 * Creates a new semester with an empty name, 1 day per week, 1 time
	 * slot per day, 1 max daily lecture per curriculum and default constraint
	 * weightings (see {@link ConstraintWeightings#ConstraintWeightings()}).
	 */
	public SemesterImpl() {
		this.name = "semester";
		this.daysPerWeek = ValidationHelper.DAYS_PER_WEEK_MIN;
		this.timeSlotsPerDay = ValidationHelper.TIME_SLOTS_PER_DAY_MIN;
		this.maxDailyLecturesPerCur =
				ValidationHelper.MIN_DAILY_LECTURES_PER_CUR_MIN;
		this.constrWeightings = new ConstraintWeightings();
	}

	/**
	 * Creates a new semester.
	 *
	 * @param name                   the name of the semester, must not be null.
	 * @param daysPerWeek            the number of days in a week, must be >=
	 *                               {@value ValidationHelper#DAYS_PER_WEEK_MIN}
	 *                               and <= {@value ValidationHelper#DAYS_PER_WEEK_MAX}.
	 * @param timeSlotsPerDay        the number of time slots in a day, must be
	 *                               >= {@value ValidationHelper#TIME_SLOTS_PER_DAY_MIN}
	 *                               and <= {@value ValidationHelper#TIME_SLOTS_PER_DAY_MAX}.
	 * @param maxDailyLecturesPerCur the maximum number of daily lectures per
	 *                               curriculum, must be >=
	 *                               {@value ValidationHelper#MIN_DAILY_LECTURES_PER_CUR_MIN}.
	 * @param constrWeightings       the soft constraint weightings for the
	 *                               semester, must not be null.
	 * @throws WctttModelException if parameters do not adhere to their
	 *                             respective value ranges.
	 */
	public SemesterImpl(String name, int daysPerWeek, int timeSlotsPerDay,
	                    int maxDailyLecturesPerCur,
	                    ConstraintWeightings constrWeightings) throws
			WctttModelException {
		setName(name);
		setDaysPerWeek(daysPerWeek);
		setTimeSlotsPerDay(timeSlotsPerDay);
		setMaxDailyLecturesPerCur(maxDailyLecturesPerCur);
		setConstrWeightings(constrWeightings);
	}

	@Override
	@XmlAttribute(required = true)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Parameter 'name' must not be " +
					"null");
		}
		this.name = name;
	}

	private void checkTimetablesEmpty(String name) throws WctttModelException {
		if (!getTimetables().isEmpty()) {
			throw new WctttModelException("Timetable list must be empty before " +
					"editing the " + name);
		}
	}

	@Override
	@XmlAttribute(required = true)
	public int getDaysPerWeek() {
		return daysPerWeek;
	}

	@Override
	public void setDaysPerWeek(int daysPerWeek) throws WctttModelException {
		checkTimetablesEmpty("number of days per week");
		ValidationHelper.validateDaysPerWeek(daysPerWeek);
		this.daysPerWeek = daysPerWeek;
	}

	@Override
	@XmlAttribute(required = true)
	public int getTimeSlotsPerDay() {
		return timeSlotsPerDay;
	}

	@Override
	public void setTimeSlotsPerDay(int timeSlotsPerDay) throws
			WctttModelException {
		checkTimetablesEmpty("number of time slots per day");
		ValidationHelper.validateTimeSlotsPerDay(timeSlotsPerDay);
		this.timeSlotsPerDay = timeSlotsPerDay;
	}

	@Override
	@XmlAttribute(required = true)
	public int getMaxDailyLecturesPerCur() {
		return maxDailyLecturesPerCur;
	}

	@Override
	public void setMaxDailyLecturesPerCur(int maxDailyLecturesPerCur) throws
			WctttModelException {
		checkTimetablesEmpty("maximum number of daily lectures per curriculum");
		ValidationHelper.validateMaxDailyLecturesPerCur(maxDailyLecturesPerCur);
		this.maxDailyLecturesPerCur = maxDailyLecturesPerCur;
	}

	@Override
	@XmlElement(required = true)
	public ConstraintWeightings getConstrWeightings() {
		return constrWeightings;
	}

	@Override
	public void setConstrWeightings(ConstraintWeightings constrWeightings) {
		if (constrWeightings == null) {
			throw new IllegalArgumentException("Parameter 'constrWeightings' " +
					"must not be null");
		}
		this.constrWeightings = constrWeightings;
		if (!timetables.isEmpty()) {
			ConstraintViolationsCalculator constrCalc =
					new ConstraintViolationsCalculator(this);
			for (Timetable timetable : timetables) {
				timetable.setSoftConstraintPenalty(
						constrCalc.calcTimetablePenalty(timetable));
			}
		}
	}

	@Override
	@XmlElementWrapper(required = true)
	@XmlElement(name = "chair")
	public ObservableList<Chair> getChairs() {
		return chairs;
	}

	@Override
	@XmlElementWrapper(required = true)
	@XmlElement(name = "internalRoom")
	public ObservableList<InternalRoom> getInternalRooms() {
		return internalRooms;
	}

	@Override
	@XmlElementWrapper(required = true)
	@XmlElement(name = "externalRoom")
	public ObservableList<ExternalRoom> getExternalRooms() {
		return externalRooms;
	}

	@Override
	@XmlElementWrapper(required = true)
	@XmlElement(name = "course")
	public ObservableList<Course> getCourses() {
		return courses;
	}

	@Override
	@XmlElementWrapper(required = true)
	@XmlElement(name = "curriculum")
	public ObservableList<Curriculum> getCurricula() {
		return curricula;
	}

	@Override
	@XmlElementWrapper(required = true)
	@XmlElement(name = "timetable")
	public ObservableList<Timetable> getTimetables() {
		return timetables;
	}

	private boolean chairIdExists(String id) {
		for (Chair chair : chairs) {
			if (id.equals(chair.getId())) {
				return true;
			}
		}
		return false;
	}

	private boolean teacherIdExists(String id) {
		for (Chair chair : chairs) {
			for (Teacher teacher : chair.getTeachers()) {
				if (id.equals(teacher.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean roomIdExists(String id) {
		for (Room room : internalRooms) {
			if (id.equals(room.getId())) {
				return true;
			}
		}
		for (Room room : externalRooms) {
			if (id.equals(room.getId())) {
				return true;
			}
		}
		return false;
	}

	private boolean courseIdExists(String id) {
		for (Course course : courses) {
			if (id.equals(course.getId())) {
				return true;
			}
		}
		return false;
	}

	private boolean sessionIdExists(String id) {
		for (Course course : courses) {
			for (Session session : course.getLectures()) {
				if (id.equals(session.getId())) {
					return true;
				}
			}
			for (Session session : course.getPracticals()) {
				if (id.equals(session.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean curriculumIdExists(String id) {
		for (Curriculum curriculum : curricula) {
			if (id.equals(curriculum.getId())) {
				return true;
			}
		}
		return false;
	}

	private boolean timetableNameExists(String name) {
		for (Timetable timetable : timetables) {
			if (name.equals(timetable.getName())) {
				return true;
			}
		}
		return false;
	}

	private void checkIfIdAvailable(String id) throws WctttModelException {
		if ("".equals(id)) {
			throw new WctttModelException("Id '" + id + "' is not allowed");
		} else if (chairIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to a chair of the semester");
		} else if (teacherIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to a teacher of the semester");
		} else if (roomIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to a room of the semester");
		} else if (courseIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to a course of the semester");
		} else if (sessionIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to a session of the semester");
		} else if (curriculumIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to a curriculum of the semester");
		}
	}

	@Override
	public void addChair(Chair chair) throws WctttModelException {
		if (chair == null) {
			throw new IllegalArgumentException("Parameter 'chair' must not be" +
					" null");
		}
		checkTimetablesEmpty("chairs");
		checkIfIdAvailable(chair.getId());
		for (Teacher teacher : chair.getTeachers()) {
			checkIfIdAvailable(teacher.getId());
		}
		this.chairs.add(chair);
	}

	@Override
	public boolean removeChair(Chair chair) throws WctttModelException {
		if (chair == null) {
			throw new IllegalArgumentException("Parameter 'chair' must not be" +
					" null");
		}
		checkTimetablesEmpty("chairs");
		if (!chair.getTeachers().isEmpty()) {
			throw new WctttModelException("Chair cannot be removed because it" +
					" has teachers assigned to it");
		}
		for (Course course : courses) {
			if (chair.equals(course.getChair())) {
				throw new WctttModelException("Chair cannot be removed " +
						"because it is responsible for course '" + course + "'");
			}
		}
		return chairs.remove(chair);
	}

	@Override
	public void updateChairId(Chair chair, String id) throws
			WctttModelException {
		if (chair == null || id == null) {
			throw new IllegalArgumentException("Parameters 'chair' and 'id' " +
					"must not be null");
		} else if (!chairIdExists(chair.getId())) {
			throw new WctttModelException("Chair '" + chair + "' is not " +
					"assigned to the semester");
		}
		checkIfIdAvailable(id);
		chair.setId(id);
	}

	@Override
	public void updateChairData(Chair chair, String name, String abbreviation)
			throws WctttModelException {
		if (chair == null) {
			throw new IllegalArgumentException("Parameter 'chair' must not " +
					"be null");
		} else if (!chairIdExists(chair.getId())) {
			throw new WctttModelException("Chair '" + chair + "' is not " +
					"assigned to the semester");
		}
		chair.setName(name);
		chair.setAbbreviation(abbreviation);
	}

	@Override
	public void addTeacherToChair(Teacher teacher, Chair chair) throws
			WctttModelException {
		if (teacher == null || chair == null) {
			throw new IllegalArgumentException("Parameter 'teacher' and " +
					"'chair' must not be null");
		}
		checkTimetablesEmpty("chairs");
		if (!chairIdExists(chair.getId())) {
			throw new WctttModelException("Chair '" + chair + "' is not" +
					" assigned to the semester");
		}
		checkIfIdAvailable(teacher.getId());
		chair.addTeacher(teacher);
	}

	@Override
	public boolean removeTeacherFromChair(Teacher teacher, Chair chair) throws
			WctttModelException {
		if (teacher == null || chair == null) {
			throw new IllegalArgumentException("Parameters 'teacher' and " +
					"'chair' must not be null");
		}
		checkTimetablesEmpty("chairs");
		for (Course course : courses) {
			for (Session lecture : course.getLectures()) {
				if (teacher.equals(lecture.getTeacher())) {
					throw new WctttModelException("Teacher cannot be removed " +
							"because he/she is assigned to lecture '" +
							lecture + "'");
				}
			}
			for (Session practical : course.getPracticals()) {
				if (teacher.equals(practical.getTeacher())) {
					throw new WctttModelException("Teacher cannot be removed " +
							"because he/she is assigned to practical '" +
							practical + "'");
				}
			}
		}
		return chair.removeTeacher(teacher);
	}

	@Override
	public void updateTeacherId(Teacher teacher, Chair chair, String id) throws
			WctttModelException {
		if (teacher == null || chair == null || id == null) {
			throw new IllegalArgumentException("Parameter 'teacher', 'chair' " +
					"and 'id' must not be null");
		} else if (!teacherIdExists(teacher.getId())) {
			throw new WctttModelException("Teacher '" + teacher + "' is not " +
					"assigned to the semester");
		} else if (!chairIdExists(chair.getId())) {
			throw new WctttModelException("Chair '" + chair + "' is not" +
					" assigned to the semester");
		}
		checkIfIdAvailable(id);
		chair.updateTeacherId(teacher, id);
	}

	@Override
	public void updateTeacherData(Teacher teacher, String name,
	                              List<Period> unfavorablePeriods,
	                              List<Period> unavailablePeriods)
			throws WctttModelException {
		if (teacher == null) {
			throw new IllegalArgumentException("Parameter 'teacher' must not " +
					"be null");
		} else if (!teacherIdExists(teacher.getId())) {
			throw new WctttModelException("Teacher '" + teacher + "' is not " +
					"assigned to the semester");
		}
		if (!Objects.equals(unfavorablePeriods, teacher.getUnfavorablePeriods()) ||
				!Objects.equals(unavailablePeriods, teacher.getUnavailablePeriods())) {
			checkTimetablesEmpty("chairs");
		}
		for (Course course : courses) {
			for (Session lecture : course.getLectures()) {
				if (lecture.getTeacher().equals(teacher) &&
						!teacherCanFulfillPreAssignment(unavailablePeriods,
								lecture.getPreAssignment(), lecture.isDoubleSession())) {
					throw new WctttModelException("Unavailable time periods " +
							"of teacher '" + teacher + "' are conflicting " +
							"with the pre-assignment of lecture '" + lecture +
							"'");
				}
			}
			for (Session practical : course.getPracticals()) {
				if (practical.getTeacher().equals(teacher) &&
						!teacherCanFulfillPreAssignment(unavailablePeriods,
								practical.getPreAssignment(), practical.isDoubleSession())) {
					throw new WctttModelException("Unavailable time periods " +
							"of teacher '" + teacher + "' are conflicting " +
							"with the pre-assignment of practical '" +
							practical + "'");
				}
			}
		}
		teacher.setName(name);
		teacher.getUnfavorablePeriods().clear();
		teacher.getUnfavorablePeriods().addAll(unfavorablePeriods);
		teacher.getUnavailablePeriods().clear();
		teacher.getUnavailablePeriods().addAll(unavailablePeriods);
	}

	private boolean teacherCanFulfillPreAssignment(List<Period> newUnavailable,
	                                               Optional<Period> preAssignment,
	                                               boolean doubleSession) {
		if (preAssignment.isPresent()) {
			Period period = preAssignment.get();
			Period secondPeriod = null;
			boolean checkDoubleSession = doubleSession &&
					period.getTimeSlot() < timeSlotsPerDay;
			if (checkDoubleSession) {
				try {
					secondPeriod = new Period(period.getDay(),
							period.getTimeSlot() + 1);
				} catch (WctttModelException e) {
					throw new WctttModelFatalException("Implementation error," +
							" double session in the last time slot should " +
							"have been detected before", e);
				}
			}
			for (Period unavailable : newUnavailable) {
				if (unavailable.equals(period)) {
					return false;
				} else if (checkDoubleSession && unavailable.equals(secondPeriod)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void addInternalRoom(InternalRoom room) throws WctttModelException {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not be " +
					"null");
		}
		checkTimetablesEmpty("rooms");
		checkIfIdAvailable(room.getId());
		this.internalRooms.add(room);
	}

	@Override
	public void addExternalRoom(ExternalRoom room) throws WctttModelException {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not be " +
					"null");
		}
		checkTimetablesEmpty("rooms");
		checkIfIdAvailable(room.getId());
		this.externalRooms.add(room);
	}

	@Override
	public boolean removeInternalRoom(InternalRoom room)
			throws WctttModelException {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not " +
					"be null");
		}
		checkTimetablesEmpty("rooms");
		return this.internalRooms.remove(room);
	}

	@Override
	public boolean removeExternalRoom(ExternalRoom room)
			throws WctttModelException {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not " +
					"be null");
		}
		checkTimetablesEmpty("rooms");
		checkIfRoomIsUsedForSession(room);
		return this.externalRooms.remove(room);
	}

	private void checkIfRoomIsUsedForSession(ExternalRoom room)
			throws WctttModelException {
		for (Course course : courses) {
			for (Session lecture : course.getLectures()) {
				if (lecture instanceof ExternalSession &&
						room.equals(((ExternalSession) lecture).getRoom())) {
					throw new WctttModelException("Room '" + room + "' is " +
							"still used for lecture '" + lecture + "'");
				}
			}
			for (Session practical : course.getPracticals()) {
				if (practical instanceof ExternalSession &&
						room.equals(((ExternalSession) practical).getRoom())) {
					throw new WctttModelException("Room '" + room + "' is " +
							"still used for practical '" + practical + "'");
				}
			}
		}
	}

	@Override
	public void updateRoomId(Room room, String id) throws WctttModelException {
		if (room == null || id == null) {
			throw new IllegalArgumentException("Parameter 'room' and 'id' " +
					"must not be null");
		} else if (!roomIdExists(room.getId())) {
			throw new WctttModelException("Room '" + room + "' is not " +
					"assigned to the semester");
		}
		checkIfIdAvailable(id);
		room.setId(id);
	}

	@Override
	public void updateInternalRoomData(InternalRoom room, String name,
	                                   int capacity, RoomFeatures features)
			throws WctttModelException {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not be " +
					"null");
		} else if (!roomIdExists(room.getId())) {
			throw new WctttModelException("Room '" + room + "' is not " +
					"assigned to the semester");
		}
		if (capacity != room.getCapacity() ||
				!Objects.equals(features, room.getFeatures())) {
			checkTimetablesEmpty("rooms");
		}
		room.setName(name);
		room.setCapacity(capacity);
		room.setFeatures(features);
	}

	@Override
	public void updateExternalRoomData(ExternalRoom room, String name)
			throws WctttModelException {
		if (room == null) {
			throw new IllegalArgumentException("Parameter 'room' must not be " +
					"null");
		} else if (!roomIdExists(room.getId())) {
			throw new WctttModelException("Room '" + room + "' is not " +
					"assigned to the semester");
		}
		room.setName(name);
	}

	@Override
	public void addCourse(Course course) throws WctttModelException {
		if (course == null) {
			throw new IllegalArgumentException("Parameter 'course' must not " +
					"be null");
		}
		checkTimetablesEmpty("courses");
		if (chairs.isEmpty()) {
			throw new WctttModelException("You have to add at least one chair" +
					" before adding a course");
		} else if (!chairIdExists(course.getChair().getId())) {
			throw new WctttModelException("Chair '" + course.getChair() +
					"' is not assigned to the semester");
		}
		checkIfIdAvailable(course.getId());
		for (Session lecture : course.getLectures()) {
			checkIfIdAvailable(lecture.getId());
		}
		for (Session practical : course.getPracticals()) {
			checkIfIdAvailable(practical.getId());
		}
		this.courses.add(course);
	}

	@Override
	public boolean removeCourse(Course course) throws WctttModelException {
		if (course == null) {
			throw new IllegalArgumentException("Parameter 'course' must not " +
					"be null");
		}
		checkTimetablesEmpty("courses");
		for (Curriculum curriculum : curricula) {
			for (Course otherCourse : curriculum.getCourses()) {
				if (course.equals(otherCourse)) {
					throw new WctttModelException("Course cannot be removed " +
							"because it is part of curriculum '" + curriculum +
							"'");
				}
			}
		}
		return this.courses.remove(course);
	}

	@Override
	public void updateCourseId(Course course, String id) throws
			WctttModelException {
		if (course == null || id == null) {
			throw new IllegalArgumentException("Parameters 'course' and 'id' " +
					"must not be null");
		} else if (!courseIdExists(course.getId())) {
			throw new WctttModelException("Course '" + course + "' is " +
					"not assigned to the semester");
		}
		checkIfIdAvailable(id);
		course.setId(id);
	}

	@Override
	public void updateCourseData(Course course, String name, String abbreviation,
	                             Chair chair, CourseLevel courseLevel,
	                             int minNumberOfDays)
			throws WctttModelException {
		if (course == null) {
			throw new IllegalArgumentException("Parameter 'course' must not " +
					"be null");
		} else if (!courseIdExists(course.getId())) {
			throw new WctttModelException("Course '" + course + "' is not " +
					"assigned to the semester");
		}
		if (!Objects.equals(chair, course.getChair()) ||
				courseLevel != course.getCourseLevel() ||
				minNumberOfDays != course.getMinNumberOfDays()) {
			checkTimetablesEmpty("courses");
		}
		course.setName(name);
		course.setAbbreviation(abbreviation);
		course.setChair(chair);
		course.setCourseLevel(courseLevel);
		course.setMinNumberOfDays(minNumberOfDays);
	}

	private boolean teacherListIsEmpty() {
		for (Chair chair : chairs) {
			if (!chair.getTeachers().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private void addCourseSession(Session session, Course course,
	                              boolean lecture) throws WctttModelException {
		assert session != null;
		assert course != null;
		checkTimetablesEmpty("courses");
		if (!courseIdExists(course.getId())) {
			throw new WctttModelException("Course '" + course + "' is not " +
					"assigned to the semester");
		} else if (teacherListIsEmpty()) {
			throw new WctttModelException("You have to add at least one " +
					"teacher before adding a session");
		}
		checkIfIdAvailable(session.getId());
		if (lecture) {
			course.addLecture(session);
		} else {
			course.addPractical(session);
		}
		session.setCourse(course);
	}

	@Override
	public void addCourseLecture(Session lecture, Course course) throws
			WctttModelException {
		if (course == null || lecture == null) {
			throw new IllegalArgumentException("Parameter 'course' and " +
					"'lecture' must not be null");
		}
		addCourseSession(lecture, course, true);
	}

	@Override
	public boolean removeCourseLecture(Session lecture) throws
			WctttModelException {
		if (lecture == null) {
			throw new IllegalArgumentException("Parameter 'lecture' course' " +
					"must not be null");
		}
		if (!courseIdExists(lecture.getCourse().getId())) {
			throw new WctttModelException("Course '" + lecture.getCourse() +
					"' is not assigned to the semester");
		}
		checkTimetablesEmpty("courses");
		return lecture.getCourse().removeLecture(lecture);
	}

	@Override
	public void addCoursePractical(Session practical, Course course) throws
			WctttModelException {
		if (course == null || practical == null) {
			throw new IllegalArgumentException("Parameter 'course' and " +
					"'practical' must not be null");
		}
		addCourseSession(practical, course, false);
	}

	@Override
	public boolean removeCoursePractical(Session practical)
			throws WctttModelException {
		if (practical == null) {
			throw new IllegalArgumentException("Parameter 'practical' must " +
					"not be null");
		}
		if (!courseIdExists(practical.getCourse().getId())) {
			throw new WctttModelException("Course '" + practical.getCourse() +
					"' is not assigned to the semester");
		}
		checkTimetablesEmpty("courses");
		return practical.getCourse().removePractical(practical);
	}

	@Override
	public void updateCourseSessionId(Session session, Course course, String id)
			throws WctttModelException {
		if (course == null || session == null || id == null) {
			throw new IllegalArgumentException("Parameter 'session', 'course'" +
					" and 'id' must not be null");
		} else if (!courseIdExists(course.getId())) {
			throw new WctttModelException("Course '" + course + "' is not " +
					"assigned to the semester");
		} else if (!sessionIdExists(session.getId())) {
			throw new WctttModelException("Session '" + session + "' is not " +
					"assigned to the semester");
		}
		checkIfIdAvailable(id);
		course.updateSessionId(session, id);
	}

	@Override
	public void updateInternalSessionData(InternalSession session, String name,
	                                      Teacher teacher, boolean doubleSession,
	                                      Period preAssignment, int students,
	                                      RoomFeatures roomRequirements)
			throws WctttModelException {
		validateUpdateSessionDataParameters(session, teacher, doubleSession, preAssignment);
		if (!Objects.equals(teacher, session.getTeacher()) ||
				doubleSession != session.isDoubleSession() ||
				!Objects.equals(preAssignment, session.getPreAssignment().orElse(null)) ||
				students != session.getStudents() ||
				!Objects.equals(roomRequirements, session.getRoomRequirements())) {
			checkTimetablesEmpty("courses");
		}
		session.setName(name);
		session.setTeacher(teacher);
		session.setDoubleSession(doubleSession);
		session.setPreAssignment(preAssignment);
		session.setStudents(students);
		session.setRoomRequirements(roomRequirements);
	}

	@Override
	public void updateExternalSessionData(ExternalSession session, String name,
	                                      Teacher teacher, boolean doubleSession,
	                                      Period preAssignment, ExternalRoom room)
			throws WctttModelException {
		validateUpdateSessionDataParameters(session, teacher, doubleSession,
				preAssignment);
		if (!Objects.equals(teacher, session.getTeacher()) ||
				doubleSession != session.isDoubleSession() ||
				!Objects.equals(preAssignment, session.getPreAssignment().orElse(null)) ||
				!Objects.equals(room, session.getRoom())) {
			checkTimetablesEmpty("courses");
		}
		session.setName(name);
		session.setTeacher(teacher);
		session.setDoubleSession(doubleSession);
		session.setPreAssignment(preAssignment);
		session.setRoom(room);
	}

	private void validateUpdateSessionDataParameters(Session session,
	                                                 Teacher teacher,
	                                                 boolean doubleSession,
	                                                 Period preAssignment)
			throws WctttModelException {
		if (session == null) {
			throw new IllegalArgumentException("Parameter 'session' must not " +
					"be null");
		} else if (!sessionIdExists(session.getId())) {
			throw new WctttModelException("Session '" + session + "' is not " +
					"assigned to the semester");
		} else if (!teacherIdExists(teacher.getId())) {
			throw new WctttModelException("Teacher '" + teacher + "' is not " +
					"assigned to the semester");
		} else if (preAssignment != null &&
					session.getTeacher().getUnavailablePeriods().contains(preAssignment)) {
			throw new WctttModelException("Pre-assignment of session '" +
					session + "' is within the unavailable periods of " +
					"the corresponding teacher");
		}
		if (preAssignment != null && doubleSession) {
			if (preAssignment.getTimeSlot() == getTimeSlotsPerDay()) {
				throw new WctttModelException("Session '" + session + "' is a" +
						" double session and cannot be assigned to the last " +
						"time slot");
			}
			Period secondPeriod;
			try {
				secondPeriod = new Period(preAssignment.getDay(),
						preAssignment.getTimeSlot() + 1);
			} catch (WctttModelException e) {
				throw new WctttModelFatalException("Implementation error," +
						" double session in the last time slot should " +
						"have been detected before", e);
			}
			if (session.getTeacher().getUnavailablePeriods().
					contains(secondPeriod)) {
				throw new WctttModelException("Pre-assignment of double " +
						"session '" + session + "' is within the " +
						"unavailable periods of the corresponding teacher");
			}
		}
	}

	@Override
	public void addCurriculum(Curriculum curriculum) throws
			WctttModelException {
		if (curriculum == null) {
			throw new IllegalArgumentException("Parameter 'curriculum' must " +
					"not be null");
		}
		checkTimetablesEmpty("curricula");
		checkIfIdAvailable(curriculum.getId());
		checkIfCoursesAssignedToSemester(curriculum, curriculum.getCourses());
		this.curricula.add(curriculum);
	}

	private void checkIfCoursesAssignedToSemester(Curriculum curriculum,
	                                              List<Course> currCourses)
			throws WctttModelException {
		for (Course course : currCourses) {
			if (!courses.contains(course)) {
				throw new WctttModelException("Course '" + course + "' of " +
						"curriculum '" + curriculum + "' is not assigned to " +
						"the semester");
			}
		}
	}

	@Override
	public boolean removeCurriculum(Curriculum curriculum)
			throws WctttModelException {
		if (curriculum == null) {
			throw new IllegalArgumentException("Parameter 'curriculum' must " +
					"not be null");
		}
		checkTimetablesEmpty("curricula");
		return this.curricula.remove(curriculum);
	}

	@Override
	public void updateCurriculumId(Curriculum curriculum, String id) throws
			WctttModelException {
		if (curriculum == null || id == null) {
			throw new IllegalArgumentException("Parameters 'curriculum' and " +
					"'id' must not be null");
		} else if (!curriculumIdExists(curriculum.getId())) {
			throw new WctttModelException("Curriculum '" + curriculum +
					"' is not assigned to the semester");
		}
		checkIfIdAvailable(id);
		curriculum.setId(id);
	}

	@Override
	public void updateCurriculumData(Curriculum curriculum, String name,
	                                 List<Course> courses)
			throws WctttModelException {
		if (curriculum == null) {
			throw new IllegalArgumentException("Parameter 'curriculum' must " +
					"not be null");
		} else if (!curriculumIdExists(curriculum.getId())) {
			throw new WctttModelException("Curriculum '" + curriculum +
					"' is not assigned to the semester");
		}
		if (!Objects.equals(courses, curriculum.getCourses())) {
			checkTimetablesEmpty("curricula");
		}
		checkIfCoursesAssignedToSemester(curriculum, courses);
		curriculum.setName(name);
		curriculum.getCourses().clear();
		for (Course course : courses) {
			curriculum.addCourse(course);
		}
	}

	@Override
	public void addTimetable(Timetable timetable) throws WctttModelException {
		if (timetable == null) {
			throw new IllegalArgumentException("Parameter 'timetable' must " +
					"not be null");
		} else if (timetableNameExists(timetable.getName())) {
			throw new WctttModelException("Name '" + timetable +
					"' is already assigned to a timetable of the semester");
		}
		ConstraintViolationsCalculator constrCalc =
				new ConstraintViolationsCalculator(this);
		timetable.setSoftConstraintPenalty(
				constrCalc.calcTimetablePenalty(timetable));
		timetables.add(timetable);
	}

	@Override
	public boolean removeTimetable(Timetable timetable) {
		if (timetable == null) {
			throw new IllegalArgumentException("Parameter 'timetable' must " +
					"not be null");
		}
		return timetables.remove(timetable);
	}

	@Override
	public void updateTimetableName(Timetable timetable, String name)
			throws WctttModelException {
		if (timetable == null) {
			throw new IllegalArgumentException("Parameter 'timetable' must " +
					"not be null");
		} else if (timetableNameExists(name)) {
			throw new WctttModelException("Name '" + name + "' is already " +
					"assigned to a timetable of the semester");
		}
		timetable.setName(name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SemesterImpl semester = (SemesterImpl) o;
		return daysPerWeek == semester.daysPerWeek &&
				timeSlotsPerDay == semester.timeSlotsPerDay &&
				maxDailyLecturesPerCur == semester.maxDailyLecturesPerCur &&
				Objects.equals(name, semester.name) &&
				Objects.equals(constrWeightings, semester.constrWeightings) &&
				Objects.equals(chairs, semester.chairs) &&
				Objects.equals(internalRooms, semester.internalRooms) &&
				Objects.equals(externalRooms, semester.externalRooms) &&
				Objects.equals(courses, semester.courses) &&
				Objects.equals(curricula, semester.curricula) &&
				Objects.equals(timetables, semester.timetables);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, daysPerWeek, timeSlotsPerDay,
				maxDailyLecturesPerCur, constrWeightings, chairs, internalRooms,
				externalRooms, courses, curricula, timetables);
	}

	@Override
	public String toString() {
		return name;
	}
}
