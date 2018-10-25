
package sci.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/** Encrypt.java: 对字符串或文件进行自定义加解密。 适用于较小文件的加解密，载入内存操作; 
 * 加密文件：EncryptFile(filePath, int Password)
 * 解密文件：DecryptFile(filePath, int Password)
 * 加密字符串：Encryption(String str, int change)
 * 解密字符串：Encryption(String str, int -change) 
 * 加密byte数组：Encryption(byte[] bytes, int change) 
 * 解密byte数组：Encryption(byte[] bytes, int -change) 
 * ----- 2018-10-22 下午5:50:35 scimence */
public class Encrypt
{
	public static int Password = 65537;
	
	public static void main(String[] args)
	{
		args = AnalyseArg(args);	// 解析参数信息
		
		String[] files = FileInfo.getSubFiles(args); // 获取args对应的所有目录下的文件列表
		for (String filePath : files)
		{
			boolean isEncrypt = isEncryptFile(filePath); // 判断是否为加密文件
			if (!isEncrypt)
			{
				System.out.println("加密文件，" + filePath);
				EncryptFile(filePath, Password);
			}
			else
			{
				System.out.println("解密文件，" + filePath);
				DecryptFile(filePath, Password);
			}
		}
	}
	
//	/** 解析参数信息 */
//	private static String[] AnalyseArg(String[] args)
//	{
//		for (String arg : args)
//		{
//			System.out.println("参数：" + arg.toString());
//		}
//		return args;
//	}
	
	/** 解析参数信息 */
	private static String[] AnalyseArg(String[] args)
	{
		if (args == null || args.length == 0) return args;
		
		ArrayList<String> argList = new ArrayList<String>();
		for (Object arg0 : args)
		{
			String arg = arg0.toString();
			if (arg.startsWith("PSW:"))
			{
				try
				{
					Password = Integer.parseInt(arg.substring(4));
				}
				catch (Exception ex)
				{
					System.out.print(ex.toString());
				}
			}
			else argList.add(arg);
		}
		
		return FileInfo.List2Array(argList);
	}
	
	/** 对字符串数据进行加解密， change加密、 -chage解密 */
	public static String Encryption(String str, int change)
	{
		if ((str.equals("")) || (str == null)) return "";
		
		byte[] bytes;
		if (change < 0)
			bytes = toBytes(str);
		else bytes = str.getBytes();
		
		Encryption(bytes, change);
		
		if (change < 0)
			str = new String(bytes);
		else str = toHex(bytes);
		
		return str;
	}
	
	/** 对bytes数据进行加密、解密操作， change加密、 -chage解密 */
	public static void Encryption(byte[] bytes, int change)
	{
		short sign = 1;
		if (change < 0)
		{
			sign = -1;
			change *= -1;
		}
		
		int num = 0;
		for (int i = 0; i < bytes.length; i++)
		{
			if (num == 0) num = change;
			
			int tmp = bytes[i] + sign * (num % 3);
			
			if (tmp > 127)
				tmp -= 255;
			else if (tmp < -128) tmp += 255;
			
			bytes[i] = ((byte) tmp);
			num /= 3;
		}
	}
	
	private static String toHex(byte[] B)
	{
		String tmp = "";
		byte[] arrayOfByte = B;
		int j = B.length;
		for (int i = 0; i < j; i++)
		{
			byte b = arrayOfByte[i];
			tmp = tmp + toHex(b);
		}
		return tmp;
	}
	
	private static byte[] toBytes(String Hex)
	{
		byte[] B = new byte[Hex.length() / 2];
		for (int i = 0; i + 1 < Hex.length(); i += 2)
		{
			String hexStr = Hex.substring(i, i + 2);
			B[(i / 2)] = toByte(hexStr);
		}
		
		return B;
	}
	
	private static String toHex(byte B)
	{
		int N = B + 128;
		return "" + (char) (65 + N / 26) + (char) (65 + N % 26);
	}
	
	private static byte toByte(String Hex)
	{
		int N = (Hex.charAt(0) - 'A') * 26 + (Hex.charAt(1) - 'A');
		return (byte) (N - 128);
	}
	
	/** 对文件进行加密 */
	public static void EncryptFile(String filePath, int Password)
	{
		boolean isEncrypt = isEncryptFile(filePath);
		if (!isEncrypt)
		{
			byte[] bytes = FileToBytes(filePath);	// 读取文件数据
			Encryption(bytes, Password);				// 加密
			String data = "DATA$" + toHex(bytes);	// 添加前缀 + 转化为字符串
			write(filePath, data);					// 输出文件
		}
	}
	
	/** 对文件进行解密 */
	public static void DecryptFile(String filePath, int Password)
	{
		boolean isEncrypt = isEncryptFile(filePath);
		if (isEncrypt)
		{
			byte[] bytes = FileToBytes(filePath);			// 读取文件数据
			String data = new String(bytes).substring(5);	// 转化为字符串，获取数据串
			bytes = toBytes(data);							// 还原为原有数据
			
			Encryption(bytes, -Password);					// 解密
			write(filePath, bytes);							// 输出数据
		}
	}
	
	// # region 读取文件
	
	/** 读取文件中的数据转化为字符串，若为加密数据则自动解密 */
	public static String FileToString(String filePath)
	{
		try
		{
			byte[] data = FileToBytes(filePath);
			String tmp = new String(data);
			if (tmp.startsWith("DATA$")) tmp = Encrypt.Encryption(tmp.substring(5), -65537); // 解密数据
				
			return tmp;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/** 判断数据是否为加密的数据 */
	private static boolean isEncrypt(String data)
	{
		return data.startsWith("DATA$");
	}
	
	/** 判断文件是否为加密的文件 */
	private static boolean isEncryptFile(String filePath)
	{
		String str = getFileStart(filePath, 5);
		return str.startsWith("DATA$");
	}
	
	/** 获取文件起始的len个字节对应的字符串 */
	private static String getFileStart(String filePath, int len)
	{
		try
		{
			FileInputStream input = new FileInputStream(filePath);
			byte[] cache = new byte[len];
			int count = input.read(cache);
			
			return new String(cache);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/** 获取文件中的数据 */
	private static byte[] FileToBytes(String filePath)
	{
		byte[] bytes = null;
		try
		{
			FileInputStream in = new FileInputStream(filePath);
			return InputStreamToBytes(in);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bytes;
	}
	
	/** 获取InputStream中的数据 */
	private static byte[] InputStreamToBytes(InputStream input)
	{
		byte[] bytes = null;
		try
		{
			ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
			byte[] cache = new byte[1024];
			int len = -1;
			while ((len = input.read(cache)) != -1)
			{
				byteArr.write(cache, 0, len);
			}
			bytes = byteArr.toByteArray();
			byteArr.flush();
			byteArr.close();
			input.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bytes;
	}
	
	// # endregion 读取文件
	
	// # region 写文件
	private static boolean write(String filepath, String data)
	{
		try
		{
			byte[] tmp = data.getBytes();
			return write(filepath, tmp, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean write(String filepath, byte[] data)
	{
		return write(filepath, data, false);
	}
	
	private static boolean write(String filepath, byte[] data, boolean append)
	{
		try
		{
			File file = new File(filepath);
			if (file.exists()) file.delete();	// 若文件存在，则删除
			if (!file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(filepath, append);
			stream.write(data);
			stream.flush();
			stream.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	// # endregion 写文件
}
