package comp4321_proj;

public class QueryClient {

	private final String RMI
	
	public static java.util.List<QueryResult> Query(String query_term)
			throws Exception {
		QueryRMIInterface query_srv = (QueryRMIInterface) java.rmi.Naming
				.lookup("rmi://localhost:9958/QueryRMI");
		return query_srv.Query(query_term);
	}
	
	public static java.util.List<String> MostFreqTerm(Integer pageid, int max_term) throws Exception {
		
	}
	
	public static void main(String[] args) throws Exception {
		String rmi_server = args[0];
		QueryRMIInterface query_srv = (QueryRMIInterface) java.rmi.Naming
				.lookup(rmi_server);
		java.util.Scanner input_scan = new java.util.Scanner(System.in);
		String query = input_scan.nextLine();
		java.util.List<QueryResult> ret = query_srv.Query(query);
		for (QueryResult e : ret) {
			e.print();
		}
		input_scan.close();
	}
}
