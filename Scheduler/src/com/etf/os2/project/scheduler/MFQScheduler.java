package com.etf.os2.project.scheduler;


import java.util.ArrayList;
import java.util.LinkedList;


import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;

public class MFQScheduler extends Scheduler {

	static final int MAX_WAITING_TIME = 3000;
	
	private long[] timeSlices;
	private LinkedList<Pcb>[] queues;
	private ArrayList<Pcb> waiting;
	
	
	public MFQScheduler(int numOfQ, long[] timeS) {
		
		queues = new LinkedList[numOfQ];
		waiting = new ArrayList<Pcb>();
		timeSlices = timeS;
		
		for(int i = 0; i < numOfQ; i++)
			queues[i] = new LinkedList<Pcb>();
		
	}

	@Override
	public Pcb get(int cpuId) {
		
		age(); // for eliminating starvation 
		
		int notEmpty = 0;
		for(int i = 0; i < queues.length; i++)
			if(!queues[i].isEmpty()) {
				
				notEmpty = i;
				break;
				
			}
		
		for(Pcb p : queues[notEmpty])
			if(p.getAffinity() == cpuId) {
				
				queues[notEmpty].remove(p);
				waiting.remove(p);
				return p;
				
			}
		
		
		for(int i = 0; i < queues.length; i++) {
			
			Pcb pcb = queues[i].poll();
				
			if(pcb != null) {
				
				waiting.remove(pcb);
				return pcb;
				
			}
			
		}
		return null;
		
	}

	@Override
	public void put(Pcb pcb) {
		
		if(pcb.getPcbData() == null)
			pcb.setPcbData(new PcbData());
		
		pcb.getPcbData().setPutTime(Pcb.getCurrentTime()); // update put time for pcb
		waiting.add(pcb); // in order they come
		
		int i = 0;
		
		switch(pcb.getPreviousState()) {
		
		case CREATED:{
			
			if(pcb.getPriority() >= queues.length)
				queues[i = queues.length - 1].add(pcb);
			else
				queues[i =pcb.getPriority()].add(pcb);
			
			break;
		}
		case BLOCKED:{
			
			int prevQueue = pcb.getPcbData().getPreviousQueue();
			
			if(prevQueue - 1 < 0)
				queues[i = 0].add(pcb);
			else
				queues[i = prevQueue - 1].add(pcb);
				
			break;
		}
		case RUNNING:{
			
			int prevQueue = pcb.getPcbData().getPreviousQueue();
			
			if(prevQueue + 1 >= queues.length)
				queues[i = queues.length - 1].add(pcb);
			else
				queues[i = prevQueue + 1].add(pcb);
			
			break;
		}
		
		}
		
		pcb.getPcbData().setPreviousQueue(i);
		pcb.setTimeslice(timeSlices[i]);
		

	}
	
	void age() { 
		
		long currentTime = Pcb.getCurrentTime();
		
		while(!waiting.isEmpty() && currentTime - waiting.get(0).getPcbData().getPutTime() > MAX_WAITING_TIME) {
			
			Pcb pcb = waiting.remove(0);
			pcb.getPcbData().setPutTime(currentTime);
			
			int prevQueue = pcb.getPcbData().getPreviousQueue(); // update put time
			queues[prevQueue].remove(pcb);
			
			if(prevQueue != 0) {
				
				
				queues[prevQueue - 1].add(pcb);
				pcb.getPcbData().setPreviousQueue(prevQueue - 1);
				pcb.setTimeslice(timeSlices[prevQueue - 1]);
				
			}
			else {
				
				queues[prevQueue].add(pcb);
				pcb.setTimeslice(timeSlices[0]);
				
			}
			
			waiting.add(pcb); // updated and again added
			
		}
		
	}

}
