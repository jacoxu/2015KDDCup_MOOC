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

import Tools.EnrollmentIDInfo;
import Tools.TimeParser;

public class Step01_01_10_features {
	/**
	 * @param args
	 * @author jacoxu-2015/05/17
	 */
	
	private static void extractFeature(String rawTrainLog, String tarFeatureTrainFile) {
		//HashMap<注册ID，注册ID特征集合>
		HashMap<Long, EnrollmentIDInfo> enrollmentIDMap = new HashMap<Long, EnrollmentIDInfo>();
		ArrayList<Long> enrollmentIDList = new ArrayList<Long>();
		try {
			String encoding = "UTF-8";
			File rawTrainLogfile = new File(rawTrainLog);
			if (rawTrainLogfile.isFile() && rawTrainLogfile.exists()) {
				InputStreamReader rawTrainRead = new InputStreamReader(
						new FileInputStream(rawTrainLogfile), encoding);
				BufferedReader rawTrainReader = new BufferedReader(rawTrainRead);

				
				long tmpEnrollment_id = -1;
				long tmpTime = -1;
				String tmpSourceStr = null;
				String tmpEventStr = null;
				String tmpObjectStr = null;
				
				int lineNum=0;
				String tmpLineStr = null;
				while ((tmpLineStr = rawTrainReader.readLine()) != null) {
					lineNum++;
					if (lineNum<=1) continue;
					String[] logInfo = tmpLineStr.split(",");
					if (logInfo.length!=5) {
						System.err.print("Error: logInfo.length is "+ logInfo.length 
								+ " in lineNum" + lineNum);
						System.exit(0);
					}
					tmpEnrollment_id = Long.valueOf(logInfo[0].trim());
					tmpTime = Long.valueOf(TimeParser.stringDateToLongDate(logInfo[1].trim()));
					tmpSourceStr = logInfo[2].trim();
					tmpEventStr = logInfo[3].trim();
					tmpObjectStr = logInfo[4].trim();
					//抽取训练样本的注册ID活跃时间跨度，同时更新活跃次数及活跃天数集合
					EnrollmentIDInfo tmpEnrollmentIDInfo;
					if (!enrollmentIDMap.containsKey(tmpEnrollment_id)) {
						//若是第一次出现此注册ID
						enrollmentIDList.add(tmpEnrollment_id);
						tmpEnrollmentIDInfo = new EnrollmentIDInfo();
						//1) 更新活动起始和终止时间
						tmpEnrollmentIDInfo.setStartActTime(tmpTime);
						tmpEnrollmentIDInfo.setEndActTime(tmpTime);
					}else {
						//若是之前出现过的注册ID
						tmpEnrollmentIDInfo = enrollmentIDMap.get(tmpEnrollment_id);
						//1) 更新活动起始和终止时间
						if (tmpTime < tmpEnrollmentIDInfo.getStartActTime()) {
							tmpEnrollmentIDInfo.setStartActTime(tmpTime);
						}else if (tmpTime > tmpEnrollmentIDInfo.getEndActTime()) {
							tmpEnrollmentIDInfo.setEndActTime(tmpTime);
						}
					}
					//2) 更新活跃次数集合
					tmpEnrollmentIDInfo.addActiveNum();						
					//3) 更新活跃天数集合
					int tmpDayTime = TimeParser.longDateToDayTime(tmpTime);
					tmpEnrollmentIDInfo.setActiveDayset(tmpDayTime);
					//4) 注册ID访问Problem（问题）的频次（单位：次）及比重（%百分比）
					if(tmpEventStr.equals("problem")) tmpEnrollmentIDInfo.addProblemNum();
					//5) 注册ID访问Video（问题）的频次（单位：次）及比重（%百分比）
					else if(tmpEventStr.equals("video")) tmpEnrollmentIDInfo.addVideoNum();
					//6) 注册ID访问Access（问题）的频次（单位：次）及比重（%百分比）
					else if(tmpEventStr.equals("access")) tmpEnrollmentIDInfo.addAccessNum();
					//7) 注册ID访问Wiki（问题）的频次（单位：次）及比重（%百分比）
					else if(tmpEventStr.equals("wiki")) tmpEnrollmentIDInfo.addWikiNum();
					//8) 注册ID访问Discussion（问题）的频次（单位：次）及比重（%百分比）
					else if(tmpEventStr.equals("discussion")) tmpEnrollmentIDInfo.addDiscussNum();
					//9) 注册ID访问Navigate（问题）的频次（单位：次）及比重（%百分比）
					else if(tmpEventStr.equals("nagivate")) tmpEnrollmentIDInfo.addNavigateNum();
					//10) 注册ID访问Page_close（问题）的频次（单位：次）及比重（%百分比）
					else if(tmpEventStr.equals("page_close")) tmpEnrollmentIDInfo.addPageCloseNum();
					else {
						System.err.print("Error: tmpEventStr is "+ tmpEventStr 
								+ " in lineNum" + lineNum);
						System.exit(0);
					}
					//最后更新特征集合
					enrollmentIDMap.put(tmpEnrollment_id, tmpEnrollmentIDInfo);
					
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed log info numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed log info numbers:" + lineNum);
				//开始输出特征
				StringBuffer tmpFeaBuffer = new StringBuffer(); 
				int i;
				for (i = 0; i < enrollmentIDList.size(); i++) {
					//TODO 开始逐个输出特征
					EnrollmentIDInfo tmpFeaInfo = enrollmentIDMap.get(enrollmentIDList.get(i));
					tmpFeaBuffer.append(tmpFeaInfo.getStartActTime()+","); //1).
					tmpFeaBuffer.append(tmpFeaInfo.getEndActTime()+","); //1).
					tmpFeaBuffer.append(tmpFeaInfo.getSpanSeconds()+","); //1).
					tmpFeaBuffer.append(tmpFeaInfo.getSpanDays()+","); //1).
					tmpFeaBuffer.append(tmpFeaInfo.getActiveNum()+","); //2).
					tmpFeaBuffer.append(tmpFeaInfo.getActiveDayNum()+","); //3).
					tmpFeaBuffer.append(tmpFeaInfo.getActiveNumPerDay()+","); //3).
					tmpFeaBuffer.append(tmpFeaInfo.getActiveDayRate()+","); //3).
					tmpFeaBuffer.append(tmpFeaInfo.getProblemNum()+","); //4).
					tmpFeaBuffer.append(tmpFeaInfo.getProblemRate()+","); //4).
					tmpFeaBuffer.append(tmpFeaInfo.getVideoNum()+","); //5).
					tmpFeaBuffer.append(tmpFeaInfo.getVideoRate()+","); //5).
					tmpFeaBuffer.append(tmpFeaInfo.getAccessNum()+","); //6).
					tmpFeaBuffer.append(tmpFeaInfo.getAccessRate()+","); //6).
					tmpFeaBuffer.append(tmpFeaInfo.getWikiNum()+","); //7).
					tmpFeaBuffer.append(tmpFeaInfo.getWikiRate()+","); //7).
					tmpFeaBuffer.append(tmpFeaInfo.getDiscussNum()+","); //8).
					tmpFeaBuffer.append(tmpFeaInfo.getDiscussRate()+","); //8).
					tmpFeaBuffer.append(tmpFeaInfo.getNavigateNum()+","); //9).
					tmpFeaBuffer.append(tmpFeaInfo.getNavigateRate()+","); //9).
					tmpFeaBuffer.append(tmpFeaInfo.getPageCloseNum()+","); //10).
					tmpFeaBuffer.append(tmpFeaInfo.getPageCloseRate()); //10).
					Result2Txt(tarFeatureTrainFile,tmpFeaBuffer.toString());
					tmpFeaBuffer.delete(0, tmpFeaBuffer.length());
					if (i%1000 ==0) {
						System.out.println("hasProcessed feature numbers:" + i);
					}
				}
				System.out.println("Totally processed feature numbers:" + i);
				rawTrainRead.close();
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
		//处理Log数据
	    String rawTrainLog=dataPathStr+"train/log_train.csv";
	    String rawTestLog=dataPathStr+"test/log_test.csv";
	    //生成特征文件 一,1). - 10).
		String tarFeatureTrainFile=dataPathStr+"features/Step01_01_10_train.txt";
		
		long readstart=System.currentTimeMillis();
		extractFeature(rawTrainLog, tarFeatureTrainFile);
        long readend=System.currentTimeMillis();
        System.out.println((readend-readstart)/1000.0+"s had been consumed to process the raw training data");
		
		String tarFeatureTestFile=dataPathStr+"features/Step01_01_10_test.txt";
       
		readstart=System.currentTimeMillis();
		extractFeature(rawTestLog, tarFeatureTestFile);
        readend=System.currentTimeMillis();
        System.out.println((readend-readstart)/1000.0+"s had been consumed to process the raw test data");
	}
}
