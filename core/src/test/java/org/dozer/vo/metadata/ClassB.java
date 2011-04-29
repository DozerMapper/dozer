package org.dozer.vo.metadata;

/**
 * <brief_description>
 * @author  florian.kunz
 */
public class ClassB {
	
	private String autoField;
	private String customFieldB;
	private ClassD classD;
	
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
	 * stringFieldB getter
	 * @return the stringFieldB
	 */
	public String getCustomFieldB() {
		return customFieldB;
	}
	/**
	 * stringFieldB setter
	 * @param stringFieldB the stringFieldB to set
	 */
	public void setCustomFieldB(String stringFieldB) {
		this.customFieldB = stringFieldB;
	}
	/**
	 * classD setter
	 * @param classD the classD to set
	 */
	public void setClassD(ClassD classD) {
		this.classD = classD;
	}
	/**
	 * classD getter
	 * @return the classD
	 */
	public ClassD getClassD() {
		return classD;
	}
	
}
