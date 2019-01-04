package com.etf.os2.project.scheduler;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;
import com.etf.os2.project.process.Process;

public class SJFPScheduler extends SJFScheduler {

	public SJFPScheduler(double expC) {
		super(expC);
	}

	@Override
	public Pcb get(int cpuId) {
		
		Pcb p = queue.poll();
		
		if(p != null)
			p.getPcbData().setGetTime(Process.getCurrentTime());

		return p;
		
	}

	@Override
	public void put(Pcb pcb) {
		
		if(pcb.getPcbData() == null) { // pcb arrived for the first time
			
			pcb.setPcbData(new PcbData());
			pcb.getPcbData().setPredictedExecutionTime(10);
			
		}
		else {
			
			pcb.getPcbData().setPredictedExecutionTime((long)(pcb.getPcbData().getPredictedExecutionTime() * (1 - expCoef) + pcb.getExecutionTime() * expCoef));
			
		}
		
		// try to preempt cpu where process already executed if it's possible
		if(Pcb.RUNNING[pcb.getAffinity()] != Pcb.IDLE && Process.getCurrentTime() - Pcb.RUNNING[pcb.getAffinity()].getPcbData().getGetTime() < pcb.getPcbData().getPredictedExecutionTime()) {
			
			Pcb.RUNNING[pcb.getAffinity()].preempt();
			queue.add(pcb);
			
			return;
			
		}
		
		for(int i = 0; i < Pcb.RUNNING.length; i++) {
			
			if(Pcb.RUNNING[i] != Pcb.IDLE && Process.getCurrentTime() - Pcb.RUNNING[i].getPcbData().getGetTime() < pcb.getPcbData().getPredictedExecutionTime()) {
				
				Pcb.RUNNING[i].preempt();
				break;
				
			}
			
		}
		
		queue.add(pcb);
		
		
	}

}
