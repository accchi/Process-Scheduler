package com.etf.os2.project.scheduler;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public class SJFNPScheduler extends SJFScheduler {

	public SJFNPScheduler(double expC) {
		super(expC);
	}
	
	@Override
	public Pcb get(int cpuId) {
		
		Pcb p = queue.poll();

		return p;
		
	}

	@Override
	public void put(Pcb pcb) {
		
		if(pcb.getPcbData() == null) { // pcb arrived for first time
			
			pcb.setPcbData(new PcbData());
			//pcb.getPcbData().setPredictedExecutionTime(1);
			queue.add(pcb);
			
		}
		else {
			
			pcb.getPcbData().setPredictedExecutionTime((long)(pcb.getPcbData().getPredictedExecutionTime() * (1 - expCoef) + pcb.getExecutionTime() * expCoef));
			queue.add(pcb); // put process where he was executed to avoid cache miss
			
		}
	}

}
