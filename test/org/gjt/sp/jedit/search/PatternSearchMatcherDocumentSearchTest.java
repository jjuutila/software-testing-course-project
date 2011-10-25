package org.gjt.sp.jedit.search;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.gjt.sp.jedit.search.SearchMatcher.Match;
import org.junit.Test;

public class PatternSearchMatcherDocumentSearchTest {

	/**
	 * This is the regex we are searching for
	 */
	private static final String NEEDLE = "vi";
	
	/**
	 * These are the matches we expect to find from {@link #DOCUMENT} using {@link #NEEDLE}
	 */
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
	
	/**
	 * This is our text document we want to search through
	 */
	private static final String DOCUMENT = 
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
	 * <p>Makes sure that {@link PatternSearchMatcher} can find all matches of
	 * {@link #NEEDLE} in the {@link #DOCUMENT}.</p>
	 * @testcreated 2011-10-21
	 * @testpriority high
	 */
	@Test
	public void findAllMatchesForward() {
		
		boolean reverse = false;
		
		documentMatchingScenario(reverse);
	}

	private void documentMatchingScenario(boolean reverse) {
		
		final PatternSearchMatcher spm =
		        new PatternSearchMatcher(NEEDLE, true);
		
		Set<MatchStatus> matches = findAllMatches(spm, DOCUMENT, reverse);
		
		assertEquals("not enough matches in " + matches, EXPECTED_MATCHES.size(), matches.size());
		assertTrue(matches.removeAll(EXPECTED_MATCHES));
		assertTrue("not all matches were found: " + matches, matches.isEmpty());
	}

	private Set<MatchStatus> findAllMatches(PatternSearchMatcher matcher, String text, boolean reverse) {
		// this is our view over the document containing all of the "not matched" text.
		// in the beginning it's the whole document.
		String documentView = text;
		
		int offset = 0; // not updated if reverse == true, more commentary below
		Match match = null;
		Set<MatchStatus> matches = new LinkedHashSet<MatchStatus>();
		
		while ((match = matcher.nextMatch(documentView, true, true, matches.isEmpty(), reverse)) != null) {
			
			MatchStatus ms = new MatchStatus(match, offset);
			
			if (!reverse) {
				// offset is used to keep track on how far we are in the document
				offset += match.end;
				documentView = documentView.substring(match.end);
			} else {
				// for reverse searches the semantics are different; first we need
				// to reverse the match, then we substring from the beginning to until the
				// reversed match end
				ms = ms.reverse(documentView.length());
				documentView = documentView.substring(0, documentView.length() - match.end);
			}
			
			assertTrue("duplicate match: " + ms + " for " + matches, matches.add(ms));
		}
		
		return matches;
	}
	
	/**
	 * <p>Like {@link #findAllMatchesForward()} but finds the matches in reverse fashion.</p>
	 * @testcreated 2011-10-25
	 * @testpriority high
	 */
	@Test
	public void findAllMatchesReverse() {
		
		boolean reverse = true;
		
		documentMatchingScenario(reverse);
	}
	
	/**
	 * Wrapper class for {@link Match} which implements hashCode and equals
	 * @author joonas
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
		 * @param textLength the length of the matched text for which this 
		 * 					 MatchStatus represents a reverse (counted from the end) match
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
