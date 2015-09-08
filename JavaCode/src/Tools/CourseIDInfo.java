package Tools;

import java.util.HashSet;

public class CourseIDInfo {

	HashSet<String> enrollUsernameSet = new HashSet<String>();
	int dropoutRecordNum = 0;
	int dropoutUsernameNum = 0;
	int dropoutRecordNum4valid = 0;
	int dropoutUsernameNum4valid = 0;
	//每个课程分配一个旷课估分，默认为0.7929，根据旷课记录进行比例调整
	boolean hasDropoutRecord = false;
	boolean hasDropoutRecord4valid = false;
	double baseDropoutScore = 0.792926946624413;
	double scaleFactor = 2;
	double dropoutUsernameScore = baseDropoutScore;
	double dropoutUsernameScore4valid = baseDropoutScore;
	
	public void setEnrollUsername(String __enrollUsername){
		enrollUsernameSet.add(__enrollUsername);
	}
	public int getEnrollUsernameNum(){
		return enrollUsernameSet.size();
	}
	public void addDropoutRecordNum(){
		dropoutRecordNum++;
	}
	public void addDropoutUsernameNum(){
		dropoutUsernameNum++;
	}
	public void addDropoutRecordNum4valid(){
		dropoutRecordNum4valid++;
	}
	public void addDropoutUsernameNum4valid(){
		dropoutUsernameNum4valid++;
	}
	public int getDropoutRecordNum(){
		return dropoutRecordNum;
	}
	public int getDropoutUsernameNum(){
		return dropoutUsernameNum;
	}
	public int getDropoutRecordNum4valid(){
		return dropoutRecordNum4valid;
	}
	public int getDropoutUsernameNum4valid(){
		return dropoutUsernameNum4valid;
	}
	public float getDropoutUsernameRate(){
		return ((float)dropoutUsernameNum)/getDropoutRecordNum();
	}
	public float getDropoutUsernameRate4valid(){
		return ((float)dropoutUsernameNum4valid)/getDropoutRecordNum4valid();
	}
	//生成每个用户ID的旷课评估分
	public void setHasDropoutUsernameRecord(){
		hasDropoutRecord = true;
	}
	public void setHasDropoutUsernameRecord4valid(){
		hasDropoutRecord4valid = true;
	}
	public float getDropoutUsernameScore(){
		if (hasDropoutRecord) {
			return (float) (baseDropoutScore 
				+ (getDropoutUsernameRate()-baseDropoutScore)*Math.tanh((double)getDropoutRecordNum()/scaleFactor));
		}else {
			return (float) baseDropoutScore;
		}
	}
	public float getDropoutUsernameScore4valid(){
		if (hasDropoutRecord4valid) {
			return (float) (baseDropoutScore 
				+ (getDropoutUsernameRate4valid()-baseDropoutScore)*Math.tanh((double)getDropoutRecordNum4valid()/scaleFactor));
		}else {
			return (float) baseDropoutScore;
		}
	}
}