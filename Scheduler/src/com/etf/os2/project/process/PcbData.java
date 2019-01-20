package com.etf.os2.project.process;

public class PcbData {
	
	private long predictedExecutionTime, getTime; // for SJF
	private int previousQueue; // for MFQS
	private long exeTime, waitTime, putTime ; // for CFS;
	private boolean preempt = false; // for SJF Preempted

	public long getPredictedExecutionTime() {
		return predictedExecutionTime;
	}

	public void setPredictedExecutionTime(long predictedExecutionTime) {
		this.predictedExecutionTime = predictedExecutionTime;
	}

	public int getPreviousQueue() {
		return previousQueue;
	}

	public void setPreviousQueue(int previousQueue) {
		this.previousQueue = previousQueue;
	}

	public long getExeTime() {
		return exeTime;
	}

	public void setExeTime(long exeTime) {
		this.exeTime = exeTime;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	public long getPutTime() {
		return putTime;
	}

	public void setPutTime(long putTime) {
		this.putTime = putTime;
	}

	public long getGetTime() {
		return getTime;
	}

	public void setGetTime(long getTime) {
		this.getTime = getTime;
	}

	public boolean isPreempt() {
		return preempt;
	}

	public void setPreempt(boolean preempt) {
		this.preempt = preempt;
	}
	
	
	
	
	
}
