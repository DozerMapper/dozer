package org.dozer.osgi;

import org.dozer.DozerBeanMapper;
import org.dozer.DozerInitializer;
import org.dozer.util.DozerConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.MavenUtils.asInProject;

/**
 * @author Dmitry Buzdin
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)

public class OsgiContainerTest {

  @Inject
  private BundleContext bundleContext;

  @Configuration
  public Option[] config() {

    return options(
        mavenBundle().groupId("net.sf.dozer").artifactId("dozer-osgi").version(asInProject()),
        mavenBundle().groupId("commons-beanutils").artifactId("commons-beanutils").version(asInProject()),
        mavenBundle().groupId("commons-collections").artifactId("commons-collections").version("3.2.1"),
        mavenBundle().groupId("org.apache.commons").artifactId("commons-lang3").version(asInProject()),
        junitBundles()
    );
  }

  @Test
  public void shouldInstantiate() {
    DozerBeanMapper beanMapper = new DozerBeanMapper();
    assertThat(beanMapper, notNullValue());
  }

}
