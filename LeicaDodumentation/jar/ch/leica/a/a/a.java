package ch.leica.a.a;

import ch.leica.sdk.logging.Logs;
import ch.leica.sdk.logging.Logs.LogTypes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class a {
	List<char[]> a = new ArrayList();
	private static boolean b = false;
	private static boolean c = false;
	private static boolean d = false;
	private static boolean e = false;
	private char[] f;
	private char[] g;
	private char[] h;
	private char[] i;

	public a() {
		char[] arrayOfChar1 = { 'a', 'B', '1', '3', '1', 'a', 'B', '1', '3',
				'1', '5', '7', 'P', 'z', '5', '7', 'P', 'z', '8', '0', 'a',
				'B', '1', '3', '1', 'a', 'B', '1', '3', '1', '5', '7', 'P',
				'z', '5', '7', 'P', 'z', '8', '0', '5', '7', 'P', 'z', '5',
				'7', 'P', 'z', '8', '0' };
		a.add(arrayOfChar1);
		char[] arrayOfChar2 = { '5', '7', 'P', 'z', '5', '7', 'P', 'z', '8',
				'0', 'a', 'B', '1', '3', '1', 'a', 'B', '1', '3', '1', '5',
				'7', 'P', 'z', '5', '7', 'P', 'z', '8', '0', 'a', 'B', '1',
				'3', '1', 'a', 'B', '1', '3', '1', 'a', 'B', '1', '3', '1',
				'a', 'B', '1', '3', '1' };
		a.add(arrayOfChar2);
		char[] arrayOfChar3 = { '5', '\r', 65504, 'k', '\017', 65479, '.',
				65500, '?', 65448, 65535, '\020', '*', 65427, 65434, '[',
				'\036', 'F', 'c', 65431, 65458, '\004', ']', '%', 65475, '2',
				'_', '9', '3', 'h', 'm', 'O', 'k', '7', '+', 65493, 'J', 65474,
				'\006', 65510, 65457, 65422, ';', 'M', '#', '>', 'S', 65428,
				65532, '\005', 65470, '\b', 65413, 'Z', ' ', '3', '\033',
				65512, ')', 'U', 65456, 'J', 'L', 65454 };
		f = arrayOfChar3;
		char[] arrayOfChar4 = { 65514, 65493, 65522, ' ', 65452, 'Q', 65508,
				65482, 65453, 65465, 65421, 'F', '}', 65493, 65521, 65512, '`',
				65408, 65478, 'n', '1', '?', 'c', 65431, 65452, 'Y', 65444,
				'v', 'Q', 65474, 65431, ']', 65427, ':', 'N', 65475, 65498,
				65460, 65523, '\\', 'w', 65438, 'q', 65481, 65515, 65488,
				65431, 65511, 65436, 65512, 65408, '\035', '\027', 'V', 65487,
				'e', 'z', 'T', 65455, '~', 'J', '4', 'L', 'C' };
		g = arrayOfChar4;
		char[] arrayOfChar5 = { 65455, '\034', 65527, 65425, 65479, '-', '+',
				65529, 65458, 'h', '}', 65458, '\007', '\033', 65441, 'F', 'M',
				'\n', 65523, '\036', 65448, 65440, 65453, 65514, 65474, 65482,
				65416, '\037', 65425, 65447, 65535, 65477, 65516, 'Y', 65439,
				65502, 65446, 65438, 65514, 65529, 65415, 65509, '\016', 65515,
				'<', '\001', 65413, 65419, 'b', 65452, '9', 65517, 65463,
				'\000', 'V', 'B', 65417, '\025', '*', 65408, 65532, 65430,
				65424, 65408 };
		h = arrayOfChar5;
		char[] arrayOfChar6 = { 65425, 65453, 'L', 65489, 65525, 'n', 65518,
				'4', '\027', 65526, 65412, 65516, 65417, '>', 'K', '?', 's',
				'(', 65529, '}', '\000', 'Z', 't', ';', 65521, 65521, 65435,
				65501, 65449, 65506, 65518, 65414, 65514, ';', 'F', 65461,
				65487, '\001', '#', 'O', 65411, 'G', 65453, '\021', 65533,
				65524, 65438, 65474, 65517, ')', '5', 65432, 65434, '9', 'o',
				'\037', 'f', 'T', '-', 'w', '\021', 'o', 65515, 65487 };
		i = arrayOfChar6;
	}

	public char[] a(char[] paramArrayOfChar) {
		if ((paramArrayOfChar == null) || (paramArrayOfChar.length < 1)) {
			return null;
		}
		byte[] arrayOfByte1 = new byte[paramArrayOfChar.length];
		for (int j = 0; j < paramArrayOfChar.length; j++) {
			arrayOfByte1[j] = ((byte) paramArrayOfChar[j]);
		}
		b localB = new b();
		byte[] arrayOfByte2 = localB.a((char[]) a.get(0), (char[]) a.get(1),
				arrayOfByte1);
		char[] arrayOfChar1 = new char[arrayOfByte2.length];
		for (int k = 0; k < arrayOfByte2.length; k++) {
			arrayOfChar1[k] = ((char) arrayOfByte2[k]);
		}
		MessageDigest localMessageDigest = null;
		char[] arrayOfChar2;
		try {
			localMessageDigest = MessageDigest.getInstance("SHA-512");
			byte[] arrayOfByte3 = new byte[arrayOfChar1.length];
			for (int m = 0; m < arrayOfChar1.length; m++) {
				arrayOfByte3[0] = ((byte) arrayOfChar1[0]);
			}
			byte[] arrayOfByte4 = localMessageDigest.digest(arrayOfByte3);
			arrayOfChar2 = new char[arrayOfByte4.length];
			for (int n = 0; n < arrayOfByte4.length; n++) {
				arrayOfChar2[n] = ((char) arrayOfByte4[n]);
			}
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
			localNoSuchAlgorithmException.printStackTrace();
			arrayOfChar2 = arrayOfChar1;
		}
		return arrayOfChar2;
	}

	private boolean a(char[] paramArrayOfChar1, char[] paramArrayOfChar2) {
		if ((paramArrayOfChar1 == null) || (paramArrayOfChar1.length < 1)) {
			return false;
		}
		char[] arrayOfChar = a(paramArrayOfChar1);
		if (arrayOfChar.length != paramArrayOfChar2.length) {
			return false;
		}
		for (int j = 0; j < arrayOfChar.length; j++) {
			if (arrayOfChar[j] != paramArrayOfChar2[j]) {
				return false;
			}
		}
		return true;
	}

	private boolean b(char[] paramArrayOfChar) {
		boolean bool = a(paramArrayOfChar, f);
		Logs.log(Logs.LogTypes.debug, "keyValid: " + bool);
		return bool;
	}

	private boolean c(char[] paramArrayOfChar) {
		boolean bool = a(paramArrayOfChar, g);
		Logs.log(Logs.LogTypes.debug, "keyValid: " + bool);
		return bool;
	}

	private boolean d(char[] paramArrayOfChar) {
		boolean bool = a(paramArrayOfChar, h);
		Logs.log(Logs.LogTypes.debug, "keyValid: " + bool);
		return bool;
	}

	private boolean e(char[] paramArrayOfChar) {
		boolean bool = a(paramArrayOfChar, i);
		Logs.log(Logs.LogTypes.debug, "keyValid: " + bool);
		return bool;
	}

	public static synchronized void a(List<String> paramList) {
		a localA = new a();
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			String str = (String) localIterator.next();
			if (localA.b(str.toCharArray()) == true) {
				b = true;
			} else if (localA.c(str.toCharArray()) == true) {
				c = true;
			} else if (localA.d(str.toCharArray()) == true) {
				d = true;
			} else if (localA.e(str.toCharArray()) == true) {
				e = true;
			}
		}
	}

	public static boolean a() {
		return b;
	}

	public static boolean b() {
		return c;
	}

	public static boolean c() {
		return d;
	}

	public static boolean d() {
		return e;
	}
}
