package wcttt.lib.model;

/**
 * @author Nicolas Bruch
 * 
 * Represents a violation of a constraint that occured while 
 * creating an {@link TimeTable}. 
 *
 */
public class ConstraintViolation {

	private ConstraintType violationType;
	
	private String information;
	
	public ConstraintViolation(ConstraintType violationType, String information) {
		this.violationType = violationType;
		this.information = information;
	}

	public ConstraintType getViolationType() {
		return violationType;
	}

	public String getInformation() {
		return information;
	}
	
}
