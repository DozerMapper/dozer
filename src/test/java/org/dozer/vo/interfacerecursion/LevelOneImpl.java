/**
 * 
 */
package org.dozer.vo.interfacerecursion;

import java.io.Serializable;

/**
 * @author poxenham
 *
 */
@SuppressWarnings("serial")
public class LevelOneImpl extends BaseImpl implements LevelOne, Serializable
{

  public String getFirstName()
  {
    return null;
  }

  public String getLastName()
  {
    return null;
  }

  public UserGroup getUserGroup()
  {
    return null;
  }

  public void setFirstName( String firstName )
  {
  }

  public void setLastName( String lastName )
  {
  }

  public void setUserGroup( UserGroup userGroup )
  {
  }

}
