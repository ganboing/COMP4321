final class JavaDontHaveGlobalVar {
	static final String[] urls = { "http://www.cse.ust.hk/",
			"http://www.cse.ust.hk?lang=hk", "http://www.cse.ust.hk?lang=cn",
			"http://www.ust.hk/", "http://www.seng.ust.hk/",
			"http://www.cse.ust.hk/admin/recruitment/head/",
			"http://dmd505.resnet.ust.hk",
			"http://www.cse.ust.hk/admin/sitemap/?map=About",
			"http://www.cse.ust.hk/admin/welcome/",
			"http://www.cse.ust.hk/admin/about/",
			"http://www.cse.ust.hk/admin/factsheet/",
			"http://www.cse.ust.hk/News/?type=news|achievement",
			"http://www.cse.ust.hk/News/?type=event",
			"http://cssystem.cse.ust.hk/UGuides/csd_manage.html",
			"http://www.cse.ust.hk/admin/contact/",
			"http://www.ust.hk/eng/about/campus_gethere.htm",
			"http://www.cse.ust.hk/admin/sitemap/?map=People",
			"http://www.cse.ust.hk/admin/people/faculty/",
			"http://www.cse.ust.hk/admin/people/staff/",
			"http://www.cse.ust.hk/ct/",
			"http://www.cse.ust.hk/admin/people/pg/",
			"http://www.cse.ust.hk/admin/recruitment/",
			"http://www.cse.ust.hk/admin/sitemap/?map=Undergraduate",
			"http://www.cse.ust.hk/ug/admissions/comp/",
			"http://www.cse.ust.hk/ug/hkust_only/",
			"http://www.cse.ust.hk/ug/admissions/comp/4yr/",
			"http://www.cse.ust.hk/ug/hkust_only/4yr/",
			"http://www.cse.ust.hk/ug/admissions/",
			"http://www.cse.ust.hk/admin/sitemap/?map=Postgraduate",
			"http://www.cse.ust.hk/pg/",
			"http://www.cse.ust.hk/pg/hkust_only/", };
	int var_cnt = urls.length;

	public synchronized String[] GiveMeUrl(int cnt) {
		if (var_cnt < cnt) {
			cnt = var_cnt;
		}
		return java.util.Arrays.copyOfRange(urls, urls.length - var_cnt,
				urls.length - var_cnt + cnt);
	}
}


final class HttpWorkerMonitor implements Runnable {

	Object ThreadPool;

	TheSoCalledMonitor(Object _ThreadPool) {
		ThreadPool = _ThreadPool;
	}

	public void run() {

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}

public class TestHttpConnection {
	public static void main(String[] args) {
		System.out.printf("argc == %d \n", args.length);
		int page_cnt = Integer.parseInt(args[0]);

		
		IdxExecutor.
		
		for (int i = 0; i < page_cnt; i++) {
			IdxExecSrv
					.submit(new HttpWorkerTask(JavaDontHaveGlobalVar.urls[i]));
		}

		try {
			while (true) {
				Thread.sleep(50);

			}
			// java.net.URLConnection connection =
			// org.htmlparser.lexer.Page.getConnectionManager().openConnection(url_requested);

			// connection.setConnectTimeout(5000);
			// connection.setReadTimeout(10000);

			/*
			 * System.out.print("url == ");
			 * System.out.print(connection.getURL()); System.out.print('\n');
			 * BufferedReader in = new BufferedReader(new
			 * InputStreamReader(connection.getInputStream())); String
			 * inputLine; while ((inputLine = in.readLine()) != null)
			 * System.out.println(inputLine); in.close();
			 * System.out.print('\n'); System.out.printf("last modified == ");
			 * System.out.print(connection.getLastModified());
			 */
			/*
			 * try { connection.connect();
			 * System.out.print(connection.getContentType());
			 * System.out.print('\n'); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace();
			 * System.out.print(e.toString()); System.out.print('\n'); } try {
			 * System.out.print(connection.getContent().toString());
			 * System.out.print('\n'); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * System.out.print('\n'); System.out.printf("last modified == ");
			 * System.out.print(connection.getLastModified());
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.print("catched in main\n");
			e.printStackTrace();
			System.out.print(e.toString());
		}

	}
}
