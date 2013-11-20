public final class IndexingProc {

	private static final int POOL_SIZE = 16;

	private static java.util.concurrent.ExecutorService IdxExecutor = java.util.concurrent.Executors
			.newFixedThreadPool(POOL_SIZE);
	// IdxExecutor.
	private static java.util.concurrent.CompletionService<IntermediatePageDescriptor> IdxExecSrv = new java.util.concurrent.ExecutorCompletionService<IntermediatePageDescriptor>(
			IdxExecutor);

	private static final class HttpWorkerMonitor implements Runnable {
		public static volatile boolean should_continue = true;

		private static int page_indexing = 0;

		private void PickAndProcWithWait() {
			try {
				java.util.concurrent.Future<IntermediatePageDescriptor> impage_future = IndexingProc.IdxExecSrv
						.take();
				PageProc.ProcPage(impage_future.get());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-2);
			}
		}

		private boolean PickAndProcWithoutWait() {
			try {
				java.util.concurrent.Future<IntermediatePageDescriptor> impage_future = IndexingProc.IdxExecSrv
						.poll();
				if (impage_future != null) {
					PageProc.ProcPage(impage_future.get());
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-2);
			}
			return false;
		}

		public void run() {
			while (should_continue) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-2);
				}
				for (; page_indexing < POOL_SIZE; page_indexing++) {
					Integer page_id = PageDB.GetOnePending();
					IdxExecSrv.submit(new HttpWorkerTask(PageDB
							.GetPageUrl(page_id), page_id));
				}
				while (PickAndProcWithoutWait()) {
					page_indexing--;
				}
			}
			HttpWorkerTask.should_continue = false;
			for (int i = 0; i < page_indexing; i++) {
				PickAndProcWithWait();
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
