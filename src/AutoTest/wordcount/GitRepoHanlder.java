package AutoTest.wordcount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class GitRepoHanlder {
	public Map<String, String> repoMapTable = new HashMap<String, String>();
	
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
				System.out.println("downloads/"+studentId+"/"+suffix+"/WordCount");
				out.write("downloads/"+studentId+"/"+suffix+"/WordCount\r\n"); // \r\n即为换行

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
