
public class TestHttpConnection {
	public static void main (String[] args)
    {
		System.out.printf("argc == %d \n", args.length);
		assert(args.length == 1);
		String url_requested = args[0];
		try {
			//java.net.URLConnection connection = org.htmlparser.lexer.Page.getConnectionManager().openConnection(url_requested);

		    //connection.setConnectTimeout(5000);
		    //connection.setReadTimeout(10000);
			
		    org.htmlparser.beans.StringBean sb = new org.htmlparser.beans.StringBean();
			sb.setURL(url_requested);
			System.out.print(sb.getStrings());
			
		    org.htmlparser.beans.LinkBean lb = new org.htmlparser.beans.LinkBean();
		    lb.setURL(url_requested);
		    java.net.URL[] URL_array = lb.getLinks();
		    for(int i=0; i<URL_array.length; i++){
		    	System.out.println(URL_array[i]);
		    }
			/*System.out.print("url == ");
			System.out.print(connection.getURL());
			System.out.print('\n');
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            System.out.println(inputLine);
	        in.close();
			System.out.print('\n');
			System.out.printf("last modified == ");
			System.out.print(connection.getLastModified());*/
			/*try {
				connection.connect();
				System.out.print(connection.getContentType());
				System.out.print('\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.print(e.toString());
				System.out.print('\n');
			}
			try {
				System.out.print(connection.getContent().toString());
				System.out.print('\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print('\n');
			System.out.printf("last modified == ");
			System.out.print(connection.getLastModified());*/
		    while(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.print("catched in main\n");
			e.printStackTrace();
			System.out.print(e.toString());
		}
		
    }
}
