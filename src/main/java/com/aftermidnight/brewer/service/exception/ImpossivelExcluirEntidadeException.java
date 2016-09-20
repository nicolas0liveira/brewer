package com.aftermidnight.brewer.service.exception;

public class ImpossivelExcluirEntidadeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ImpossivelExcluirEntidadeException() {

	}
	
	public ImpossivelExcluirEntidadeException(String msg) {
		super(msg);
	}
	
	public ImpossivelExcluirEntidadeException(String msg, Throwable e) {
		super(msg,e);
	}

}
