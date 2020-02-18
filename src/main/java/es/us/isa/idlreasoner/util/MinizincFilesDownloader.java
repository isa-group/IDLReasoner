package es.us.isa.idlreasoner.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class MinizincFilesDownloader {
	
	/*
	 * The files are available at the following link:
	 * https://www.dropbox.com/sh/zl9l928hpt9uxqu/AAD7MyGSXHJluzkx-ms5PuZNa?dl=0
	 */
	private static String ACCESS_TOKEN = "<API-KEY>";
	private static String minizinc;
	private static String downloadDirectory;
	private static String minizincFileLocal;

	private static String minizincWURL = "http://download1592.mediafire.com/6h1pwkfkkfcg/zz7f9yu9ewrsh80/minizinc.zip";
	private static String minizincLURL = "https://download1582.mediafire.com/5228ipxk783g/rluflqq2dubvj7d/minizinc-linux.zip";
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static WebContentAuxiliar webContent = new WebContentAuxiliar();
	private String url;
	
	public static void downloadMinizincFiles() {
		String destination = System.getProperty("user.dir");

		//We check if we are from a webcontent
		if(webContent.isFromAWebContent()) {
			downloadDirectory = webContent.getPath("/");
			destination = webContent.getPath("");
			if (OS.contains("windows")) {
				minizinc = webContent.getPath("/minizinc");
				minizincFileLocal = webContent.getPath("/minizinc.zip");
			} else if (OS.equals("linux")){
				minizinc = webContent.getPath("/minizinc-linux");
				minizincFileLocal = webContent.getPath("/minizinc-linux.zip");
			}
		}else {
			downloadDirectory = "./";
			if (OS.contains("windows")) {
				minizinc = "./minizinc";
				minizincFileLocal = "./minizinc.zip";
			} else if (OS.equals("linux")) {
				minizinc = "./minizinc-linux";
				minizincFileLocal = "./minizinc-linux.zip";
			}
		}
		
		//We check if the Minizinc files are in the project
        File dir = new File(minizinc);
        boolean exists = dir.exists();
        if(!exists) {        
        	
			//Download Zip file
            	try {
            		if(OS.contains("windows")) {
            			HttpDownloadUtility.downloadFile(minizincWURL, downloadDirectory);
            		}else if (OS.equals("linux")) {
                    	HttpDownloadUtility.downloadFile(minizincLURL,downloadDirectory);
                	}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        
			//UnZip the Zip file
			try {
			    ZipFile zipFile = new ZipFile(minizincFileLocal);
			    zipFile.extractAll(destination);
			} catch (ZipException e1) {
			    e1.printStackTrace();
			}
			
			
			//Delete the Zip file
			File f= new File(minizincFileLocal);   
			f.delete();

		}
	}
	

	public static String repeat(String s, int n) {
	    if(s == null) {
	        return null;
	    }
	    final StringBuilder sb = new StringBuilder(s.length() * n);
	    for(int i = 0; i < n; i++) {
	        sb.append(s);
	    }
	    return sb.toString();
	}


}
