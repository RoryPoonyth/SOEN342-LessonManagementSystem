package com.Team5.op_handlers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import com.Team5.operations.Operation;
import com.Team5.operations.RegisterClient;
import com.Team5.operations.RegisterInstructor;

public class Registration {
    public static Map<String, Operation> getRegistrationCommands(Scanner scanner) {
        Map<String, Operation> commands = new LinkedHashMap<>();
        commands.put("Register as Client", new RegisterClient(scanner));
        commands.put("Register as Instructor", new RegisterInstructor(scanner));
        return commands;
    }
}