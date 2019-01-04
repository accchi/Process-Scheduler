package com.etf.os2.project.scheduler;


import java.util.LinkedList;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public class MFQScheduler extends Scheduler {

	private long[] timeSlices;
	private LinkedList<Pcb>[] queues;
	
	
	public MFQScheduler(int numOfQ, long[] timeS) {
		
		queues = new LinkedList[numOfQ];
		timeSlices = timeS;
		
		for(int i = 0; i < numOfQ; i++)
			queues[i] = new LinkedList<Pcb>();
		
	}

	@Override
	public Pcb get(int cpuId) {
		
		for(int i = 0; i < queues.length; i++) {
			
			Pcb pcb = queues[i].poll();
			
			if(pcb != null) {
				
				if(pcb.getPcbData() == null)
					pcb.setPcbData(new PcbData());
				
				pcb.getPcbData().setPreviousQueue(i);
				pcb.setTimeslice(timeSlices[i]);
				return pcb;
				
			}
			
		}
		
		return null;
		
	}

	@Override
	public void put(Pcb pcb) {
		
		switch(pcb.getPreviousState()) {
		
		case CREATED:{
			
			if(pcb.getPriority() >= queues.length)
				queues[queues.length - 1].add(pcb);
			else
				queues[pcb.getPriority()].add(pcb);
			
			break;
		}
		case BLOCKED:{
			
			int prevQueue = pcb.getPcbData().getPreviousQueue();
			
			if(prevQueue - 1 < 0)
				queues[0].add(pcb);
			else
				queues[prevQueue - 1].add(pcb);
				
			break;
		}
		case RUNNING:{
			
			int prevQueue = pcb.getPcbData().getPreviousQueue();
			
			if(prevQueue + 1 >= queues.length)
				queues[queues.length - 1].add(pcb);
			else
				queues[prevQueue + 1].add(pcb);
			
			break;
		}
		
		}
		

	}

}
