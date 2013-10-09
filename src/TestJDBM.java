public class TestJDBM {

	public static void main(String[] args) throws Exception {

		for (int i = 0; i < args.length; i++) {
			System.out.printf("now args %d == %s", i, args[i]);
		}
		if (args.length == 1) {
			if (args[0].equals("createdb")) {
				
				jdbm.RecordManager record_manager;
				jdbm.htree.HTree hashtable;

				try {
					record_manager = jdbm.RecordManagerFactory
							.createRecordManager("./test_db");
				} catch (Exception e) {
					System.out.println(e.toString());
					return;
				}

				long recid;
				recid = record_manager.getNamedObject("test_ht");
				if (recid != 0) // If the object has already been recorded in
								// record
					// manager;
					hashtable = jdbm.htree.HTree.load(record_manager, recid);

				else // If not, create a new hashtable;
				{
					hashtable = jdbm.htree.HTree.createInstance(record_manager);
					record_manager.setNamedObject("test_ht",
							hashtable.getRecid()); // Store
													// object
					// hashtable ht1
					// into recman
					// as the name
					// "ht1";
				}

				WebPageDescriptor web1 = new WebPageDescriptor(0x12345678,
						0x12345678, 0xaabbccdd, new String("t"));

				hashtable.put(((long) 0xabcd), web1);

				record_manager.commit();
				record_manager.close();
			} else if (args[0].equals("testdb")) {

			} else {
				throw new Exception("option not implemented");
			}
		}

	}

}
