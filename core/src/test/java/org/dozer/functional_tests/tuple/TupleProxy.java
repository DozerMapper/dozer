package org.dozer.functional_tests.tuple;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.util.ArrayList;
import java.util.List;

public class TupleProxy implements Tuple {

    public static void initTuples(List<TupleElementProxy> elements) {
        tupleElements.clear();
        for (TupleElement element : elements)
            tupleElements.add(element);
    }

    private Object[] tuples;
    private static List<TupleElement<?>> tupleElements = new ArrayList<TupleElement<?>>();

    public TupleProxy() {
    }

    public TupleProxy(Object[] tuples) {
        this();
        if ( tuples.length != tupleElements.size() ) {
            throw new IllegalArgumentException(
                    "Size mismatch between tuple result [" + tuples.length
                            + "] and expected tuple elements [" + tupleElements.size() + "]"
            );
        }
        this.tuples = tuples;
    }

    public <X> X get(TupleElement<X> tupleElement) {
        int index = tupleElements.indexOf(tupleElement);
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                    "Given alias [" + tupleElement.getAlias() + "] did not correspond to an element in the result tuple"
            );
        }
        // index should be "in range" by nature of size check in ctor
        return (X) get( index );
    }

    private int find(String alias) {
        for (int i=0; i<tupleElements.size(); i++) {
            if (tupleElements.get(i).getAlias().equals(alias)) {
                return i;
            }
        }
        return -1;
    }

    public Object get(String alias) {
        int index = find(alias);
        if ( index < 0 ) {
            throw new IllegalArgumentException(
                    "Given alias [" + alias + "] did not correspond to an element in the result tuple"
            );
        }
        // index should be "in range" by nature of size check in ctor
        return get( index );
    }

    public <X> X get(String alias, Class<X> type) {
        return ( X ) get( alias );
    }

    public Object get(int i) {
        if ( i >= tuples.length ) {
            throw new IllegalArgumentException(
                    "Given index [" + i + "] was outside the range of result tuple size [" + tuples.length + "] "
            );
        }
        return tuples[i];
    }

    public <X> X get(int i, Class<X> type) {
        return ( X ) get( i );
    }

    public Object[] toArray() {
        return tuples;
    }

    public List<TupleElement<?>> getElements() {
        return tupleElements;
    }
}
