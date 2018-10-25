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
 * Represents a course.
 *
 * Instead of manipulating returned references directly, please use the add/
 * remove/update methods provided by this class to edit the session data. These
 * methods ensure the consistency of the course.
 */
@XmlType(propOrder = {"id", "name", "abbreviation", "chair", "courseLevel",
		"minNumberOfDays", "lectures", "practicals"})
public class Course {

	private String id;
	private String name;
	private String abbreviation;
	private Chair chair;
	private CourseLevel courseLevel;
	private int minNumberOfDays;
	private final List<Session> lectures = new LinkedList<>();
	private final List<Session> practicals = new LinkedList<>();

	public Course() {
		this.id = "course";
		this.name = "course";
		this.abbreviation = "";
		this.chair = new Chair();
		this.courseLevel = CourseLevel.Bachelor;
		this.minNumberOfDays = ValidationHelper.MIN_NUM_OF_DAYS_MIN;
	}

	public Course(String id, String name, String abbreviation, Chair chair,
	              CourseLevel courseLevel, int minNumberOfDays) throws
			WctttModelException {
		if (id == null || name == null || abbreviation == null ||
				chair == null) {
			throw new IllegalArgumentException("Parameters 'id', 'name', " +
					"'abbreviation' and 'chair' must not be null");
		}
		ValidationHelper.validateMinNumOfDays(minNumberOfDays);
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
		this.chair = chair;
		this.courseLevel = courseLevel;
		this.minNumberOfDays = minNumberOfDays;
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

	@XmlAttribute(required = true)
	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		if (abbreviation == null) {
			throw new IllegalArgumentException("Parameter 'abbreviation' must" +
					" not be null");
		}
		this.abbreviation = abbreviation;
	}

	@XmlElement(required = true)
	@XmlIDREF
	public Chair getChair() {
		return chair;
	}

	public void setChair(Chair chair) {
		if (chair == null) {
			throw new IllegalArgumentException("Parameter 'chair' must not " +
					"be null");
		}
		this.chair = chair;
	}

	@XmlAttribute(required = true)
	public CourseLevel getCourseLevel() {
		return courseLevel;
	}

	public void setCourseLevel(CourseLevel courseLevel) {
		this.courseLevel = courseLevel;
	}

	@XmlAttribute(required = true)
	public int getMinNumberOfDays() {
		return minNumberOfDays;
	}

	public void setMinNumberOfDays(int minNumberOfDays) throws
			WctttModelException {
		ValidationHelper.validateMinNumOfDays(minNumberOfDays);
		this.minNumberOfDays = minNumberOfDays;
	}

	@XmlElementWrapper(required = true)
	@XmlElement(name = "lecture")
	public List<Session> getLectures() {
		return lectures;
	}

	@XmlElementWrapper(required = false)
	@XmlElement(name = "practical")
	public List<Session> getPracticals() {
		return practicals;
	}

	private boolean lectureIdExists(String id) {
		for (Session lecture : lectures) {
			if (id.equals(lecture.getId())) {
				return true;
			}
		}
		return false;
	}

	private boolean practicalIdExists(String id) {
		for (Session practical : practicals) {
			if (id.equals(practical.getId())) {
				return true;
			}
		}
		return false;
	}

	private boolean sessionIdExists(String id) {
		return lectureIdExists(id) || practicalIdExists(id);
	}

	private void addSession(Session session, boolean isLecture) throws
			WctttModelException {
		if (session == null) {
			throw new IllegalArgumentException("Parameter 'session' must not " +
					"be null");
		} else if (lectureIdExists(session.getId())) {
			throw new WctttModelException("Id '" + session.getId() + "' is " +
					"already assigned to another lecture of the course");
		} else if (practicalIdExists(session.getId())) {
			throw new WctttModelException("Id '" + session.getId() + "' is " +
					"already assigned to another practical of the course");
		}
		if (isLecture) {
			lectures.add(session);
		} else {
			practicals.add(session);
		}
	}

	public void addLecture(Session lecture) throws WctttModelException {
		addSession(lecture, true);
	}

	public void addPractical(Session practical) throws WctttModelException {
		addSession(practical, false);
	}

	public boolean removeLecture(Session lecture) {
		if (lecture == null) {
			throw new IllegalArgumentException("Parameter 'lecture' must not " +
					"be null");
		}
		return lectures.remove(lecture);
	}

	public boolean removePractical(Session practical) {
		if (practical == null) {
			throw new IllegalArgumentException("Parameter 'practical' must " +
					"not be null");
		}
		return practicals.remove(practical);
	}

	public void updateSessionId(Session session, String id) throws
			WctttModelException {
		if (session == null || id == null) {
			throw new IllegalArgumentException("Parameters 'session' and 'id'" +
					" must not be null");
		} else if (!sessionIdExists(session.getId())) {
			throw new WctttModelException("Session '" + id + "' is not " +
					"assigned to the course");
		} else if (lectureIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to another lecture of the course");
		} else if (practicalIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to another practical of the course");
		}
		session.setId(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Course course = (Course) o;
		return minNumberOfDays == course.minNumberOfDays &&
				Objects.equals(id, course.id) &&
				Objects.equals(name, course.name) &&
				Objects.equals(abbreviation, course.abbreviation) &&
				Objects.equals(chair, course.chair) &&
				courseLevel == course.courseLevel &&
				Objects.equals(lectures, course.lectures) &&
				Objects.equals(practicals, course.practicals);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, abbreviation, chair, courseLevel,
				minNumberOfDays, lectures, practicals);
	}

	@Override
	public String toString() {
		return abbreviation + " - " + name;
	}

}
