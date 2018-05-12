package AutoTest.wordcount;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class GitRepoCloner {
	
	Map<String, Integer> downloadTryTimes = new HashMap<String, Integer>();
	
	public static void createFolder(String filePath){
		File f = new File(filePath);
		if (!f.exists()){
			f.mkdirs();
		}
	}
	
	private void deleteFile(File file) {  
	    if (file.exists()) {//判断文件是否存在  
	    	if (file.isFile()) {//判断是否是文件  
	    		file.delete();//删除文件   
	    	} else if (file.isDirectory()) {//否则如果它是一个目录  
	    		File[] files = file.listFiles();//声明目录下所有的文件 files[];  
	    		for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件  
	    			this.deleteFile(files[i]);//把每个文件用这个方法进行迭代  
	    		}  
	    		file.delete();//删除文件夹  
	    	}  
	    } else {  
	    	System.out.println("没有所删除的文件!");  
	    }  
	}  
	
	public int cloneRepository(String url, String localPath, String studentId) {
	  try{
		  System.out.println("开始下载:"+url);

		  CloneCommand cloneCommand = Git.cloneRepository().setURI(url);
		  UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider("ZhaoYi1031", "zy12345678");                
		  cloneCommand.setCredentialsProvider(user);
		  
		  cloneCommand.setDirectory(new File(localPath))
		  					.setTimeout(60)
		  					.call();

		  System.out.println("下载完成:"+url);
		  deleteFile(new File(localPath+"/.git")); //删除项目的.git文件夹（里面包括了一堆信息，节约空间)
		  return 1;
	  	}catch (InvalidRemoteException e){//这GitHub项目不存在
//	  		e.printStackTrace();
	  		System.out.println("GitHub项目不存在:"+url);
	  		File f = new File(localPath);;
	  		deleteFile(f);
	  		return 0;
	  	}catch (TransportException e){//网络问题
	  		System.out.println("网络崩了:"+url);
	  		e.printStackTrace();
	  		File f = new File(localPath);;
	  		deleteFile(f);
	  		int newvalue = 1;
	  		if (downloadTryTimes.containsKey(studentId)){
		    	 newvalue = downloadTryTimes.get(studentId);
		    	 newvalue++;
		    }
	  		if (newvalue > 5)//跑6次仍然是网络错误，代表是那种503（DMCA）问题例如:https://github.com/Battlecruiser/L2J_Server
	  			return 5;
	  		downloadTryTimes.put(studentId, newvalue);
	  		return 3;
	  	}catch(JGitInternalException e){//该存储的目录已经存在
//	  		e.printStackTrace();
	  		System.out.println("已经保存过了:"+url);
	  		return 2;
	  	}catch (Exception e){
	  		e.printStackTrace();
	  		return 4;
	  	}
	 }
}
