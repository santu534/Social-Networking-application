package com.creative.cutebond.callbacks;


public interface IItemHandler {

	public void onFinish(Object results, int requestType);

	public void onError(String errorCode, int requestType);

}