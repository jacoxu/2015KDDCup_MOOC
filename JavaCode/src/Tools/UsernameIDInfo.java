package Tools;

import java.util.HashSet;

public class UsernameIDInfo {
	
	HashSet<String> enrollCourseSet = new HashSet<String>();
	int dropoutRecordNum = 0;
	int dropoutCourseNum = 0;
	int dropoutRecordNum4valid = 0;
	int dropoutCourseNum4valid = 0;
	//每个人分配一个旷课估分，默认为0.7929，根据旷课记录进行比例调整
	boolean hasDropoutRecord = false;
	boolean hasDropoutRecord4valid = false;
	double baseDropoutScore = 0.792926946624413;
	double scaleFactor = 2;
	double dropoutCourseScore = baseDropoutScore;
	double dropoutCourseScore4valid = baseDropoutScore;
	
	public void setEnrollCourse(String enrollCourse){
		enrollCourseSet.add(enrollCourse);
	}
	public int getEnrollCourseNum(){
		return enrollCourseSet.size();
	}
	public void addDropoutRecordNum(){
		dropoutRecordNum++;
	}
	public void addDropoutCourseNum(){
		dropoutCourseNum++;
	}
	public void addDropoutRecordNum4valid(){
		dropoutRecordNum4valid++;
	}
	public void addDropoutCourseNum4valid(){
		dropoutCourseNum4valid++;
	}
	public int getDropoutRecordNum(){
		return dropoutRecordNum;
	}
	public int getDropoutCourseNum(){
		return dropoutCourseNum;
	}
	public int getDropoutRecordNum4valid(){
		return dropoutRecordNum4valid;
	}
	public int getDropoutCourseNum4valid(){
		return dropoutCourseNum4valid;
	}
	public float getDropoutCourseRate(){
		return ((float)dropoutCourseNum)/getDropoutRecordNum();
	}
	public float getDropoutCourseRate4valid(){
		return ((float)dropoutCourseNum4valid)/getDropoutRecordNum4valid();
	}
	//生成每个用户ID的旷课评估分
	public void setHasDropoutCourseRecord(){
		hasDropoutRecord = true;
	}
	public void setHasDropoutCourseRecord4valid(){
		hasDropoutRecord4valid = true;
	}
	public float getDropoutCourseScore(){
		if (hasDropoutRecord) {
			return (float) (baseDropoutScore
				+ (getDropoutCourseRate()-baseDropoutScore)*Math.tanh((double)getDropoutRecordNum()/scaleFactor));
		}else {
			return (float) baseDropoutScore;
		}
	}
	public float getDropoutCourseScore4valid(){
		if (hasDropoutRecord4valid) {
			return (float) (baseDropoutScore
				+ (getDropoutCourseRate4valid()-baseDropoutScore)*Math.tanh((double)getDropoutRecordNum4valid()/scaleFactor));
		}else {
			return (float) baseDropoutScore;
		}
	}
}
