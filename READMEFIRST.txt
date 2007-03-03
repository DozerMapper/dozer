Run 'mvn eclipse:eclipse' to build eclipse's .project and .classpath file.

Eclipse needs to know the path to the local maven repository. Therefore the classpath variable M2_REPO has to be set. Execute the following command or add it manually to Eclipse (see below)

mvn -Declipse.workspace=<path-to-eclipse-workspace> eclipse:add-maven-repo 

You can also define a new classpath variable inside eclipse: From the menu bar, select Window > Preferences. Select the Java > Build Path > Classpath Variables page.


Run 'mvn clean test' to build and test dozer.
Run 'mvn clean site install' to build and package dozer.
Run 'mvn -P deploy-dozer' to deploy dozer

