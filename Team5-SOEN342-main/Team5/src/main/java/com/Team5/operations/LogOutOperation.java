package com.Team5.operations;

public class LogOutOperation implements Operation {
    @SuppressWarnings("unused")
	private Object user;

    public LogOutOperation(Object user) {
        this.user = user;
    }

    @Override
    public void execute() {
    }
}
