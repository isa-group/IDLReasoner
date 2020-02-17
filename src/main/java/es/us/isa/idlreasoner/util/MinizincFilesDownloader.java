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
	private static String ACCESS_TOKEN = "XtO39Mn-eG"+ repeat("A", 10)+ "Fuacz2BhvhjQlekfehsFjz5I3C2_kTqvg7fACsDFPmwT";
	private static String minizinc;
	private static String minizincFilesDropbox;
	private static String minizincFileLocal;
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static WebContentAuxiliar webContent = new WebContentAuxiliar();
	
	public static void downloadMinizincFiles() {
		String destination = System.getProperty("user.dir"); 
		System.out.println(ACCESS_TOKEN);

		//We check if we are from a webcontent
		if(webContent.isFromAWebContent()) {
			destination = webContent.getPath("");
			if (OS.contains("windows")) {
				minizincFilesDropbox =  "/minizinc.zip";
				minizinc = webContent.getPath("/minizinc");
				minizincFileLocal = webContent.getPath("/minizinc.zip");
			} else if (OS.equals("linux")){
				minizincFilesDropbox =  "/minizinc-linux.zip";
				minizinc = webContent.getPath("/minizinc-linux");
				minizincFileLocal = webContent.getPath("/minizinc-linux.zip");
			}
		}else {
			if (OS.contains("windows")) {
				minizinc = "./minizinc";
				minizincFileLocal = "./minizinc.zip";
				minizincFilesDropbox =  "/minizinc.zip";
			} else if (OS.equals("linux")) {
				minizinc = "./minizinc-linux";
				minizincFileLocal = "./minizinc-linux.zip";
				minizincFilesDropbox =  "/minizinc-linux.zip";
			}
		}
		
		//We check if the Minizinc files are in the project
		System.out.println(minizinc);
        File dir = new File(minizinc);
        boolean exists = dir.exists();
        
        if(!exists) {
			// Create Dropbox client
			DbxRequestConfig config = DbxRequestConfig.newBuilder(minizinc).build();
			DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
			
			//Download Zip file
			try {
				OutputStream outputStream = new FileOutputStream(minizincFileLocal);
				client.files().download(minizincFilesDropbox)
						.download(outputStream);
				outputStream.close();
			} catch (DbxException | IOException e) {
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
