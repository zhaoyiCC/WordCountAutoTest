package AutoTest.wordcount;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadAllFileFromParentCatalog {
	
	//读取一个文件夹下所有文件及子文件夹下的所有文件
	public void ReadAllFile(String filePath) {
		File f = null;
		f = new File(filePath);
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
		List<File> list = new ArrayList<File>();
		for (File file : files) {
			if(file.isDirectory()) {
				//如果当前路径是文件夹，则循环读取这个文件夹下的所有文件
				ReadAllFile(file.getAbsolutePath());
			} else {
				list.add(file);
			}
		}
		for(File file : files) {
			System.out.println(file.getAbsolutePath());
		}
	}
	
	//读取一个文件夹下的所有文件夹和文件
	public void ReadFile(String filePath) {
		File f = null;
		f = new File(filePath);
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
		List<File> list = new ArrayList<File>();
		for (File file : files) {
			list.add(file);
		}
		for(File file : files) {
			System.out.println(file.getAbsolutePath());
		}
	}
	
	public static void main(String[] args) {
		String filePath = "/Users/Mr.ZY/GitHub/wordcountautotest/downloads/6/PairProject2018/06";
//		File f = new File(filePath);
//		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
//		for (File ff: files){
//			System.out.println(ff.getAbsolutePath());
//		}
//		new ReadAllFileFromParentCatalog().ReadAllFile(filePath);
		new ReadAllFileFromParentCatalog().ReadFile(filePath);
	}
}