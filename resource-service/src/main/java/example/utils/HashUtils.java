package example.utils;

import example.constants.ResourceConstants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashUtils
{

	public static String generateNameFromBytes(byte[] data) {
		return HashUtils.bytesToHex(HashUtils.getSHA256MessageDigest().digest(data));
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0'); // Pad with leading zero if needed
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private static MessageDigest getSHA256MessageDigest() {
		try {
			return MessageDigest.getInstance(ResourceConstants.SHA_256);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
