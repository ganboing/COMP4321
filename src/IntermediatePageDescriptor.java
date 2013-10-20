
public class IntermediatePageDescriptor {
		
	String url_fetched;
	String content;
	java.net.URL[] links;
	
	public IntermediatePageDescriptor(String _url, String _content, java.net.URL[] _links)
	{
		KeyWordMap keyword_map = new KeyWordMap();
		java.util.Scanner scanner = new java.util.Scanner(_content);
		
		while(true)
		{
			String word = scanner.next();
			
			break;
		}
		
		url_fetched = _url;
		content = _content;
		links = _links;	
	}
}
