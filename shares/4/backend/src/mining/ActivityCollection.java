package mining;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActivityCollection {
    
	List<Activity> activities;
	int size;

	public ActivityCollection() {
		// TODO Auto-generated constructor stub
	    activities = new ArrayList<Activity>();
	    size = 0;
	}

	public void addActivity(Activity a) {
	    activities.add(a);
	    size++;
	}
	
	public int getSize(){
		return size;
	}
	
    public int getMinimalFrequency(){
        return getActivity(getSize() - 1).getFrequency();
    }
    
    public int getMaximalFrequency(){
        return getActivity(0).getFrequency();
    }
    
    public double getMeanFrequency(){
        int totalFrequency = 0;
        for(int i = 0; i < getSize(); i++)
        {
            totalFrequency += getActivity(i).getFrequency();
        }
        return totalFrequency * 1.0 / getSize();
    }
    
    public double getMedianFrequency(){
        double result;
        if(getSize() % 2 == 1)
        {
            result = getActivity(getSize() / 2).getFrequency();
        }
        else
        {
            result = (getActivity(getSize() / 2).getFrequency() + getActivity(getSize() / 2 - 1).getFrequency()) * 1.0 / 2;
        }
        return result;
    }
	
    public double getFrequencyStdDeviation(){
        double meanFrequency = getMeanFrequency();
        double total = 0;
        for(int i = 0; i < getSize(); i++)
        {
            total += Math.pow(getActivity(i).getFrequency() - meanFrequency, 2);
        }
        return Math.sqrt(total / (getSize() - 1));
    }
    
	public Activity getActivity(int pos) {
		return activities.get(pos);
	}
	
	public Activity getActivity(String activity) {
	    for(int i = 0; i < getSize(); i++)
	    {
	        if(activities.get(i).getActivity().equals(activity))
	        {
	            return activities.get(i);
	        }
	    }
	    return null;
	}	
}
