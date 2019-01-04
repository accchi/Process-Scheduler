package com.etf.os2.project.scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public abstract class SJFScheduler extends Scheduler {

	protected PriorityQueue<Pcb> queue;
	protected double expCoef;
	
	public SJFScheduler(double expC) {
		
		if(expC < 0) expC = 0; // expC can only be between 0 and 1
		if(expC > 1) expC = 1;
		
		expCoef = expC;
		
		queue = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
				
			public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion  
                	
				/*if(p1.getPcbData().getPredictedExecutionTime() > p2.getPcbData().getPredictedExecutionTime())
                    return 1;
                    
                if(p1.getPcbData().getPredictedExecutionTime() < p2.getPcbData().getPredictedExecutionTime())
                    return -1;
                    
                return 0;*/
				return Long.compare(p1.getPcbData().getPredictedExecutionTime(), p2.getPcbData().getPredictedExecutionTime());
                }    
            });  
	}
	
	
	
	
	
}
