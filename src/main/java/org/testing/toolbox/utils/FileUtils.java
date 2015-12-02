package org.testing.toolbox.utils;

import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileUtils class.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class FileUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * Checking if a file exists.
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean existFile(String path) {
		File f = new File(path);
		return f.exists();
	}

	/**
	 * Deleting file quietly.
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean deleteFileQuietly(String path) {
		if (existFile(path)) {
			File f = new File(path);
			boolean rtn = f.delete();

			if (existFile(path) || !rtn) {
				try {
					Path p = FileSystems.getDefault().getPath(path, new String[0]);
					Files.delete(p);
					return true;
				} catch (IOException e) {
					LOGGER.error("Reading error", e);
					return false;
				}
			}

			return rtn;
		}

		return false;
	}

	/**
	 * File to byte array.
	 * 
	 * @param path
	 * @return byte[]
	 */
	public static byte[] fileToByteQuietly(String path) {
		Path p = Paths.get(path);
		try {
			return Files.readAllBytes(p);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Getting absolute path from relative path.
	 * 
	 * @param relatifPath
	 * @return String
	 */
	public static String getAbsolutePath(String relatifPath) {
		File file = new File(relatifPath);
		return file.getAbsolutePath();
	}

	/**
	 * Getting file content as string.
	 * 
	 * @param pathFile
	 * @return String
	 */
	public static String file2stringQuietly(String pathFile) {
		if (!existFile(pathFile)) {
			LOGGER.warn("[file2string] The file " + pathFile + " doesn't exist !");
			return null;
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile), "UTF8"));
			StringBuilder builder = new StringBuilder();
			String line;

			// For every line in the file, append it to the string builder
			while (null != (line = reader.readLine())) {
				builder.append(line);
			}

			reader.close();
			return builder.toString();
		} catch (IOException e) {
			LOGGER.error("Reading error", e);
			return null;
		}
	}

	/**
	 * Write into a file.
	 * 
	 * @param pathFile
	 * @param content
	 * @throws IOException
	 */
	public static void string2fileQuietly(String pathFile, String content) {
		try {
			writeStringToFile(new File(pathFile), content);
		} catch (IOException e) {
			LOGGER.error("Reading error", e);
		}
	}

	/**
	 * Static class : private constructor.
	 */
	private FileUtils() {
	}
}
