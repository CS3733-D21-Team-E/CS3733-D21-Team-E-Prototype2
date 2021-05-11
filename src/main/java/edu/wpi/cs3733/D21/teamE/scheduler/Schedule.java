package edu.wpi.cs3733.D21.teamE.scheduler;

import edu.wpi.cs3733.D21.teamE.Time;
import edu.wpi.cs3733.D21.teamE.map.Node;
import java.util.*;

public class Schedule implements Iterable<ToDo>{
    private final List<ToDo> schedule;

    public Schedule(List<ToDo> todoList){
        schedule = new ArrayList<>(todoList.size());
        for(ToDo todo : todoList){
            add(todo);
        }
    }

    public boolean add(ToDo newToDo){
        if(isAvailable(newToDo) && schedule.add(newToDo)){
            Collections.sort(schedule);
            return true;
        } else {
            return false;
        }
    }

    public boolean isAvailable(ToDo t) {
        return isAvailable(t.getStartTime(), t.getEndTime());
    }

    public boolean isAvailable(Time startTime, Time endTime){
        boolean isAvailable = true;
        Iterator<ToDo> itr = schedule.iterator();
        if(startTime.isEmpty() || endTime.isEmpty()){
            return true;
        } else {
            while(isAvailable && itr.hasNext()){
                isAvailable &= !(itr.next().isConflict(startTime, endTime));
            }
            return isAvailable;
        }
    }

    public List<Node> getLocations(){
        List<Node> locations = new LinkedList<>();
        for(ToDo event : schedule){
            if(event.getLocation() != null) {
                locations.add(event.getLocation());
            }
        }
        return locations;
    }

    public List<ToDo> getTodoList(){
        return schedule;
    }

    public ToDo get(int index){
        return schedule.get(index);
    }

    @Override
    public Iterator<ToDo> iterator() {
        return schedule.iterator();
    }
}