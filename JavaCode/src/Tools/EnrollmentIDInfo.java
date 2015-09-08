package Tools;

import java.util.HashSet;

public class EnrollmentIDInfo {
	//1). 单位是秒
	long startActTime = -1;
	long endActTime = -1;
	//2).
	int activeNum = 0;
	//3).
	HashSet<Integer> activeDayset = new HashSet<Integer>();
	//4).
	int problemNum = 0;
	double problemRate = 0;
	//5).
	int videoNum = 0;
	double videoRate = 0;
	//6).
	int accessNum = 0;
	double accessRate = 0;
	//7).
	int wikiNum = 0;
	double wikiRate = 0;
	//8).
	int discussNum = 0;
	double discussRate = 0;
	//9).
	int navigateNum = 0;
	double navigateRate = 0;
	//10).
	int pageCloseNum = 0;
	double pageCloseRate = 0;
	//11).
	String userNameID = null;
	String courseID = null;
	
	public void setStartActTime(long secondTime){
		startActTime = secondTime;
	}
	public void setEndActTime(long secondTime){
		endActTime = secondTime;
	}
	public void addActiveNum(){
		activeNum++;
	}
	public void addProblemNum(){
		problemNum++;
	}
	public void addVideoNum(){
		videoNum++;
	}
	public void addAccessNum(){
		accessNum++;
	}
	public void addWikiNum(){
		wikiNum++;
	}
	public void addDiscussNum(){
		discussNum++;
	}
	public void addNavigateNum(){
		navigateNum++;
	}
	public void addPageCloseNum(){
		pageCloseNum++;
	}
	public void setActiveDayset(int dayTime){
		activeDayset.add(dayTime);
	}
	public long getStartActTime(){
		return startActTime;
	}
	public long getEndActTime(){
		return endActTime;
	}
	public HashSet<Integer> getActiveDayset(){
		return activeDayset;
	}
	public void setUsernameID(String __usernameID){
		userNameID = __usernameID;
	}
	public void setCourseID(String __courseID){
		courseID = __courseID;
	}

	//1) 注册ID的活跃时间跨度（单位：天）
	public int getSpanDays(){
		if ((startActTime == -1)||(endActTime == -1)) {
			return -1;
		}else {
			return (int) Math.ceil(((endActTime-startActTime)+1.0)/(24 * 60 * 60));
		}
	}
	public long getSpanSeconds(){
		if ((startActTime == -1)||(endActTime == -1)) {
			return -1;
		}else {
			return (endActTime-startActTime+1);
		}
	}
	//2) 注册ID的活跃次数（单位：次）
	public int getActiveNum(){
		return activeNum;
	}
	//3) 注册ID的活跃天数（单位：天）
	public int getActiveDayNum(){
		return activeDayset.size();
	}
	public float getActiveNumPerDay(){
		return ((float)activeNum)/activeDayset.size();
	}
	public float getActiveDayRate(){
		return ((float)activeDayset.size())/getSpanDays();
	}	
	//4) 注册ID访问Problem（问题）的频次（单位：次）及比重（%百分比）
	public int getProblemNum(){
		return problemNum;
	}
	public float getProblemRate(){
		return (float)problemNum/activeNum;
	}
	//5) 注册ID访问Video（问题）的频次（单位：次）及比重（%百分比）
	public int getVideoNum(){
		return videoNum;
	}
	public float getVideoRate(){
		return (float)videoNum/activeNum;
	}
	//6) 注册ID访问Access（问题）的频次（单位：次）及比重（%百分比）
	public int getAccessNum(){
		return accessNum;
	}
	public float getAccessRate(){
		return (float)accessNum/activeNum;
	}
	//7) 注册ID访问Wiki（问题）的频次（单位：次）及比重（%百分比）
	public int getWikiNum(){
		return wikiNum;
	}
	public float getWikiRate(){
		return (float)wikiNum/activeNum;
	}
	//8) 注册ID访问Discussion（问题）的频次（单位：次）及比重（%百分比）
	public int getDiscussNum(){
		return discussNum;
	}
	public float getDiscussRate(){
		return (float)discussNum/activeNum;
	}
	//9) 注册ID访问Navigate（问题）的频次（单位：次）及比重（%百分比）
	public int getNavigateNum(){
		return navigateNum;
	}
	public float getNavigateRate(){
		return (float)navigateNum/activeNum;
	}
	//10) 注册ID访问Page_close（问题）的频次（单位：次）及比重（%百分比）
	public int getPageCloseNum(){
		return pageCloseNum;
	}
	public float getPageCloseRate(){
		return (float)pageCloseNum/activeNum;
	}
	//11) 获取当前注册ID的用户ID和课程ID
	public String getUsernameID(){
		return userNameID;
	}
	public String getCourseID(){
		return courseID;
	}
}
