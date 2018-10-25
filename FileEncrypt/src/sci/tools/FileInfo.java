
package sci.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/** FileInfo.java:解析文件、文件夹路径下的所有文件 ----- 2018-10-24 下午4:12:08 scimence */
public class FileInfo
{
	/** 获取paths路径下所有文件信息 */
	public static String[] getSubFiles(String... Paths)
	{
		List<String> list = new ArrayList<String>();	// paths路径下所有文件信息
		
		if (Paths != null && Paths.length > 0)
		{
			for (String path : Paths)
			{
				File file = new File(path);
				List<String> subFiles = getSubFiles(file);	// 获取路径path下所有文件列表信息
				
				list = ListAdd(list, subFiles);
			}
		}
		
		String[] A = List2Array(list);					// 转化为数组形式
		
		return A;
	}
	
	/** 合并list1和list2到新的list */
	public static List<String> ListAdd(List<String> list1, List<String> list2)
	{
		List<String> list = new ArrayList<String>();
		
		for (String path : list1)
			if (!list.contains(path)) list.add(path);
		for (String path : list2)
			if (!list.contains(path)) list.add(path);
		
		return list;
	}
	
	/** 获取file目录下所有文件列表 */
	public static List<String> getSubFiles(File file)
	{
		List<String> list = new ArrayList<String>();
		
		if (file.exists())
		{
			if (file.isFile()) 	// 若为文件则添加到list
			{
				String path = file.getAbsolutePath();
				if (!list.contains(path)) list.add(path);
			}
			else
			// 若为目录，则添加目录下的所有文件到list
			{
				for (File iteam : file.listFiles())
				{
					List<String> L = getSubFiles(iteam);	// 获取子目录下所有文件列表
					
					for (String path : L)
					{
						if (!list.contains(path)) list.add(path);
					}
				}
			}
		}
		
		return list;
	}
	
	/** 转化list为数组 */
	public static String[] List2Array(List<String> list)
	{
		int size = (list == null ? 0 : list.size());
		String[] A = new String[size];
		
		int i = 0;
		for (String S : list)
		{
			A[i++] = S;
		}
		
		return A;
	}
}
