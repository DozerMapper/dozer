package org.dozer.vo.deep3;

import java.util.List;

/**
 * AbstractDest
 *
 * @author tetra
 * @version $Id$
 */
public class AbstractDest<T> {
    private List<T> nestedList;

    public List<T> getNestedList() {
        return nestedList;
    }

    public void setNestedList(List<T> nestedList) {
        this.nestedList = nestedList;
    }
}
