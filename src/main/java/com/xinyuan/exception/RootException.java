//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xinyuan.exception;

import org.apache.log4j.Logger;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class RootException extends Exception {
    private static final long serialVersionUID = 1L;
    private Throwable rootCause;
    private Logger log = Logger.getLogger(RootException.class);

    public RootException() {
    }

    public RootException(String s) {
        super(s);
    }

    public RootException(String s, Throwable rootCause) {
        super(s);
        this.rootCause = rootCause;
    }

    public RootException(Throwable rootCause) {
        this.rootCause = rootCause;
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        String msg = super.getMessage();
        if (msg != null) {
            sb.append(msg);
            if (this.rootCause != null) {
                sb.append(": ");
            }
        }

        if (this.rootCause != null) {
            sb.append("root cause: " + (this.rootCause.getMessage() == null ? this.rootCause.toString() : this.rootCause.getMessage()));
        }

        return sb.toString();
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (this.rootCause != null) {
            PrintStream var1 = System.err;
            synchronized (System.err) {
                System.err.println("\nRoot cause:");
                this.rootCause.printStackTrace();
            }
        }

    }

    @Override
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (this.rootCause != null) {
            synchronized (ps) {
                ps.println("\nRoot cause:");
                this.rootCause.printStackTrace(ps);
            }
        }

    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (this.rootCause != null) {
            synchronized (pw) {
                pw.println("\nRoot cause:");
                this.rootCause.printStackTrace(pw);
            }
        }

    }

    public void saveLog() {
        this.log.error(this.getMessage());
    }
}
