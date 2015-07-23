package projectNS.library.mycrypto.util;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	public static String getBitChain(String text){
		String bitChain = "";
		String chainToAttach;
		int lenChain;
		
		int i;
		for(i = 0; i < text.length(); ++i){
			chainToAttach = Integer.toBinaryString((int)text.charAt(i));
			lenChain = chainToAttach.length();
			
			int j;
			for(j = 0; j < 8 - lenChain; ++j){
				chainToAttach = "0" + chainToAttach; 
			}
			
			bitChain += chainToAttach;
		}

		return bitChain;
	}

	public static String getString(String bitChain) {
		String original = "";
		
		for(int i = 0; i < bitChain.length(); i+=8) {
			original += Character.toString((char)Integer.parseInt(bitChain.substring(i, i+8), 2));
		}

		return original;
	}

	public static int getInteger(String bitChain){
		return Integer.parseInt(bitChain, 2);
	}

	public static String getBitChain4Integer(int integer, int bits){
		String bitChain = Integer.toBinaryString(integer);

		int len = bitChain.length();
		if(len < bits){
			for(int i = 0; i < bits-len; ++i){
				bitChain = "0" + bitChain;
			}
		}

		return bitChain;
	}

	public static String xor(String bitchain1, String bitchain2) {
		String result = "";
		for(int i = 0; i < bitchain1.length(); ++i){
			if(bitchain1.charAt(i) == bitchain2.charAt(i)) {
				result += "0";
			} else {
				result += "1";
			}
		}

		return result;
	}

	public static float euclides(float a, float b) {
		if(b == 0) return a;
		else return Utils.euclides(b, a%b);
	}

	public static List<Float> extendedEuclides(float a, float b){
		List<Float> result = new ArrayList<Float>();
		if(a == 0) {
			result.add(b);
			result.add((float)0);
			result.add((float)1);
		
			return result;
		} else {
			List<Float> result2 = Utils.extendedEuclides(b%a, a);
			float g = result2.get(0);
			float y = result2.get(1);
			float x = result2.get(2);
			
			result.add(0, g);
			result.add(1,(float) (x-(Math.floor(b/a)*y)));
			result.add(2, y);
			return result;
		}
	}

	public static float inverseModulo(float a, float b){
		return Utils.extendedEuclides(a,b).get(1) + b;
	}

	public static String applyPadding(String string) {
		if(string.length() == 8) return string;
		
		String result = string;
		char paddingChar = (char) 0;
		int count = 8 - string.length();
		for(int i = 0; i < count; ++i){
			result += ""+paddingChar;
		}

		return result;
	}

	public static String removePadding(String string) {
		StringBuilder result = new StringBuilder(string);
		
		char paddingChar = (char) 0;
		boolean conti = true;
		for(int i = string.length()-1; i >= 0 && conti; --i){
			if(string.charAt(i) != paddingChar) {
				conti = false;
			} else {
				result.deleteCharAt(i);
			}
		}
		return result.toString();		
	}

//	Utils.modularExp = function(a, b, n) {
//		var bRep = b.toString(2);
//		var k = bRep.length;
//
//		var x = 1;
//		for(var i = 0; i < k; ++i) {
//			x = Math.pow(x,2);
//
//			if(bRep[i] == "1") x = (x*a)%n;
//			else x = x%n;
//		}
//
//		return x;
//	}
//
//	Utils.isPrime = function(n) {
//	 if (isNaN(n) || !isFinite(n) || n%1 || n<2) return false; 
//	 var m=Math.sqrt(n);
//	 for (var i=2;i<=m;i++) if (n%i==0) return false;
//	 return true;
//	}
//
//	Utils.getNextPrime = function(num){
//		while(!Utils.isPrime(num)){
//			num += 1;
//		}
//
//		return num;
//	}
}
