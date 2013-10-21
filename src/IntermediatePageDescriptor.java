import java.util.regex.Pattern;

public class IntermediatePageDescriptor {

	private static Pattern pattern_alpha = Pattern.compile("[a-zA-Z]+");
	
	public String url_fetched;
	public KeyWordMap keyword_map;
	public java.net.URL[] links;

	public IntermediatePageDescriptor(String _url, String title,
			String _content, java.net.URL[] _links) {
		keyword_map = new KeyWordMap();
		// java.util.Scanner scanner = new java.util.Scanner(_content);
		java.util.regex.Matcher matcher = pattern_alpha.matcher(_content);
		while (matcher.find()) {
			String word = matcher.group();
			if ((word = StopStem.process_input_word(word)) != null)
				keyword_map.addWord(word);
		}
		
		/*try {
			while (true) {
				String word = scanner.nextLine();
				
				if( (word = StopStem.process_input_word(word)) != null)
				keyword_map.addWord(word);
			}
		} catch (Exception e) {
			assert (e instanceof java.util.NoSuchElementException);
		}
		scanner.close();*/
		try {
			synchronized (Class.forName("IntermediatePageDescriptor")) {
				System.out.printf("Imm Page: %s\n", title);
				keyword_map.print();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		url_fetched = _url;
		links = _links;
	}
}
