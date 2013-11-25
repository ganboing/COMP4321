import java.rmi.Remote;
import java.rmi.RemoteException;
interface HiInterface extends Remote      {
	   public String speak() throws RemoteException;     
}
public class QueryRMIServer {

}
