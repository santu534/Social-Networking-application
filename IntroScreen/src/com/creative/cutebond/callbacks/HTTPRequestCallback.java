package com.creative.cutebond.callbacks;

public interface HTTPRequestCallback {

	public void onComplete(int requestId, String response);

	public void onError(int requestId, String errorData);

}
