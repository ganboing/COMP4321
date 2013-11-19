
public final class IndexingProc {
	

	private static final class HttpWorkerMonitor implements Runnable {
	public static volatile boolean should_continue = true;
		public void run() {
			while (should_continue) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		}
	}
	
	private static Runnable monitor_runnable = new HttpWorkerMonitor();
	private static Thread monitor_thread = new Thread(monitor_runnable);
	
	public static void Start()
	{
		monitor_thread.run();
	}
	
	public static void Stop()
	{
		HttpWorkerMonitor.should_continue = false;
		try {
			monitor_thread.join();
		} catch (InterruptedException e) {
			// XXX Auto-generated catch block
			e.printStackTrace();
		}
	}
}
