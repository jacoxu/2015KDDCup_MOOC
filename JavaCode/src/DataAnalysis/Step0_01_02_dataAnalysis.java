package DataAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Tools.CourseIDInfo;
import Tools.EnrollmentIDInfo;
import Tools.TimeParser;

public class Step0_01_02_dataAnalysis {
	/**
	 * @param args
	 * @author jacoxu-2015/05/17
	 */
	
	private static void extractFeature(String rawTrainEnrollInfo, String rawTestEnrollInfo, 
			String rawTrainLog, String rawTestLog, String rawTrainObject, String rawTestObject, 
			String tarFeatureTrainFile) {
		//1)	总共多少人，多少门课，Log的活动时间范围，Object的发布时间范围
		long totalUserNum = 0;
		HashSet<String> totalUserSet = new HashSet<String>();
		HashSet<String> trainUserSet = new HashSet<String>();
		
		long totalCourseNum = 0;
		HashSet<String> totalCourseSet = new HashSet<String>();
		HashSet<String> trainCourseSet = new HashSet<String>();
		// <注册ID, 课程ID>
		HashMap<Long, String> enrollCourseMap = new HashMap<Long, String>();
		
		String startTrainLogTimeStr = "1970-01-01T00:00:01";
		String endTrainLogTimeStr = "1970-01-01T00:00:01";
		String startTestLogTimeStr = "1970-01-01T00:00:01";
		String endTestLogTimeStr = "1970-01-01T00:00:01";
		
		String startObjectTimeStr = "1970-01-01T00:00:01";
		String endObjectTimeStr = "1970-01-01T00:00:01";
		
		//2)	每门课的退学率、Log活动时间范围、Object的发布时间范围
		//HashMap<课程ID，课程ID特征集合>		
		HashMap<Long, CourseIDInfo> courseIDMap = new HashMap<Long, CourseIDInfo>();

		try {
			String encoding = "UTF-8";
			File rawTrainEnrollInfofile = new File(rawTrainEnrollInfo);
			File rawTestEnrollInfofile = new File(rawTestEnrollInfo);
			File rawTrainLogfile = new File(rawTrainLog);
			File rawTestLogfile = new File(rawTestLog);
			File rawTrainObjectfile = new File(rawTrainObject);
			File rawTestObjectfile = new File(rawTestObject);
			
			
			if (rawTrainLogfile.isFile() && rawTrainLogfile.exists()) {
				BufferedReader rawTrainEnrollInfoReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(rawTrainEnrollInfofile), encoding));
				BufferedReader rawTestEnrollInfoReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(rawTestEnrollInfofile), encoding));
				BufferedReader rawTrainLogReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(rawTrainLogfile), encoding));
				BufferedReader rawTestLogReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(rawTestLogfile), encoding));
				BufferedReader rawTrainObjectReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(rawTrainObjectfile), encoding));
				BufferedReader rawTestObjectReader = new BufferedReader(new InputStreamReader(
						new FileInputStream(rawTestObjectfile), encoding));

				
				long tmpEnrollment_id = -1;
				String tmpUsernameStr = null;
				String tmpCourseStr = null;
				//开始读取注册的训练数据
				System.out.println("Start to process train enroll info...");
				int lineNum=0;
				String tmpLineStr = null;
				while ((tmpLineStr = rawTrainEnrollInfoReader.readLine()) != null) {
					lineNum++;
					if (lineNum<=1) continue;
					String[] enrollInfo = tmpLineStr.split(",");
					if (enrollInfo.length!=3) {
						System.err.print("Error: enrollInfo.length is "+ enrollInfo.length 
								+ " in lineNum" + lineNum);
						System.exit(0);
					}
					tmpEnrollment_id = Long.valueOf(enrollInfo[0].trim());
					tmpUsernameStr = enrollInfo[1].trim();
					totalUserSet.add(tmpUsernameStr);
					trainUserSet.add(tmpUsernameStr);
					
					tmpCourseStr = enrollInfo[2].trim();
					totalCourseSet.add(tmpCourseStr);
					trainCourseSet.add(tmpCourseStr);
					enrollCourseMap.put(tmpEnrollment_id, tmpCourseStr);
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed feature numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed feature numbers:" + lineNum);
				rawTrainEnrollInfoReader.close();
				
				//开始读取注册的测试数据
				System.out.println("Start to process test enroll info...");
				lineNum=0;
				tmpLineStr = null;
				while ((tmpLineStr = rawTestEnrollInfoReader.readLine()) != null) {
					lineNum++;
					if (lineNum<=1) continue;
					String[] enrollInfo = tmpLineStr.split(",");
					if (enrollInfo.length!=3) {
						System.err.print("Error: enrollInfo.length is "+ enrollInfo.length 
								+ " in lineNum" + lineNum);
						System.exit(0);
					}
					tmpEnrollment_id = Long.valueOf(enrollInfo[0].trim());
					tmpUsernameStr = enrollInfo[1].trim();
					totalUserSet.add(tmpUsernameStr);
					
					tmpCourseStr = enrollInfo[2].trim();
					totalCourseSet.add(tmpCourseStr);
					enrollCourseMap.put(tmpEnrollment_id, tmpCourseStr);
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed feature numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed feature numbers:" + lineNum);
				rawTestEnrollInfoReader.close();
				
				//开始读取训练数据的Log数据
				System.out.println("Start to process train log info...");
				lineNum=0;
				tmpLineStr = null;
				while ((tmpLineStr = rawTrainLogReader.readLine()) != null) {
					lineNum++;
					if (lineNum<=1) continue;
					String[] enrollInfo = tmpLineStr.split(",");
					if (enrollInfo.length!=5) {
						System.err.print("Error: enrollInfo.length is "+ enrollInfo.length 
								+ " in lineNum" + lineNum);
						System.exit(0);
					}
					tmpEnrollment_id = Long.valueOf(enrollInfo[0].trim());
					tmpUsernameStr = enrollInfo[1].trim();
					totalUserSet.add(tmpUsernameStr);
					
					tmpCourseStr = enrollInfo[2].trim();
					totalCourseSet.add(tmpCourseStr);
					
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed feature numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed feature numbers:" + lineNum);
				rawTestEnrollInfoReader.close();
				
			} else {
				System.out.println("can't find the file");
			}
			
			System.out.println("Total user size is:"+totalUserSet.size());
			System.out.println("Total train user size is:"+trainUserSet.size());
			
			System.out.println("Total user size is:"+totalCourseSet.size());
			System.out.println("Total train user size is:"+trainCourseSet.size());
			
		} catch (Exception e) {
			System.out.println("something error when reading the content of the file");
			e.printStackTrace();
		}
		return;
		
	}

	public static void Result2Txt(String file, String txt) {
		  try {
			  BufferedWriter os = new BufferedWriter(new OutputStreamWriter(   
		               new FileOutputStream(new File(file),true), "UTF-8")); 
			  os.write(txt + "\n");
			  os.close();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	 }
	
	public static void main(String[] args) {
		//***********测试区域************
		System.out.println("test");
		//***********测试区域************
		
		String dataPathStr="./../Data/";
		//过滤注册信息
	    String rawTrainEnrollInfo=dataPathStr+"train/enrollment_train.csv";
	    String rawTestEnrollInfo=dataPathStr+"test/enrollment_test.csv";		
		//过滤Log数据
	    String rawTrainLog=dataPathStr+"train/log_train.csv";
	    String rawTestLog=dataPathStr+"test/log_test.csv";
	    //过滤Object发布信息
	    String rawTrainObject=dataPathStr+"train/log_train.csv";
	    String rawTestObject=dataPathStr+"test/log_test.csv";	    
	    
	    //生成特征文件 一,1). - 10).
		String tarFeatureTrainFile=dataPathStr+"features/Step0_01_02_dataAnalysis.txt";
		
		long readstart=System.currentTimeMillis();
		extractFeature(rawTrainEnrollInfo, rawTestEnrollInfo, rawTrainLog, rawTestLog,
				rawTrainObject, rawTestObject, tarFeatureTrainFile);
        long readend=System.currentTimeMillis();
        System.out.println((readend-readstart)/1000.0+"s had been consumed to process the raw training data");
	}
}
