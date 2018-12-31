package com.etf.os2.project.scheduler;

import com.etf.os2.project.process.Pcb;

public abstract class Scheduler {
    public abstract Pcb get(int cpuId);

    public abstract void put(Pcb pcb);

    public static Scheduler createScheduler(String[] args) { // SJF: 0 - expCoef, 1 - Pre/NonPre; 
    	
		/*switch(args[0]) {
		
		case "SJF":
			
			
		case "MFQS":
			
		case "CFS":
			break;
		
		}*/
		return new SJFScheduler(Integer.parseInt(args[1]), Double.parseDouble(args[2])); // Getting number of processors from length of RUNNING arr
		
    }
}
