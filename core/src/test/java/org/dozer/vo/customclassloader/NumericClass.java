package org.dozer.vo.customclassloader;

/**
 * Created by mmatosevic on 6.7.2015.
 */
public class NumericClass {
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumericClass that = (NumericClass) o;

        return number == that.number;

    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public String toString() {
        return "NumericClass{" +
                "number=" + number +
                '}';
    }
}
