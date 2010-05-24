package org.dozer.factory;

/**
* @author Dmitry Buzdin
*/
public interface BeanCreationStrategy {

  boolean isApplicable(BeanCreationDirective directive);

  Object create(BeanCreationDirective directive);

}
