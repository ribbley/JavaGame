package General.Utility;
import java.io.*;
public class TextAquirer 
{	
	public static String path="config.conf";
	public static String defaultText=	
						"forceOpenGL=true\n"
						+"ServerIP=127.0.0.1\n"
						+"Username=Player\n"
						;

 public static String[][] readConfig(String path, String defText){
	 int lines=0;
	 String[][] array=null;
  try{
	  File file=new File(path);
  if(!file.exists()){
	  System.out.println("No config file found.");
	  //create new file.
	  FileOutputStream fos = new FileOutputStream(file);
		file.createNewFile();
		// get the content in bytes
		byte[] contentInBytes = defaultText.getBytes();

		fos.write(contentInBytes);
		fos.flush();
		fos.close();
		System.out.println("Default config file created.");
  }

  FileInputStream in = new FileInputStream(file);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine;
//count lines:
  while ((strLine = br.readLine()) != null)   {
  lines++;
  }
  array=new String[lines][2];
  String[] tmp=new String[2];
  int cnt=0;
  in = new FileInputStream(file);
  br=new BufferedReader(new InputStreamReader(in));
  while ((strLine = br.readLine()) != null)   {
	 if(strLine.contains(""+'=')){
		 tmp=TextAquirer.split(strLine);
		 array[cnt][0]=tmp[0];
		 array[cnt++][1]=tmp[1];
	 }
  }
  System.out.println("Config file read.");
  //Close the input stream
  in.close();
    }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
  for(int k=0;k<array.length;k++){
	  System.out.println(array[k][0]+" "+array[k][1]);
  }
  return array;
 }
 
 public static void editConfig(String param, String edit) {
     try {
         File inFile = new File(TextAquirer.path);

         if (!inFile.isFile()) {
             System.out.println("Parameter is not an existing file");
             return;
         }

         //Construct the new file that will later be renamed to the original filename.
         File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

         BufferedReader br = new BufferedReader(new FileReader(TextAquirer.path));
         PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

         String line = null;

         //Read from the original file and write to the new
         //unless content matches data to be removed.
         while ((line = br.readLine()) != null) {
             if (!line.trim().contains(param)) {
                 pw.println(line);
                 pw.flush();
             }
         }
         pw.println(param+"="+edit);
         pw.flush();
         pw.close();
         br.close();

         //Delete the original file
         if (!inFile.delete()) {
             System.out.println("Could not delete file");
             return;
         }

         //Rename the new file to the filename the original file had.
         if (!tempFile.renameTo(inFile)) System.out.println("Could not rename file");

     }
     catch (FileNotFoundException ex) {
         ex.printStackTrace();
     }
     catch (IOException ex) {
         ex.printStackTrace();
     }
 }

 
 public static String[] split(String cmd){
		int j=0,cnt=0;
		char splitchar='=';
		// 		Count the splitcharacters
		for(int i=0;i<cmd.length();i++){
			if(cmd.charAt(i)==splitchar)
				cnt++;
		}
		String[] tmp = new String[cnt+1];
		cnt=0;
		for(int i=0;i<cmd.length();i++){
			if(cmd.charAt(i)==splitchar){
				//splitpoint!
				tmp[cnt++]=cmd.substring(j, i);
				j=i+1;
			}
		}
		tmp[cnt]=cmd.substring(j);
		return tmp;
	}
}
