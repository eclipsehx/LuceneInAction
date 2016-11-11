package lia.tika;

import java.io.PrintStream;
import java.io.PrintWriter;

// From chapter 7

public class DocumentHandlerException extends Exception {

	private Throwable cause;

	/**
	 * Default constructor.
	 */
	public DocumentHandlerException() {
		super();
	}

	/**
	 * Constructs with message.
	 */
	public DocumentHandlerException(String message) {
		super(message);
	}

	/**
	 * Constructs with chained exception.
	 */
	public DocumentHandlerException(Throwable cause) {
		super(cause.toString());
		this.cause = cause;
	}

	/**
	 * Constructs with message and exception.
	 */
	public DocumentHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Retrieves nested exception.
	 */
	public Throwable getException() {
		return cause;
	}

	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream ps) {
		synchronized (ps) {
			super.printStackTrace(ps);
			if (cause != null) {
				ps.println("--- Nested Exception ---");
				cause.printStackTrace(ps);
			}
		}
	}

	public void printStackTrace(PrintWriter pw) {
		synchronized (pw) {
			super.printStackTrace(pw);
			if (cause != null) {
				pw.println("--- Nested Exception ---");
				cause.printStackTrace(pw);
			}
		}
	}
}
