package comp4321_proj;
public final class IndexingProc {

	private static final int POOL_SIZE = 16;

	private static final class HttpWorkerMonitor implements Runnable {

		private java.util.concurrent.ExecutorService IdxExecutor = java.util.concurrent.Executors
				.newFixedThreadPool(POOL_SIZE);
		// IdxExecutor.
		private java.util.concurrent.CompletionService<IntermediatePageDescriptor> IdxExecSrv = new java.util.concurrent.ExecutorCompletionService<IntermediatePageDescriptor>(
				IdxExecutor);
		public volatile boolean should_continue = false;
		private int page_indexing = 0;

		private void PickAndProcWithWait() {
			try {
				java.util.concurrent.Future<IntermediatePageDescriptor> impage_future = IdxExecSrv
						.take();
				assert (impage_future != null);
				PageProc.ProcPage(impage_future.get());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-2);
			}
		}

		private boolean PickAndProcWithoutWait() {
			try {
				java.util.concurrent.Future<IntermediatePageDescriptor> impage_future = IdxExecSrv
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
				HttpWorkerTask.should_continue = true;
				try {
					// Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-2);
				}
				while (page_indexing < POOL_SIZE) {
					Integer page_id = PageDB.PollOnePending();
					if (page_id != null) {
						page_indexing++;
						IdxExecSrv.submit(new HttpWorkerTask(PageDB
								.GetPageUrl(page_id), page_id));
					} else {
						break;
					}
				}
				while (PickAndProcWithoutWait()) {
					page_indexing--;
				}
				if (page_indexing > 0) {
					PickAndProcWithWait();
					page_indexing--;
				}
			}
			HttpWorkerTask.should_continue = false;
			for (; page_indexing > 0; page_indexing--) {
				PickAndProcWithWait();
			}
			IdxExecutor.shutdown();
			try {
				Init.DBSem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-2);
			}
			System.out.printf("Indexed %d words and %d documents\n", InvertedIdx.GetDBSize(),PageDB.GetExistPageSize());
			Init.DBSem.release();
		}
	}

	private static HttpWorkerMonitor monitor_runnable = null;
	private static Thread monitor_thread = null;

	public static void Start() {
		monitor_runnable = new HttpWorkerMonitor();
		monitor_thread = new Thread(monitor_runnable);
		monitor_runnable.should_continue = true;
		monitor_thread.start();
	}

	public static void Stop() {
		monitor_runnable.should_continue = false;
		try {
			monitor_thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		monitor_thread = null;
		monitor_runnable = null;
	}
}
