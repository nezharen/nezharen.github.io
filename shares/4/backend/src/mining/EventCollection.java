package mining;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//eventºØ∫œ¿‡
public class EventCollection {
	List<Event> events;
	int size;

	public EventCollection() {
		// TODO Auto-generated constructor stub
	    events = new ArrayList<Event>();
	}

	public void addEvent(Event c) {
	    events.add(c);
	    size++;
	}
	
	public int getSize(){
		return size;
	}
	
	public Event getEvent(int pos) {
		return events.get(pos);
	}
}
