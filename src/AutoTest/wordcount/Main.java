package AutoTest.wordcount;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {
	private static int timeLimit = 3; //默认时限是三秒
	private static String modeChoice, gitFile = "", grabId = "", testId = "";
	
	public static void commandParameter(String[] args){
		Options options = new Options();

        Option grab = new Option("m", "mode", true, "必选！选择下载模式，选择clone或者test");
        grab.setRequired(true);
        options.addOption(grab);


        Option mode = new Option("g", "grab", true, "文件[blogFile] 对应学号与GitHub仓库的关系，默认是当前目录");
        mode.setRequired(false);
        options.addOption(mode);
        
        Option scoreLimit = new Option("l", "limit", true, "数字[limit second]测试的时限");
        scoreLimit.setRequired(false);
        options.addOption(scoreLimit);
        
        Option singleGrab = new Option("gi", "grabId", true, "学号[number id]单个克隆的学生学号");
        singleGrab.setRequired(false);
        options.addOption(singleGrab);
        
        Option singleTest = new Option("ti", "testId", true, "学号[number id]单个测试的学生学号");
        singleTest.setRequired(false);
        options.addOption(singleTest);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("WordCountAutoTest", options);
            System.exit(0);
            return;
        }

        modeChoice = cmd.getOptionValue("mode");
        if (!modeChoice.toLowerCase().equals("clone") && !modeChoice.toLowerCase().equals("test")){
            System.out.println("错误的模式！请使用clone或者test!");
            formatter.printHelp("WordCountAutoTest", options);
            System.exit(0);
        }
        if (modeChoice.toLowerCase().equals("clone") && cmd.hasOption("limit")) {
            System.out.println("clone模式无需制定时限！");
        }
        if (modeChoice.toLowerCase().equals("test") && cmd.hasOption("grab")) {
            System.out.println("test模式无需制定gitRepos.txt！");
        }
        if (cmd.hasOption("grab"))
            gitFile = cmd.getOptionValue("grab");
        if (cmd.hasOption("limit"))
            timeLimit = Integer.parseInt(cmd.getOptionValue("limit"));
        if (cmd.hasOption("grabId"))
            grabId = cmd.getOptionValue("grabId");
        if (cmd.hasOption("testId"))
            testId = cmd.getOptionValue("testId");

//        System.out.println(modeChoice);
//        System.out.println(gitFile);
//        System.out.println(timeLimit);
//        System.out.println(grabId);
//        System.out.println(testId);
	}
	public static void main(String[] args){
		commandParameter(args);


		GitRepoHanlder gitRepoHanlder = new GitRepoHanlder();
        if (modeChoice.toLowerCase().equals("clone")) {
            System.out.println("Start Clone");
            gitRepoHanlder.handle(gitFile); //对同学的GitHub仓库进行下载
            System.exit(0);
        }
        System.out.println("Start Test");
        GitRepoCloner.createFolder("logs");
        try{
            final String SAMPLE_CSV_FILE = "./scores.csv";
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
            try(
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                            .withHeader("ID", "Scores", "Score1", "Time1", "Score2", "Time2", "Score3",
                                    "Time3", "Score4", "Time4", "Score5", "Time5", "Score6", "Time6"));
            ) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("gen.txt"));
                String str, studentId;
                String[] vals;
            int cnt = 0;
            while ((str = bufferedReader.readLine()) != null) {
                vals = str.split("/");
                studentId = vals[1]; //获取学生的学号
                GitRepoCloner.createFolder("logs/"+studentId);
                System.out.println(studentId+" projectPath: "+str);
                WordCountTester wordCountTestr = new WordCountTester(studentId, str, timeLimit);//"./projects/"+studentId);
                ArrayList<String> res = wordCountTestr.getScore();
                csvPrinter.printRecord(res);
            }
                bufferedReader.close();
                csvPrinter.flush();
                csvPrinter.close();
            }
            System.out.println("End Test");
        }catch (Exception e){
            e.printStackTrace();
        }

//		for (String StudentId: gitRepoHanlder.repoMapTable.keySet()){
//			System.out.println(StudentId+" "+gitRepoHanlder.repoMapTable.get(StudentId));
//			//不需要，我自己生成出来！//是否缺少一个查找EXE文件的操作，即EXE文件路径是否需要递归查找呢
//			WordCountTester wordCountTestr = new WordCountTester(StudentId, "./projects/"+StudentId);
//		}
	}
}
