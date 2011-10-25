package org.gjt.sp.jedit.search;

import static org.gjt.sp.jedit.search.MatchAssert.*;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.search.SearchMatcher.Match;
import org.junit.Test;

public class PatternSearchMatcherConformanceTest {

	/**
	 * <p>Make sure that null regex {@link String} is not accepted
	 * and exception is thrown immediatedly.</p>
	 * @testcreated 2011-09-28
	 * @testpriority low
	 */
	@Test(expected=IllegalArgumentException.class)
	public void nullStringCtorParameterThrows() {
		new PatternSearchMatcher((String)null, true);
	}
	
	/**
	 * <p>Make sure that empty regex {@link String} is not accepted
	 * and exception is thrown immediatedly.</p>
	 * @testcreated 2011-09-28
	 * @testpriority low
	 */
	@Test(expected=IllegalArgumentException.class)
	public void emptyStringCtorParameterThrows() {
		new PatternSearchMatcher("", true);
	}
	
	/**
	 * <p>Make sure that null {@link Pattern} parameter is not
	 * accepted and exception is thrown immediatedly.</p>
	 * @testcreated 2011-09-28
	 * @testpriority low 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void nullPatternCtorParameterThrows() {
		new PatternSearchMatcher((Pattern)null, true);
	}
	
	/**
	 * <p>Tests whether or not 
	 * {@link PatternSearchMatcher#PatternSearchMatcher(Pattern, boolean)}
	 * enforces the Pattern flags required (case insensitivity 
	 * iff {@code ignoreCase} is true, multiline always).</p>
	 * @testcreated 2011-09-22
	 * @testpriority medium
	 */
	@Test
	public void patternCtorPatternFlagValidation() {
		final String testPattern = "fo\\s*o";
		final String text = "Fo\nOfoo";
		
		final List<PatternSearchMatcher> matchers = new ArrayList<PatternSearchMatcher>();
		
		matchers.add(new PatternSearchMatcher(testPattern, false));
		matchers.add(new PatternSearchMatcher(Pattern.compile(testPattern), false));
		matchers.add(new PatternSearchMatcher(Pattern.compile(testPattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE), false));
		
		int index = 0;
		Match firstMatch = null;
		
		for (PatternSearchMatcher m : matchers) {
			
			final Match actualMatch = m.nextMatch(text, true, true, true, false);
			
			if (index++ == 0) {
				firstMatch = actualMatch;
				continue;
			}
			
			assertMatch("mismatch between 1th and " + (index) + "th", firstMatch.start, firstMatch.end, actualMatch.start, actualMatch.end);
		}
	}
	
	/**
	 * <p>Tests whether or not {@link PatternSearchMatcher#nextMatch(CharSequence, boolean, boolean, boolean, boolean)}
	 * validates it's nullable document parameter.</p>
	 * @testcreated 2011-09-22
	 * @testpriority low
	 */
	@Test(expected=IllegalArgumentException.class)
	public void nextMatchThrowsForNullBuffer() {
		PatternSearchMatcher m = new PatternSearchMatcher("foo", false);
		m.nextMatch(null, true, true, true, false);
	}
	
	/**
	 * <p>Assures that nextMatch handles matching against empty region gracefully without returning a result;
	 * this is of medium importance because it's not impossible that the user could have an empty selection and
	 * that selection would at some point get passed to {@link PatternSearchMatcher}.</p>
	 * @testcreated 2011-09-25
	 * @testpriority medium
	 */
	@Test
	public void nextMatchReturnsNoMatchForEmptyBuffer() {
		PatternSearchMatcher m = new PatternSearchMatcher("foo", false);
		assertNull(m.nextMatch("", true, true, true, false));
	}
}
