package ExtractFeature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import Tools.CourseIDInfo;
import Tools.EnrollmentIDInfo;
import Tools.UsernameIDInfo;

public class Step01_11_14_features {
	/**
	 * @param args
	 * @author jacoxu-2015/05/17
	 */
	
	private static void extractFeature(String rawTrainEnrollInfo, String rawTestEnrollInfo, String rawTrainDropout, 
			String tarFeatureTrainFile, String tarFeatureTestFile, String tarFeatureTrain4ValidFile) {
		//HashMap<注册ID，注册ID特征集合>
		HashMap<Long, EnrollmentIDInfo> enrollmentIDMap = new HashMap<Long, EnrollmentIDInfo>();
		//HashMap<用户ID，用户ID特征集合>
		HashMap<String, UsernameIDInfo> usernameIDMap = new HashMap<String, UsernameIDInfo>();
		//HashMap<课程ID，课程ID特征集合>
		HashMap<String, CourseIDInfo> courseIDMap = new HashMap<String, CourseIDInfo>();
		
		ArrayList<Long> enrollmentTrainIDList = new ArrayList<Long>();
		ArrayList<Long> enrollmentTestIDList = new ArrayList<Long>();
		try {
			String encoding = "UTF-8";
			File rawTrainEnrollInfofile = new File(rawTrainEnrollInfo);
			File rawTestEnrollInfofile = new File(rawTestEnrollInfo);
			File rawrawTrainDropoutfile = new File(rawTrainDropout);
			if (rawTrainEnrollInfofile.isFile() && rawTrainEnrollInfofile.exists() 
			  && rawTestEnrollInfofile.isFile() && rawTestEnrollInfofile.exists()
			  && rawrawTrainDropoutfile.isFile() && rawrawTrainDropoutfile.exists()) {
				InputStreamReader rawTrainRead = new InputStreamReader(
						new FileInputStream(rawTrainEnrollInfofile), encoding);
				InputStreamReader rawTestRead = new InputStreamReader(
						new FileInputStream(rawTestEnrollInfofile), encoding);
				InputStreamReader rawTrainDropoutRead = new InputStreamReader(
						new FileInputStream(rawrawTrainDropoutfile), encoding);
				BufferedReader rawTrainEnrollInfoReader = new BufferedReader(rawTrainRead);
				BufferedReader rawTestEnrollInfoReader = new BufferedReader(rawTestRead);
				BufferedReader rawTrainDropoutReader = new BufferedReader(rawTrainDropoutRead);
							
				long tmpEnrollment_id = -1;
				String tmpUsername_id = null;
				String tmpCourse_id = null;
				//开始读取训练样本中的注册ID信息
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
					tmpUsername_id = enrollInfo[1].trim();
					tmpCourse_id = enrollInfo[2].trim();
					
					enrollmentTrainIDList.add(tmpEnrollment_id);
					//开始统计注册ID所属用户和所属课程的信息
					EnrollmentIDInfo tmpEnrollmentIDInfo  = new EnrollmentIDInfo();
					tmpEnrollmentIDInfo.setUsernameID(tmpUsername_id);			
					tmpEnrollmentIDInfo.setCourseID(tmpCourse_id);
					//最后更新注册ID的特征集合
					enrollmentIDMap.put(tmpEnrollment_id, tmpEnrollmentIDInfo);		
					
					UsernameIDInfo tmpUsernameIDInfo;
					if (!usernameIDMap.containsKey(tmpUsername_id)) {
						//若是第一次出现此用户ID
						tmpUsernameIDInfo = new UsernameIDInfo();
					}else {
						//若是之前出现过的此用户ID
						tmpUsernameIDInfo = usernameIDMap.get(tmpUsername_id);
					}
					//11) 更新此用户的选课集合
					tmpUsernameIDInfo.setEnrollCourse(tmpCourse_id);
					//更新旷课记录信息
					tmpUsernameIDInfo.setHasDropoutCourseRecord();
					tmpUsernameIDInfo.addDropoutRecordNum();
					if (lineNum%3!=0) {
						tmpUsernameIDInfo.setHasDropoutCourseRecord4valid();
						tmpUsernameIDInfo.addDropoutRecordNum4valid();
					}
					usernameIDMap.put(tmpUsername_id, tmpUsernameIDInfo);
					
					CourseIDInfo tmpCourseIDInfo;
					if (!courseIDMap.containsKey(tmpCourse_id)) {
						//若是第一次出现此课程ID
						tmpCourseIDInfo = new CourseIDInfo();
					}else {
						//若是之前出现过的此课程ID
						tmpCourseIDInfo = courseIDMap.get(tmpCourse_id);
					}
					//11) 更新此课程的用户集合
					tmpCourseIDInfo.setEnrollUsername(tmpUsername_id);
					//更新旷课记录信息
					tmpCourseIDInfo.setHasDropoutUsernameRecord();
					tmpCourseIDInfo.addDropoutRecordNum();
					if (lineNum%3!=0) {
						tmpCourseIDInfo.setHasDropoutUsernameRecord4valid();
						tmpCourseIDInfo.addDropoutRecordNum4valid();
					}
					courseIDMap.put(tmpCourse_id, tmpCourseIDInfo);
					
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed train enroll info numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed train enroll info numbers:" + lineNum);
				
				//开始读取测试样本中的注册ID信息
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
					tmpUsername_id = enrollInfo[1].trim();
					tmpCourse_id = enrollInfo[2].trim();
					
					enrollmentTestIDList.add(tmpEnrollment_id);
					//开始统计注册ID所属用户和所属课程的信息
					EnrollmentIDInfo tmpEnrollmentIDInfo  = new EnrollmentIDInfo();
					tmpEnrollmentIDInfo.setUsernameID(tmpUsername_id);			
					tmpEnrollmentIDInfo.setCourseID(tmpCourse_id);
					//最后更新注册ID的特征集合
					enrollmentIDMap.put(tmpEnrollment_id, tmpEnrollmentIDInfo);		
					
					UsernameIDInfo tmpUsernameIDInfo;
					if (!usernameIDMap.containsKey(tmpUsername_id)) {
						//若是第一次出现此用户ID
						tmpUsernameIDInfo = new UsernameIDInfo();
					}else {
						//若是之前出现过的此用户ID
						tmpUsernameIDInfo = usernameIDMap.get(tmpUsername_id);
					}
					//11) 更新此用户的选课集合
					tmpUsernameIDInfo.setEnrollCourse(tmpCourse_id);
					usernameIDMap.put(tmpUsername_id, tmpUsernameIDInfo);
					
					CourseIDInfo tmpCourseIDInfo;
					if (!courseIDMap.containsKey(tmpCourse_id)) {
						//若是第一次出现此课程ID
						tmpCourseIDInfo = new CourseIDInfo();
					}else {
						//若是之前出现过的此课程ID
						tmpCourseIDInfo = courseIDMap.get(tmpCourse_id);
					}
					//11) 更新此课程的用户集合
					tmpCourseIDInfo.setEnrollUsername(tmpUsername_id);
					courseIDMap.put(tmpCourse_id, tmpCourseIDInfo);
					
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed test enroll info numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed test enroll info numbers:" + lineNum);
				
				//开始读取训练样本中的翘课记录，同时处理一份给验证集合版本
				lineNum=0;
				tmpLineStr = null;
				boolean isDropout;
				while ((tmpLineStr = rawTrainDropoutReader.readLine()) != null) {
					lineNum++;
					String[] dropoutInfo = tmpLineStr.split(",");
					if (dropoutInfo.length!=2) {
						System.err.print("Error: dropoutInfo.length is "+ dropoutInfo.length 
								+ " in lineNum" + lineNum);
						System.exit(0);
					}
					tmpEnrollment_id = Long.valueOf(dropoutInfo[0].trim());
					if (dropoutInfo[1].equals("0")) {
						isDropout = false;
					}else {
						isDropout = true;
					}
					
					if (isDropout) {
						//如果有翘课，那么进行相应处理
						EnrollmentIDInfo tmpEnrollmentIDInfo = enrollmentIDMap.get(tmpEnrollment_id);
						
						UsernameIDInfo tmpUsernameIDInfo = usernameIDMap.get(tmpEnrollmentIDInfo.getUsernameID());
						tmpUsernameIDInfo.addDropoutCourseNum();
						//顺便考虑验证集合特征
						if (lineNum%3!=0) {
							tmpUsernameIDInfo.addDropoutCourseNum4valid();
						}
						usernameIDMap.put(tmpEnrollmentIDInfo.getUsernameID(), tmpUsernameIDInfo);
						
						CourseIDInfo tmpCourseIDInfo = courseIDMap.get(tmpEnrollmentIDInfo.getCourseID());
						tmpCourseIDInfo.addDropoutUsernameNum();
						//顺便考虑验证集合特征
						if (lineNum%3!=0) {
							tmpCourseIDInfo.addDropoutUsernameNum4valid();
						}
						courseIDMap.put(tmpEnrollmentIDInfo.getCourseID(), tmpCourseIDInfo);
					}
					
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed train dropout info numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed train dropout info numbers:" + lineNum);
				
				//开始输出特征
				StringBuffer tmpFeaBuffer = new StringBuffer(); 
				StringBuffer tmpFeaBuffer4valid = new StringBuffer();
				int i;
				for (i = 0; i < enrollmentTrainIDList.size(); i++) {
					// 开始逐个输出特征
					EnrollmentIDInfo tmpFeaInfo = enrollmentIDMap.get(enrollmentTrainIDList.get(i));
					UsernameIDInfo tmpUsernameIDInfo = usernameIDMap.get(tmpFeaInfo.getUsernameID());
					CourseIDInfo tmpCourseIDInfo = courseIDMap.get(tmpFeaInfo.getCourseID());
					//标准测试版本特征
					tmpFeaBuffer.append(tmpUsernameIDInfo.getEnrollCourseNum()+","); //11).
					tmpFeaBuffer.append(tmpCourseIDInfo.getEnrollUsernameNum()+","); //12).
//					tmpFeaBuffer.append(tmpUsernameIDInfo.getDropoutCourseNum()+","); //13).
//					tmpFeaBuffer.append(tmpUsernameIDInfo.getDropoutCourseRate()+","); //13).
//					tmpFeaBuffer.append(tmpCourseIDInfo.getDropoutUsernameNum()+","); //14).
//					tmpFeaBuffer.append(tmpCourseIDInfo.getDropoutUsernameRate()); //14).
					tmpFeaBuffer.append(tmpUsernameIDInfo.getDropoutCourseScore()+","); //13).
					tmpFeaBuffer.append(tmpCourseIDInfo.getDropoutUsernameScore()); //14).
					Result2Txt(tarFeatureTrainFile,tmpFeaBuffer.toString());
					tmpFeaBuffer.delete(0, tmpFeaBuffer.length());
					
					//内部验证版本特征
					tmpFeaBuffer4valid.append(tmpUsernameIDInfo.getEnrollCourseNum()+","); //11).
					tmpFeaBuffer4valid.append(tmpCourseIDInfo.getEnrollUsernameNum()+","); //12).
//					tmpFeaBuffer4valid.append(tmpUsernameIDInfo.getDropoutCourseNum4valid()+","); //13).
//					tmpFeaBuffer4valid.append(tmpUsernameIDInfo.getDropoutCourseRate4valid()+","); //13).
//					tmpFeaBuffer4valid.append(tmpCourseIDInfo.getDropoutUsernameNum4valid()+","); //14).
//					tmpFeaBuffer4valid.append(tmpCourseIDInfo.getDropoutUsernameRate4valid()); //14).
					tmpFeaBuffer4valid.append(tmpUsernameIDInfo.getDropoutCourseScore4valid()+","); //13).
					tmpFeaBuffer4valid.append(tmpCourseIDInfo.getDropoutUsernameScore4valid()); //14).
					Result2Txt(tarFeatureTrain4ValidFile,tmpFeaBuffer4valid.toString());
					tmpFeaBuffer4valid.delete(0, tmpFeaBuffer4valid.length());
					
					if (i%1000 ==0) {
						System.out.println("hasProcessed train feature numbers:" + i);
					}
				}
				System.out.println("Totally processed train feature numbers:" + i);
				
				for (i = 0; i < enrollmentTestIDList.size(); i++) {
					// 开始逐个输出特征
					EnrollmentIDInfo tmpFeaInfo = enrollmentIDMap.get(enrollmentTestIDList.get(i));
					UsernameIDInfo tmpUsernameIDInfo = usernameIDMap.get(tmpFeaInfo.getUsernameID());
					CourseIDInfo tmpCourseIDInfo = courseIDMap.get(tmpFeaInfo.getCourseID());
					
					tmpFeaBuffer.append(tmpUsernameIDInfo.getEnrollCourseNum()+","); //11).
					tmpFeaBuffer.append(tmpCourseIDInfo.getEnrollUsernameNum()+","); //12).
//					tmpFeaBuffer.append(tmpUsernameIDInfo.getDropoutCourseNum()+","); //13).
//					tmpFeaBuffer.append(tmpUsernameIDInfo.getDropoutCourseRate()+","); //13).
//					tmpFeaBuffer.append(tmpCourseIDInfo.getDropoutUsernameNum()+","); //14).
//					tmpFeaBuffer.append(tmpCourseIDInfo.getDropoutUsernameRate()); //14).
					tmpFeaBuffer.append(tmpUsernameIDInfo.getDropoutCourseScore()+","); //13).
					tmpFeaBuffer.append(tmpCourseIDInfo.getDropoutUsernameScore()); //14).
					
					Result2Txt(tarFeatureTestFile,tmpFeaBuffer.toString());
					tmpFeaBuffer.delete(0, tmpFeaBuffer.length());
					if (i%1000 ==0) {
						System.out.println("hasProcessed test feature numbers:" + i);
					}
				}
				System.out.println("Totally processed test feature numbers:" + i);
				
				rawTrainEnrollInfoReader.close();
				rawTestEnrollInfoReader.close();
				rawTrainDropoutReader.close();
			} else {
				System.out.println("can't find the file");
			}
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
		//处理注册ID信息文件及翘课记录
	    String rawTrainEnrollInfo=dataPathStr+"train/enrollment_train.csv";
	    String rawTestEnrollInfo=dataPathStr+"test/enrollment_test.csv";
	    String rawTrainDropout=dataPathStr+"train/truth_train.csv";
	    
	    //生成特征文件 一,11). - 14). 
		String tarFeatureTrainFile=dataPathStr+"features/Step01_11_14_train.txt";
		String tarFeatureTestFile=dataPathStr+"features/Step01_11_14_test.txt";
		//用于自己交叉验证使用
		String tarFeatureTrain4ValidFile=dataPathStr+"features/Step01_11_14_train4valid.txt";

		long readstart=System.currentTimeMillis();
		extractFeature(rawTrainEnrollInfo, rawTestEnrollInfo, rawTrainDropout,
				tarFeatureTrainFile, tarFeatureTestFile, tarFeatureTrain4ValidFile);
        long readend=System.currentTimeMillis();
        System.out.println((readend-readstart)/1000.0+"s had been consumed to process the enrollment info");
		
	}
}
