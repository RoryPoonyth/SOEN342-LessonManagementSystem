package com.Team5.op_handlers;

import com.Team5.enums.UserType;
import com.Team5.models.*;
import com.Team5.operations.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Command {
    public static Map<String, Operation> getCommands(UserType userType, Object user, Scanner scanner) {
        Map<String, Operation> commands = new LinkedHashMap<>();


        switch (userType) {
            case INSTRUCTOR:
                commands.put("Accept an Offering", new AcceptOffering((Instructor) user, scanner));
                commands.put("View/Edit Assigned Offering(s)", new ViewEditOfferings((Instructor) user, scanner));
                break;
            case CLIENT:
            	commands.put("Book a Lesson", new CreateBooking((Client) user, scanner));
                commands.put("View/Edit Current Booking(s)", new ViewEditBookings((Client) user, scanner));
                commands.put("Add a Dependent", new AddDependent((Client) user, scanner));
                commands.put("Remove a Dependent", new RemoveDependent((Client) user, scanner));
                break;
            case ADMINISTRATOR:
                commands.put("Create a Lesson", new CreateLesson((Administrator) user, scanner));
                commands.put("View/Cancel Bookings", new AdminViewCancelBookings((Administrator) user, scanner));
                commands.put("Delete a User", new DeleteUser((Administrator) user, scanner));
                commands.put("Manage Locations", new ManageLocations((Administrator) user, scanner));
                break;
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }

        commands.put("Log Out", new LogOutOperation(user));

        return commands;
    }
}
