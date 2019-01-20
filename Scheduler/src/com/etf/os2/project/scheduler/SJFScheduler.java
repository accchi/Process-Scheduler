package com.etf.os2.project.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public abstract class SJFScheduler extends Scheduler {

	protected PriorityQueue<Pcb> queue;
	protected ArrayList<Pcb> waiting; // list of processes in order they come in scheduler
	protected double expCoef;
	
	
	public SJFScheduler(double expC) {
		
		if(expC < 0) expC = 0; // expC can only be between 0 and 1
		if(expC > 1) expC = 1;
		
		expCoef = expC;
		waiting = new ArrayList<Pcb>();
		queue = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
				
			public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion 
				
				if(p1.getPcbData().getPredictedExecutionTime() > p2.getPcbData().getPredictedExecutionTime())
                return 1;
                
            	if(p1.getPcbData().getPredictedExecutionTime() < p2.getPcbData().getPredictedExecutionTime())
                return -1;
                
            	return -1; // to put him on beginning
            	
				//return Long.compare(p1.getPcbData().getPredictedExecutionTime(), p2.getPcbData().getPredictedExecutionTime());
                }    
            });  
	}
	
	
	
	
	
}
