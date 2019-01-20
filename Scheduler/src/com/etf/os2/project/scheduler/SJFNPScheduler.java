package com.etf.os2.project.scheduler;

import java.util.ArrayList;
import java.util.LinkedList;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public class SJFNPScheduler extends SJFScheduler {
	
	static final int PREDICTED = 30;
	static final int MAX_WAITING_TIME = 400;
	
	
	public SJFNPScheduler(double expC) {
		super(expC);
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
		
		return p;
		
	}

	@Override
	public void put(Pcb pcb) {
		
		if(pcb.getPcbData() == null) { // pcb arrived for first time
			
			pcb.setPcbData(new PcbData());
			pcb.getPcbData().setPredictedExecutionTime(PREDICTED);
			
		}
		else {
			
			pcb.getPcbData().setPredictedExecutionTime((long)(pcb.getPcbData().getPredictedExecutionTime() * (1 - expCoef) + pcb.getExecutionTime() * expCoef));
			
		}
		
		pcb.getPcbData().setPutTime(Pcb.getCurrentTime());
		queue.add(pcb);
		waiting.add(pcb); // list of processes in order they come
		
	}

}
