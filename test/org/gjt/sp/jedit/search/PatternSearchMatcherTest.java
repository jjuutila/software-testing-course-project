package org.gjt.sp.jedit.search;

import static junit.framework.Assert.*;
import static org.gjt.sp.jedit.search.MatchAssert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.search.SearchMatcher.Match;
import org.junit.Test;

public class PatternSearchMatcherTest {
	
	/**
	 * <p>{@code start} is used to signify whether or not the character
	 * sequence (could be user selected) starts from a beginning of a
	 * line. If it doesn’t start, ^ should not match the in the beginning
	 * of the input (the first match must be skipped).</p>
	 * @testcreated 2011-09-22
	 * @testpriority medium
	 */
	@Test
	public void trueStartMatchesFromBeginningOfTheLine() {
	    final PatternSearchMatcher spm =
	        new PatternSearchMatcher("^abbacd$", false);

	    assertMatch(0, 6, 
	    	spm.nextMatch("abbacd\nabbacd\n", true, true, true, false));
	}
	
	/**
	 * <p>Like {@link #trueStartMatchesFromBeginningOfTheLine()} but
	 * expect it to match on the line after first LF.</p>
	 * @testcreated 2011-09-28
	 * @testpriority medium
	 */
	@Test
	public void falseStartMatchesFromNextLine() {
		final PatternSearchMatcher spm =
	        new PatternSearchMatcher("^abbacd$", false);
	
	    assertMatch(7, 13, 
	    	spm.nextMatch("abbacd\nabbacd\n", false, true, true, false));
	}
	
	/**
	 * <p>{@code end} is used to signify whether or not the character
	 * sequence (could be user selected) ends to end of line.
	 * If it doesn’t end, $ should not match the in the end
	 * of the input (the last match must be skipped).</p>
	 * @testcreated 2011-09-22
	 * @testpriority medium
	 */
	@Test
	public void trueEndMatchesToTheEnd() {
	    final PatternSearchMatcher spm =
	        new PatternSearchMatcher("^abbacd$", false);
	
	    assertMatch(7, 13, 
	    	spm.nextMatch("abbacd\nabbacd\n", false, true, true, false));
	}
	
	/**
	 * <p>Like {@link #trueEndMatchesToTheEnd()} but
	 * expect it to match on the line before the last LF.</p>
	 * @testcreated 2011-09-28
	 * @testpriority medium
	 */
	@Test
	public void falseEndMatchesToTheEOLBeforeLast() {
		final PatternSearchMatcher spm =
	        new PatternSearchMatcher("^abbacd$", false);
	
	    assertMatch(0, 6, 
	    	spm.nextMatch("abbacd\nabbacd\n", true, false, true, false));
	}	
	
	/**
	 * @testcreated 2011-09-28
	 * @testpriority medium
	 */
	@Test
	public void trueFirstTimeMatchesFirstMatch() {
		final PatternSearchMatcher spm =
			new PatternSearchMatcher("^.*$", false);
		
		assertMatch(0, 0,
			spm.nextMatch("\nabbacd\nabbacd\n", true, true, true, false));
	}

	/**
	 * @testcreated 2011-09-28
	 * @testpriority medium
	 */
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
	 * @testcreated 2011-09-22
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
	 * @testcreated 2011-09-22
	 * @testpriority medium
	 */
	@Test
	public void falseFirstTimeMatchesSecondToLastReverseMatch() {
		final PatternSearchMatcher spm =
			new PatternSearchMatcher("^.*$", false);

		// we substring here to adhere to the normal usage convention
		// of #nextMatch(..); while finding the first reverse match
		// firstTime=true will return (21,21) (in reversed, see above)
		// so we'll need to do substring(0, 21) in order to accomodate
		// this use case
		
		//                        111 1111111 2 2
		//             012345 6789012 3456789 0 1
		String text = "abbacd\nabbacd\nabbacd\n\n".substring(0, 21);
		//                             ^      ^ (non inclusive)
		
		assertReverseMatch(text, 14, 20,
			spm.nextMatch(text, true, true, false, true));
	}
	
	/**
	 * <p>Tests if matcher can handle chars outside of ASCII charset.</p>
	 * @testcreated 2011-10-11
	 * @testpriority low
	 */
	@Test
	public void matchesNonAscii() {
        final String text = "aoääffЩ௫𐍈";
        
        PatternSearchMatcher spm =
                new PatternSearchMatcher("ää", false);
        
        assertMatch(2, 4, 
        		spm.nextMatch(text, true, true, true, false));
        
        spm = new PatternSearchMatcher("Щ௫", false);
        
        assertMatch(6, 8, 
                spm.nextMatch(text, true, true, true, false));
	}

	/**
	 * <p>Tests for the negative case of pattern with a match to (line) end cannot
	 * find a suitable match if the {@code end} is <code>false</code>; this would be the
	 * case when user has selected "abbacda " from the middle of a line in a text document.</p>
	 * @testcreated 2011-10-25
	 * @testpriority medium
	 */
	@Test
	public void endMatchingPatternDoesNotFindMatchWithFalseEnd() {
		final PatternSearchMatcher spm = new PatternSearchMatcher("a.$", false);
        String text = "abbacda ";

        assertNull(spm.nextMatch(text, true, false, true, false));
	}
	
	/**
	 * <p>Tests that with a (line) end matching pattern the second to last returning
	 * code path is triggered when {@code end} is <code>false</code> (see situation 
	 * explanation from {@link #endMatchingPatternDoesNotFindMatchWithFalseEnd()}).</p>
	 * @testcreated 2011-10-25
	 * @testpriority medium
	 */
	@Test
	public void endMatchingPatternFindsTheSecondToLastWithFalseEnd() {
		final PatternSearchMatcher spm = new PatternSearchMatcher("a.$", false);
		//             01234567 8901234567
		String text = "abbacda \nabbacda ";
		//                   ^  ^ (not inclusive)
        
		assertMatch(6, 8,
				spm.nextMatch(text, true, false, true, false));
	}
	
	/**
	 * <p>Like {@link #endMatchingPatternFindsTheSecondToLastWithFalseEnd()} but
	 * this time in reverse.</p>
	 * @testcreated 2011-10-25
	 * @testpriority medium
	 */
    @Test
    public void endMatchingPatternFindsTheSecondToLastReverseWithFalseEnd() {
    	final PatternSearchMatcher spm = new PatternSearchMatcher("a.$", false);
        //             01234567 8901234567
        String text = "abbacda \nabbacda ";
        //                   ^  ^ (not inclusive)
        
        assertReverseMatch(text, 6, 8,
        		spm.nextMatch(text, true, false, true, true));
    }
    
    /**
     * <p>While zero width matching patterns are not of great interest to the
     * user to use the class should handle them gracefully by allowing the user
     * step through each character of the document.</p>
     * @testcreated 2011-10-25
     * @testpriority low
     */
    @Test
    public void zeroWidthMatches() {
    	final PatternSearchMatcher spm = new PatternSearchMatcher("(?=.*$)", false);

    	Pattern p = Pattern.compile("(?=.*$)");
    	Matcher m = p.matcher("abbacd\n");
    	assertTrue(m.find());
    	assertEquals(0, m.start());
    	assertEquals(0, m.end());
    	assertTrue(m.find());
    	assertEquals(1, m.start());
    	assertEquals(1, m.end());
    	
    	//                        1111111 11
    	//             01234567 890123456 78
        String text = "abbacda \nabbacda \n";
        //                                ^
        //                              ^
        assertReverseMatch(text, 0, 0,
        		spm.nextMatch(text, true, true, true, false));
        
        assertReverseMatch(text, 1, 1,
        		spm.nextMatch(text, true, true, false, false));
    }
    
    /**
     * <p>Like {@link #zeroWidthMatches()} but for reverse matching.</p>
     * @testcreated 2011-10-25
     * @testpriority low
     */
    @Test
    public void zeroWidthReverseMatches() {
    	final PatternSearchMatcher spm = new PatternSearchMatcher("(?=.*$)", false);

    	//                        1111111 11
    	//             01234567 890123456 78
        String text = "abbacda \nabbacda \n";
        //                                ^
        //                              ^
        assertReverseMatch(text, 18, 18,
        		spm.nextMatch(text, true, true, true, true));
        
        assertReverseMatch(text, 17, 17,
        		spm.nextMatch(text, true, true, false, true));
    }
    
    /**
     * <p>A zero width match pattern such as <code>(?=.*$)</code> should be handled
     * as a no match when tried against empty selection. This is of low importance
     * because two unlikely actions need to happen; first the user must be using a pattern
     * that is zero width matching pattern, second the users empty selection must be passed over
     * to the {@link PatternSearchMatcher}.</p>
     * @testcreated 2011-10-25
     * @testpriority low
     */
    @Test
    public void emptyZeroWidthMatchIsHandledGracefully() {
    	final PatternSearchMatcher spm = new PatternSearchMatcher("(?=.*$)", false);
       
        assertNull(spm.nextMatch("", true, true, true, true));
    }
}
