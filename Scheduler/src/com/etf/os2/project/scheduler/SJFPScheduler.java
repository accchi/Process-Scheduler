package com.etf.os2.project.scheduler;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;
import com.etf.os2.project.process.Process;

public class SJFPScheduler extends SJFScheduler {
	
	static final int MAX_WAITING_TIME = 1000;
	static final int PREDICTED = 300;
	
	public SJFPScheduler(double expC) {
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
		
		if(p != null)
			p.getPcbData().setGetTime(Pcb.getCurrentTime());

		return p;
		
	}

	@Override
	public void put(Pcb pcb) {
		
		if(pcb.getPcbData() == null) { // pcb arrived for the first time
			
			pcb.setPcbData(new PcbData());
			pcb.getPcbData().setPredictedExecutionTime(PREDICTED);
			
		}
		else {
			//this can be done with 2 formulas
			if(pcb.getPcbData().isPreempt() && pcb.getPcbData().getPredictedExecutionTime() - pcb.getExecutionTime() > 0) {
				pcb.getPcbData().setPredictedExecutionTime((long)pcb.getPcbData().getPredictedExecutionTime() - pcb.getExecutionTime());
				pcb.getPcbData().setPreempt(false);
			}
			else
				pcb.getPcbData().setPredictedExecutionTime((long)(pcb.getPcbData().getPredictedExecutionTime() * (1 - expCoef) + pcb.getExecutionTime() * expCoef));
			
		}
		
		pcb.getPcbData().setPutTime(Pcb.getCurrentTime()); // update put time for pcb
		
		// try to preempt cpu where process already executed if it's possible
		if(Pcb.RUNNING[pcb.getAffinity()] != Pcb.IDLE && Pcb.RUNNING[pcb.getAffinity()].getPcbData().getPredictedExecutionTime() - (Pcb.getCurrentTime() - Pcb.RUNNING[pcb.getAffinity()].getPcbData().getGetTime())  > pcb.getPcbData().getPredictedExecutionTime()) {
			
			Pcb.RUNNING[pcb.getAffinity()].preempt();
			Pcb.RUNNING[pcb.getAffinity()].getPcbData().setPreempt(true);
			queue.add(pcb);
			waiting.add(pcb); 
			
			return;
			
		} 

		for(int i = 0; i < Pcb.RUNNING.length; i++) {

			if(Pcb.RUNNING[i] != Pcb.IDLE && Pcb.RUNNING[i].getPcbData().getPredictedExecutionTime() - (Pcb.getCurrentTime() - Pcb.RUNNING[i].getPcbData().getGetTime()) > pcb.getPcbData().getPredictedExecutionTime()) {

				Pcb.RUNNING[i].preempt();
				Pcb.RUNNING[i].getPcbData().setPreempt(true);
				break;
				
			} 
			
		}
		

		
		queue.add(pcb);
		waiting.add(pcb);
		
	}
}
