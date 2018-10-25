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
 * Represents a curriculum.
 *
 * Instead of manipulating returned references directly, please use the add/
 * remove/update methods provided by this class to edit the course data. These
 * methods ensure the consistency of the curriculum.
 */
@XmlType(propOrder = {"id", "name", "courses"})
public class Curriculum {

	private String id;
	private String name;
	private final List<Course> courses = new LinkedList<>();

	public Curriculum() {
		this.id = "curriculum";
		this.name = "curriculum";
	}

	public Curriculum(String id, String name) {
		if (id == null || name == null) {
			throw new IllegalArgumentException("Parameters 'id' and 'name' " +
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

	@XmlElementWrapper(required = true)
	@XmlElement(name = "course")
	@XmlIDREF
	public List<Course> getCourses() {
		return courses;
	}

	private boolean courseIdExists(String id) {
		for (Course course : courses) {
			if (id.equals(course.getId())) {
				return true;
			}
		}
		return false;
	}

	public void addCourse(Course course) throws WctttModelException {
		if (course == null) {
			throw new IllegalArgumentException("Parameter 'course' must not " +
					"be null");
		} else if (courseIdExists(course.getId())) {
			throw new WctttModelException("Id '" + course.getId() + "' is " +
					"already assigned to another course of the curriculum");
		}
		courses.add(course);
	}

	public boolean removeCourse(Course course) {
		if (course == null) {
			throw new IllegalArgumentException("Parameter 'course' must not " +
					"be null");
		}
		return courses.remove(course);
	}

	public void updateCourseId(Course course, String id) throws
			WctttModelException {
		if (course == null || id == null) {
			throw new IllegalArgumentException("Parameters 'course' and 'id' " +
					"must not be null");
		} else if (!courseIdExists(course.getId())) {
			throw new WctttModelException("Course '" + id + "' is not " +
					"assigned to the curriculum");
		} else if (courseIdExists(id)) {
			throw new WctttModelException("Id '" + id + "' is already " +
					"assigned to another course of the curriculum");
		}
		course.setId(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Curriculum that = (Curriculum) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(name, that.name) &&
				Objects.equals(courses, that.courses);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, courses);
	}

	@Override
	public String toString() {
		return name;
	}

}
