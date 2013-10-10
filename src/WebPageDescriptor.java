import java.io.Serializable;


public class WebPageDescriptor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long interalId;
	long collectedTime;
	long lastModifiedTime;
	
	public String pageURL;
	String children[]; //url
	String parents[]; //url
	
	
	static class TermDescriptor implements Serializable {
		
		public String term;
		public long pos[];
	}
	
	
	
	//XXX: only for testing
	public WebPageDescriptor(long id, long collectedtime, long lastmodtime, String url)
	{
		this.interalId = id;
		this.collectedTime = collectedtime;
		this.lastModifiedTime = lastmodtime;
		this.pageURL = new String(url);
	}
	
	public void print()
	{
		System.out.printf("id == %x \ncollectedtime == %d \nlastmodtime == %d \n url == %s \n", interalId, collectedTime, lastModifiedTime, pageURL);
	}
}
