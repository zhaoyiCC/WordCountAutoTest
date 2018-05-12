package AutoTest.wordcount;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WordCountTester {
	private int timeLimit;
	private static String baseDir;
	private static String studentId;
	public static ArrayList<Integer> scores;  //每条命令下该同学的得分情况
	public static ArrayList<Double> times; //每条命令下该同学的用时情况。如果为负，则为用错误类型
	private static String[] argumentScoreMaps = new String[]{ //测试参数
			"-n 100 tests/rural.txt",
			"-m 5 tests/rural.txt",
			"-m 3 -n 30 tests/science.txt",
			"-n 20 -r tests/inaugural",
			"-m 2 -n 1000 -r tests/inaugural", //gutenberg
			"-m 2 -n 1000 -r tests/gutenberg", //gutenberg
		};
	private static String[] stdResults = new String[]{ //测试用例的参考答案
			"stds/std1.txt",
			"stds/std2.txt",
			"stds/std3.txt",
			"stds/std4.txt",
			"stds/std5.txt",
			"stds/std6.txt",
	};
	
	public WordCountTester(String studentId, String baseDir, int timeLimit){
		scores = new ArrayList<Integer>();
		times = new ArrayList<Double>();
		this.baseDir = baseDir; //例如.../bin
		this.studentId = studentId;
		this.timeLimit = timeLimit;
	}
	
	public static void executeTest(String argument, String stdTxt, int testId, int timeLimit){ // 返回执行时间 // String arguments,
        Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象  
        try {
        	long startTime=System.currentTimeMillis();   //获取开始时间

            System.out.println("Begin test: " + argument);
            //预先用标程代码计算出标准答案到result_standard.txt
//    		String cmd = baseDir + "wordcount.exe"+ arguments;
			Process p = run.exec(baseDir + "/wc.exe "+ argument); //cmd // 启动另一个进程来执行命令

			if (!p.waitFor(timeLimit, TimeUnit.SECONDS)) { //时限为timeLimit
				//timeout - kill the process.
				p.destroy(); // consider using destroyForcibly instead
				scores.add(0);
				times.add(-1.0);
				System.out.println("TLE") ;
				return; //"TLE"
			}
			File f = new File("result.txt"); //bin/
			if (!f.exists()) {
				scores.add(0);
				times.add(-5.0);
				System.out.println("No result.txt") ;
				return; //"No result file"
			}
			System.out.println("START evalutaion!") ;
			File dst = new File("logs/"+studentId+"/result"+String.valueOf(testId)+".txt");

			Files.copy(f.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);

			//可以省略吧
			//检查命令是否执行失败。
			if (p.waitFor() != 0) {
				if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束
					System.err.println("命令执行失败!");
				scores.add(0);
				times.add(-6.0);
				return; //"Fail"
			}
			long endTime = System.currentTimeMillis(); //获取结束时间
			System.out.println("程序运行时间： " + (1.0*endTime - 1.0*startTime)/1000 + "s");

			int scorePerTest = checkValid(stdTxt, "result.txt");
			System.out.println("测试" + studentId + " 命令" + argument + " 得分：" + scorePerTest);
			scores.add(scorePerTest);
			times.add((1.0 * endTime - 1.0 * startTime) / 1000);
			return ;//endTime-startTime; //TODO：返回得分与实践
		} catch (Exception e) {
			e.printStackTrace();
			scores.add(0);
			times.add(-3.0);
			return ; //other error
		}
	}
	
	public ArrayList<String> getScore(){ //String standardPath, String filePath
		ArrayList<String> ans = new ArrayList<>();
		int totScore = 0;
		ans.add(this.studentId);

		File fExe = new File(baseDir+"/wc.exe");//WordCount.exe"); //去掉了bin/
		System.out.println(studentId+" exe filePath: "+fExe.getAbsolutePath());

		if (!fExe.exists()){
			ans.add("Lack exe");
			System.out.println(studentId+ " Lacks exe file!");
			return ans; //"No exe file"
		}

		for (int i = 0; i < argumentScoreMaps.length; ++i){ //对于每个参数进行测试 //String argument: argumentScoreMaps
			executeTest(argumentScoreMaps[i], stdResults[i], i+1, this.timeLimit); //3
//			scores.add();
		}

		for (int i = 0; i < times.size(); ++i){
			totScore = totScore + scores.get(i);
//			System.out.println(times.get(i)+ "s---"+scores.get(i));
		}

		ans.add(String.valueOf(totScore));
		for (int i = 0; i < times.size(); ++i){
			ans.add(String.valueOf(scores.get(i)));
			ans.add(String.valueOf(times.get(i)));
		}
//		String[] ans = new String[]{
//				this.studentId,
//				String.valueOf(totScore),
//		};
		return ans;
	}
	
	//Overview: 将标程standard生成的result文件和用户生成的result文件进行对比，完全一致则
	public static int checkValid(String standardPath, String filePath) throws FileNotFoundException{
		int count = 0; 
		InputStreamReader isrStandard = new InputStreamReader(new FileInputStream(standardPath));
		BufferedReader buffStandard = new BufferedReader(isrStandard);

		InputStreamReader isrTest = new InputStreamReader(new FileInputStream(filePath));
		BufferedReader buffTest = new BufferedReader(isrTest);
		
		String strStandard, strTest;
		try {
			strStandard = buffStandard.readLine();
			strTest = buffTest.readLine();
			if (strTest != null && strStandard.equals(strTest)){ //第一行characters: n是相同的
				count += 1; //得1分
			}
			strStandard = buffStandard.readLine();
			strTest = buffTest.readLine();
			if (strTest != null && strStandard.equals(strTest)){ //第二行words: m是相同的
				count += 2; //得2分
			}
			strStandard = buffStandard.readLine();
			strTest = buffTest.readLine();
			if (strTest != null && strStandard.equals(strTest)){ //第三行lines: m是相同的
				count += 2; //得2分
			}
			boolean checkMain = true; //检查核心部分是否正确(求词频、求词组、求特定出现量的单词)
			while ((strStandard = buffStandard.readLine()) != null){
				strTest = buffTest.readLine();
				if (strTest == null || !strStandard.equals(strTest)){
					checkMain = false;
					break;
				}
			}
			if (checkMain){ //核心部分占15分
				count += 15; 
			}
			return count;
		} catch (IOException e) {
			e.printStackTrace();
			return -1; //其它错误
		}
	}
	
//	public static void main(String[] args){
//		WordCountTester wordCountTester = new WordCountTester("/Users/Mr.ZY/GitHub/WordCountAutoTest/", "14011100");
//		wordCountTester.executeTest("ls -lt", 4);
//	}
}
