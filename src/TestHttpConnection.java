import java.io.IOException;



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
			try {
				connection.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.print(e.toString());
			}
			System.out.print('\n');
			System.out.printf("last modified == ");
			System.out.print(connection.getLastModified());
		} catch (org.htmlparser.util.ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print(e.toString());
		}
		
    }
}
