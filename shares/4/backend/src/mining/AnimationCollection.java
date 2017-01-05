package mining;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AnimationCollection {
	List<Long> frames;
	List<Animation> animations;
	int size;
		
	HashMap<String, Integer> activityIDMap;
	public String[] activityNames;
	public int activityCount;   //活动数
	public long beginTime;   //整个图的开始时间
	public long endTime; 	//整个图的结束时间
	public long[] animationFrame;
	public Animation[] dragAnimation;

	public AnimationCollection() {
		// TODO Auto-generated constructor stub
		frames = new ArrayList<Long>();
		animations = new ArrayList<Animation>();
		activityIDMap = new HashMap<String, Integer>();
		activityCount = 2;
		beginTime = 0;
		endTime = 0;
	    size = 0;
	    animationFrame = new long[101];
	    dragAnimation = new Animation[101];
	}
	
	public int getSize(){
		return size;
	}
	
	//hash判断活动数
	public boolean activityExist(String activityName) {
		return activityIDMap.containsKey(activityName);
	}
	
	//各类活动操作
	public void setActivityName(int id, String name) {
		activityNames[id] = name;
	}

	public int getActivityId(String activityString) {
		return activityIDMap.get(activityString);
	}

	public void addActivity(String activityName) {
		activityIDMap.put(activityName, activityCount);
		activityCount++;
	}
	
	//分配动态内存
	public void setMemory() {
		activityNames = new String[activityCount];
	}
	
	public void setBeginTime(long time){
		if (time < beginTime || beginTime == 0 ){
			beginTime = time;
		}
	}
	
	public void setEndTime(long time){
		if (time > endTime){
			endTime = time;
		}
	}
	
	public void addAnimation(Animation c) {
		if(size == 0){
			animations.add(c);
			frames.add(c.getFrame());
			size++;
			return;
		}
		
		int l = 0;
		int r = size - 1;
		int temp = size / 2;
		while(true){
			if(frames.get(temp) > c.getFrame() && temp == l){
				animations.add(l, c);
				frames.add(l, c.getFrame());
				break;
			}
			
			if(frames.get(temp) < c.getFrame() && temp == r){
				animations.add(r + 1, c);
				frames.add(r + 1, c.getFrame());
				break;
			}
			
			if(frames.get(temp) > c.getFrame()){
				r = temp - 1;
				temp = (l + r) / 2;
			}
			
			if(frames.get(temp) < c.getFrame()){
				l = temp + 1;
				temp = (l + r) / 2;
			}
		}
	    size++;
	}
	
	public Animation getAnimation(long key) {
		return animations.get(frames.indexOf(key));
	}
	
	public Animation getAnimation(int i) {
		return animations.get(i);
	}
	
	public int getIndex(long key) {
		return frames.indexOf(key);
	}
	
	public boolean hasAnimation(long key) {
		return frames.contains(key);
	}
	
	public void merge(){
    	animationFrame[0] = beginTime;
    	
    	for(int i = 0; i < frames.size(); i++){
        	animationFrame[((int) Math.ceil((frames.get(i) - beginTime) * 100 / (endTime - beginTime)))] = frames.get(i);
        }
        
        for(int i = 1; i < 101; i++){
        	if(animationFrame[i] == 0)
        		animationFrame[i] = animationFrame[i - 1];
        }
        
	    for(int i = 0; i < 101; i++){
	    	dragAnimation[i] = new Animation(activityCount);
	    	dragAnimation[i].setFrame(animationFrame[i]);
	    }
	}
	
	public Animation getAnimationFrame(int i){
		if(i < 0 || i > 100)
			return null;
		return getAnimation(animationFrame[i]);
	}
}
