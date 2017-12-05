/**
 * 
 */
package com.kingbase.db.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Duke
 *
 */
public class FileUtil {

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
		} else {
			File[] fx = file.listFiles();
			for (int i = 0; i < fx.length; i++) {
				delete(fx[i]);
			}
		}
		file.delete();
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 */

	/**
	 * 文件复制
	 * 
	 * @param sourcePath
	 * @param targetPath
	 */

	/**
	 * 新建文件
	 * 
	 * @param filePath
	 */

	/**
	 * 新建目录/文件夹
	 * 
	 * @param filePath
	 */

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
}
