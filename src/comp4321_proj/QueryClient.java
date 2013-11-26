package comp4321_proj;

public class QueryClient {

	private static final String RMI_PATH = "rmi://192.168.254.2:9958/QueryRMI";

	private static QueryRMIInterface GetQueryRMI() throws Exception {
		return (QueryRMIInterface) java.rmi.Naming.lookup(RMI_PATH);
	}

	public static java.util.List<QueryResult> Query(String query_term)
			throws Exception {
		return GetQueryRMI().Query(query_term);
	}

	public static java.util.List<String> MostFreqTerm(Integer pageid,
			int max_term) throws Exception {
		return GetQueryRMI().MostFreqTerm(pageid, max_term);
	}

	public static java.util.List<String> GetAllWord() throws Exception {
		return GetQueryRMI().GetAllWord();
	}

	public static void main(String[] args) throws Exception {
		String rmi_server = args[0];
		QueryRMIInterface query_srv = (QueryRMIInterface) java.rmi.Naming
				.lookup(rmi_server);
		if(args[1].equals("query"))
		{
			java.util.Scanner input_scan = new java.util.Scanner(System.in);
			String query = input_scan.nextLine();
			java.util.List<QueryResult> ret = query_srv.Query(query);
			for (QueryResult e : ret) {
				e.print();
			}
			input_scan.close();
		}
		else if(args[1].equals("mostfreq"))
		{
			java.util.Scanner input_scan = new java.util.Scanner(System.in);
			int pageid = input_scan.nextInt();
			java.util.List<String> words = MostFreqTerm(pageid, 5);
			for(String e : words)
			{
				System.out.printf("%s, ",e);
			}
			System.out.printf("\n");
			input_scan.close();
		}
	}
	
}
