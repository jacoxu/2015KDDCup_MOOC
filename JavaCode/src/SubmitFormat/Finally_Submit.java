package SubmitFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.xml.internal.bind.v2.model.core.MaybeElement;

import Tools.EnrollmentIDInfo;
import Tools.TimeParser;

public class Finally_Submit {
	/**
	 * @param args
	 * @author jacoxu-2015/05/17
	 */
	
	private static void submitFormat(String rawResults, String rawSubmitFile, String tarSubmitFile) {
		try {
			String encoding = "UTF-8";
			File rawResultsfile = new File(rawResults);
			File rawSampleSubmitfile = new File(rawSubmitFile);
			if (rawResultsfile.isFile() && rawResultsfile.exists()&&
					rawSampleSubmitfile.isFile() && rawSampleSubmitfile.exists()) {
				InputStreamReader rawResultsRead = new InputStreamReader(
						new FileInputStream(rawResultsfile), encoding);
				BufferedReader rawResultsReader = new BufferedReader(rawResultsRead);
				
				InputStreamReader rawSampleSubmitRead = new InputStreamReader(
						new FileInputStream(rawSampleSubmitfile), encoding);
				BufferedReader rawSampleSubmitReader = new BufferedReader(rawSampleSubmitRead);
				
				int lineNum=0;
				String tmpResultsLineStr = null;
				double tmpResult = -1;
				String tmpSampleSubmitStr = null;
				String tmpNewSubmitStr = null;
				while ((tmpResultsLineStr = rawResultsReader.readLine()) != null) {
					tmpSampleSubmitStr = rawSampleSubmitReader.readLine();
					lineNum++;
					String[] results_Segs = tmpResultsLineStr.trim().split("\\s+");
					tmpResult = Double.valueOf(results_Segs[1]);
					if (Math.abs(tmpResult-1)<0.001) {
						results_Segs[1] = "1";
					}else if (Math.abs(tmpResult-0)<0.001){
						results_Segs[1] = "0";
					}
					String[] sampbleSubmit_Segs = tmpSampleSubmitStr.trim().split(",");
					tmpNewSubmitStr = sampbleSubmit_Segs[0].trim()+","+results_Segs[1];
					Result2Txt(tarSubmitFile,tmpNewSubmitStr);
					
					if (lineNum%1000 ==0) {
						System.out.println("hasProcessed log info numbers:" + lineNum);
					}
				}
				System.out.println("Totally processed log info numbers:" + lineNum);

				rawResultsRead.close();
				rawSampleSubmitReader.close();
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
		
	    String rawResults=dataPathStr+"Submission/20150625-01-predictMultiNomialLR_labelP.txt";
	    String rawSubmitFile=dataPathStr+"sampleSubmission.csv/sampleSubmission.csv";
		String tarSubmitFile=dataPathStr+"Submission/20150625-01-MLR_label_0001.csv";
		
		long readstart=System.currentTimeMillis();
		submitFormat(rawResults, rawSubmitFile, tarSubmitFile);
        long readend=System.currentTimeMillis();
        System.out.println((readend-readstart)/1000.0+"s had been consumed to process the raw training data");
	}
}
