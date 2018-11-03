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

package wcttt.lib.algorithms.tabu_based_memetic_approach;

import wcttt.lib.algorithms.WctttAlgorithmException;
import wcttt.lib.algorithms.WctttAlgorithmFatalException;
import wcttt.lib.model.*;
import wcttt.lib.util.ConstraintViolationsCalculator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Methods whose functionality is required in multiple classes of the algorithm.
 */
class Util {

	/**
	 * Creates a list of rooms that are suitable for a session.
	 *
	 * @param session the session.
	 * @param semester the semester the session belongs to.
	 * @return a list of rooms that are suitable for the session.
	 * @throws WctttAlgorithmException if no suitable rooms were found.
	 */
	static List<InternalRoom> findSuitableRooms(InternalSession session,
	                                            Semester semester)
			throws WctttAlgorithmException {
		List<InternalRoom> suitableRooms = new LinkedList<>();
		semester.getInternalRooms().forEach(room -> {
			if (room.getFeatures().compareTo(session.getRoomRequirements()) >= 0) {
				suitableRooms.add(room);
			}
		});

		if (suitableRooms.isEmpty()) {
			throw new WctttAlgorithmException("No suitable room was found for" +
					" session '" + session + "'");
		} else {
			return suitableRooms;
		}
	}

	/**
	 * Creates a list that contains all periods of the semester.
	 *
	 * @param semester the semester.
	 * @return a list of all periods of the semester.
	 */
	static List<Period> createPeriodList(Semester semester) {
		List<Period> periods = new LinkedList<>();
		for (int i = 1; i <= semester.getDaysPerWeek(); i++) {
			for (int j = 1; j <= semester.getTimeSlotsPerDay(); j++) {
				Period period;
				try {
					period = new Period(i, j);
				} catch (WctttModelException e) {
					throw new WctttAlgorithmFatalException("Implementation " +
							"error, a period was created with illegal " +
							"parameters", e);
				}
				periods.add(period);
			}
		}
		return periods;
	}

	/**
	 * Takes a sessions and randomly assigns it to a suitable room and period.
	 *
	 * Randomization is realized by shuffling the list of suitable rooms before
	 * they are iteratively checked for a suitable period.
	 *
	 * @param session the session that should be assigned.
	 * @param timetable the timetable the session should be assigned to.
	 * @param semester the semester the timetable belongs to.
	 * @param orderedPeriods the list of periods that is used to find a suitable
	 *                       assignment, the order of the list determines the
	 *                       order in which the periods are tried to be used.
	 * @param unassignedSessions the list of unassigned sessions, can be
	 *                              {@code null}.
	 * @param periodUsages the mapping of periods to their number of usages, can
	 *                        be {@code null}.
	 * @param unassignedPeriods the mapping of internal rooms to the respective
	 *                          periods where the room is still free, can be
	 *                          {@code null}.
	 * @param assignmentMap the mapping of sessions to their respectively
	 *                         assigned periods, or null if unassigned, can be
	 *                         {@code null}.
	 * @return {@code true} if an assignment was found, otherwise {@code false}.
	 * @throws WctttAlgorithmException if no suitable room was found.
	 */
	static boolean assignSessionRandomly(InternalSession session, Timetable timetable,
	                                     Semester semester, List<Period> orderedPeriods,
	                                     List<InternalSession> unassignedSessions,
	                                     Map<Period, Integer> periodUsages,
	                                     Map<InternalRoom, List<Period>> unassignedPeriods,
	                                     Map<Session, TimetablePeriod> assignmentMap)
			throws WctttAlgorithmException {
		List<InternalRoom> suitableRooms = findSuitableRooms(session, semester);
		Collections.shuffle(suitableRooms);
		
		for (Period period : orderedPeriods) {
			Period secondPeriod = null;
			if (session.isDoubleSession()) {
				if (period.getTimeSlot() == semester.getTimeSlotsPerDay()) {
					// Double session cannot be assigned to last time slot
					continue;
				} else {
					try {
						secondPeriod = new Period(period.getDay(),
								period.getTimeSlot() + 1);
					} catch (WctttModelException e) {
						throw new WctttAlgorithmFatalException(
								"Implementation error, period created with " +
										"illegal parameters", e);
					}
				}
			}
			for (InternalRoom room : suitableRooms) {				
				//Check if the room is free
				if (roomIsFree(room, period, timetable, unassignedPeriods) &&
						(!session.isDoubleSession() ||
								roomIsFree(room, secondPeriod, timetable,
										unassignedPeriods))) {
					try {
						assignSession(session, period, room, timetable,
								semester, periodUsages, unassignedPeriods,
								assignmentMap);
						if (unassignedSessions != null) {
							unassignedSessions.remove(session);
						}
						return true;
					} catch (WctttAlgorithmException e) {
						// ignore hard constraint violation and search on
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether a room is free in a period.
	 *
	 * @param room the room that is checked.
	 * @param period the period that is checked.
	 * @param timetable the timetable that is used for checking if {@code
	 *                     unassigned periods == null}.
	 * @param unassignedPeriods a mapping from rooms to the periods that are
	 *                             still free. Can be {@code null}.
	 * @return true if the room is free at the period, otherwise false.
	 */
	static boolean roomIsFree(InternalRoom room, Period period, Timetable timetable,
	                           Map<InternalRoom, List<Period>> unassignedPeriods) {
		if (unassignedPeriods != null) {
			return unassignedPeriods.get(room).contains(period);
		} else {
			TimetablePeriod tablePeriod =
					timetable.getDays().get(period.getDay() - 1).
							getPeriods().get(period.getTimeSlot() - 1);
			for (TimetableAssignment assgmt : tablePeriod.getAssignments()) {
				if (assgmt.getRoom().equals(room)) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Prints the plan for a given room. Used for debugging.
	 * @param timetable
	 * @param parentPeriod
	 * @param parentAssgmt
	 */
	public static void printRoomPlan(Timetable timetable, TimetablePeriod parentPeriod, TimetableAssignment parentAssgmt) {
		System.out.println("Room plan: ");
		for(TimetablePeriod period : timetable.getDays().get(parentPeriod.getDay() - 1).getPeriods()) {
			for(TimetableAssignment assignment : period.getAssignments()) {
				if(assignment.getRoom().equals(parentAssgmt.getRoom())) {
					System.out.println(" - " + assignment + "  : " +  period.getTimeSlot());
				}
			}
		}
		System.out.println("END ROOM PLAN");
	}

	/**
	 * Assigns a (double) session to a timetable.
	 *
	 * @param session the session that should be assigned.
	 * @param period the period the session should be assigned to.
	 * @param room the room the session should be assigned to.
	 * @param timetable the timetable the session should be assigned to.
	 * @param semester the semester the timetable belongs to.
	 * @param periodUsages the mapping of periods to their number of usages, can
	 *                        be {@code null}.
	 * @param unassignedPeriods the mapping of internal rooms to the respective
	 *                          periods where the room is still free, can be
	 *                          {@code null}.
	 * @param assignmentMap the mapping of sessions to their respectively
	 *                         assigned periods, or null if unassigned, can be
	 *                         {@code null}.
	 * @throws WctttAlgorithmException if the assignment violates any hard
	 * constraints.
	 */
	static void assignSession(Session session, Period period, Room room,
	                          Timetable timetable, Semester semester,
	                          Map<Period, Integer> periodUsages,
	                          Map<InternalRoom, List<Period>> unassignedPeriods,
	                          Map<Session, TimetablePeriod> assignmentMap)
			throws WctttAlgorithmException {
		ConstraintViolationsCalculator constraintCalc =
				new ConstraintViolationsCalculator(semester);
		TimetableAssignment firstAssgmt = new TimetableAssignment();
		firstAssgmt.setSession(session);
		firstAssgmt.setRoom(room);
		TimetableAssignment secondAssgmt = null;
		if (session.isDoubleSession()) {
			secondAssgmt = new TimetableAssignment();
			secondAssgmt.setSession(session);
			secondAssgmt.setRoom(room);
		}
		try {
			TimetablePeriod firstPeriod = timetable.getDays().get(
					period.getDay() - 1).getPeriods().get(period.getTimeSlot() - 1);
			List<ConstraintType> hardConstraintViolations = constraintCalc.
					calcAssignmentHardViolations(timetable,
							firstPeriod, firstAssgmt);
			if (!hardConstraintViolations.isEmpty()) {
				throw new WctttAlgorithmException("Assignment of session '" +
						session + "' to period '" + period + "' and room '" +
						room + "' violates the following hard constraints: " +
						hardConstraintViolations);
			}
			if (session.isDoubleSession()) {
				TimetablePeriod secondPeriod = timetable.getDays().get(
						period.getDay() - 1).getPeriods().get(period.getTimeSlot());
				hardConstraintViolations = constraintCalc.calcAssignmentHardViolations(
						timetable, secondPeriod, secondAssgmt);
				if (!hardConstraintViolations.isEmpty()) {
					throw new WctttAlgorithmException("Assignment of session '" +
							session + "' to period '" + period + "' and " +
							"room '" + room + "' violates the following hard " +
							"constraints: " + hardConstraintViolations);
				}
				secondPeriod.addAssignment(secondAssgmt);
				Period periodTwo =
						new Period(period.getDay(), period.getTimeSlot() + 1);
				if (periodUsages != null) {
					periodUsages.put(periodTwo, periodUsages.get(periodTwo) + 1);
				}
				if (room instanceof InternalRoom && unassignedPeriods != null) {
					unassignedPeriods.get(room).remove(periodTwo);
				}
			}
			if (assignmentMap != null) {
				assignmentMap.put(session, firstPeriod);
			}
			firstPeriod.addAssignment(firstAssgmt);
			if (periodUsages != null) {
				periodUsages.put(period, periodUsages.get(period) + 1);
			}
			if (room instanceof InternalRoom && unassignedPeriods != null) {
				unassignedPeriods.get(room).remove(period);
			}
			
		} catch (WctttModelException e) {
			throw new WctttAlgorithmFatalException("Implementation error, " +
					"problem while adding an assignment to the timetable", e);
		}
	}
}
