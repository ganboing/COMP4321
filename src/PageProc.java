public class PageProc {

	public static void ProcPage(IntermediatePageDescriptor imm_desc) {
		synchronized (Init.DBLock) {
			if (imm_desc.interrupted) {
				PageDB.AddOnePending(imm_desc.PageID);
				return;
			}
			PageDB.UpdateLink(imm_desc.PageID, imm_desc.links, 0.5);
			if (imm_desc.keyword_map != null) {
				for (java.util.Map.Entry<String, KeyWordDescriptor> SK : imm_desc.keyword_map
						.entrySet()) {
					Integer word_id = InvertedIdx.InsertWordDoc(
							imm_desc.PageID, SK.getKey(), SK.getValue());
					PageDB.AddDocWord(imm_desc.PageID, word_id, SK.getValue()
							.Cnt());
				}
			}
			PageDB.CreateMeta(imm_desc.PageID, imm_desc.title,
					imm_desc.last_mod);
		}
	}
}
