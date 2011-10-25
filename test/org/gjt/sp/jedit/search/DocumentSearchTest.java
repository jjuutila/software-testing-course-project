package org.gjt.sp.jedit.search;

import static org.gjt.sp.jedit.search.MatchAssert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.gjt.sp.jedit.search.SearchMatcher.Match;
import static junit.framework.Assert.*;

public class DocumentSearchTest {

	private static final String NEEDLE = "vi";
	
	private static final Set<MatchStatus> EXPECTED_MATCHES = Collections.unmodifiableSet(new LinkedHashSet<MatchStatus>(
		Arrays.asList(
				new MatchStatus(241, 243),
				new MatchStatus(287, 289),
				new MatchStatus(320, 322),
				new MatchStatus(420, 422),
				new MatchStatus(427, 429),
				new MatchStatus(435, 437),
				new MatchStatus(485, 487),
				new MatchStatus(626, 628),
				new MatchStatus(634, 636)
		)
	));
	
	private final String document = 
		"Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" + 
		"Nullam odio eros, ultrices id ornare at, auctor vel felis.\n" +
		"Nulla facilisis metus nulla, id auctor elit.\n" +
		"Pellentesque tincidunt lobortis est, et molestie elit facilisis vel.\n" +
		"Vestibulum vitae velit quis nulla dignissim sollicitudin." +
		"Vivamus sagittis arcu non leo pulvinar tincidunt.\n" +
		"Suspendisse nec ipsum tortor, in aliquam leo." +
		"Donec nibh orci, congue vel tincidunt vitae, viverra vitae nibh.\n" +
		"Nam in purus at sapien semper egestas vitae non nibh.\n" +
		"Aenean in sapien mauris, et mattis nibh.\n" +
		"Maecenas at ante justo, et congue felis.\n" +
		"Phasellus tortor nulla, malesuada sit amet viverra vitae, tristique sit amet dolor.\n" +
		"Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos.\n" +
		"Duis ac tortor sit amet magna sodales dictum.";

	
	
	/**
	 * 
	 */
	@Test
	public void findAllMatchesForward() {
		
		String text = document;
		
		final PatternSearchMatcher spm =
		        new PatternSearchMatcher(NEEDLE, true);
		
		int offset = 0;
		Match match = null;
		Set<MatchStatus> matches = new LinkedHashSet<MatchStatus>();
		
		while ((match = spm.nextMatch(text, true, true, matches.isEmpty(), false)) != null) {
			MatchStatus ms = new MatchStatus(match, offset);
			assertTrue("duplicate match: " + ms + " for " + matches, matches.add(ms));
			text = text.substring(match.end);
			offset += match.end;
		}
		
		assertEquals("not enough matches in " + matches, 9, matches.size());
		assertTrue(matches.removeAll(EXPECTED_MATCHES));
		assertTrue("not all matches were found: " + matches, matches.isEmpty());
	}
	
	@Test
	public void findAllMatchesReverse() {
		String text = document;
		
		final PatternSearchMatcher spm =
		        new PatternSearchMatcher(NEEDLE, true);
		
		Match match = null;
		Set<MatchStatus> matches = new LinkedHashSet<MatchStatus>();
		
		while ((match = spm.nextMatch(text, true, true, matches.isEmpty(), true)) != null) {
			MatchStatus ms = new MatchStatus(match, 0).reverse(text.length());
			assertTrue("duplicate match: " + ms + " for " + matches, matches.add(ms));
			text = text.substring(0, text.length() - match.end);
		}
		
		assertEquals("not enough matches in " + matches, 9, matches.size());
		assertTrue(matches.removeAll(EXPECTED_MATCHES));
		assertTrue("not all matches were found: " + matches, matches.isEmpty());
	}
	
	/**
	 * Wrapper class for {@link Match} which implements hashCode and equals
	 * @author joonas
	 *
	 */
	private static class MatchStatus {
		private final Match m;

		public MatchStatus(Match m, int offset) {
			this(m.start + offset, m.end + offset);
		}
		
		public MatchStatus(int start, int end) {
			this.m = new Match();
			m.start = start;
			m.end = end;
			if (start > end) {
				m.end = start;
				m.start = end;
			}
		}
		
		/**
		 * @param textLength the length of the matched text for which this MatchStatus represents a reverse (counted from the end) match
		 * @return a reversed MatchStatus in relation to the given {@code textLength}
		 */
		public MatchStatus reverse(int textLength) {
			int matchLength = m.end - m.start;
			int correctedEnd = textLength - m.start;
			int correctedStart = correctedEnd - matchLength;
			return new MatchStatus(correctedStart, correctedEnd);
		}
		
		@Override
		public int hashCode() {
			return 31 + m.start ^ m.end;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof MatchStatus 
				&& m.start == ((MatchStatus)obj).m.start 
				&& m.end == ((MatchStatus)obj).m.end;
		}
		
		@Override
		public String toString() {
			return String.format("[%d, %d]", m.start, m.end);
		}
	}
}
