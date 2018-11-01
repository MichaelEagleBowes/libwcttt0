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

import java.util.*;

/**
 * Provides functionality to calculate the number violated soft and hard
 * constraints for a timetable.
 */
public class ConstraintViolationsCalculator {

	private Semester semester;

	public ConstraintViolationsCalculator(Semester semester) {
		this.semester = semester;
	}

	/**
	 * This method checks for a timetable assignment whether it would violate
	 * any hard constraints if it would be added to the timetable. Thus, the
	 * method is used BEFORE the assignment is added to the timetable.
	 *
	 * @param timetable the timetable that should be checked.
	 * @param period the period to which the assignment should be assigned.
	 * @param assignment the new assignment that should be checked.
	 * @return a list containing every hard constraint as many times as it would
	 * have been violated by the assignment.
	 */
	public List<ConstraintType> calcAssignmentHardViolations(
			Timetable timetable, TimetablePeriod period,
			TimetableAssignment assignment) {
		List<ConstraintType> hardViolations = new LinkedList<>();

		if (!assignment.getSession().isLecture()) {
			hardViolations.addAll(Collections.nCopies(
					h1ViolationCount(period, assignment), ConstraintType.h1));
		} else {
			hardViolations.addAll(Collections.nCopies(
					h2ViolationCount(period, assignment), ConstraintType.h2));
		}

		hardViolations.addAll(Collections.nCopies(
				h3ViolationCount(period, assignment), ConstraintType.h3));

		if (assignment.getSession().isLecture()) {
			hardViolations.addAll(Collections.nCopies(
					h4ViolationCount(period, assignment), ConstraintType.h4));
		} else {
			hardViolations.addAll(Collections.nCopies(
					h5ViolationCount(period, assignment), ConstraintType.h5));
		}

		hardViolations.addAll(Collections.nCopies(
				h6ViolationCount(period, assignment), ConstraintType.h6));

		hardViolations.addAll(Collections.nCopies(
				h7ViolationCount(period, assignment), ConstraintType.h7));

		if (assignment.getSession().isLecture()) {
			hardViolations.addAll(Collections.nCopies(
					h8ViolationCount(timetable, period, assignment),
					ConstraintType.h8));
		}

		hardViolations.addAll(Collections.nCopies(
				h9ViolationCount(period, assignment), ConstraintType.h9));

		hardViolations.addAll(Collections.nCopies(
				h10ViolationCount(assignment), ConstraintType.h10));

		return hardViolations;
	}

	public double calcTimetablePenalty(Timetable timetable) {
		double penalty = 0.0;

		List<ConstraintType> s6Violations = Collections.nCopies(
				s6ViolationCount(timetable), ConstraintType.s6);
		penalty += calculateViolationsPenalty(s6Violations);

		for (Course course : semester.getCourses()) {
			List<ConstraintType> violations =
					calcCourseSoftViolations(course, timetable);
			penalty += calculateViolationsPenalty(violations);
		}

		for (Curriculum curriculum : semester.getCurricula()) {
			List<ConstraintType> violations =
					calcCurriculumSoftViolations(curriculum, timetable);
			penalty += calculateViolationsPenalty(violations);
		}

		for (TimetableDay day : timetable.getDays()) {
			for (TimetablePeriod period : day.getPeriods()) {
				for (TimetableAssignment assignment : period.getAssignments()) {
					List<ConstraintType> violations = calcAssignmentSoftViolations(
							period, assignment, timetable);
					penalty += calculateViolationsPenalty(violations);
				}
			}
		}

		return penalty;
	}

	private double calculateViolationsPenalty(List<ConstraintType> violations) {
		double penalty = 0.0;
		for (ConstraintType violation : violations) {
			double weighting = semester.getConstrWeightings().
					getWeighting(violation);
			if (weighting != -1.0) {
				penalty += weighting * 1.0;
			}
		}
		return penalty;
	}

	private List<ConstraintType> calcCourseSoftViolations(
			Course course, Timetable timetable) {
		List<ConstraintType> violations = new LinkedList<>();

		violations.addAll(Collections.nCopies(
				s2ViolationCount(course, timetable), ConstraintType.s2));

		violations.addAll(Collections.nCopies(
				s4ViolationCount(course, timetable), ConstraintType.s4));

		return violations;
	}

	private List<ConstraintType> calcCurriculumSoftViolations(
			Curriculum curriculum, Timetable timetable) {
		List<ConstraintType> violations = new LinkedList<>();

		violations.addAll(Collections.nCopies(
				s7ViolationCount(curriculum, timetable), ConstraintType.s1));

		return violations;
	}

	private List<ConstraintType> calcAssignmentSoftViolations(
			TimetablePeriod period, TimetableAssignment assignment,
			Timetable timetable) {
		List<ConstraintType> violations = new LinkedList<>();

		violations.addAll(Collections.nCopies(
				s1ViolationCount(assignment), ConstraintType.s1));

		violations.addAll(Collections.nCopies(
				s3ViolationCount(period, assignment, timetable),
				ConstraintType.s3));

		violations.addAll(Collections.nCopies(
				s5ViolationCount(period, assignment), ConstraintType.s5));

		return violations;
	}

	private int h1ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		int counter = 0;
		for (TimetableAssignment otherAssignment : period.getAssignments()) {
			if (!otherAssignment.equals(assignment) &&
					otherAssignment.getSession().getCourse().equals(
							assignment.getSession().getCourse())) {
				counter++;
			}
		}
		return counter;
	}

	private int h2ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		if (assignment.getSession().getCourse().getPracticals().size() == 1) {
			// If the course has only one practical, it is treated like a lecture
			return h1ViolationCount(period, assignment);
		}
		int counter = 0;
		for (TimetableAssignment otherAssignment : period.getAssignments()) {
			if (!otherAssignment.equals(assignment) &&
					otherAssignment.getSession().getCourse().equals(
							assignment.getSession().getCourse()) &&
					otherAssignment.getSession().isLecture()) {
				counter++;
			}
		}
		return counter;
	}

	private int h3ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		int counter = 0;
		for (TimetableAssignment otherAssignment : period.getAssignments()) {
			if (!otherAssignment.equals(assignment) &&
					otherAssignment.getRoom().equals(assignment.getRoom())) {
				counter++;
			}
		}
		return counter;
	}

	private int h4ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		int counter = 0;
		for (Curriculum curriculum : semester.getCurricula()) {
			if (curriculum.getCourses().contains(
					assignment.getSession().getCourse())) {
				for (TimetableAssignment otherAssignment : period.getAssignments()) {
					if (curriculum.getCourses().contains(
							otherAssignment.getSession().getCourse()) 
							&& otherAssignment.getSession().getCourse().getPracticals().size() == 1) {
						counter++;
					}
				}
			}
		}
		return counter;
	}

	private int h5ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		int numberOfCoursePracticals =
				assignment.getSession().getCourse().getPracticals().size();
		int numberOfCoursePracticalsInPeriod = 0;
		for (TimetableAssignment otherAssignment : period.getAssignments()) {
			if (otherAssignment.getSession().getCourse().equals(
					assignment.getSession().getCourse()) &&
					!assignment.getSession().isLecture()) {
				numberOfCoursePracticalsInPeriod++;
			}
		}
		
		if (numberOfCoursePracticals == 1 ||
				numberOfCoursePracticals == numberOfCoursePracticalsInPeriod) {
			return h4ViolationCount(period, assignment);
		}
		
		int counter = 0;
		for (Curriculum curriculum : semester.getCurricula()) {
			if (curriculum.getCourses().contains(
					assignment.getSession().getCourse())) {
				for (TimetableAssignment otherAssignment : period.getAssignments()) {
					if (curriculum.getCourses().contains(
							otherAssignment.getSession().getCourse()) &&
							(otherAssignment.getSession().isLecture() ||
									otherAssignment.getSession().getCourse().
											getPracticals().size() == 1)) {
						counter++;
					}
				}
			}
		}
		return counter;
	}

	private int h6ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		int counter = 0;
		for (TimetableAssignment otherAssignment : period.getAssignments()) {
			if (!otherAssignment.equals(assignment) &&
					otherAssignment.getSession().getTeacher().equals(
							assignment.getSession().getTeacher())) {
				counter++;
			}
		}
		return counter;
	}

	private int h7ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		for (Period unavailable :
				assignment.getSession().getTeacher().getUnavailablePeriods()) {
			if (unavailable.getDay() == period.getDay() &&
					unavailable.getTimeSlot() == period.getTimeSlot()) {
				return 1;
			}
		}
		return 0;
	}

	private int h8ViolationCount(Timetable timetable, TimetablePeriod period,
	                             TimetableAssignment assignment) {
		TimetableDay day = timetable.getDays().get(period.getDay() - 1);
		for (TimetablePeriod otherPeriod : day.getPeriods()) {
			for (TimetableAssignment otherAssgmt : otherPeriod.getAssignments()) {
				if (otherAssgmt.getSession().getCourse().equals(
						assignment.getSession().getCourse()) &&
						otherAssgmt.getSession().isLecture() &&
						!(otherAssgmt.getSession().equals(assignment.getSession())
								&& otherAssgmt.getSession().isDoubleSession())) {
					return 1;
				}
			}
		}
		return 0;
	}

	private int h9ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		if (!assignment.getSession().getPreAssignment().isPresent()) {
			return 0;
		}

		Period preAssignment = assignment.getSession().getPreAssignment().get();
		Period secondPeriod;
		try {
			secondPeriod = new Period(period.getDay(),
					period.getTimeSlot() + 1);
		} catch (WctttModelException e) {
			throw new WctttUtilFatalException("Implementation error, " +
					"period created with illegal parameters", e);
		}
		if (preAssignment.getDay() == period.getDay() &&
				(preAssignment.getTimeSlot() == period.getTimeSlot() ||
				(assignment.getSession().isDoubleSession() &&
						(preAssignment.getTimeSlot() + 1) ==
								period.getTimeSlot()))) {
			return 0;
		} else {
			return 1;
		}
	}

	private int h10ViolationCount(TimetableAssignment assignment) {
		if (assignment.getRoom() instanceof InternalRoom &&
				assignment.getSession() instanceof InternalSession &&
				((InternalRoom) assignment.getRoom()).getFeatures().compareTo(
						((InternalSession) assignment.getSession()).
								getRoomRequirements()) < 0) {
			return 1;
		} else {
			return 0;
		}
	}

	private int s1ViolationCount(TimetableAssignment assignment) {
		if (assignment.getRoom() instanceof InternalRoom &&
				assignment.getSession() instanceof InternalSession) {
			int actualRoomCapacity =
					((InternalRoom) assignment.getRoom()).getCapacity();
			int requiredRoomCapacity =
					((InternalSession) assignment.getSession()).getStudents();
			if (actualRoomCapacity > requiredRoomCapacity) {
				return actualRoomCapacity - requiredRoomCapacity;
			} else {
				return requiredRoomCapacity - actualRoomCapacity;
			}
		} else {
			return 0;
		}
	}

	private int s2ViolationCount(Course course, Timetable timetable) {
		int earliestDay = ValidationHelper.PERIOD_DAY_MIN;
		int lastDay = ValidationHelper.DAYS_PER_WEEK_MAX;
		for (TimetableDay day : timetable.getDays()) {
			for (TimetablePeriod period : day.getPeriods()) {
				for (TimetableAssignment assignment : period.getAssignments()) {
					if (assignment.getSession().getCourse().equals(course) &&
							assignment.getSession().isLecture()) {
						if (period.getDay() < earliestDay) {
							earliestDay = period.getDay();
						} else if (period.getDay() > lastDay) {
							lastDay = period.getDay();
						}
					}
				}
			}
		}
		int difference = course.getMinNumberOfDays() -
				((lastDay - earliestDay) + 1);
		if (difference > 0) {
			return difference;
		} else {
			return 0;
		}
	}


	private int s3ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment,
	                             Timetable timetable) {
		if (!assignment.getSession().isLecture() ||
				semester.getTimeSlotsPerDay() == 1) {
			return 0;
		}
		int counter = 0;
		for (Curriculum curriculum : semester.getCurricula()) {
			if (curriculum.getCourses().contains(
					assignment.getSession().getCourse())) {
				boolean foundAdjacent = false;
				if (period.getTimeSlot() >
						ValidationHelper.PERIOD_TIME_SLOT_MIN) {
					TimetablePeriod before = timetable.getDays().get(
							period.getDay() - 1).getPeriods().get(
							period.getTimeSlot() - 2);
					for (TimetableAssignment assBefore : before.getAssignments()) {
						if (assBefore.getSession().isLecture() &&
								curriculum.getCourses().contains(
										assBefore.getSession().getCourse())) {
							foundAdjacent = true;
							break;
						}
					}
				}
				if (!foundAdjacent &&
						period.getTimeSlot() < semester.getTimeSlotsPerDay()) {
					TimetablePeriod after = timetable.getDays().get(
							period.getDay() - 1).getPeriods().get(
							period.getTimeSlot());
					for (TimetableAssignment assAfter : after.getAssignments()) {
						if (assAfter.getSession().isLecture() &&
								curriculum.getCourses().contains(
										assAfter.getSession().getCourse())) {
							foundAdjacent = true;
							break;
						}
					}
				}
				if (!foundAdjacent) {
					counter++;
				}
			}
		}
		return counter;
	}

	private int s4ViolationCount(Course course, Timetable timetable) {
		int counter = 0;
		Room room = null;
		for (TimetableDay day : timetable.getDays()) {
			for (TimetablePeriod period : day.getPeriods()) {
				for (TimetableAssignment assgmt : period.getAssignments()) {
					if (assgmt.getSession().getCourse().equals(course) &&
							assgmt.getSession().isLecture()) {
						if (room == null) {
							room = assgmt.getRoom();
						} else if (!room.equals(assgmt.getRoom())) {
							counter++;
						}
					}
				}
			}
		}
		return counter;
	}

	private int s5ViolationCount(TimetablePeriod period,
	                             TimetableAssignment assignment) {
		for (Period unfavorable :
				assignment.getSession().getTeacher().getUnfavorablePeriods()) {
			if (unfavorable.getDay() == period.getDay() &&
					unfavorable.getTimeSlot() == period.getTimeSlot()) {
				return 1;
			}
		}
		return 0;
	}

	private int s6ViolationCount(Timetable timetable) {
		int counter = 0;
		for (TimetableDay day : timetable.getDays()) {
			List<List<Teacher>> activeTeachers =
					new ArrayList<>(semester.getTimeSlotsPerDay());
			for (TimetablePeriod period : day.getPeriods()) {
				List<Teacher> activeInTimeSlot = new LinkedList<>();
				for (TimetableAssignment assgmt : period.getAssignments()) {
					activeInTimeSlot.add(assgmt.getSession().getTeacher());
				}
				activeTeachers.add(activeInTimeSlot);
			}
			for (int i = 0; i < activeTeachers.size() - 2; i++) {
				for (Teacher teacher : activeTeachers.get(i)) {
					for (int j = i + 1; j < activeTeachers.size(); j++) {
						if (!activeTeachers.get(j).contains(teacher)) {
							if (j - i > 2) {
								counter += j - i;
								for (int k = i + 1; k < j; k++) {
									activeTeachers.get(k).remove(teacher);
								}
							}
							break;
						} else if (j == activeTeachers.size() - 1 &&
								activeTeachers.get(j).contains(teacher)) {
							if ((j - i) + 1 > 2) {
								counter += (j - i) + 1;
							}
						}
					}

				}
			}
		}
		return counter;
	}

	private int s7ViolationCount(Curriculum curriculum, Timetable timetable) {
		int counter = 0;
		for (TimetableDay day : timetable.getDays()) {
			int lecturesPerDay = 0;
			for (TimetablePeriod period : day.getPeriods()) {
				for (TimetableAssignment assgmt : period.getAssignments()) {
					if (curriculum.getCourses().contains(
							assgmt.getSession().getCourse()) &&
							assgmt.getSession().isLecture()) {
						lecturesPerDay++;
					}
				}
			}
			if (lecturesPerDay > semester.getMaxDailyLecturesPerCur()) {
				counter += lecturesPerDay - semester.getMaxDailyLecturesPerCur();
			}
		}
		return counter;
	}
}

