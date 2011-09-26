package org.gjt.sp.jedit.search;

import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.search.SearchMatcher.Match;
import org.junit.Test;

public class PatternSearchMatcherTest {

	/**
	 * <p>Tests whether or not 
	 * {@link PatternSearchMatcher#PatternSearchMatcher(Pattern, boolean)}
	 * enforces the Pattern flags required (case insensitivity 
	 * iff {@code ignoreCase} is true, multiline always).</p>
	 * @testcreated 2011-08-22
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
	 * <p>{@code start} is used to signify whether or not the character
	 * sequence (could be user selected) starts from a beginning of a
	 * line. If it doesn’t start, ^ should not match the in the beginning
	 * of the input (the first match must be skipped).</p>
	 * @testcreated 2011-08-22
	 * @testpriority medium
	 */
	@Test
	public void trueStartMatchesFromBeginningOfTheLine() {
	    final PatternSearchMatcher spm =
	        new PatternSearchMatcher("^abbacd$", false);

	    assertMatch(0, 6, 
	    	spm.nextMatch("abbacd\nabbacd\n", true, true, true, false));
	}
	
	@Test
	public void trueFirstTimeMatchesFirstMatch() {
		final PatternSearchMatcher spm =
			new PatternSearchMatcher("^.*$", false);
		
		assertMatch(0, 0,
			spm.nextMatch("\nabbacd\nabbacd\n", true, true, true, false));
	}

	@Test
	public void falseFirstTimeMatchesSecondMatch() {
		final PatternSearchMatcher spm =
			new PatternSearchMatcher("^.*$", false);
		
		assertMatch(1, 7,
				spm.nextMatch("\nabbacd\nabbacd\n", true, true, false, false));
	}
	
	/**
	 * <p>Validates the behaviour of {@code firstTime} with reverse
	 * matching. When {@code firstTime} is {@code true} the last possible
	 * match must be returned.</p>
	 * @testcreated 2011-08-22
	 * @testpriority medium
	 */
	@Test
	public void trueFirstTimeMatchesLastReverseMatch() {
		final PatternSearchMatcher spm =
	        new PatternSearchMatcher("^.*$", false);
		
		//			              111 1111111 2 2
		//             012345 6789012 3456789 0 1
	    String text = "abbacd\nabbacd\nabbacd\n\n";
		
	    Match m = spm.nextMatch(text, true, true, true, true);
		assertMatch(1, 1, m);
	    assertReverseMatch(text, 21, 21, m);		
	}
	
	/**
	 * <p>Opposite to {@link #trueFirstTimeMatchesFirstMatch()}.</p>
	 * @testcreated 2011-08-22
	 * @testpriority medium
	 */
	@Test
	public void falseFirstTimeMatchesSecondToLastReverseMatch() {
		final PatternSearchMatcher spm =
			new PatternSearchMatcher("^.*$", false);

		//			              111 1111111 2 2
		//             012345 6789012 3456789 0 1
		String text = "abbacd\nabbacd\nabbacd\n\n";
		
		assertReverseMatch(text, 14, 20,
			spm.nextMatch(text, true, true, false, true));
	}
	
	private void assertMatch(int start, int end, Match m) {
		assertNotNull("no match", m);
		assertMatch(start, end, m.start, m.end);
	}
	
	private void assertReverseMatch(String text, int start, int end, Match m) {
		assertNotNull("no match", m);
		
		int matchLength = m.end - m.start;
		int correctedEnd = text.length() - m.start;
		int correctedStart = correctedEnd - matchLength;
		assertMatch(start, end, correctedStart, correctedEnd);
	}
	
	private void assertMatch(int start, int end, int actualStart, int actualEnd) {
		assertMatch(null, start, end, actualStart, actualEnd);
	}
	
	private void assertMatch(String message, int start, int end, int actualStart, int actualEnd) {
		assertIndex("start", start);
		assertIndex("end", end);
		assertIndex("m.start", actualStart);
		assertIndex("m.end", actualEnd);
		
		if (start != actualStart || end != actualEnd) {
			throw new AssertionError(
				String.format(
					(message != null ? message + ": " : "") 
					+ "Expected <%d, %d> but was: <%d, %d>", start, end, actualStart, actualEnd));
		}
	}
	
	private void assertIndex(String name, int index) {
		if (index < 0) {
			throw new AssertionError(
				String.format("Invalid index" + (name != null ? " for " + name : "") + ": %d", index));
		}
	}

}
