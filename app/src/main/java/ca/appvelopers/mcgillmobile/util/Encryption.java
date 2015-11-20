/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.util;
/*
 * I did not implement an optimized encryption mechanism with direct binary
 * manipulation given the extra effort required and the little impact on
 * performance a small encryption mechanism will have.
 */

/**
 * TODO
 * Provides manual facilities for encrypting information into a simple modification of base 64
 * @author Omar Gonzalez
 * @version 2.0.1
 * @since 1.0.0
 */
public class Encryption {
	
	/**
	 * Private constructor to avoid instances of this class.
	 * Only pure methods encode and decode should be used.
	 */
	private Encryption(){
		
	}
	
	/**
	 * Decodes the provided string
	 * @param s The string to decode.
	 * @return The decoded string.
	 */
	public static String decode(String s){
        if(s == null){
            return null;
        }

		StringBuilder sb = new StringBuilder(s);
		StringBuilder binary = new StringBuilder();
		StringBuilder resultValue = new StringBuilder();
		char codes[] = new char[sb.length()];
		int values[] = new int[sb.length()];
		
		int length = (sb.length() * 6) / 8;
		
		if(sb.charAt(sb.length()-1) == '=') length--;
		if(sb.charAt(sb.length()-2) == '=') length--;
		
		String bits[] = new String[length];
		int ascii[] = new int[length];
		char result[] = new char[length];
		
		for(int i = 0; i < sb.length(); i++){
			codes[i] = sb.charAt(i);
			values[i] = reverseMap(codes[i]);
			binary.append(toBinary(values[i] , 6));
		}
		
		for(int i = 0; i < bits.length; i++){
			bits[i] = binary.substring(i*8, i*8 + 8);
			ascii[i] = toInt(bits[i], 8) - (i % 42);
			result[i] = (char)ascii[i];
			resultValue.append(result[i]);
		}
		
		return resultValue.toString();
	}
	
	/**
	 * Encode the provided string.
	 * @param s The string to be encoded.
	 * @return The encoded string.
	 */
	public static String encode(String s){
		
		//declarations
		String encodedString;
		StringBuilder sb = new StringBuilder();
		int values[];
		int length, padding = 0;
		String bits[];
		char result[], helper[];
		
		//shift string characters
		helper = s.toCharArray();
		for(int i = 0; i < s.length(); i++){
			helper[i] = (char)((int)helper[i] + (i % 42)); /// + i!!!!! not minus!
		}
		
		//create a string holding the binary equivalent of the input
		int value;
		for(int i = 0; i < s.length(); i++){
			value = (int)helper[i];
			sb.append(toBinary(value, 8));
		}
		
		//create arrays to prepare encoding
		length = (((sb.length()/8) + 2) / 3) * 4;
		values = new int[length];
		bits = new String[length];
		result = new char[length];
		
		//Pad with extra zeros if necessary 
		if(sb.length() % 6 != 0){
			if(sb.length() % 6 == 2){
				padding = 2;
				sb.append("0000000000000000");
			}
			else if(sb.length() % 6 == 4){
				padding = 1;
				sb.append("00000000");
			}
		}
		
		//encode values and store them into result[]
		for(int i = 0; i < bits.length - padding; i++){
			bits[i] = sb.substring(i*6, i*6 + 6);
			values[i] = toInt(bits[i], 6);
			result[i] = map(values[i]);
		}
		
		//adds '+' for padding if needed
		if(padding == 1) result[bits.length - 1] = '=';
		else if(padding == 2){
			result[bits.length - 1] = '=';
			result[bits.length - 2] = '=';
		}
		
		encodedString = String.valueOf(result);
		return encodedString;
				
	}
	
	// number (less than 2^8-1) is translated to its binary string
	// representation using n bits
	private static String toBinary(int i, int n){
		int remainder = i;
		int result[] = new int[n];
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < n; j++){
			result[j] = remainder % 2;
			remainder = remainder/2;
		}
		
		for(int j = 0; j < n; j++){
			sb.append(result[n - 1 - j]);
		}
		
		return sb.toString();
	}
	
	// binary strings ("100101") of length n are converted to decimal integers
	private static int toInt(String s, int n){
		int result = 0;
		
		for(int i = 0; i < n; i++){
			if(s.charAt(i) == '1') result += Math.pow(2, n - 1 - i);
		}
		
		return result;
	}
	
	// value from 0-63 is encoded to a character
	private static char map(int number){
		char val = '?';
		
		if(number > -1 && number < 26){         //Upper case
			val = (char)(number + 65);
		}else if(number > 25 && number < 52){   //lower case
			val = (char)(number + 97 - 26);
		}else if(number > 51 && number < 62){   //numbers
			val = (char)(number + 48 - 52);
		}else if(number == 62){
			val = '+';
		}else if(number == 63){
			val = '/';
		}
		
		return val;
	}
	
	// decodes character into int 0-63
	private static int reverseMap(char c){
		int val = 0;
		
		if(c >= 'A' && c <= 'Z'){
			val = (int)c - 65;
		}else if(c >= 'a' && c <= 'z'){
			val = (int)c - 97 + 26;
		}else if(c >= '0' && c <= '9'){
			val = (int)c - 48 + 52;
		}else if(c == '+'){
			val = 62;
		}else if(c == '/'){
			val = 63;
		}
		
		return val;
	}
}
