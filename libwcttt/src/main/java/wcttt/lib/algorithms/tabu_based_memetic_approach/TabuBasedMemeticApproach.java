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

import wcttt.lib.algorithms.*;
import wcttt.lib.model.*;
import wcttt.lib.util.ConstraintViolationsCalculator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of the tabu-based memetic approach, which was proposed by
 * Salwani Abdullah and Hamza Turabieh in 'On the use of multi neighbourhood
 * structures within a Tabu-based memetic approach to university timetabling
 * problems', 2012.
 */
public class TabuBasedMemeticApproach extends AbstractAlgorithm {

	private static final String NAME = "Tabu-based memetic approach";
	private static final List<ParameterDefinition> PARAMETERS = Arrays.asList(
			new ParameterDefinition("Population size", "50", ParameterType.INT),
			new ParameterDefinition("Crossover rate", "0.8",ParameterType.DOUBLE),
			new ParameterDefinition("Mutation rate", "0.04",ParameterType.DOUBLE),
			new ParameterDefinition("Tabu list size", "3", ParameterType.INT));
	private static final int POPULATION_SIZE_MIN = 2;
	private static final double CROSSOVER_RATE_MIN = 0.0;
	private static final double CROSSOVER_RATE_MAX = 1.0;
	private static final double MUTATION_RATE_MIN = 0.0;
	private static final double MUTATION_RATE_MAX = 1.0;
	private static final int TABU_LIST_SIZE_MIN = 1;
	private static final List<NeighborhoodStructure> NBS_LIST = Arrays.asList(
			new NeighborhoodStructure2(),
			new NeighborhoodStructure3()
			// TODO add more neighborhood structures
	);

	private int populationSize;
	private double crossoverRate;
	private double mutationRate;
	private int tabuListSize;
	private final int numberOfSessions;

	public TabuBasedMemeticApproach(Semester semester) {
		super(semester);
		numberOfSessions = calcNumberOfSessions();
	}

	private int calcNumberOfSessions() {
		int counter = 0;
		for (Course course : getSemester().getCourses()) {
			for (Session lecture : course.getLectures()) {
				counter++;
			}
			for (Session practical : course.getPracticals()) {
				counter++;
			}
		}
		return counter;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<ParameterDefinition> getParameters() {
		return PARAMETERS;
	}

	@Override
	public void setParameterValues(List<ParameterValue> parameterValues)
			throws WctttAlgorithmException {
		List<ParameterDefinition> expected = new ArrayList<>(PARAMETERS);
		for (ParameterValue value : parameterValues) {
			for (int i = 0; i < PARAMETERS.size(); i++) {
				if (value.getDefinition().equals(PARAMETERS.get(i))) {
					expected.remove(PARAMETERS.get(i));
					switch (i) {
						case 0:
							populationSize = (Integer) value.getValue();
							validatePopulationSize();
							break;
						case 1:
							crossoverRate = (Double) value.getValue();
							validateCrossoverRate();
							break;
						case 2:
							mutationRate = (Double) value.getValue();
							validateMutationRate();
							break;
						case 3:
							tabuListSize = (Integer) value.getValue();
							validateTabuListSize();
							break;
					}
				}
			}
		}
		if (!expected.isEmpty()) {
			throw new WctttAlgorithmFatalException("List of parameter values " +
					"was not complete");
		}
	}

	private void validatePopulationSize() throws WctttAlgorithmException {
		if (populationSize < POPULATION_SIZE_MIN) {
			throw new WctttAlgorithmException("Population size must be >= " +
					POPULATION_SIZE_MIN);
		}
	}

	private void validateCrossoverRate() throws WctttAlgorithmException {
		if (crossoverRate < CROSSOVER_RATE_MIN ||
				crossoverRate > CROSSOVER_RATE_MAX) {
			throw new WctttAlgorithmException("Crossover rate must be >= " +
					CROSSOVER_RATE_MIN + " and <= " + CROSSOVER_RATE_MAX);
		}
	}

	private void validateMutationRate() throws WctttAlgorithmException {
		if (mutationRate < MUTATION_RATE_MIN ||
				mutationRate > MUTATION_RATE_MAX) {
			throw new WctttAlgorithmException("Mutation rate must be >= " +
					MUTATION_RATE_MIN + " and <= " + MUTATION_RATE_MAX);
		}
	}

	private void validateTabuListSize() throws WctttAlgorithmException {
		if (tabuListSize < TABU_LIST_SIZE_MIN) {
			throw new WctttAlgorithmException("Tabu list size must be >= " +
					TABU_LIST_SIZE_MIN);
		}
	}

	@Override
	protected Timetable runAlgorithm(AtomicBoolean isCancelled)
			throws WctttAlgorithmException {
		
		this.state.firePropertyChange(new PropertyChangeEvent(this, null, null, "Population is being created..."));
		
		// Generate random initial population of feasible solutions:
		SaturationDegreeHeuristic satDegHeuristic =
				new SaturationDegreeHeuristic(getSemester());
		
		satDegHeuristic.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				int populationSize = (int) evt.getNewValue();
				state.firePropertyChange(new PropertyChangeEvent(
						this, null, null, "Population is being created [" + populationSize + "]"));				
			}
			
		});
		List<Timetable> population = satDegHeuristic.generateFeasibleSolutions(
				populationSize, isCancelled);

		// If initialization of population was cancelled before a feasible
		// solution was found, then return no feasible solution:
		if (population.isEmpty()) {
			return null;
		}	
		
		// Find best solution:
		ConstraintViolationsCalculator constrCalc =
				new ConstraintViolationsCalculator(getSemester());
		population.forEach(t -> t.setSoftConstraintPenalty(
				constrCalc.calcTimetablePenalty(t)));
		Timetable bestSolution = chooseBestSolution(population);

		Queue<NeighborhoodStructure> tabuList = new LinkedList<>();
		boolean chooseNewNbs = true; // Nbs == neighborhood structure
		NeighborhoodStructure selectedNbs = null;
		
		int mutationCounter = 0;
		while (bestSolution.getSoftConstraintPenalty() != 0 &&
				!isCancelled.get()) {
			// Genetic operators:
			Timetable[] parents = rouletteWheelSelectParents(population);
			Timetable[] offspring = crossoverOperator(parents);
			mutationOperator(offspring[0]);
			mutationOperator(offspring[1]);

			// Local search:
			if (chooseNewNbs) {
				selectedNbs = selectNbsRandomly(tabuList);
				// TODO other selection strategies as proposed in the paper
			}
			Timetable[] improvedOffspring = {new Timetable(offspring[0]),
					new Timetable(offspring[1])};
			localSearch(improvedOffspring[0], selectedNbs);
			localSearch(improvedOffspring[1], selectedNbs);

			// Calculate constraint violations of new solutions:
			List<Timetable> allNewSolutions = Arrays.asList(offspring[0],
					offspring[1], improvedOffspring[0], improvedOffspring[1]);
			allNewSolutions.forEach(t -> t.setSoftConstraintPenalty(
					constrCalc.calcTimetablePenalty(t)));
			Timetable bestNewSolution = chooseBestSolution(allNewSolutions);

			// Update best solution and selected neighborhood structure:
			if (bestNewSolution.getSoftConstraintPenalty() <
					bestSolution.getSoftConstraintPenalty()) {
				bestSolution = bestNewSolution;
				chooseNewNbs = false;
			} else {
				// TODO uncomment following line if NBS_LIST.size() > tabuListSize
				// tabuList.add(selectedNbs);
				if (tabuList.size() > tabuListSize) {
					tabuList.remove();
				}
				chooseNewNbs = true;
			}

			updatePopulation(population, bestNewSolution);
			mutationCounter++;
			//Every 100 mutations fire event
			if(mutationCounter % 100 == 0) {
				this.state.firePropertyChange(new PropertyChangeEvent(this, null, null,
						"Mutations: [" + mutationCounter + "]" + 
				" Current penalty: [" + Util.round(bestNewSolution.getSoftConstraintPenalty(), 2) +"]"));
			}		
		}

		return bestSolution;
	}

	private Timetable chooseBestSolution(List<Timetable> solutions) {
		Timetable bestSolution = solutions.get(0);
		for (Timetable solution : solutions) {
			if (solution.getSoftConstraintPenalty() <
					bestSolution.getSoftConstraintPenalty()) {
				bestSolution = solution;
			}
		}
		return bestSolution;
	}

	/**
	 * Selects two distinct parents using a roulette wheel selection, where the
	 * selection probability p of a solution is defined by the following formula:
	 * <p>
	 * p = x / y
	 * <p>
	 * where x is the fitness value of the solution and y is the sum of all
	 * solutions' fitness values.
	 *
	 * @param population the population.
	 * @return the selected parents.
	 */
	private Timetable[] rouletteWheelSelectParents(List<Timetable> population) {
		Timetable[] parents = new Timetable[2];
		double[] fitnessValues = new double[population.size()];
		double fitnessSum = 0.0;
		double highestPenalty =
				chooseWorstSolution(population).getSoftConstraintPenalty();

		int i = 0;
		for (Timetable timetable : population) {
			double fitness =
					highestPenalty - timetable.getSoftConstraintPenalty();
			fitnessValues[i] = fitness;
			fitnessSum += fitness;
			i++;
		}

		while (parents[0] == null || parents[1] == null) {
			double selection = new Random().nextDouble() * fitnessSum;
			for (int j = 0; j < fitnessValues.length; j++) {
				selection -= fitnessValues[j];
				if (selection <= 0) {
					if (parents[0] == null) {
						parents[0] = population.get(j);
					} else {
						parents[1] = population.get(j);
					}
					break;
				}
			}
		}

		return parents;
	}

	private Timetable chooseWorstSolution(List<Timetable> solutions) {
		Timetable worstSolution = solutions.get(0);
		for (Timetable solution : solutions) {
			if (solution.getSoftConstraintPenalty() >
					worstSolution.getSoftConstraintPenalty()) {
				worstSolution = solution;
			}
		}
		return worstSolution;
	}

	private Timetable[] crossoverOperator(Timetable[] parents) {
		Timetable[] offspring = {new Timetable(parents[0]),
				new Timetable(parents[1])};
		if (new Random().nextDouble() > crossoverRate) {
			// No crossover, offspring equals parents
			return offspring;
		}

		Period randPeriodA = selectRandomPeriod();
		Period randPeriodB = selectRandomPeriod();

		copyAssignmentsFromTo(parents[0], offspring[1], randPeriodA, randPeriodB);
		copyAssignmentsFromTo(parents[1], offspring[0], randPeriodB, randPeriodA);

		return offspring;
	}

	private Period selectRandomPeriod() {
		try {
			return new Period(
					new Random().nextInt(getSemester().getDaysPerWeek()) + 1,
					new Random().nextInt(getSemester().getTimeSlotsPerDay()) + 1);
		} catch (WctttModelException e) {
			throw new WctttAlgorithmFatalException("Implementation error, " +
					"period was created with illegal parameters", e);
		}
	}

	/**
	 * Copies all assignments from one timetable period to another timetable
	 * period, if the respective rooms are unassigned and no hard constraints
	 * are violated. Duplicates are removed.
	 *
	 * @param parent the timetable from which assignments are copied.
	 * @param child the timetable to which assignments are added.
	 * @param fromParent the period from which assignments are copied.
	 * @param toChild the period to which assignments are added.
	 */
	private void copyAssignmentsFromTo(Timetable parent, Timetable child,
	                                   Period fromParent, Period toChild) {
		TimetablePeriod childPeriod = child.getDays().get(toChild.getDay() - 1).
				getPeriods().get(toChild.getTimeSlot() - 1);
		TimetablePeriod parentPeriod = parent.getDays().get(fromParent.getDay() - 1).
				getPeriods().get(fromParent.getTimeSlot() - 1);
		for (TimetableAssignment parentAssgmt : parentPeriod.getAssignments()) {
			// Pre-assigned sessions cannot be scheduled in another period:
			if (parentAssgmt.getSession().getPreAssignment().isPresent()) {
				continue;
			}

			// If it is a double session, find second period:
			TimetablePeriod childSecondPeriod = null;
			if (parentAssgmt.getSession().isDoubleSession()) {
				childSecondPeriod = findChildSecondPeriod(child, parent,
					childPeriod, parentPeriod, parentAssgmt);
				if (childSecondPeriod == null) {
					// Copying not possible
					continue;
				}
			}

			// Check if the same room is free in child periods:
			boolean[] firstPeriodRoomFree =
					checkIfRoomIsFree(childPeriod, parentAssgmt);
			boolean canBeCopied = firstPeriodRoomFree[0];
			boolean duplicateInFirstPeriod = firstPeriodRoomFree[1];
			boolean duplicateInSecondPeriod = false;
			if (canBeCopied && parentAssgmt.getSession().isDoubleSession()) {
				boolean[] secondPeriodRoomFree =
						checkIfRoomIsFree(childSecondPeriod, parentAssgmt);
				canBeCopied = secondPeriodRoomFree[0];
				duplicateInSecondPeriod = secondPeriodRoomFree[1];
			}
			if (!canBeCopied) {
				continue;
			}

			TimetableAssignment newAssgmt = new TimetableAssignment(
					parentAssgmt.getSession(), parentAssgmt.getRoom());
			TimetableAssignment newSecondAssgmt = null;
			if (parentAssgmt.getSession().isDoubleSession()) {
				newSecondAssgmt = new TimetableAssignment(
						parentAssgmt.getSession(), parentAssgmt.getRoom());
			}

			// Check if hard-constraints would be violated by the new assignments:
			ConstraintViolationsCalculator constrCalc =
					new ConstraintViolationsCalculator(getSemester());
			// If the duplicate is in the same period, no constraints can be
			// violated besides the ones created by the duplicate, which are
			// only temporary.
			if (!duplicateInFirstPeriod) {
				canBeCopied = constrCalc.calcAssignmentHardViolations(
						child, childPeriod, newAssgmt).isEmpty();
			}
			if (parentAssgmt.getSession().isDoubleSession() &&
					!duplicateInSecondPeriod) {
				canBeCopied &= constrCalc.calcAssignmentHardViolations(
						child, childSecondPeriod, newSecondAssgmt).isEmpty();
			}

			if (canBeCopied) {
				try {
					childPeriod.addAssignment(newAssgmt);
					if (parentAssgmt.getSession().isDoubleSession()) {
						childSecondPeriod.addAssignment(newSecondAssgmt);
					}
				} catch (WctttModelException e) {
					throw new WctttAlgorithmFatalException("Implementation " +
							"error", e);
				}

				// Remove random duplicate:
				removeRandomDuplicate(child, childPeriod, childSecondPeriod,
						newAssgmt, newSecondAssgmt);
			}
		}
	}

	private TimetablePeriod findChildSecondPeriod(
		Timetable child, Timetable parent, TimetablePeriod childPeriod,
		TimetablePeriod parentPeriod, TimetableAssignment parentAssgmt) {
		TimetablePeriod parentSecondPeriod = null;
		TimetableAssignment parentSecondAssgmt = null;
		boolean isFirstSession = true;
		if (parentPeriod.getTimeSlot() ==
			ValidationHelper.PERIOD_TIME_SLOT_MIN &&
			childPeriod.getTimeSlot() ==
				getSemester().getTimeSlotsPerDay()) {
			// Copying not possible
			return null;
		} else if (parentPeriod.getTimeSlot() ==
			getSemester().getTimeSlotsPerDay() &&
			childPeriod.getTimeSlot() ==
				ValidationHelper.PERIOD_TIME_SLOT_MIN) {
			// Copying not possible
			return null;
		}

		// Check whether this is the first or the second session:
		// Check period after:
		if (parentPeriod.getTimeSlot() < getSemester().getTimeSlotsPerDay()) {
			parentSecondPeriod = parent.getDays().get(parentPeriod.
				getDay() - 1).getPeriods().get(parentPeriod.
				getTimeSlot());
			parentSecondAssgmt = findAssignmentInPeriod(
				parentAssgmt.getSession(), parentSecondPeriod);
			if (parentSecondAssgmt != null) {
				// Check if the child period is the last period in case
				// the second sessions comes after this one.
				if (childPeriod.getTimeSlot() ==
					getSemester().getTimeSlotsPerDay()) {
					return null;
				}
			}
		}
		if (parentSecondAssgmt == null && parentPeriod.getTimeSlot() >
			ValidationHelper.PERIOD_TIME_SLOT_MIN) {
			// If not found, check period before:
			// Check if the child period is the first period in case
			// the second sessions comes before this one.
			if (childPeriod.getTimeSlot() ==
				ValidationHelper.PERIOD_TIME_SLOT_MIN) {
				return null;
			}
			parentSecondPeriod = parent.getDays().get(parentPeriod.
				getDay() - 1).getPeriods().get(parentPeriod.
				getTimeSlot() - 2);
			parentSecondAssgmt = findAssignmentInPeriod(
				parentAssgmt.getSession(), parentSecondPeriod);
			isFirstSession = false;
		}
		if (parentSecondAssgmt == null) {
			throw new WctttAlgorithmFatalException("Implementation " +
				"error, failed to find second session of double " +
				"session");
		}

		return child.getDays().get(childPeriod.getDay() - 1).
			getPeriods().get(isFirstSession ?
			childPeriod.getTimeSlot() : childPeriod.getTimeSlot() - 2);
	}

	private TimetableAssignment findAssignmentInPeriod(Session session,
	                                                   TimetablePeriod period) {
		
		for (TimetableAssignment assgmt : period.getAssignments()) {
			if (assgmt.getSession().equals(session)) {				
				return assgmt;
			}
		}
		return null;
	}

	private boolean[] checkIfRoomIsFree(TimetablePeriod childPeriod,
	                                    TimetableAssignment parentAssgmt) {
		boolean canBeCopied = true;
		boolean duplicateInPeriod = false;
		for (TimetableAssignment childAssgmt : childPeriod.getAssignments()) {
			if (childAssgmt.getRoom().equals(parentAssgmt.getRoom())) {
				canBeCopied = false;
				break;
			}
			if (childAssgmt.getSession().equals(parentAssgmt.getSession())) {
				duplicateInPeriod = true;
			}
		}
		return new boolean[]{canBeCopied, duplicateInPeriod};
	}

	private void removeRandomDuplicate(
			Timetable timetable, TimetablePeriod childPeriod,
			TimetablePeriod childSecondPeriod, TimetableAssignment newAssgmt,
			TimetableAssignment newSecondAssgmt) {
		boolean isDoubleSession = newAssgmt.getSession().isDoubleSession();
		if (new Random().nextDouble() > 0.5) {
			// Remove new:
			// Because removeAssignment() uses List.remove() and the method uses
			// equals() to find the element, we have to make the new and old
			// assignments distinguishable. Therefore, we change the room.
			newAssgmt.setRoom(new InternalRoom());
			childPeriod.removeAssignment(newAssgmt);
			if (isDoubleSession) {
				newSecondAssgmt.setRoom(new InternalRoom());
				childSecondPeriod.removeAssignment(newSecondAssgmt);
			}
		} else {
			// Remove old:
			List<TimetablePeriod> removePeriods = new ArrayList<>(2);
			List<TimetableAssignment> removeAssgmts = new ArrayList<>(2);
			int removed = 0;
			outerloop:
			for (TimetableDay day : timetable.getDays()) {
				for (TimetablePeriod period : day.getPeriods()) {
					for (TimetableAssignment assgmt : period.getAssignments()) {
						if (assgmt != newAssgmt && assgmt != newSecondAssgmt &&
								assgmt.getSession().equals(newAssgmt.getSession())) {
							removePeriods.add(period);
							assgmt.setRoom(new InternalRoom());
							removeAssgmts.add(assgmt);
							removed++;
							if ((!isDoubleSession && removed == 1) ||
									(isDoubleSession && removed == 2)) {
								break outerloop;
							}
						}
					}
				}
			}
			for (int i = 0; i < removePeriods.size(); i++) {
				removePeriods.get(i).removeAssignment(removeAssgmts.get(i));
			}
		}
	}

	/**
	 * Randomly selects a neighborhood structure and applies it with a
	 * probability defined in the mutation rate. There are as many chances of
	 * mutation as there are sessions in the semester.
	 *
	 * @param timetable the timetable that should be mutated.
	 * @throws WctttAlgorithmException if an error occurred in the neighborhood
	 * structure.
	 */
	private void mutationOperator(Timetable timetable)
			throws WctttAlgorithmException {
		NeighborhoodStructure nbs = selectNbsRandomly(null);
		Random random = new Random();

		for (int i = 0; i < numberOfSessions; i++) {
			if (random.nextDouble() <= mutationRate) {
				nbs.apply(timetable, getSemester());
			}
		}
	}

	private NeighborhoodStructure selectNbsRandomly(
			Queue<NeighborhoodStructure> tabuList) {
		if (tabuList == null || tabuList.isEmpty()) {
			return NBS_LIST.get(new Random().nextInt(NBS_LIST.size()));
		} else if (NBS_LIST.size() == tabuList.size()) {
			throw new WctttAlgorithmFatalException("Implementation error, all" +
					" neighborhood structures are in the tabu list");
		} else {
			List<NeighborhoodStructure> nbsList = new LinkedList<>(NBS_LIST);
			nbsList.removeAll(tabuList);
			return nbsList.get(new Random().nextInt(nbsList.size()));
		}
	}

	private void localSearch(Timetable timetable,
	                         NeighborhoodStructure selectedNbs) {
		// TODO
	}

	/**
	 * Removes the worst solution in the population and and adds another
	 * solution to the population. If the other solution is worse than the worst
	 * solution of the population, nothing happens.
	 *
	 * @param population  the current population.
	 * @param newSolution the new solution that should be added to the population.
	 */
	private void updatePopulation(List<Timetable> population,
	                              Timetable newSolution) {
		Timetable worstSolution = population.get(0);
		for (Timetable timetable : population) {
			if (timetable.getSoftConstraintPenalty() >
					worstSolution.getSoftConstraintPenalty()) {
				worstSolution = timetable;
			}
		}
		if (newSolution.getSoftConstraintPenalty() <
				worstSolution.getSoftConstraintPenalty()) {
			population.remove(worstSolution);
			population.add(newSolution);
		}
	}
}
