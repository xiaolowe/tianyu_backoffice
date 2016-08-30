package com.kdgz.uwifi.backoffice.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class FileUtil {

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		 BufferedInputStream inBuff = null;
		 BufferedOutputStream outBuff = null;
         try {
             // 新建文件输入流并对它进行缓冲
        	 inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

             // 新建文件输出流并对它进行缓冲
        	 outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

             // 缓冲数组
        	 byte[] b = new byte[1024 * 5];
             int len;
             while ((len = inBuff.read(b)) != -1) {
                 outBuff.write(b, 0, len);
             }
             // 刷新此缓冲的输出流
             outBuff.flush();
         } finally {
             // 关闭流
        	 if (inBuff != null)
        		 inBuff.close();
        	 if (outBuff != null)
        		 outBuff.close();
         }
     }
	 
	 public static String FormetFileSize(long fileS) {//转换文件大小
		 DecimalFormat df = new DecimalFormat("#.00");
		 String fileSizeString = "";
		 if (fileS < 1024) {
			 fileSizeString = df.format((double) fileS) + "B";
		 } else if (fileS < 1048576) {
			 fileSizeString = df.format((double) fileS / 1024) + "K";
		 } else if (fileS < 1073741824) {
			 fileSizeString = df.format((double) fileS / 1048576) + "M";
		 } else {
			 fileSizeString = df.format((double) fileS / 1073741824) +"G";
		 }
		 return fileSizeString;
	}
	 
	public static void main(String args[]){
		 
		File sourceFile = new File("F:/war/market_base.apk");
		File targetFile = new File("F:/war/temp/com.guozhen.tianyumarket_.apk");
		try {
			copyFile(sourceFile, targetFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

}
