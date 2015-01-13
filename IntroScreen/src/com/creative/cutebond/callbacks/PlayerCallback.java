package com.creative.cutebond.callbacks;


public interface PlayerCallback {

	public void onMediaStart();
	
	public void onMediaFinish();

	public void onMediaError(String errorData);
	
}
