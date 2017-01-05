package mining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Animation {
	public int[] activityFre; //case频率
	public int[][] activityQueFre; //边的case频率
	public List<AnimationCase>[] caseList; //case频率
	public List<AnimationCase>[][] caseQueList; //边的case频率
	long frame;
	
	public class MyArrayList extends ArrayList<AnimationCase> {
		public MyArrayList() {
			super();
		}
	}
	
	public class AnimationCase {
		String caseId;
		long begin;
		long end;
		
		public AnimationCase() {
			caseId = "";
			begin = 0;
			end = 0;
		}
		
		public AnimationCase(String caseId, long begin, long end) {
			this.caseId = caseId;
			this.begin = begin;
			this.end = end;
		}
	}
	
	public Animation(int activityCount) {
		// TODO Auto-generated constructor stub
		activityQueFre = new int[activityCount][activityCount];
		activityFre = new int[activityCount];
		caseList = new MyArrayList[activityCount];
		caseQueList = new MyArrayList[activityCount][activityCount];
		for(int i = 0; i < activityCount; i++)
			caseList[i] = new MyArrayList();
		for(int i = 0; i < activityCount; i++)
			for(int j = 0; j < activityCount; j++)
				caseQueList[i][j] = new MyArrayList();
		frame = -1;
	}
	
	public void setFrame(long num){
		frame = num;
	}
	
	public long getFrame(){
		return frame;
	}
	
	public void setActivityFre(int pos, int num) {
		activityFre[pos] = num;
	}

	public void setActivityQueFre(int parent, int children, int num) {
		activityQueFre[parent][children] = num;
	}
	
	public void incActivityFre(int pos) {
		activityFre[pos]++;
	}

	public void addActivityCase(int pos, String caseID, long start, long end) {
		AnimationCase animationCase = new AnimationCase(caseID, start, end);
		caseList[pos].add(animationCase);
	}
	
	public void incActivityQueFre(int parent, int children) {
		activityQueFre[parent][children]++;
	}
	
	public void addActivityQueCase(int parent, int children, String caseID, long start, long end) {
		AnimationCase animationCase = new AnimationCase(caseID, start, end);
		caseQueList[parent][children].add(animationCase);
	}
	
	public void decActivityFre(int pos) {
		activityFre[pos]--;
	}

	public void decActivityQueFre(int parent, int children) {
		activityQueFre[parent][children]--;
	}
	
	public int getActivityFre(int pos) {
		return activityFre[pos];
	}

	public int getActivityQueFre(int parent, int children) {
		return activityQueFre[parent][children];
	}
}
