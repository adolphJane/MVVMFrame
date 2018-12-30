/*
 Copyright © 2015, 2016 Jenly Yu <a href="mailto:jenly1314@gmail.com">Jenly</a>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 	http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */
package com.adolph.project.baseutils;

import android.content.Context;
import androidx.annotation.RawRes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源文件工具类
 * @author Jenly
 */
public class ResourcesUtils {
	
	private static final String RES_ID = "id";
	private static final String RES_STRING = "string";
	private static final String RES_DRABLE = "drable";
	private static final String RES_LAYOUT = "layout";
	private static final String RES_STYLE = "style";
	private static final String RES_COLOR = "color";
	private static final String RES_DIMEN = "dimen";
	private static final String RES_ANIM = "anim";
	private static final String RES_MENU = "menu";

	private ResourcesUtils(){
		throw new AssertionError();
	}
	
	/**
	 * 获取资源文件的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getId(Context context,String resName){
		return getResId(context,resName,RES_ID);
	}
	
	/**
	 * 获取资源文件string的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getStringId(Context context,String resName){
		return getResId(context,resName,RES_STRING);
	}
	
	/**
	 * 获取资源文件drable的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getDrableId(Context context,String resName){
		return getResId(context,resName,RES_DRABLE);
	}
	
	/**
	 * 获取资源文件layout的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getLayoutId(Context context,String resName){
		return getResId(context,resName,RES_LAYOUT);
	}
	
	/**
	 * 获取资源文件style的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getStyleId(Context context,String resName){
		return getResId(context,resName,RES_STYLE);
	}
	
	/**
	 * 获取资源文件color的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getColorId(Context context,String resName){
		return getResId(context,resName,RES_COLOR);
	}
	
	/**
	 * 获取资源文件dimen的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getDimenId(Context context,String resName){
		return getResId(context,resName,RES_DIMEN);
	}
	
	/**
	 * 获取资源文件ainm的id
	 * @param context
	 * @param resName
	 * @return
	 */
	public static int getAnimId(Context context,String resName){
		return getResId(context,resName,RES_ANIM);
	}
	
	/**
	 * 获取资源文件menu的id
	 * @param context
	 * @param resName
	 */
	public static int getMenuId(Context context,String resName){
		return getResId(context,resName,RES_MENU);
	}
	
	/**
	 * 获取资源文件ID
	 * @param context
	 * @param resName
	 * @param defType
	 * @return
	 */
	public static int getResId(Context context,String resName,String defType){
		return context.getResources().getIdentifier(resName, defType, context.getPackageName());
	}

	private static final int BUFFER_SIZE = 8192;

	/**
	 * 从 assets 中拷贝文件
	 *
	 * @param assetsFilePath The path of file in assets.
	 * @param destFilePath   The path of destination file.
	 * @return {@code true}: success<br>{@code false}: fail
	 */
	public static boolean copyFileFromAssets(final String assetsFilePath, final String destFilePath) {
		boolean res = true;
		try {
			String[] assets = AppManageUtils.getApp().getAssets().list(assetsFilePath);
			if (assets.length > 0) {
				for (String asset : assets) {
					res &= copyFileFromAssets(assetsFilePath + "/" + asset, destFilePath + "/" + asset);
				}
			} else {
				res = writeFileFromIS(
						destFilePath,
						AppManageUtils.getApp().getAssets().open(assetsFilePath),
						false
				);
			}
		} catch (IOException e) {
			e.printStackTrace();
			res = false;
		}
		return res;
	}

	/**
	 * 从 assets 中读取字符串
	 *
	 * @param assetsFilePath The path of file in assets.
	 * @return the content of assets
	 */
	public static String readAssets2String(final String assetsFilePath) {
		return readAssets2String(assetsFilePath, null);
	}

	/**
	 * 从 assets 中读取字符串
	 *
	 * @param assetsFilePath The path of file in assets.
	 * @param charsetName    The name of charset.
	 * @return the content of assets
	 */
	public static String readAssets2String(final String assetsFilePath, final String charsetName) {
		InputStream is;
		try {
			is = AppManageUtils.getApp().getAssets().open(assetsFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		byte[] bytes = is2Bytes(is);
		if (bytes == null) return null;
		if (isSpace(charsetName)) {
			return new String(bytes);
		} else {
			try {
				return new String(bytes, charsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}
	}

	/**
	 * 从 assets 中按行读取字符串
	 *
	 * @param assetsPath The path of file in assets.
	 * @return the content of file in assets
	 */
	public static List<String> readAssets2List(final String assetsPath) {
		return readAssets2List(assetsPath, null);
	}

	/**
	 * 从 assets 中按行读取字符串
	 *
	 * @param assetsPath  The path of file in assets.
	 * @param charsetName The name of charset.
	 * @return the content of file in assets
	 */
	public static List<String> readAssets2List(final String assetsPath,
											   final String charsetName) {
		try {
			return is2List(AppManageUtils.getApp().getResources().getAssets().open(assetsPath), charsetName);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 从 raw 中拷贝文件
	 *
	 * @param resId        The resource id.
	 * @param destFilePath The path of destination file.
	 * @return {@code true}: success<br>{@code false}: fail
	 */
	public static boolean copyFileFromRaw(@RawRes final int resId, final String destFilePath) {
		return writeFileFromIS(
				destFilePath,
				AppManageUtils.getApp().getResources().openRawResource(resId),
				false
		);
	}

	/**
	 * 从 raw 中读取字符串
	 *
	 * @param resId The resource id.
	 * @return the content of resource in raw
	 */
	public static String readRaw2String(@RawRes final int resId) {
		return readRaw2String(resId, null);
	}

	/**
	 * 从 raw 中读取字符串
	 *
	 * @param resId       The resource id.
	 * @param charsetName The name of charset.
	 * @return the content of resource in raw
	 */
	public static String readRaw2String(@RawRes final int resId, final String charsetName) {
		InputStream is = AppManageUtils.getApp().getResources().openRawResource(resId);
		byte[] bytes = is2Bytes(is);
		if (bytes == null) return null;
		if (isSpace(charsetName)) {
			return new String(bytes);
		} else {
			try {
				return new String(bytes, charsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}
	}

	/**
	 * 从 raw 中按行读取字符串
	 *
	 * @param resId The resource id.
	 * @return the content of file in assets
	 */
	public static List<String> readRaw2List(@RawRes final int resId) {
		return readRaw2List(resId, null);
	}

	/**
	 * 从 raw 中按行读取字符串
	 *
	 * @param resId       The resource id.
	 * @param charsetName The name of charset.
	 * @return the content of file in assets
	 */
	public static List<String> readRaw2List(@RawRes final int resId,
											final String charsetName) {
		return is2List(AppManageUtils.getApp().getResources().openRawResource(resId), charsetName);
	}

	///////////////////////////////////////////////////////////////////////////
	// other utils methods
	///////////////////////////////////////////////////////////////////////////

	private static boolean writeFileFromIS(final String filePath,
										   final InputStream is,
										   final boolean append) {
		return writeFileFromIS(getFileByPath(filePath), is, append);
	}

	private static boolean writeFileFromIS(final File file,
										   final InputStream is,
										   final boolean append) {
		if (!createOrExistsFile(file) || is == null) return false;
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file, append));
			byte data[] = new byte[BUFFER_SIZE];
			int len;
			while ((len = is.read(data, 0, BUFFER_SIZE)) != -1) {
				os.write(data, 0, len);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static File getFileByPath(final String filePath) {
		return isSpace(filePath) ? null : new File(filePath);
	}

	private static boolean createOrExistsFile(final File file) {
		if (file == null) return false;
		if (file.exists()) return file.isFile();
		if (!createOrExistsDir(file.getParentFile())) return false;
		try {
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean createOrExistsDir(final File file) {
		return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
	}

	private static boolean isSpace(final String s) {
		if (s == null) return true;
		for (int i = 0, len = s.length(); i < len; ++i) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static byte[] is2Bytes(final InputStream is) {
		if (is == null) return null;
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			byte[] b = new byte[BUFFER_SIZE];
			int len;
			while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
				os.write(b, 0, len);
			}
			return os.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static List<String> is2List(final InputStream is,
										final String charsetName) {
		BufferedReader reader = null;
		try {
			List<String> list = new ArrayList<>();
			if (isSpace(charsetName)) {
				reader = new BufferedReader(new InputStreamReader(is));
			} else {
				reader = new BufferedReader(new InputStreamReader(is, charsetName));
			}
			String line;
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
