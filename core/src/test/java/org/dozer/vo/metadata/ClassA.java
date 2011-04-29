package org.dozer.vo.metadata;

/**
 * <brief_description>
 * @author  florian.kunz
 */
public class ClassA {
	
	private String autoField;
	private String customFieldA;
	private ClassC classC;
	
	/**
	 * stringField1 getter
	 * @return the stringField1
	 */
	public String getAutoField() {
		return autoField;
	}
	/**
	 * stringField1 setter
	 * @param stringField1 the stringField1 to set
	 */
	public void setAutoField(String stringField1) {
		this.autoField = stringField1;
	}
	/**
	 * stringFieldA getter
	 * @return the stringFieldA
	 */
	public String getCustomFieldA() {
		return customFieldA;
	}
	/**
	 * stringFieldA setter
	 * @param stringFieldA the stringFieldA to set
	 */
	public void setCustomFieldA(String stringFieldA) {
		this.customFieldA = stringFieldA;
	}
	/**
	 * classC setter
	 * @param classC the classC to set
	 */
	public void setClassC(ClassC classC) {
		this.classC = classC;
	}
	/**
	 * classC getter
	 * @return the classC
	 */
	public ClassC getClassC() {
		return classC;
	}
	
}
