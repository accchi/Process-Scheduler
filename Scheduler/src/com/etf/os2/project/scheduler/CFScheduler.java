package com.etf.os2.project.scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.Pcb.ProcessState;
import com.etf.os2.project.process.PcbData;

public class CFScheduler extends Scheduler {
	
	static final int TS = 10;
	
	private PriorityQueue<Pcb> queue;
	
	public CFScheduler() {
		
		queue = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
			
			public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion  
                	
				return Long.compare(p1.getPcbData().getExeTime(), p2.getPcbData().getExeTime());
			}    
       });  
	}

	@Override
	public Pcb get(int cpuId) {
		
		Pcb p = queue.poll();
		
		if(p != null) {
			
			PcbData data = p.getPcbData();
			data.setWaitTime(data.getWaitTime() + System.currentTimeMillis() - data.getPutTime());
			p.setTimeslice(data.getWaitTime() / Pcb.getProcessCount());
			
			if(p.getTimeslice() == 0) // in case division give result 0
				p.setTimeslice(TS);
			
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
		
		data.setPutTime(System.currentTimeMillis());
		queue.add(pcb);

	}

}
