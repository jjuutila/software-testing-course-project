package org.gjt.sp.jedit.search;


import org.junit.Test;
import org.gjt.sp.jedit.search.SearchMatcher.Match;
import static junit.framework.Assert.assertEquals;

public class DocumentSearchTest {

	@Test
	public void countMatches() {
		String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n"
				+ "Nullam odio eros, ultrices id ornare at, auctor vel felis.\n" +
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
		
		final PatternSearchMatcher spm =
		        new PatternSearchMatcher("vi", true);
		
		Match match = null;
		int count = 0;
		
		while ((match = spm.nextMatch(text, true, true, true, false)) != null) {
			count++;
			text = text.substring(match.end);
		}
		
		assertEquals(9, count);
	}
}
