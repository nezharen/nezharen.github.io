package mining;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mining.Animation.AnimationCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opencsv.CSVReader;

public class Mining {
	static final String fileName = "ExampleLog.csv";
	static final char separator = ',';
	static final boolean hasTableHead = true;
	static final String encodingText = "GBK";
	static final String timestamp = "dd.MM.yy HH:mm";
	long animationPrepareTime = 1000;
	long animationSameTime = 30;
	
	static EventCollection eventCollection;
	static GraphNet graphNet;
	static AnimationCollection animationCollection;
	static ActivityCollection activityCollection;
	String[][] rowData;
	
	public Mining() {
		init();
		readCsv();
		setEventCollection();
		setGraphNet();
		writeGraphNet();
		setAnimation();
		writeAnimation();
		setActivityCollection();
		writeActivity();
	}
	
	public void init() {
        eventCollection = new EventCollection();
        graphNet = new GraphNet();
        animationCollection = new AnimationCollection();
        activityCollection = new ActivityCollection();
	}
	
	public void readCsv() {
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(new File(fileName)));
			CSVReader reader = new CSVReader(new InputStreamReader(input, encodingText), separator);
			List<String[]> myEntries;
			myEntries = reader.readAll();
			if (hasTableHead) {
				myEntries.remove(0);
			}
			rowData = myEntries.toArray(new String[0][]);
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//更新event集
    public void setEventCollection() {

        for (int i = 0; i < rowData.length; i++) {
            Event event = new Event();
            for (int j = 0; j < rowData[i].length; j++) {
                switch (j) {
                case 0:
                    event.setCase(rowData[i][j]);
                    break;
                case 1:
                    event.setActivity(rowData[i][j]);
                    if (!graphNet
                            .activityExist(rowData[i][j])) {
                        graphNet
                                .addActivity(rowData[i][j]);
                    }
                    if (!animationCollection
                            .activityExist(rowData[i][j])) {
                    	animationCollection
                                .addActivity(rowData[i][j]);
                    }
                    break;
                case 2:
                    event.setDate(rowData[i][j], timestamp);
                    break;
                case 3:
                    event.setDate(rowData[i][j], timestamp);
                    break;
                case 8:
                    event.setResource(rowData[i][j]);
                    break;
                default:
                    break;
                }
            }
            eventCollection.addEvent(event);
        }
        
    }
    
    //更新graph
    public void setGraphNet() {
        graphNet.setMemory();
        graphNet.activityNames[0] = "begin";
        graphNet.activityNames[1] = "end";

        int[] temp = new int[graphNet.activityCount]; 
        int[][] tempQue = new int[graphNet.activityCount][graphNet.activityCount]; 
        int lastActivityId = -1;
        String lastCase = "";
        Date lastDate = new Date();
        //按顺序遍历得出case，并运算各个参数
        for (int i = 0; i < eventCollection.getSize(); i++) {
            Event event = eventCollection.getEvent(i);
            String activityName = event.getActivity();
            String caseName = event.getCase();
            long time = event.getTime();
            int activityId = graphNet
                    .getActivityId(activityName);
            graphNet.setActivityName(activityId,
                    activityName);
            graphNet.addActivityTime(activityId, time);
            graphNet.setTime(activityId, time);
            graphNet.setBeginTime(event.getStartDate().getTime());
            graphNet.setEndTime(event.getEndDate().getTime());
            
            if (caseName.equals(lastCase)) {
                graphNet.addActivityFre(activityId);
                graphNet.addActivityQueFre(
                        lastActivityId, activityId);
                long queTime = (event.getStartDate().getTime()-lastDate.getTime());
                graphNet.addActivityQueTime(lastActivityId, activityId, queTime);
                graphNet.setQueTime(lastActivityId, activityId, queTime);
                temp[activityId]++;
                tempQue[lastActivityId][activityId]++;
                lastActivityId = activityId;
                lastDate = event.getEndDate();
            } else {
                if (lastActivityId != -1) {
                    graphNet.addActivityQueFre(
                            lastActivityId, 1);
                    graphNet.addActivityQueFre(0,
                            activityId);
                    tempQue[lastActivityId][1]++;
                } else{
                    graphNet.addActivityQueFre(0,
                            activityId);
                }
                for (int j=0; j< graphNet.activityCount; j++){
                    if (temp[j] > graphNet.maxActivityRep[j]){
                        graphNet.maxActivityRep[j] = temp[j];
                    }
                    if (temp[j] > 0){
                        graphNet.activityCaseFre[j]++;
                    }
                    temp[j] = 0;
                    for (int k = 0; k< graphNet.activityCount; k++){
                        if (tempQue[j][k] >0){
                            graphNet.activityCaseQueFre[j][k]++;
                        }
                        tempQue[j][k] = 0;
                    }
                }
                tempQue[0][activityId]++;
                temp[activityId]++;
                lastCase = caseName;
                lastActivityId = activityId;
                lastDate = event.getEndDate();
                graphNet.addActivityFre(lastActivityId);
            }
        }
        tempQue[lastActivityId][1]++;
        for (int j=0; j< graphNet.activityCount; j++){
            if (temp[j] > graphNet.maxActivityRep[j]){
                graphNet.maxActivityRep[j] = temp[j];
            }
            if (temp[j] > 0){
                graphNet.activityCaseFre[j]++;
            }
            for (int k = 0; k< graphNet.activityCount; k++){
                if (tempQue[j][k] >0){
                    graphNet.activityCaseQueFre[j][k]++;
                }
            }
        }
        graphNet.addActivityQueFre(
                lastActivityId, 1);

//        graphNet.beginTime /= 1000*60*60;
//        graphNet.endTime /= 1000*60*60;
        
        for (int i = 0; i < graphNet.activityCount; i++)
            for (int j = 0; j < graphNet.activityCount; j++)
            {
                graphNet.activityQueFreSort.add(graphNet.activityQueFre[i][j]);
            }
        Collections.sort(graphNet.activityQueFreSort);
        
        animationPrepareTime = (graphNet.endTime - graphNet.beginTime) / 120;
    }
    
    public void setAnimation() {
    	animationCollection.setMemory();
    	animationCollection.activityNames[0] = "begin";
    	animationCollection.activityNames[1] = "end";

        int lastActivityId = -1;
        String lastCase = "";
        long lastTime = -1;
        Animation tempAnimation = null;
        Date lastDate = new Date();
        
        for (int i = 0; i < eventCollection.getSize(); i++) {
            Event event = eventCollection.getEvent(i);
            Event lastEvent = null;
            if(i > 0)
            	lastEvent = eventCollection.getEvent(i - 1);
            String caseName = event.getCase();
            String activityName = event.getActivity();
            int activityId = animationCollection
                    .getActivityId(activityName);
            animationCollection.setActivityName(activityId,
                    activityName);
            animationCollection.setBeginTime(event.getStartDate().getTime());
            animationCollection.setEndTime(event.getEndDate().getTime());
            
    
            if (caseName.equals(lastCase)) {
                if(lastTime >= event.getStartDate().getTime())
                	lastTime = lastTime + animationSameTime;
                else
                	lastTime = event.getStartDate().getTime();
                if(!animationCollection.hasAnimation(lastTime)) {
                	Animation animation = new Animation(animationCollection.activityCount);
                	animation.setFrame(lastTime);
                	animationCollection.addAnimation(animation);
                }
                	
                if(lastTime >= event.getEndDate().getTime())
                	lastTime = lastTime + animationSameTime;
                else
                	lastTime = event.getEndDate().getTime();
                if(!animationCollection.hasAnimation(lastTime)) {
                	Animation animation = new Animation(animationCollection.activityCount);
                	animation.setFrame(lastTime);
                	animationCollection.addAnimation(animation);
                }
             } else {
                if (lastActivityId == -1) {
                    lastTime = event.getStartDate().getTime() - animationPrepareTime;
                    if(!animationCollection.hasAnimation(lastTime)) {
                    	Animation animation = new Animation(animationCollection.activityCount);
                    	animation.setFrame(lastTime);
                    	animationCollection.addAnimation(animation);
                        animationCollection.setBeginTime(lastTime);
                    }
                	
                    lastTime = event.getStartDate().getTime();
                    if(!animationCollection.hasAnimation(lastTime)) {
                    	Animation animation = new Animation(animationCollection.activityCount);
                    	animation.setFrame(lastTime);
                    	animationCollection.addAnimation(animation);
                    }
                    	
                    if(lastTime >= event.getEndDate().getTime())
                    	lastTime = lastTime + animationSameTime;
                    else
                    	lastTime = event.getEndDate().getTime();
                    if(!animationCollection.hasAnimation(lastTime)) {
                    	Animation animation = new Animation(animationCollection.activityCount);
                    	animation.setFrame(lastTime);
                    	animationCollection.addAnimation(animation);
                    }
                } else{
                	if(lastTime >= lastEvent.getEndDate().getTime() + animationPrepareTime)
                    	lastTime = lastTime + animationSameTime;
                    else
                    	lastTime = lastEvent.getEndDate().getTime() + animationPrepareTime;
                    if(!animationCollection.hasAnimation(lastTime)) {
                    	Animation animation = new Animation(animationCollection.activityCount);
                    	animation.setFrame(lastTime);
                    	animationCollection.addAnimation(animation);
                        animationCollection.setEndTime(lastTime);
                    }

                    lastTime = event.getStartDate().getTime() - animationPrepareTime;
                    if(!animationCollection.hasAnimation(lastTime)) {
                    	Animation animation = new Animation(animationCollection.activityCount);
                    	animation.setFrame(lastTime);
                    	animationCollection.addAnimation(animation);
                        animationCollection.setBeginTime(lastTime);
                    }
                	
                    lastTime = event.getStartDate().getTime();
                    if(!animationCollection.hasAnimation(lastTime)) {
                    	Animation animation = new Animation(animationCollection.activityCount);
                    	animation.setFrame(lastTime);
                    	animationCollection.addAnimation(animation);
                    }
                    	
                    if(lastTime >= event.getEndDate().getTime())
                    	lastTime = lastTime + animationSameTime;
                    else
                    	lastTime = event.getEndDate().getTime();
                    if(!animationCollection.hasAnimation(lastTime)) {
                    	Animation animation = new Animation(animationCollection.activityCount);
                    	animation.setFrame(lastTime);
                    	animationCollection.addAnimation(animation);
                    }
               }
                
                lastCase = caseName;
                lastActivityId = activityId;
            }
        }
        
        Event tempEvent = eventCollection.getEvent(eventCollection.getSize() - 1);
        if(lastTime >= tempEvent.getEndDate().getTime() + animationPrepareTime)
        	lastTime = lastTime + animationSameTime;
        else
        	lastTime = tempEvent.getEndDate().getTime() + animationPrepareTime;
        if(!animationCollection.hasAnimation(lastTime)) {
        	Animation animation = new Animation(animationCollection.activityCount);
        	animation.setFrame(lastTime);
        	animationCollection.addAnimation(animation);
            animationCollection.setEndTime(lastTime);
        }
        
        animationCollection.merge(); // set drag frame
        
        lastActivityId = -1;
        lastCase = "";
        tempAnimation = null;
        lastDate = new Date();
        lastTime = -1;
        long thisTime = -1;
        
        for (int i = 0; i < eventCollection.getSize(); i++) {
            Event event = eventCollection.getEvent(i);
            Event lastEvent = null;
            if(i > 0)
            	lastEvent = eventCollection.getEvent(i - 1);
            String activityName = event.getActivity();
            String caseName = event.getCase();
            int activityId = animationCollection
                    .getActivityId(activityName);
            int index = -1;
            
            if (caseName.equals(lastCase)) {
                lastTime = thisTime;
            	if(thisTime >= event.getStartDate().getTime())
                	thisTime = thisTime + animationSameTime;
                else
                	thisTime = event.getStartDate().getTime();
            	index = animationCollection.getIndex(lastTime);
            	tempAnimation = animationCollection.getAnimation(index);
//            	while(tempAnimation.getFrame() < event.getStartDate().getTime()) {
                	tempAnimation.incActivityQueFre(lastActivityId, activityId);	
                	tempAnimation.addActivityQueCase(lastActivityId, activityId, event.getCase(), lastTime, thisTime);
//                	index++;
//                	tempAnimation = animationCollection.getAnimation(index);
//            	}
                	
                for(int it = 0; it < 101; it++){
                	tempAnimation = animationCollection.dragAnimation[it];
                	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
	                	tempAnimation.incActivityQueFre(lastActivityId, activityId);	
	                	tempAnimation.addActivityQueCase(lastActivityId, activityId, event.getCase(), lastTime, thisTime);
                	}
                }

                lastTime = thisTime;
            	if(thisTime >= event.getEndDate().getTime())
                	thisTime = thisTime + animationSameTime;
                else
                	thisTime = event.getEndDate().getTime();
            	index = animationCollection.getIndex(lastTime);
            	tempAnimation = animationCollection.getAnimation(index);
//            	while(tempAnimation.getFrame() < event.getEndDate().getTime()) {
                	tempAnimation.incActivityFre(activityId);
                	tempAnimation.addActivityCase(activityId, event.getCase(), lastTime, thisTime);
//                	index++;
//                	tempAnimation = animationCollection.getAnimation(index);
//            	}
                
                for(int it = 0; it < 101; it++){
                	tempAnimation = animationCollection.dragAnimation[it];
                	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
                    	tempAnimation.incActivityFre(activityId);
                    	tempAnimation.addActivityCase(activityId, event.getCase(), lastTime, thisTime);
                	}
                }
             } else {
                if (lastActivityId == -1) {
                   	lastTime = event.getStartDate().getTime() - animationPrepareTime;
                   	thisTime = event.getStartDate().getTime();
                	index = animationCollection.getIndex(lastTime);
                	tempAnimation = animationCollection.getAnimation(index);
//                	while(tempAnimation.getFrame() < event.getStartDate().getTime()) {
                    	tempAnimation.incActivityQueFre(0, activityId);
                    	tempAnimation.addActivityQueCase(0, activityId, event.getCase(), lastTime, thisTime);
//                    	index++;
//                    	tempAnimation = animationCollection.getAnimation(index);
//                	}
                    
                    for(int it = 0; it < 101; it++){
                    	tempAnimation = animationCollection.dragAnimation[it];
                    	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
                        	tempAnimation.incActivityQueFre(0, activityId);
                        	tempAnimation.addActivityQueCase(0, activityId, event.getCase(), lastTime, thisTime);
                    	}
                    }
                    	
                    lastTime = thisTime;
                	if(thisTime >= event.getEndDate().getTime())
                    	thisTime = thisTime + animationSameTime;
                    else
                    	thisTime = event.getEndDate().getTime();
                	index = animationCollection.getIndex(lastTime);
                	tempAnimation = animationCollection.getAnimation(index);
//                	while(tempAnimation.getFrame() < event.getEndDate().getTime()) {
                    	tempAnimation.incActivityFre(activityId);	
                    	tempAnimation.addActivityCase(activityId, event.getCase(), lastTime, thisTime);
//                    	index++;
//                    	tempAnimation = animationCollection.getAnimation(index);
//                	}
                    
                    for(int it = 0; it < 101; it++){
                    	tempAnimation = animationCollection.dragAnimation[it];
                    	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
                        	tempAnimation.incActivityFre(activityId);
                        	tempAnimation.addActivityCase(activityId, event.getCase(), lastTime, thisTime);
                    	}
                    }
                } else{
                    lastTime = thisTime;
                	if(thisTime >= lastEvent.getEndDate().getTime() + animationPrepareTime)
                    	thisTime = thisTime + animationSameTime;
                    else
                    	thisTime = lastEvent.getEndDate().getTime() + animationPrepareTime;
                	index = animationCollection.getIndex(lastTime);
                	tempAnimation = animationCollection.getAnimation(index);
//                	while(tempAnimation.getFrame() < lastEvent.getEndDate().getTime() + animationPrepareTime) {
                    	tempAnimation.incActivityQueFre(lastActivityId, 1);
                    	tempAnimation.addActivityQueCase(lastActivityId, 1, lastEvent.getCase(), lastTime, thisTime);
//                    	index++;
//                    	tempAnimation = animationCollection.getAnimation(index);
//                	}
                	
                    for(int it = 0; it < 101; it++){
                    	tempAnimation = animationCollection.dragAnimation[it];
                    	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
                        	tempAnimation.incActivityQueFre(lastActivityId, 1);
                        	tempAnimation.addActivityQueCase(lastActivityId, 1, lastEvent.getCase(), lastTime, thisTime);
                    	}
                    }
                    	
                	index = animationCollection.getIndex(lastEvent.getEndDate().getTime() + animationPrepareTime);
                	tempAnimation = animationCollection.getAnimation(index);
//                	tempAnimation.incActivityQueFre(lastActivityId, 1);
                	tempAnimation.addActivityQueCase(lastActivityId, 1, lastEvent.getCase(), -1, -1);
                	
                   	lastTime = event.getStartDate().getTime() - animationPrepareTime;
                   	thisTime = event.getStartDate().getTime();
                	index = animationCollection.getIndex(lastTime);
                	tempAnimation = animationCollection.getAnimation(index);
//                	while(tempAnimation.getFrame() < event.getStartDate().getTime()) {
                    	tempAnimation.incActivityQueFre(0, activityId);
                    	tempAnimation.addActivityQueCase(0, activityId, event.getCase(), lastTime, thisTime);
//                    	index++;
//                    	tempAnimation = animationCollection.getAnimation(index);
//                	}
                    
                    for(int it = 0; it < 101; it++){
                    	tempAnimation = animationCollection.dragAnimation[it];
                    	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
                        	tempAnimation.incActivityQueFre(0, activityId);
                        	tempAnimation.addActivityQueCase(0, activityId, event.getCase(), lastTime, thisTime);
                    	}
                    }
                    	
                    lastTime = thisTime;
                	if(thisTime >= event.getEndDate().getTime())
                    	thisTime = thisTime + animationSameTime;
                    else
                    	thisTime = event.getEndDate().getTime();
                	index = animationCollection.getIndex(lastTime);
                	tempAnimation = animationCollection.getAnimation(index);
//                	while(tempAnimation.getFrame() < event.getEndDate().getTime()) {
                    	tempAnimation.incActivityFre(activityId);	
                    	tempAnimation.addActivityCase(activityId, event.getCase(), lastTime, thisTime);
//                    	index++;
//                    	tempAnimation = animationCollection.getAnimation(index);
//                	}
                    
                    for(int it = 0; it < 101; it++){
                    	tempAnimation = animationCollection.dragAnimation[it];
                    	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
                        	tempAnimation.incActivityFre(activityId);
                        	tempAnimation.addActivityCase(activityId, event.getCase(), lastTime, thisTime);
                    	}
                    }
                }
                
                lastCase = caseName;
                lastActivityId = activityId;
            }
        }
        
        int index = -1;
        Event lastEvent = eventCollection.getEvent(eventCollection.getSize() - 1);
        int activityId = animationCollection.getActivityId(lastEvent.getActivity());
        
        lastTime = thisTime;
    	if(thisTime >= lastEvent.getEndDate().getTime() + animationPrepareTime)
        	thisTime = thisTime + animationSameTime;
        else
        	thisTime = lastEvent.getEndDate().getTime() + animationPrepareTime;
    	index = animationCollection.getIndex(lastTime);
    	tempAnimation = animationCollection.getAnimation(index);
//    	while(tempAnimation.getFrame() < lastEvent.getEndDate().getTime() + animationPrepareTime) {
        	tempAnimation.incActivityQueFre(lastActivityId, 1);
        	tempAnimation.addActivityQueCase(lastActivityId, 1, lastEvent.getCase(), lastTime, thisTime);
//        	index++;
//        	tempAnimation = animationCollection.getAnimation(index);
//    	}
    	
        for(int it = 0; it < 101; it++){
        	tempAnimation = animationCollection.dragAnimation[it];
        	if(lastTime <= tempAnimation.getFrame() && thisTime > tempAnimation.getFrame()){
            	tempAnimation.incActivityQueFre(lastActivityId, 1);
            	tempAnimation.addActivityQueCase(lastActivityId, 1, lastEvent.getCase(), lastTime, thisTime);
        	}
        }
    	
    	index = animationCollection.getIndex(lastEvent.getEndDate().getTime() + animationPrepareTime);
    	tempAnimation = animationCollection.getAnimation(index);
//      tempAnimation.incActivityQueFre(lastActivityId, 1);
    	tempAnimation.addActivityQueCase(lastActivityId, 1, lastEvent.getCase(), -1, -1);
    }
    
    //更新activity集
    public void setActivityCollection() {
        for (int i = 0; i < eventCollection.getSize(); i++) {
            Event event = eventCollection.getEvent(i);
            
            Activity activity = activityCollection.getActivity(event.getActivity());
            if(activity == null){
            	activity = new Activity();
            	activity.setActivity(event.getActivity());
                activityCollection.addActivity(activity);
            }
            
            activity.addFrequency();
            activity.addDuration(event.getTime());
        }
        for(int i = 0; i < activityCollection.getSize(); i++)
        {
            Activity activity = activityCollection.getActivity(i);
            activity.sort();
        }
    }
    
    public void writeGraphNet() throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("activity_name", graphNet.activityNames);
		
		json.put("event_frequency", graphNet.activityFre);
		json.put("case_frequency", graphNet.activityCaseFre);
		json.put("max_repetitions", graphNet.maxActivityRep);
		json.put("total_duration", graphNet.activityTime);
		json.put("max_duration", graphNet.maxActivityTime);
		json.put("min_duration", graphNet.minActivityTime);
		
		json.put("event_frequency_edge", graphNet.activityQueFre);
		json.put("case_frequency_edge", graphNet.activityCaseQueFre);
		json.put("total_duration_edge", graphNet.activityQueTime);
		json.put("max_duration_edge", graphNet.maxActivityQueTime);
		json.put("min_duration_edge", graphNet.minActivityQueTime);
		
		save("graphNet.json", json.toString(4));
//		JSONArray vertexList = new JSONArray();
//		for (int i = 0; i < graphNet.activityCount; i++) {
//			JSONObject vertex = new JSONObject();
//			vertex.put("name", graphNet.activityNames[i]);
//			vertex.put("event_frequency", graphNet.activityFre[i]);
//			vertex.put("case_frequency", graphNet.activityCaseFre[i]);
//			vertex.put("max_repetitions", graphNet.maxActivityRep[i]);
//			vertex.put("total_duration", graphNet.activityTime[i]);
//			vertex.put("max_duration", graphNet.maxActivityTime[i]);
//			vertex.put("min_duration", graphNet.minActivityTime[i]);
//			vertexList.put(vertex);
//		}
//		json.put("vertex", vertexList);
		
//		JSONArray edgeList = new JSONArray();
//		for (int i = 0; i < graphNet.activityCount; i++) {
//			for (int j = 0; i < graphNet.activityCount; i++) {
//			JSONObject edge = new JSONObject();
//			edge.put("name", graphNet.activityNames[i]);
//			edge.put("event_frequency", graphNet.activityFre[i]);
//			edge.put("case_frequency", graphNet.activityCaseFre[i]);
//			edge.put("max_repetitions", graphNet.maxActivityRep[i]);
//			edge.put("total_duration", graphNet.activityTime[i]);
//			edge.put("max_duration", graphNet.maxActivityTime[i]);
//			edge.put("min_duration", graphNet.minActivityTime[i]);
//			edgeList.put(edge);
//		}
//		json.put("edge", edgeList);
    }
    
    public void writeAnimation() throws JSONException {
    	try {
			FileWriter fw = new FileWriter("animation.json");
	
			JSONObject json = new JSONObject();
			
			json.put("activity_name", animationCollection.activityNames);
			json.put("drag_frame", animationCollection.animationFrame);
			json.put("begin", animationCollection.beginTime);
			json.put("end", animationCollection.endTime);
			json.put("activity_count", animationCollection.activityCount);
			
			fw.write("{\n\t\"activity_name\":[");
			for(int i = 0; i < animationCollection.activityCount; i++){
				if(i == animationCollection.activityCount - 1)
					fw.write("\"" + animationCollection.activityNames[i] + "\"]");
				else
					fw.write("\"" + animationCollection.activityNames[i] + "\",");
			}
			fw.write(",\n\t\"drag_frame\":[");
			for(int i = 0; i < 101; i++){
				if(i == 100)
					fw.write(animationCollection.animationFrame[i] + "]");
				else
					fw.write(animationCollection.animationFrame[i] + ",");
			}
			fw.write(",\n\t\"begin\":" + animationCollection.beginTime);
			fw.write(",\n\t\"end\":" + animationCollection.endTime);
			fw.write(",\n\t\"activity_count\":" + animationCollection.activityCount);
			
			fw.write(",\n\t\"drag_frame_list\":[\n");
			
			JSONArray dragFrameList = new JSONArray();
			for (int i = 0; i < 101; i++) {
				Animation animation = animationCollection.dragAnimation[i];
				
				JSONObject frame = new JSONObject();
				frame.put("frame", animation.frame);
//				frame.put("activity_count", animation.activityFre);
//				frame.put("edge_count", animation.activityQueFre);
//				
//				JSONArray activityCaseList = new JSONArray();
//				for(int j = 0; j < animationCollection.activityCount; j++){
//					JSONArray oneActivityCaseList = new JSONArray();
//					for(int k = 0; k < animation.caseList[j].size(); k++){
//						AnimationCase oneCase = animation.caseList[j].get(k);
//						JSONObject animationCase = new JSONObject();
//						animationCase.put("case_id", oneCase.caseId);
//						animationCase.put("begin", oneCase.begin);
//						animationCase.put("end", oneCase.end);
//						oneActivityCaseList.put(animationCase);
//					}
//					activityCaseList.put(oneActivityCaseList);
//				}
//				frame.put("activity_case", activityCaseList);
//				
//				JSONArray edgeCaseList = new JSONArray();
//				for(int j1 = 0; j1 < animationCollection.activityCount; j1++){
//					JSONArray oneEdgeCaseListRow = new JSONArray();
//					for(int j2 = 0; j2 < animationCollection.activityCount; j2++){
//						JSONArray oneEdgeCaseList = new JSONArray();
//						for(int k = 0; k < animation.caseQueList[j1][j2].size(); k++){
//							AnimationCase oneCase = animation.caseQueList[j1][j2].get(k);
//							JSONObject animationCase = new JSONObject();
//							animationCase.put("case_id", oneCase.caseId);
//							animationCase.put("begin", oneCase.begin);
//							animationCase.put("end", oneCase.end);
//							oneEdgeCaseList.put(animationCase);
//						}
//						oneEdgeCaseListRow.put(oneEdgeCaseList);
//					}
//					edgeCaseList.put(oneEdgeCaseListRow);
//				}
//				frame.put("edge_case", edgeCaseList);
				
				JSONArray activityCaseList = new JSONArray();
				for(int j = 0; j < animationCollection.activityCount; j++){
					for(int k = 0; k < animation.caseList[j].size(); k++){
						AnimationCase oneCase = animation.caseList[j].get(k);
						JSONObject animationCase = new JSONObject();
						animationCase.put("case_id", oneCase.caseId);
						animationCase.put("begin", oneCase.begin);
						animationCase.put("end", oneCase.end);
						animationCase.put("index", j);
						activityCaseList.put(animationCase);
					}
				}
				frame.put("activity_case", activityCaseList);
				
				JSONArray edgeCaseList = new JSONArray();
				for(int j1 = 0; j1 < animationCollection.activityCount; j1++){
					for(int j2 = 0; j2 < animationCollection.activityCount; j2++){
						for(int k = 0; k < animation.caseQueList[j1][j2].size(); k++){
							AnimationCase oneCase = animation.caseQueList[j1][j2].get(k);
							JSONObject animationCase = new JSONObject();
							animationCase.put("case_id", oneCase.caseId);
							animationCase.put("begin", oneCase.begin);
							animationCase.put("end", oneCase.end);
							animationCase.put("from", j1);
							animationCase.put("to", j2);
							edgeCaseList.put(animationCase);
						}
					}
				}
				frame.put("edge_case", edgeCaseList);
				
				frame.put("index", animationCollection.getIndex(animationCollection.animationFrame[i]));
				
				dragFrameList.put(frame);
				if(i == 100)
					fw.write(frame.toString() + "\n");
				else
					fw.write(frame.toString() + ",\n");
			}
			json.put("drag_frame_list", dragFrameList);
			
			fw.write("\t]");			
			
			fw.write(",\n\t\"frame_list\":[\n");
			
			JSONArray frameList = new JSONArray();
			for (int i = 0; i < animationCollection.size; i++) {
				Animation animation = animationCollection.getAnimation(i);
				
				JSONObject frame = new JSONObject();
				frame.put("frame", animation.frame);
//				frame.put("activity_count", animation.activityFre);
//				frame.put("edge_count", animation.activityQueFre);
//				
//				JSONArray activityCaseList = new JSONArray();
//				for(int j = 0; j < animationCollection.activityCount; j++){
//					JSONArray oneActivityCaseList = new JSONArray();
//					for(int k = 0; k < animation.caseList[j].size(); k++){
//						AnimationCase oneCase = animation.caseList[j].get(k);
//						JSONObject animationCase = new JSONObject();
//						animationCase.put("case_id", oneCase.caseId);
//						animationCase.put("begin", oneCase.begin);
//						animationCase.put("end", oneCase.end);
//						oneActivityCaseList.put(animationCase);
//					}
//					activityCaseList.put(oneActivityCaseList);
//				}
//				frame.put("activity_case", activityCaseList);
//				
//				JSONArray edgeCaseList = new JSONArray();
//				for(int j1 = 0; j1 < animationCollection.activityCount; j1++){
//					JSONArray oneEdgeCaseListRow = new JSONArray();
//					for(int j2 = 0; j2 < animationCollection.activityCount; j2++){
//						JSONArray oneEdgeCaseList = new JSONArray();
//						for(int k = 0; k < animation.caseQueList[j1][j2].size(); k++){
//							AnimationCase oneCase = animation.caseQueList[j1][j2].get(k);
//							JSONObject animationCase = new JSONObject();
//							animationCase.put("case_id", oneCase.caseId);
//							animationCase.put("begin", oneCase.begin);
//							animationCase.put("end", oneCase.end);
//							oneEdgeCaseList.put(animationCase);
//						}
//						oneEdgeCaseListRow.put(oneEdgeCaseList);
//					}
//					edgeCaseList.put(oneEdgeCaseListRow);
//				}
//				frame.put("edge_case", edgeCaseList);
				
				JSONArray activityCaseList = new JSONArray();
				for(int j = 0; j < animationCollection.activityCount; j++){
					for(int k = 0; k < animation.caseList[j].size(); k++){
						AnimationCase oneCase = animation.caseList[j].get(k);
						JSONObject animationCase = new JSONObject();
						animationCase.put("case_id", oneCase.caseId);
						animationCase.put("begin", oneCase.begin);
						animationCase.put("end", oneCase.end);
						animationCase.put("index", j);
						activityCaseList.put(animationCase);
					}
				}
				frame.put("activity_case", activityCaseList);
				
				JSONArray edgeCaseList = new JSONArray();
				for(int j1 = 0; j1 < animationCollection.activityCount; j1++){
					for(int j2 = 0; j2 < animationCollection.activityCount; j2++){
						for(int k = 0; k < animation.caseQueList[j1][j2].size(); k++){
							AnimationCase oneCase = animation.caseQueList[j1][j2].get(k);
							JSONObject animationCase = new JSONObject();
							animationCase.put("case_id", oneCase.caseId);
							animationCase.put("begin", oneCase.begin);
							animationCase.put("end", oneCase.end);
							animationCase.put("from", j1);
							animationCase.put("to", j2);
							edgeCaseList.put(animationCase);
						}
					}
				}
				frame.put("edge_case", edgeCaseList);
				
				frameList.put(frame);
				if(i == animationCollection.size - 1)
					fw.write(frame.toString() + "\n");
				else
					fw.write(frame.toString() + ",\n");
			}
			json.put("frame_list", frameList);
			
			fw.write("\t]\n}");
			fw.close();
	//		save("animaiton.json", json.toString());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    public void writeActivity() throws JSONException {
		JSONObject json = new JSONObject();
		
		int size = activityCollection.getSize();
		long[] aggregateDuration = new long[size];
		long[] meanDuration = new long[size];
		long[] medianDuration = new long[size];
		long[] durationRange = new long[size];
		int[] frequency = new int[size];
		String[] activityName = new String[size];
		for(int i = 0; i < size; i++){
			Activity activity = activityCollection.getActivity(i);
			activityName[i] = activity.getActivity();
			aggregateDuration[i] = activity.getAggregateDuration();
			meanDuration[i] = activity.getMeanDuration();
			medianDuration[i] = activity.getMedianDuration();
			durationRange[i] = activity.getDurationRange();
			frequency[i] = activity.getFrequency();
		}
		
		json.put("activity_name", activityName);
		json.put("aggregate_duration", aggregateDuration);
		json.put("mean_duration", meanDuration);
		json.put("median_duration", medianDuration);
		json.put("duration_range", durationRange);
		json.put("frequency", frequency);
		
		save("activity.json", json.toString(4));
//		JSONArray vertexList = new JSONArray();
//		for (int i = 0; i < graphNet.activityCount; i++) {
//			JSONObject vertex = new JSONObject();
//			vertex.put("name", graphNet.activityNames[i]);
//			vertex.put("event_frequency", graphNet.activityFre[i]);
//			vertex.put("case_frequency", graphNet.activityCaseFre[i]);
//			vertex.put("max_repetitions", graphNet.maxActivityRep[i]);
//			vertex.put("total_duration", graphNet.activityTime[i]);
//			vertex.put("max_duration", graphNet.maxActivityTime[i]);
//			vertex.put("min_duration", graphNet.minActivityTime[i]);
//			vertexList.put(vertex);
//		}
//		json.put("vertex", vertexList);
		
//		JSONArray edgeList = new JSONArray();
//		for (int i = 0; i < graphNet.activityCount; i++) {
//			for (int j = 0; i < graphNet.activityCount; i++) {
//			JSONObject edge = new JSONObject();
//			edge.put("name", graphNet.activityNames[i]);
//			edge.put("event_frequency", graphNet.activityFre[i]);
//			edge.put("case_frequency", graphNet.activityCaseFre[i]);
//			edge.put("max_repetitions", graphNet.maxActivityRep[i]);
//			edge.put("total_duration", graphNet.activityTime[i]);
//			edge.put("max_duration", graphNet.maxActivityTime[i]);
//			edge.put("min_duration", graphNet.minActivityTime[i]);
//			edgeList.put(edge);
//		}
//		json.put("edge", edgeList);
    }
    
	public void save(String fileName, String content) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(content);
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void saveAppend(String fileName, String content) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(content);
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
