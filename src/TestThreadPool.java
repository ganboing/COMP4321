class WorkerThread implements Runnable {

	private String command;

	public WorkerThread(String s) {
		this.command = s;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()
				+ " Start. Command = " + command);
		processCommand();
		System.out.println(Thread.currentThread().getName() + " End.");
	}

	private void processCommand() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return this.command;
	}
}

public class TestThreadPool {
	public static void main(String[] args) {
		java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors
				.newFixedThreadPool(5);
		for (int i = 0; i < 10; i++) {
			Runnable worker = new WorkerThread("" + i);
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		System.out.println("Finished all threads");
	}

}
