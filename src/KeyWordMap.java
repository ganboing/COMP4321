public class KeyWordMap extends java.util.HashMap<String, Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8970906244201879595L;

	public void addWord(String word)
	{
		Integer word_cnt = this.get(word);
		if(word_cnt != null)
		{
			this.put(word, Integer.valueOf(word_cnt.intValue()+1));
		}
		else
		{
			this.put(word, Integer.valueOf(1));
		}
	}
	
	public void print()
	{
		System.out.print("{ ");
		for(java.util.Map.Entry<String, Integer> i : this.entrySet())
		{
			System.out.printf("(%s, %d), ", i.getKey(), i.getValue());
		}
		System.out.print(" }\n");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.print("not implemented");
		// TODO Auto-generated method stub

	}

}
