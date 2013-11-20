
public class StringProc {

	private static java.util.regex.Pattern pattern_word = java.util.regex.Pattern
			.compile("[a-zA-Z]+");
	private static java.util.regex.Pattern pattern_phase = java.util.regex.Pattern
			.compile("\"[^\"]*\"");

	public static String Stem(String raw_word)
	{
		return Porter.Porter.stripAffixes(raw_word);
	}
	
	public static java.util.regex.Matcher GetWordMatcher(String term)
	{
		return pattern_word.matcher(term);
	}
	
	public static java.util.regex.Matcher GetPhaseMatcher(String phase)
	{
		return pattern_phase.matcher(phase);
	}
}
