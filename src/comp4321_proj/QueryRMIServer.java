package comp4321_proj;
public final class QueryRMIServer {

	static QueryRMIInterface if_instane = null;
	static String BindPath = null;

	public static void Start(int port) {
		try {
			if_instane = new QueryRMIImpl();
			java.rmi.registry.LocateRegistry.createRegistry(port);
			BindPath = "rmi://localhost:" + port + "/QueryRMI";
			java.rmi.Naming.bind(BindPath, if_instane);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
		System.out.printf("RMI bind Success at %s\n",BindPath);
	}

	public static void Stop() {
		try {
			java.rmi.Naming.unbind(BindPath);
			java.rmi.server.UnicastRemoteObject.unexportObject(if_instane,
					false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
		BindPath = null;
		if_instane = null;
		System.out.println("RMI Unbinded!");
	}
}
