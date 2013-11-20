public final class IndexingProc {

	private static final int POOL_SIZE = 16;
	
	private static java.util.concurrent.ExecutorService IdxExecutor = java.util.concurrent.Executors
			.newFixedThreadPool(POOL_SIZE);
	// IdxExecutor.
	private static java.util.concurrent.CompletionService<IntermediatePageDescriptor> IdxExecSrv = new java.util.concurrent.ExecutorCompletionService<IntermediatePageDescriptor>(
			IdxExecutor);

	private static final class HttpWorkerMonitor implements Runnable {
		public static volatile boolean should_continue = true;

		private void PickAndProc()
		{
			try {
				java.util.concurrent.Future<IntermediatePageDescriptor> impage_future = IndexingProc.IdxExecSrv.take();
				PageProc.ProcPage(impage_future.get());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-2);
			}
		}
		
		public void run() {
			int page_indexing = 0;
			while (should_continue) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-2);
				}
				if(page_indexing < POOL_SIZE)
				{
					for(int i=page_indexing ; i < POOL_SIZE ; i++)
					{
						PageDB.GetOnePending();
					}
				}
				
			}
			HttpWorkerTask.should_continue= false;
			for(int i = 0; i < page_indexing ; i++)
			{
				PickAndProc();
			}
		}
	}

	private static Runnable monitor_runnable = new HttpWorkerMonitor();
	private static Thread monitor_thread = new Thread(monitor_runnable);

	public static void Start() {
		monitor_thread.run();
	}

	public static void Stop() {
		HttpWorkerMonitor.should_continue = false;
		try {
			monitor_thread.join();
		} catch (InterruptedException e) {
			// XXX Auto-generated catch block
			e.printStackTrace();
		}
	}
}
