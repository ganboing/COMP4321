
public class IntermediatePageDescriptor {

	String url_fetched;
	String content;
	java.net.URL[] links;
	
	public IntermediatePageDescriptor(String _url, String _content, java.net.URL[] _links)
	{
		url_fetched = _url;
		content = _content;
		links = _links;	
	}
}
