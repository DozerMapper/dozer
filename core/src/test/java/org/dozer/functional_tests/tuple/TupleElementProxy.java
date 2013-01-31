package org.dozer.functional_tests.tuple;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.util.List;

public class TupleElementProxy<X> implements TupleElement<X> {

    private String alias;
    private Class javaType;

    public TupleElementProxy(String alias, Class javaType) {
        this.alias = alias;
        this.javaType = javaType;
    }

    @Override
    public Class<? extends X> getJavaType() {
        return javaType;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
