package AutoTest.wordcount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitRepoHanlder {
	public Map<String, String> repoMapTable = new HashMap<String, String>();
	public boolean hasFound = false;
	public String baseDir = "";
	public void getExeFolder(String filePath){
		File f = null;
		f = new File(filePath);
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
		for (File file : files) {
			if (hasFound){
				return ;
			}
			if(file.isDirectory()) {
				//如果当前路径是文件夹，则循环读取这个文件夹下的所有文件
				getExeFolder(file.getAbsolutePath());
			} else {
				String fileName = file.getName();
				String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(); //获取文件的后缀名
				if (suffix != null && suffix.equals("cpp") || suffix.equals("c")){ //如果该文件是cpp
					String absoultePath = filePath.replaceAll("\\\\","/"); //这个一般是用不着的，防止在Windows里遇到的路径格式
					int pos = absoultePath.lastIndexOf('/');
					String folderName = absoultePath.substring(pos+1).toLowerCase(); //找当前路径的最后的文件夹的名字，匹配是不是wordcount
					if (folderName.equals("wordcount")){
						hasFound = true;
						int startPos = filePath.lastIndexOf("downloads");
						baseDir = filePath.substring(startPos);
						return ;
					}
				}	
			}
		}
	}
	
	public void handle(String filename){
		if (filename == null || filename.equals(""))
			filename = "GithubRepos.txt";
		loadGithubMap(filename);
		
		GitRepoCloner gitRepoCloner = new GitRepoCloner();
		
		gitRepoCloner.createFolder("downloads");
		
		int status;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("gen.txt"));
			for (String studentId : repoMapTable.keySet()) {
				String suffix = repoMapTable.get(studentId).substring(repoMapTable.get(studentId).lastIndexOf("/") + 1);
				status = gitRepoCloner.cloneRepository(repoMapTable.get(studentId),
						"./downloads/" + studentId + "/" + suffix, studentId); //下载项目，返回相应的状态到status
				System.out.print("downloads/"+studentId+"/"+suffix+"/WordCount:::::");
				
				//exe文件所在的文件夹有错，即不会是一致的组织结构，需要去寻找在哪个文件夹
				hasFound = false;
				baseDir = "";
				getExeFolder("./downloads/" + studentId);
				if (!hasFound){
					System.out.println("!!!Exception:"+"No cpp file under wordcount folder Found!!!");
					out.write(studentId + " !!!Exception:"+"No cpp file under wordcount folder Found!!!");
				}else {
					System.out.println(baseDir);
					out.write(baseDir + "\r\n"); //"downloads/"+studentId+"/"+suffix+"/WordCount\r\n" // \r\n即为换行
				}

				if (status == 0) { //项目不存在
					System.out.println("项目不存在!");
				} else if (status == 3) {  //无法下载
					System.out.println("网络异常!");
				} else if (status == 2) { //如果是2，代表已经保存过了
					System.out.println("已经下载");
				} else {
					System.out.println(studentId + "下载完成！");
				}
			}
			out.flush(); // 把缓存区内容压入文件
			out.close(); // 最后记得关闭文件
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadGithubMap(String filePath){
		try{
			BufferedReader bufferedReader = new BufferedReader(new FileReader("GithubRepos.txt"));
			String str;
			String[] vals;
			int cnt = 0;
			while ((str = bufferedReader.readLine()) != null) {
				vals = str.split(" ");
				if (vals.length < 2){
					System.out.println("非法的github仓库对应格式！");
					System.exit(0);
				}
				cnt++;
				repoMapTable.put(vals[0], vals[1]);
			 }
			bufferedReader.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
