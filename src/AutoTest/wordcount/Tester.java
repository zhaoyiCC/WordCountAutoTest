package AutoTest.wordcount;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Tester {
    public static void main(String[] args){ // 返回执行时间
        Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象
        try {
            long startTime=System.currentTimeMillis();   //获取开始时间

            System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径

            //预先用标程代码计算出标准答案到result_standard.txt
//    		String cmd = baseDir + "wordcount.exe"+ arguments;

            Properties property = System.getProperties();
            String nowPosition = System.getProperty("user.dir");
//            property.setProperty("user.dir", "C:\\Program Files (x86)\\Microsoft Visual Studio" +
//                    "\\2017\\Community\\VC\\Auxiliary\\build");
            property.setProperty("user.dir", "C:\\");
//            property.setProperty("user.dir", nowPosition+"\\downloads\\15061183\\WordCount");
            System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
            String cmd = "sh hh.sh"; //vcvarsall x86";
//            String cmd = "sh " + System.getProperty("user.dir") + "\\1.sh";
            System.out.println(cmd);
            Process p = run.exec(cmd);//WordCount.EXE -n 5 rural.txt"); //cmd // 启动另一个进程来执行命令

            BufferedInputStream bufferedInputStream = new BufferedInputStream(p.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
            String str;
            while ((str = bufferedReader.readLine())!=null){
                System.out.println(str);
            }

            if(!p.waitFor(10, TimeUnit.SECONDS)) { //时限五秒
                //timeout - kill the process.
                p.destroy(); // consider using destroyForcibly instead
                return; //"TLE"
            }

            //可以省略吧
            //检查命令是否执行失败。
            if (p.waitFor() != 0) {
                if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束
                    System.err.println("命令执行失败!");
                System.err.println("命令执行FAIL!");
                return ; //"Fail"
            }
            //-----



            long endTime=System.currentTimeMillis(); //获取结束时间

            System.out.println("程序运行时间： "+(endTime-startTime)+"ms");

            return ;//endTime-startTime; //TODO：返回得分与实践
        } catch (Exception e) {
            e.printStackTrace();
            return ; //other error
        }
    }
}
