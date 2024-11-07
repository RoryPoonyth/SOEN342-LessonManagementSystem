package com.Team5.op_handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.Team5.operations.Operation;
import com.Team5.operations.RegisterClient;
import com.Team5.operations.RegisterInstructor;

public class Registration {
    public static Map<String, Operation> getRegistrationCommands(Scanner scanner) {
        Map<String, Operation> commands = new HashMap<>();
        commands.put("Register as Instructor", new RegisterInstructor(scanner));
        commands.put("Register as Client", new RegisterClient(scanner));
        return commands;
    }
}
