package projectNS.library.mycrypto;

import org.apache.commons.codec.binary.Base64;

import projectNS.library.mycrypto.util.BitBlock;
import projectNS.library.mycrypto.util.Utils;

public class MyDES extends SymmetricCrypto {

	private boolean Debug = false;
	
	int[] PC_1 = {
		57, 49, 41, 33, 25, 17, 9,
		1, 58, 50, 42, 34, 26, 18,
		10, 2, 59, 51, 43, 35, 27,
		19, 11, 3, 60, 52, 44, 36,
		63, 55, 47,39, 31, 23, 15,
		7, 62, 54, 46, 38, 30, 22,
		14, 6, 61, 53, 45, 37, 29,
		21, 13, 5, 28, 20, 12, 4
	};

	int[] PC_2 = {
		14, 17, 11, 24,  1, 5,
		3, 28, 15,  6, 21, 10,
		23, 19, 12,  4, 26, 8,
		16,  7, 27, 20, 13, 2,
		41, 52, 31, 37, 47, 55,
		30, 40, 51, 45, 33, 48,
		44, 49, 39, 56, 34, 53,
		46, 42, 50, 36, 29, 32
	};

	int[] IP = {
		58, 50, 42, 34, 26, 18, 10, 2,
		60, 52, 44, 36, 28, 20, 12, 4,
		62, 54, 46, 38, 30, 22, 14, 6,
		64, 56, 48, 40, 32, 24, 16, 8,
		57, 49, 41, 33, 25, 17,  9, 1,
		59, 51, 43, 35, 27, 19, 11, 3,
		61, 53, 45, 37, 29, 21, 13, 5,
		63, 55, 47, 39, 31, 23, 15, 7
	};

	int[] E = {
		32, 1, 2, 3, 4, 5,
		4, 5, 6, 7, 8, 9,
		8, 9, 10, 11, 12, 13,
		12, 13, 14, 15, 16, 17,
		16, 17, 18, 19, 20, 21,
		20, 21, 22, 23, 24, 25,
		24, 25, 26, 27, 28, 29,
		28, 29, 30, 31, 32, 1
	};

	int[][][] S = {
		{
			{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
			{0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
			{4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
			{15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
		},
		{
			{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
			{3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
			{0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
			{13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
		},
		{
			{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
			{13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
			{13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
			{1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
		},
		{
			{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
			{13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
			{10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
			{3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
		},
		{
			{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
			{14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
			{4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
			{11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}
		},
		{
			{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
			{10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
			{9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
			{4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}
		},
		{
			{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
			{13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
			{1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
			{6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}
		},
		{
			{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
			{1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
			{7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
			{2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}
		}
	};

	int[] P = {
		16,7,20,21,
		29,12,28,17,
		1,15,23,26,
		5,18,31,10,
		2,8,24,14,
		32,27,3,9,
		19,13,30,6,
		22,11,4,25
	};

	int[] IP_1 = {
		40,8,48,16,56,24,64,32,
		39,7,47,15,55,23,63,31,
		38,6,46,14,54,22,62,30,
		37,5,45,13,53,21,61,29,
		36,4,44,12,52,20,60,28,
		35,3,43,11,51,19,59,27,
		34,2,42,10,50,18,58,26,
		33,1,41,9,49,17,57,25
	};



	int[] Shifts = {
		 1, 1, 2, 2 ,2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
	};

	private BitBlock[][] subkeys;
	
	@Override
	public String cipher(String rawData) {
		String dataNoPadding = Utils.removePadding(rawData);
		
		BitBlock[][] subKeys = this.subkeys;

		String[] blocks = this.getBlocks(dataNoPadding);

		BitBlock cip;
		BitBlock[] resultBitBlock = new BitBlock[blocks.length];
		for(int i = 0; i < blocks.length; ++i) {
			if(Debug) System.out.println("Block: "+blocks[i]);
			if(blocks[i].length() < 8 && i == blocks.length-1) blocks[i] = Utils.applyPadding(blocks[i]);
			cip = this.encodeBlock(new BitBlock(Utils.getBitChain(blocks[i])), subKeys[0]);
			cip = this.decodeBlock(cip, subKeys[1]);
			resultBitBlock[i] = this.encodeBlock(cip, subKeys[2]);

			if(Debug) {
				System.out.println("Cifrado");
				System.out.println(cip.getBitChain());
			}
		}
		
		String resultStr = "";
		for(int i = 0; i < resultBitBlock.length; ++i) {
			resultStr += Utils.getString(resultBitBlock[i].getBitChain());
		}
		
		return new Base64().encodeToString(resultStr.getBytes());
	}

	@Override
	public String decipher(String rawData) {
		byte[] decoded = Base64.decodeBase64(rawData);
		String radata =new String(decoded);
		
		BitBlock[][] subKeys = this.subkeys;

		String[] blocks = this.getBlocks(radata);

		BitBlock decip;
		BitBlock[] resultBitBlock = new BitBlock[blocks.length];
		for(int i = 0; i < blocks.length; ++i) {
			if(Debug) System.out.println("Block: "+blocks[i]);
			decip = this.decodeBlock(new BitBlock(Utils.getBitChain(blocks[i])), subKeys[2]);
			decip = this.encodeBlock(decip, subKeys[1]);
			resultBitBlock[i] = this.decodeBlock(decip, subKeys[0]);
			if(Debug) {
				System.out.println("Descifrado");
				System.out.println(decip.getBitChain());
			}
		}
		
		String resultStr = "";
		for(int i = 0; i < resultBitBlock.length; ++i) {
			resultStr += Utils.getString(resultBitBlock[i].getBitChain());
		}

		return Utils.removePadding(resultStr);
	}

	@Override
	public void generateKey() {
		this.defaultGenerateKey();
		
		this.generateSubkeys(this.getSymmetricKey());
	}
	
	public void setSharedKey(String k) {
		super.setSharedKey(k);

		this.generateSubkeys(this.getSymmetricKey());	
	}
	
	public void generateSubkeys(String keyBitChain) {
		int count = this.configuration.getKeyLength()/3/8;
		
		this.subkeys = new BitBlock[3][16+1];
		for(int i = 0; i < 3; ++i) {
			String key = this.getSymmetricKey().substring(i*count, ((i+1)*count));
			if(Debug) System.out.println(i+" "+key);
			this.subkeys[i] = this.generateSubkeysFrom1key(Utils.getBitChain(key));	
		}
		
	}
	
	public BitBlock[] generateSubkeysFrom1key(String keyBitChain) {
		BitBlock keyBB = new BitBlock(keyBitChain);
		BitBlock[] K = new BitBlock[16+1];
		BitBlock[] C = new BitBlock[Shifts.length+1], D = new BitBlock[Shifts.length+1];

		keyBB.permutation(PC_1);
		
		if(Debug) {
			System.out.println("Permutation P-1");
			System.out.println(keyBB.getBitChain());
		}

		BitBlock key_plusBB = new BitBlock(keyBB.getSubBitChain(0, 56));

		if(Debug) {
			System.out.println("Permutation P-1 56-bits");
			System.out.println(key_plusBB.getBitChain());
		}

		C[0] = new BitBlock(key_plusBB.getSubBitChain(0, 28));
		D[0] = new BitBlock(key_plusBB.getSubBitChain(28, 28));

		for(int i = 0; i < Shifts.length; ++i){
			C[i+1] = new BitBlock(C[i].getBitChain());
			D[i+1] = new BitBlock(D[i].getBitChain());

			C[i+1].leftShift(Shifts[i]);
			D[i+1].leftShift(Shifts[i]);

			if(Debug) {
				System.out.println("C"+(i+1));
				System.out.println(C[i+1].getBitChain());
				System.out.println("D"+(i+1));
				System.out.println(D[i+1].getBitChain());
			}
		}

		for(int i = 0; i < 16; ++i){
			K[i+1] = new BitBlock(C[i+1].getBitChain()+D[i+1].getBitChain());

			K[i+1].permutation(PC_2);
			
			K[i+1] = new BitBlock(K[i+1].getSubBitChain(0,48));
			if(Debug) {
				System.out.println("Permutation 48bit K"+(i+1));
				System.out.println(K[i+1].getBitChain());
			}
		}

		return K;
	}
	
	public String[] getBlocks(String plaintext){
		if(plaintext.length() < 8) return new String[]{plaintext};
		double cont = Math.ceil(plaintext.length()/8.0);
		
		String[] blocks = new String[(int) cont];

		for(int i = 0; i < cont; ++i) {
			blocks[i] = plaintext.substring(i*8, Math.min(i*8+8, plaintext.length()));
		}

		return blocks;
	}
	
	public BitBlock encodeBlock(BitBlock M, BitBlock[] subkeys){
		BitBlock[] L = new BitBlock[16+1], R = new BitBlock[16+1];
		BitBlock[] K = subkeys;

		BitBlock IP_ = M.permutation(IP);

		L[0] = new BitBlock(IP_.getSubBitChain(0, 32));
		R[0] = new BitBlock(IP_.getSubBitChain(32, 32));

		BitBlock fBitBlock;
		int i;
		for(i = 0; i < 16; ++i) {
			fBitBlock = this.f(R[i], K[i+1]);

			L[i+1] = new BitBlock(R[i].getBitChain());
			R[i+1] = fBitBlock.xor(L[i].getBitChain());
		}

		BitBlock RL = new BitBlock(R[i].getBitChain()+L[i].getBitChain());

		return RL.permutation(IP_1);
	}
	
	public BitBlock decodeBlock(BitBlock M, BitBlock[] subkeys) {
		BitBlock[] L = new BitBlock[16+1], R = new BitBlock[16+1];
		BitBlock[] K = subkeys;

		BitBlock IP_ = M.permutation(IP);

		L[0] = new BitBlock(IP_.getSubBitChain(0, 32));
		R[0] = new BitBlock(IP_.getSubBitChain(32, 32));

		BitBlock fBitBlock;
		int i, k;
		for(k = 16, i = 0; 0 < k && i < 16; --k, ++i) {
			fBitBlock = this.f(R[i], K[k]);

			L[i+1] = new BitBlock(R[i].getBitChain());
			R[i+1] = fBitBlock.xor(L[i].getBitChain());
		}

		BitBlock RL = new BitBlock(R[i].getBitChain()+L[i].getBitChain());

		return RL.permutation(IP_1);
	}
	
	public BitBlock f(BitBlock R, BitBlock K){
		BitBlock RCopy = new BitBlock(R.getBitChain());
		BitBlock ER = RCopy.permutation(E);

		BitBlock KER = ER.xor(K.getBitChain());

		BitBlock B;
		String bitChainSB = "";
		for(int i = 0; i < 8; ++i) {
			B = new BitBlock(KER.getSubBitChain(i*6, 6));
			bitChainSB = bitChainSB + B.S(S[i]).getBitChain();
 		}

		BitBlock f = new BitBlock(bitChainSB);

 		return f.permutation(P);
	}

}
