package org.dozer.vo;

/**
 * @author Dmitry Buzdin
 */
public class SimpleEnum {

    public static final SimpleEnum ONE = new SimpleEnum("ONE");
    public static final SimpleEnum TWO = new SimpleEnum("TWO");

    private String name;

    private SimpleEnum(String aName) {
        name = aName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

}
