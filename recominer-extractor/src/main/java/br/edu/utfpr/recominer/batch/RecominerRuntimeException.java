package br.edu.utfpr.recominer.batch;

public class RecominerRuntimeException extends RuntimeException {

	public RecominerRuntimeException() {
	}
	
	public RecominerRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RecominerRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecominerRuntimeException(String message) {
		super(message);
	}

	public RecominerRuntimeException(Throwable cause) {
		super(cause);
	}
	
}
