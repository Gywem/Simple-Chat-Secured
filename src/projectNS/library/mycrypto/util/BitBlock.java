package projectNS.library.mycrypto.util;

public class BitBlock {
	private String bitChain;

	public String getBitChain() {
		return bitChain;
	}

	public void setBitChain(String bitChain) {
		this.bitChain = bitChain;
	}

	public BitBlock (String bitChain) {
		this.bitChain = bitChain;		
	}

	public String getSubBitChain(int start, int subBits) {
		String subBitChain = this.bitChain.substring(start, start+subBits);

		return subBitChain;
	}

	public BitBlock leftShift(int number){
		int desp = number % this.bitChain.length();

		String pL = this.bitChain.substring(0, desp);
		String pOther = this.bitChain.substring(desp, this.bitChain.length());

		this.bitChain = pOther + pL;

		return this;
	}

	public BitBlock rightShift(int number){
		int desp = number % this.bitChain.length();

		String pR = this.bitChain.substring((this.bitChain.length() - desp), this.bitChain.length());
		String pOther = this.bitChain.substring(0, this.bitChain.length() - desp);

		this.bitChain = pR + pOther;

		return this;
	}

	public void replacement(int index, char r){
		StringBuilder myName = new StringBuilder(this.bitChain);
		myName.setCharAt(index, r);
		
		this.bitChain = myName.toString();
	}

	public BitBlock permutation(int[] indexesMatrix) {
		StringBuilder copy = new StringBuilder(this.bitChain);
		this.bitChain = "";
		
		int i;
		for(i = 0; i < indexesMatrix.length; ++i){
			this.bitChain += copy.charAt(indexesMatrix[i]-1);
		}

		if(i < copy.length()) this.bitChain += copy.substring(i, copy.length());
		
		return this;
	}

	public BitBlock reverse() {
		StringBuilder copy = new StringBuilder(this.bitChain);
		
		this.bitChain = copy.reverse().toString();

		return this;
	}

	public BitBlock xor(String bitchain){
		this.bitChain = Utils.xor(this.bitChain, bitchain);

		return this;
	}

	public BitBlock S(int[][] S){
		char first = this.bitChain.charAt(0);
		char last = this.bitChain.charAt(this.bitChain.length()-1);

		String middle = this.getSubBitChain(1, this.bitChain.length()-2);

		int row = Utils.getInteger(first+""+last);
		int col = Utils.getInteger(middle);

		this.bitChain = Utils.getBitChain4Integer(S[row][col], 4);

		return this;
	}
}
