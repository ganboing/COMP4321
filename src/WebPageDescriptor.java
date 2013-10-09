import java.io.Serializable;


public class WebPageDescriptor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long interalId;
	long collectedTime;
	long lastModifiedTime;
	
	String URL;
	
	//XXX: only for testing
	public WebPageDescriptor(long id, long collectedtime, long lastmodtime, String url)
	{
		this.interalId = id;
		this.collectedTime = collectedtime;
		this.lastModifiedTime = lastmodtime;
		this.URL = new String(url);
	}
}
