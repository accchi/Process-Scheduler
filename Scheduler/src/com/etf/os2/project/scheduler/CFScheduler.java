package com.etf.os2.project.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.Pcb.ProcessState;
import com.etf.os2.project.process.PcbData;

public class CFScheduler extends Scheduler {
	
	static final int TS = 10;
	static final int MAX_WAITING_TIME = 3000; // this is unusual case, when new processes are always arriving
	
	private ArrayList<Pcb> waiting;
	private PriorityQueue<Pcb> queue;
	
	
	public CFScheduler() {
		
		waiting = new ArrayList<Pcb>();
		queue = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
			
			public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion  
                
				if(p1.getPcbData().getExeTime() > p2.getPcbData().getExeTime())
	                return 1;
	                
	            if(p1.getPcbData().getExeTime() < p2.getPcbData().getExeTime())
	                return -1;
	                
	            return -1; // to put him on beginning
				//return Long.compare(p1.getPcbData().getExeTime(), p2.getPcbData().getExeTime());
			}    
       });  
	}

	@Override
	public Pcb get(int cpuId) {
		
		Pcb p = null;
		
		if(!waiting.isEmpty())
			p = waiting.get(0);
		
		
		if(p != null) {
			
			if(Pcb.getCurrentTime() - p.getPcbData().getPutTime() > MAX_WAITING_TIME) {
				
				waiting.remove(0);
				queue.remove(p);
				
			}else {
				
				p = queue.poll();
				waiting.remove(p);
				
			}	
		}
		
		
		if(p != null) {
			
			PcbData data = p.getPcbData();
			data.setWaitTime(data.getWaitTime() + Pcb.getCurrentTime() - data.getPutTime());
			//p.setTimeslice(data.getWaitTime() / Pcb.getProcessCount()); // it is more fair this way
			p.setTimeslice((Pcb.getCurrentTime() - data.getPutTime()) / Pcb.getProcessCount() );
			
			if(p.getTimeslice() == 0) // in case division give result 0
				p.setTimeslice(TS);
			
		}
		
		return p;
	}

	@Override
	public void put(Pcb pcb) {
		
		if(pcb.getPcbData() == null)
			pcb.setPcbData(new PcbData());
		
		PcbData data = pcb.getPcbData();
		
		if(pcb.getPreviousState() == ProcessState.CREATED || pcb.getPreviousState() == ProcessState.BLOCKED) { // first time in this burst
			
			data.setWaitTime(0);
			data.setExeTime(0);
			
		}
		else {
			
			//data.setExeTime(pcb.getExecutionTime());
			data.setExeTime(data.getExeTime()+pcb.getExecutionTime());
			
		}
		
		data.setPutTime(Pcb.getCurrentTime());
		queue.add(pcb);
		waiting.add(pcb); // list of processes in order they come

	}

}
