package com.etf.os2.project.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.etf.os2.project.process.Pcb;
import com.etf.os2.project.process.PcbData;
import com.etf.os2.project.process.Process;

public class MyScheduler extends Scheduler {

	static final int PREDICTED = 400;
	static  int MAX_WAITING_TIME = 1000;
	private ArrayList<Pcb> waiting;
	private PriorityQueue<Pcb>[] queues;
	private double expCoef;
	private int numOfProcessors;
	
	
	public MyScheduler(int numOfPro, double expC) {
		
		if(expC < 0) expC = 0; // expC can only be between 0 and 1
		if(expC > 1) expC = 1;
		
		numOfProcessors = numOfPro;
		queues = new PriorityQueue[numOfProcessors];
		expCoef = expC;
		waiting = new ArrayList<Pcb>();
		
		for(int i = 0; i < numOfProcessors; i++) {
			
			queues[i] = new PriorityQueue<Pcb>(new Comparator<Pcb>() { 
				
                public int compare(Pcb p1, Pcb p2) { // written like this not like p1.long - p2.long because of conversion
                	
                	return Long.compare(p1.getPcbData().getPredictedExecutionTime(), p2.getPcbData().getPredictedExecutionTime());
                }    
                
            });  
		}
	}
	
	@Override
	public Pcb get(int cpuId) {
		
		Pcb p = null;
		
		if(!waiting.isEmpty())
			p = waiting.get(0);
		
		//MAX_WAITING_TIME = 30 * Pcb.getProcessCount();
		
		if(p != null) {
			
			if(Pcb.getCurrentTime() - p.getPcbData().getPutTime() > MAX_WAITING_TIME) {
				
				waiting.remove(0);
				queues[p.getAffinity()].remove(p);
				
			}else {
				
				p = queues[cpuId].poll();
				waiting.remove(p);
				
			}	
		}
		
		//p = queues[cpuId].poll();
		
		if(p == null)
			p = queues[findMostLoadedCpu()].poll();
		
		if(p != null)
			p.getPcbData().setGetTime(Process.getCurrentTime());
		
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
		waiting.add(pcb);
		// try if cpu where process last executed can be preemt
		if(Pcb.RUNNING[pcb.getAffinity()] != Pcb.IDLE && Process.getCurrentTime() - Pcb.RUNNING[pcb.getAffinity()].getPcbData().getGetTime() < pcb.getPcbData().getPredictedExecutionTime()) {
			
			Pcb.RUNNING[pcb.getAffinity()].preempt();
			queues[pcb.getAffinity()].add(pcb);
			
			return;
			
		}
		
	
		// if not find best you can find
		if(Pcb.RUNNING[findBestCpu()] != Pcb.IDLE && Process.getCurrentTime() - Pcb.RUNNING[findBestCpu()].getPcbData().getGetTime() < pcb.getPcbData().getPredictedExecutionTime()) {
			
			Pcb.RUNNING[findBestCpu()].preempt();
			queues[findBestCpu()].add(pcb);
			
			return;
			
		}
		// else try any
		for(int i = 0; i < Pcb.RUNNING.length; i++) {
			
			if(Pcb.RUNNING[i] != Pcb.IDLE && Process.getCurrentTime() - Pcb.RUNNING[i].getPcbData().getGetTime() < pcb.getPcbData().getPredictedExecutionTime()) {
				
				Pcb.RUNNING[i].preempt();
				queues[i].add(pcb);
				return;
				
			}
			
		}
		
		queues[findBestCpu()].add(pcb);
		//queues[pcb.getAffinity()].add(pcb);
		
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
