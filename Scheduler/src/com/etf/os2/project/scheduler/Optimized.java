package com.etf.os2.project.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;
import com.etf.os2.project.process.Pcb.ProcessState;

public class Optimized extends Scheduler {

	
	static final int TS = 20;
	static final int MAX_WAITING_TIME = 5000; // this is unusual case, when new processes are always arriving
	
	private ArrayList<Pcb> waiting;
	private PriorityQueue<Pcb>[] queues;
	
	public Optimized(int numOfProcessors) {
		
		waiting = new ArrayList<Pcb>();
		queues = new PriorityQueue[numOfProcessors];
		
		for(int i = 0; i < numOfProcessors; i++) {
			
			queues[i] = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
				
                public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion
                	
                	if(p1.getPcbData().getExeTime() > p2.getPcbData().getExeTime())
    	                return 1;
    	                
    	            if(p1.getPcbData().getExeTime() < p2.getPcbData().getExeTime())
    	                return -1;
    	                
    	            return -1; // to put him on beginning
                }    
                
            });  
		}
		
		
	}

	@Override
	public Pcb get(int cpuId) {
		
		Pcb p = null;
		
		if(!waiting.isEmpty())
			p = waiting.get(0);
		
		
		if(p != null) {
			
			if(Pcb.getCurrentTime() - p.getPcbData().getPutTime() > MAX_WAITING_TIME) {
				
				waiting.remove(0);
				queues[p.getAffinity()].remove(p);
				
			}else {
				
				p = queues[cpuId].poll();
				
				if(p != null)
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
			
		}else {
			
			p = queues[findMostLoadedCpu()].poll();
			
			if(p != null) {
				
				waiting.remove(p);
				PcbData data = p.getPcbData();
				data.setWaitTime(data.getWaitTime() + Pcb.getCurrentTime() - data.getPutTime());
				p.setTimeslice(((Pcb.getCurrentTime() - data.getPutTime()) / Pcb.getProcessCount()) % 5);
				
				if(p.getTimeslice() == 0) // in case division give result 0
					p.setTimeslice(5);
				
				
			}
			
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
		queues[pcb.getAffinity()].add(pcb);
		waiting.add(pcb); // list of processes in order they come

	}
	
	private int findMostLoadedCpu() { // finding cpu with most processes
		
		int maxPro = queues[0].size(), maxIndex = 0;
			
			for(int i = 1; i < queues.length; i ++ ) {
				
				if(maxPro < queues[i].size()) {
					
					maxPro = queues[i].size();
					maxIndex = i;
					
				}
			}
			
			return maxIndex;
			
		}

}
