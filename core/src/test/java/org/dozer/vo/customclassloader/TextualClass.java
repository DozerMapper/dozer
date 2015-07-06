package org.dozer.vo.customclassloader;

/**
 * Created by mmatosevic on 6.7.2015.
 */
public class TextualClass {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextualClass that = (TextualClass) o;

        return !(text != null ? !text.equals(that.text) : that.text != null);

    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TextualClass{" +
                "text='" + text + '\'' +
                '}';
    }
}
