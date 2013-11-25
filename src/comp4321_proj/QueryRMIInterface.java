package comp4321_proj;
public interface QueryRMIInterface extends java.rmi.Remote {
	public java.util.List<QueryResultEle> Query(String query_term)
			throws java.rmi.RemoteException;
	public java.util.List<String> MostFreqTerm(Integer pageid,
			int max_term)throws java.rmi.RemoteException;
	public java.util.List<String> GetAllWord() throws java.rmi.RemoteException;
}
