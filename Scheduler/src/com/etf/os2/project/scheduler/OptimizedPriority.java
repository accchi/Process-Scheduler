package com.etf.os2.project.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;
import com.etf.os2.project.process.Pcb.ProcessState;

public class OptimizedPriority extends Scheduler {

	static final int TS = 30;
	static final int MAX_WAITING_TIME = 5000; // this is unusual case, when new processes are always arriving
	
	private ArrayList<Pcb> waiting;
	//private PriorityQueue<Pcb>[] queues;
	private int numOfQueues;
	private PriorityQueue<Pcb>[][] matrix;
	private int numOfProcesses[];
	
	public OptimizedPriority(int numOfProcessors,int numOfQu) {
		
		numOfQueues = numOfQu;
		numOfProcesses = new int[numOfProcessors];
		waiting = new ArrayList<Pcb>();
		matrix=new PriorityQueue[numOfProcessors][numOfQu];
		
		for(int i = 0; i < numOfProcessors; i++) {
			
			for(int j = 0; j < numOfQueues; j++) {
				
				matrix[i][j] = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
					
	                public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion
	                	
	                	if(p1.getPcbData().getExeTime() > p2.getPcbData().getExeTime())
	    	                return 1;
	    	                
	    	            if(p1.getPcbData().getExeTime() < p2.getPcbData().getExeTime())
	    	                return -1;
	    	                
	    	            return -1; // to put it on beginning
	                }    
	                
	            });  
			}
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
				matrix[p.getAffinity()][p.getPcbData().getPreviousQueue()].remove(p);
				numOfProcesses[p.getAffinity()]--;
				
			}else {
				
				for(int i = 0; i < numOfQueues; i++) {
					
					p = matrix[cpuId][i].poll();
				
					if(p != null) {
						
						waiting.remove(p);
						numOfProcesses[cpuId]--;
						break;
						
					}
				}
				
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
			
			for(int i = 0; i < numOfQueues; i++) {
				
				int mostLoaded;
				
				p = matrix[mostLoaded = findMostLoadedCpu()][i].poll();
			
			
				if(p != null) {
					
					numOfProcesses[mostLoaded]--;
					waiting.remove(p);
					PcbData data = p.getPcbData();
					data.setWaitTime(data.getWaitTime() + Pcb.getCurrentTime() - data.getPutTime());
					p.setTimeslice(((Pcb.getCurrentTime() - data.getPutTime()) / Pcb.getProcessCount()) % 5);
				
					if(p.getTimeslice() == 0) // in case division give result 0
						p.setTimeslice(5);
					
					
					break;
				}
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
		
	
		
		int i = 0;
		
		switch(pcb.getPreviousState()) {
		
		case CREATED:{
			
			if(pcb.getPriority() >= numOfQueues)
				matrix[pcb.getAffinity()][i = numOfQueues - 1].add(pcb);
			else
				matrix[pcb.getAffinity()][i = pcb.getPriority()].add(pcb);
			
			break;
		}
		case BLOCKED:{
			
			int prevQueue = pcb.getPcbData().getPreviousQueue();
			
			if(prevQueue - 1 < 0)
				matrix[pcb.getAffinity()][i = 0].add(pcb);
			else
				matrix[pcb.getAffinity()][i = prevQueue - 1].add(pcb);
				
			break;
		}
		case RUNNING:{
			
			int prevQueue = pcb.getPcbData().getPreviousQueue();
			
			if(prevQueue + 1 >= numOfQueues)
				matrix[pcb.getAffinity()][i = numOfQueues - 1].add(pcb);
			else
				matrix[pcb.getAffinity()][i = prevQueue + 1].add(pcb);
			
			break;
		}
		
		}
		
		numOfProcesses[pcb.getAffinity()]++;
		data.setPutTime(Pcb.getCurrentTime());
		data.setPreviousQueue(i);
		waiting.add(pcb); // list of processes in order they come
		
		

	}
	
	private int findMostLoadedCpu() { // finding cpu with most processes
		
		int maxPro = numOfProcesses[0], maxIndex = 0;
		
			
		for(int i = 1; i < numOfProcesses.length; i ++ ) {
				
			if(maxPro < numOfProcesses[i]) {
					
				maxPro = numOfProcesses[i];
				maxIndex = i;
					
			}
		}
			
		return maxIndex;
			
	}

}



