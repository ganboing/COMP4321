package comp4321_proj;
public class PageProc {

	public static void ProcPage(IntermediatePageDescriptor imm_desc) {
		try {
			Init.DBSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		if (imm_desc.interrupted) {
			PageDB.AddOnePending(imm_desc.PageID);
			Init.DBSem.release();
			return;
		}
		PageDB.UpdateLink(imm_desc.PageID, imm_desc.links, 0.5);
		//System.out.printf("Page content added %s :\n", imm_desc.url);
		if (imm_desc.keyword_map != null) {
			for (java.util.Map.Entry<String, KeyWordDescriptor> SK : imm_desc.keyword_map
					.entrySet()) {
				Integer word_id = InvertedIdx.InsertWordDoc(imm_desc.PageID,
						SK.getKey(), SK.getValue());
				PageDB.AddDocWord(imm_desc.PageID, word_id, SK.getValue().Cnt());
				//System.out.printf("{%s, %d}",  word_id, SK.getValue().Cnt());
			}
			//System.out.printf("\n");
		}
		PageDB.CreateMeta(imm_desc.PageID, imm_desc.title, imm_desc.last_mod);
		Init.DBSem.release();
	}
}
