package es.us.isa.idlreasoner.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class WebContentAuxiliar {
	
	public WebContentAuxiliar() {
		
	}
	
	//For check if we are in a webcontent
	public boolean isFromAWebContent() {
		boolean res = false;
		String relativePath = System.getProperty("user.dir");
		if(relativePath.equals("C:\\WINDOWS\\system32") || relativePath.equals("C:\\WINDOWS\\System32")) {
			res = true;
		}
		return res;
			
	}
	
	
	public String getPath(String filePath) {
		String responsePath = filePath;
		
		String path = this.getClass().getClassLoader().getResource("").getPath();
		String fullPath;
		try {
			fullPath = URLDecoder.decode(path, "UTF-8");
			String pathArr[] = fullPath.split("/WEB-INF/classes/");
			fullPath = pathArr[0];
			responsePath = new File(fullPath).getPath() + File.separatorChar + filePath;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// to read a file from webcontent
		return responsePath;
	}
	
	public String getPathAndCheck(String filePath) {
		String res = filePath;
		if(isFromAWebContent()) {
			res = getPath(filePath);
		}
		return res;
	}
	


}
