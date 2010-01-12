package org.dozer.functional_tests;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;

/**
 * 
 * @author Ed Bras. Last edited by: $Author: ed $
 * 
 * @version $Revision: 461 $ - - $Date: 2007-11-04 12:46:47 +0100 (zo, 04 nov
 *          2007) $
 */
public class SuperInterfaceMappingTest extends AbstractFunctionalTest {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("superInterfaceMapping.xml");
  }

  @Test
  public void testInterfaces() {
    // test data
    String name = "name1";
    String socialNr = "socialNr1";
    Taxer source = new TaxerDef(name, socialNr);
    // convert
    TaxerDto target = TaxerDto.class.cast(mapper.map(source, TaxerDto.class));
    assertTarget(target);
  }

  private void assertTarget(TaxerDto target) {
    Assert.assertNotNull(target);
    Assert.assertNotNull(target.getSocialNr());
    Assert.assertNotNull(target.getName()); // fail due to bug
  }

  public interface Member {
    String getName();
  }

  public interface Taxer extends Member {
    String getSocialNr();
  }

  public interface EntityRich {
  }

  public interface EntityAdminObject extends EntityRich, AdminObject {
  }

  public interface AdminObject extends HasDateCreation, HasDateModified {
  }

  public interface HasDateCreation {
  }

  public interface HasDateModified {
  }

  public static class EntityDao implements EntityRich {
  }

  public static class EntityAdminDao extends EntityDao implements EntityAdminObject {
  }

  public static class EntityBase extends EntityAdminDao {
  }

  public static class MemberDef extends EntityBase implements Member {
    private String name;

    public MemberDef(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static class TaxerDef extends MemberDef implements Taxer {
    private String socialNr;

    public TaxerDef(String name, String socialNr) {
      super(name);
      this.socialNr = socialNr;
    }

    public String getSocialNr() {
      return this.socialNr;
    }
  }

  public interface HasId extends HasIdRead {
  }

  public interface HasIdRead {
  }

  public static class EntityIdSimple implements HasId, Serializable {
  }

  public static class EntityAdmin extends EntityIdSimple {
  }

  public static class MemberDto extends EntityAdmin {
    private String name;

    public String getName() {
      return name;
    }
  }

  public static class TaxerDto extends MemberDto {
    private String socialNr;

    public String getSocialNr() {
      return this.socialNr;
    }
  }

}
