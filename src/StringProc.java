
public class StringProc {
	
	private static java.util.regex.Pattern pattern_alpha = java.util.regex.Pattern
			.compile("[a-zA-Z]+");
	
	public static String Stem(String raw_word)
	{
		return Porter.Porter.stripAffixes(raw_word);
	}
	
	public static java.util.regex.Matcher GetWordMatcher(String phase)
	{
		return pattern_alpha.matcher(phase);
	}
}
