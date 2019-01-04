package com.etf.os2.project.scheduler;

import java.util.Arrays;

import com.etf.os2.project.process.Pcb;

public abstract class Scheduler {
    public abstract Pcb get(int cpuId);

    public abstract void put(Pcb pcb);

    public static Scheduler createScheduler(String[] args) { // SJF: 1 - expCoef, 2 - Pre/NonPre; 
    	
		switch(args[0]) {
		
		case "SJF":{
			
			if(Boolean.parseBoolean(args[2]))
				return new SJFPScheduler(Double.parseDouble(args[1])); // Preemptive
			else
				return new SJFNPScheduler(Double.parseDouble(args[1])); // Non-preemptive
						
		}	
		case "MFQS":{
			
			int N = Integer.parseInt(args[1]);
			String[] quants = Arrays.copyOfRange(args, 2, N + 2);
			long[] timeSlices = Arrays.stream(quants).mapToLong(Long::parseLong).toArray(); // convert string arr to long arr
			
			return new MFQScheduler(N, timeSlices);
		}
		case "CFS":
			
			return new CFScheduler();
		}
		
    	
		return null;
    }
}
