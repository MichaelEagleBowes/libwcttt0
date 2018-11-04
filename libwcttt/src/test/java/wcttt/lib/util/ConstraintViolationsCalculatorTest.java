package wcttt.lib.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wcttt.lib.model.*;
import wcttt.lib.util.ConstraintViolationsCalculator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstraintViolationsCalculatorTest {

	private static ConstraintViolationsCalculator cvc;
	private static Semester semester;
	private static Timetable timetable;
	private static Teacher teacher1;
	private static Teacher teacher2;
	private static Teacher teacher3;
	private static Teacher teacher4;
	private static Chair chair;
	private static Course course1;
	private static Course course2;
	private static Course course3;
	private static Course course4;
	private static Curriculum cur1;
	private static Curriculum cur2;
	private static Session lecture1;
	private static Session practical1;
	private static Session lecture2;
	private static Session practical2;
	private static Session lecture3;
	private static Session practical3;
	private static Session practical4;
	private static Session lecture4;
	private static Session practical5;
	private static InternalRoom room1;
	private static InternalRoom room2;
	private static InternalRoom room3;
	private static InternalRoom room4;
	
	
	@BeforeAll
	static void createSemester() {	
		
		semester = new SemesterImpl();
		try {
			chair = new Chair();
			chair.setAbbreviation("TST");
			chair.setId("9999AA");
			chair.setName("Lehrstuhl für Tests");
			teacher1 = new Teacher("AAAAAAAAAA", "Dieter");			
			teacher2 = new Teacher("BBBBBBBBBB", "Willhelm");		
			teacher3 = new Teacher("CCCCCCCCC", "Otto");			
			teacher4 = new Teacher("DDDDDDDDDD", "Klaus");
			
			chair.addTeacher(teacher1);
			chair.addTeacher(teacher2);
			
			
			course1 = new Course("123456", "Test Course 1",
					"T1", chair, CourseLevel.Bachelor, 1);
		
			lecture1 = new InternalSession();
			lecture1.setCourse(course1);
			lecture1.setTeacher(teacher1);
			lecture1.setId("0115234111");
			lecture1.setName("T1 V");
			
			
			practical1 = new InternalSession();
			practical1.setCourse(course1);
			practical1.setTeacher(teacher1);
			practical1.setId("0111112");
			practical1.setName("T1 Ü");
			
			course1.addLecture(lecture1);
			course1.addPractical(practical1);
						
			course2 = new Course("134123", "Test Course 2",
					"T2", chair, CourseLevel.Bachelor, 1);	
			
			lecture2 = new InternalSession();
			lecture2.setCourse(course2);
			lecture2.setTeacher(teacher2);
			lecture2.setId("0112111");
			lecture2.setName("T2 V");
			
			practical2 = new InternalSession();
			practical2.setCourse(course2);
			practical2.setTeacher(teacher2);
			practical2.setId("0111312");
			practical2.setName("T2 Ü");
			
			course2.addLecture(lecture2);
			course2.addPractical(practical2);
			
			course3 = new Course("124444", "Test Course 3",
					"T3", chair, CourseLevel.Bachelor, 1);
			
			lecture3 = new InternalSession();
			lecture3.setCourse(course3);
			lecture3.setTeacher(teacher3);
			lecture3.setId("0112311");
			lecture3.setName("T3 V");
			
			practical3 = new InternalSession();
			practical3.setCourse(course3);
			practical3.setTeacher(teacher3);
			practical3.setId("0131312");
			practical3.setName("T3 Ü1");
			
			practical4 = new InternalSession();
			practical4.setCourse(course3);
			practical4.setTeacher(teacher3);
			practical4.setId("0441312");
			practical4.setName("T3 Ü2");
			
			course3.addLecture(lecture3);
			course3.addPractical(practical3);
			course3.addPractical(practical4);
			
			course4= new Course("1255555", "Test Course 4",
					"T4", chair, CourseLevel.Bachelor, 1);
			
			lecture4 = new InternalSession();
			lecture4.setCourse(course4);	
			lecture4.setTeacher(teacher4);
			lecture4.setId("01008111");
			lecture4.setName("T4 V");			
			
			practical4 = new InternalSession();
			practical4.setCourse(course4);
			practical4.setTeacher(teacher4);
			practical4.setId("011774412");
			practical4.setName("T4 Ü1");
			
			practical5 = new InternalSession();
			practical5.setCourse(course4);
			practical5.setTeacher(teacher4);
			practical5.setId("01784412");
			practical5.setName("T4 Ü2");
			
			course4.addLecture(lecture4);
			course4.addPractical(practical4);
			course4.addPractical(practical5);
			
			cur1 = new Curriculum("FFFAWWQQSDA", "Curriculum 1");
			cur1.addCourse(course1);
			cur1.addCourse(course2);	
			
			cur2 = new Curriculum("FFFFFASDDDA", "Curriculum 2");
			cur2.addCourse(course3);
			cur2.addCourse(course4);
			
			room1 = new InternalRoom();
			room1.setId("LLL1123");
			room1.setName("Room 1");
			
			room2 = new InternalRoom();
			room2.setId("LLL33123");
			room2.setName("Room 2");
			
			room3 = new InternalRoom();
			room3.setId("FFL33123");
			room3.setName("Room 3");
			
			room4 = new InternalRoom();
			room4.setId("TTL33123");
			room4.setName("Room 4");		
					
			semester.addChair(chair);
			semester.addCourse(course1);
			semester.addCourse(course2);
			semester.addCourse(course3);
			semester.addCourse(course4);
			semester.addCurriculum(cur1);
			semester.addCurriculum(cur2);
			semester.addInternalRoom(room1);
			semester.addInternalRoom(room2);
			semester.addInternalRoom(room3);
			semester.addInternalRoom(room4);
			cvc = new ConstraintViolationsCalculator(semester);
			
		}catch(WctttModelException e) {			
			System.err.println(e.getMessage());
			semester = null;
		}				
	}
	
	@BeforeEach
	void createTimetable() {	
		try {
			if(timetable != null) {
				semester.removeTimetable(timetable);
			}
			timetable = new Timetable();
			for(int d = 1; d < 8; d++) {
				TimetableDay day = new TimetableDay(d);
				for(int p = 0; p < 6; p++) {
					day.addPeriod(new TimetablePeriod(d, p));
				}
				timetable.addDay(day);
			}			
			semester.addTimetable(timetable);
		} catch (WctttModelException e) {
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	void constraintH1Found() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(practical1);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room2);
		assignment2.setSession(lecture1);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, period, assignment2);
		
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h1) {
				counter++;
			}
		}
		assertEquals(1, counter);
	}
	
	@Test
	void constraintH1NotFound() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(practical1);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room2);
		assignment2.setSession(lecture1);
		TimetablePeriod nextPeriod = timetable.getDays().get(0).getPeriods().get(1);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, nextPeriod, assignment2);
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h1) {
				counter++;
			}
		}
		assertEquals(0, counter);
	}
	
	@Test
	void constraintH2Found() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(lecture1);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room2);
		assignment2.setSession(practical1);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, period, assignment2);
		
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h2) {
				counter++;
			}
		}
		assertEquals(1, counter);
	}
	
	@Test
	void constraintH2NotFound() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(lecture1);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room2);
		assignment2.setSession(practical1);
		TimetablePeriod nextPeriod = timetable.getDays().get(0).getPeriods().get(1);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, nextPeriod, assignment2);
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h2) {
				counter++;
			}
		}
		assertEquals(0, counter);
	}
	
	@Test
	void constraintH3Found() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(lecture1);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room1);
		assignment2.setSession(practical1);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, period, assignment2);
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h3) {
				counter++;
			}
		}
		assertEquals(1, counter);
	}
	
	@Test
	void constraintH3NotFound() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(lecture1);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room2);
		assignment2.setSession(practical1);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, period, assignment2);
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h3) {
				counter++;
			}
		}
		assertEquals(0, counter);
	}
	
	@Test
	void constraintH4SinglePracticalFound() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(practical2);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room2);
		assignment2.setSession(lecture1);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, period, assignment2);
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h4) {
				counter++;
			}
		}
		assertEquals(1, counter);
	}
	
	@Test
	void constraintH4MultiplePracticalNotFound() {
		TimetablePeriod period = timetable.getDays().get(0).getPeriods().get(0);
		assertNotNull(period);
		boolean couldAssign = false;
		try {
			TimetableAssignment assignment1 = new TimetableAssignment();
			assignment1.setRoom(room1);
			assignment1.setSession(practical4);
			period.addAssignment(assignment1);
			couldAssign= true;
		} catch (WctttModelException e) {
			couldAssign = false;
		}
		assertTrue(couldAssign);
		TimetableAssignment assignment2 = new TimetableAssignment();
		assignment2.setRoom(room2);
		assignment2.setSession(lecture3);
		List<ConstraintType> violations = cvc.calcAssignmentHardViolations(timetable, period, assignment2);
		//Check the number of violations
		int counter = 0;
		for(ConstraintType violation : violations) {
			if(violation == ConstraintType.h4) {
				counter++;
			}
		}
		assertEquals(0, counter);
	}
	
}
