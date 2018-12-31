package com.etf.os2.project.scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public class SJFScheduler extends Scheduler {

	private PriorityQueue<Pcb>[] queues;
	private double expCoef;
	private int numOfProcessors;
	
	public SJFScheduler(int numOfPro, double expC) {
		
		if(expC < 0) expC = 0; // expC can only be between 0 and 1
		if(expC > 1) expC = 1;
		
		numOfProcessors = numOfPro;
		queues = new PriorityQueue[numOfProcessors];
		expCoef = expC;
		
		for(int i = 0; i < numOfProcessors; i++) {
			
			queues[i] = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
				
                public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion  
                	
                    if(p1.getPcbData().getPredictedExecutionTime() > p2.getPcbData().getPredictedExecutionTime())
                    	return 1;
                    
                    if(p1.getPcbData().getPredictedExecutionTime() < p2.getPcbData().getPredictedExecutionTime())
                    	return -1;
                    
                    return 0;
                }    
                
            });  
		}
	}
	
	@Override
	public Pcb get(int cpuId) {
		
		Pcb p = queues[cpuId].poll();
		
		if(p == null)
			p = queues[findMostLoadedCpu()].poll();
		
		return p;
		
	}

	@Override
	public void put(Pcb pcb) {
		
		if(pcb.getPcbData() == null) { // pcb arrived for first time
			
			pcb.setPcbData(new PcbData());
			queues[findBestCpu()].add(pcb);
			
		}
		else {
			
			pcb.getPcbData().setPredictedExecutionTime((long)(pcb.getPcbData().getPredictedExecutionTime() * (1 - expCoef) + pcb.getExecutionTime() * expCoef));
			queues[pcb.getAffinity()].add(pcb); // put process where he was executed to avoid cache miss
			
		}
	}
	
	private int findBestCpu() { // method for finding where to put new process; load balancing
		
		int minPro = queues[0].size(), minIndex = 0;
		
		for(int i = 1; i < numOfProcessors; i ++ ) {
			
			if(minPro > queues[i].size()) {
				
				minPro = queues[i].size();
				minIndex = i;
				
			}
		}
		
		return minIndex;
		
	}
	
	private int findMostLoadedCpu() { // finding cpu with most processes
		
	int maxPro = queues[0].size(), maxIndex = 0;
		
		for(int i = 1; i < numOfProcessors; i ++ ) {
			
			if(maxPro < queues[i].size()) {
				
				maxPro = queues[i].size();
				maxIndex = i;
				
			}
		}
		
		return maxIndex;
		
	}
	
	
}
