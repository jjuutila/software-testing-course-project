package org.gjt.sp.jedit.search;

import static junit.framework.Assert.assertNotNull;

import org.gjt.sp.jedit.search.SearchMatcher.Match;

public abstract class MatchAssert {
	public static void assertMatch(int start, int end, Match m) {
		assertNotNull("no match", m);
		assertMatch(start, end, m.start, m.end);
	}
	
	public static void assertReverseMatch(String text, int start, int end, Match m) {
		assertNotNull("no match", m);
		
		int matchLength = m.end - m.start;
		int correctedEnd = text.length() - m.start;
		int correctedStart = correctedEnd - matchLength;
		assertMatch(start, end, correctedStart, correctedEnd);
	}
	
	public static void assertMatch(int start, int end, int actualStart, int actualEnd) {
		assertMatch(null, start, end, actualStart, actualEnd);
	}
	
	public static void assertMatch(String message, int start, int end, int actualStart, int actualEnd) {
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
	
	public static void assertIndex(String name, int index) {
		if (index < 0) {
			throw new AssertionError(
				String.format("Invalid index" + (name != null ? " for " + name : "") + ": %d", index));
		}
	}
}
