package org.gjt.sp.jedit.search;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	PatternSearchMatcherConformanceTest.class,
	PatternSearchMatcherTest.class,
	PatternSearchMatcherDocumentSearchTest.class
})
public class AllTests {

}
