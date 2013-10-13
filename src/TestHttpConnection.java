import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class TestHttpConnection {
	public static void main (String[] args)
    {
		System.out.printf("argc == %d \n", args.length);
		assert(args.length == 1);
		String url_requested = args[0];
		try {
			java.net.URLConnection connection = org.htmlparser.lexer.Page.getConnectionManager().openConnection(url_requested);
			System.out.print("url == ");
			System.out.print(connection.getURL());
			System.out.print('\n');
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            System.out.println(inputLine);
	        in.close();
			System.out.print('\n');
			System.out.printf("last modified == ");
			System.out.print(connection.getLastModified());
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print(e.toString());
		}
		
    }
}
