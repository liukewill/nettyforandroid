package com.kenan.nettydemo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
			'b', 'c', 'd', 'e', 'f' };

	/**
	 * 生成字符串的md5校验值
	 * 
	 * @param s
	 *            需要校验的字符串。
	 * @return 校验得到的 MD5 值。
	 */
	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}

	/**
	 * 判断字符串的md5校验码是否与一个已知的md5码相匹配
	 * 
	 * @param password
	 *            要校验的字符串
	 * @param md5PwdStr
	 *            已知的md5校验码
	 * @return 通过验证则返回 true；否则返回 false。
	 */
	public static boolean checkPassword(String password, String md5PwdStr) {
		String s = getMD5String(password);
		return s.equals(md5PwdStr);
	}

	/**
	 * 生成文件的md5校验值。
	 * 
	 * @param file
	 *            需要校验的文件对象。
	 * @return 校验得到的 MD5 值。
	 * @throws IOException
	 *             如果校验过程中发生 IO 错误。
	 */
	public static String getFileMD5String(File file) throws IOException {
		return getFileMD5String(file, 1024 * 128); // 经过测试，buffer大小定为32K、64K或者128K的时候速度最快
	}

	/**
	 * 生成文件的md5校验值。与 {@link #getFileMD5String(File)} 的不同是可以指定 IO
	 * 读取时的缓存大小。
	 *
	 * @param file
	 *            需要校验的文件对象。
	 * @param bufSize
	 *            校验时 IO 读取的缓存大小。这个值会影响校验过程的时间长短。
	 * @return 校验得到的 MD5 值。
	 * @throws IOException
	 *             如果校验过程中发生 IO 错误。
	 */
	public static String getFileMD5String(File file, int bufSize) throws IOException {
		MessageDigest messagedigest = null;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		if (messagedigest == null) {
			return "";
		}

		InputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[bufSize]; // 1MB
		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0) {
			messagedigest.update(buffer, 0, numRead);
		}
		fis.close();
		try {
			byte[] digest = messagedigest.digest();
			return bufferToHex(digest);
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
	}

	/**
	 * 与 {@link #getFileMD5String(File)} 的区别是内部使用 Java NIO 方式计算 MD5。
	 *
	 * @param file
	 *            需要校验的文件对象。
	 * @return 校验得到的 MD5 值。
	 * @throws IOException
	 *             如果校验过程中发生 IO 错误。
	 */
	public static String getFileMD5StringNIO(File file) throws IOException {
		return getFileMD5StringNIO(file, 1024 * 128);
	}

	/**
	 * 与 {@link #getFileMD5String(File, int)} 的区别是内部使用 Java NIO 方式计算
	 * MD5。
	 * 
	 * @param file
	 *            需要校验的文件对象。
	 * @param bufSize
	 *            校验时 IO 过程使用的缓存大小。这个值会影响校验过程的时间长短。
	 * @return 校验得到的 MD5 值。
	 * @throws IOException
	 *             如果校验过程中发生 IO 错误。
	 */
	public static String getFileMD5StringNIO(File file, int bufSize) throws IOException {
		MessageDigest messagedigest = null;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		if (messagedigest == null) {
			return "";
		}
		FileInputStream fis = new FileInputStream(file);
		FileChannel fChannel = fis.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(bufSize);
		for (int count = fChannel.read(buffer); count != -1; count = fChannel.read(buffer)) {
			buffer.flip();
			messagedigest.update(buffer);
			if (!buffer.hasRemaining()) {
				buffer.clear();
			}
		}
		fis.close();
		try {
			byte[] digest = messagedigest.digest();
			return bufferToHex(digest);
		} catch (Exception e) {
			throw new IOException(e.toString());
		}
	}

	/**
	 * 计算二进制数据的 MD5 值。
	 * 
	 * @param bytes
	 *            需要校验的二进制数据。
	 * @return 校验得到的 MD5 值。
	 */
	public static String getMD5String(byte[] bytes) {
		MessageDigest messagedigest = null;
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {

		}
		if (messagedigest == null) {
			return "";
		}
		messagedigest.update(bytes);
		try {
			byte[] digest = messagedigest.digest();
			return bufferToHex(digest);
		} catch (Exception e) {
			return "";
		}
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>>
												// 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
		char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

}
