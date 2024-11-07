package com.Team5.core;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Console {
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printTable(List<?> list, List<String> excludedProperties) {
        if (list == null || list.isEmpty()) {
            System.out.println("The list is empty or null.");
            return;
        }

        Object firstObject = list.get(0);
        Class<?> clazz = firstObject.getClass();

        List<String> headers = new ArrayList<>();
        List<Integer> maxWidths = new ArrayList<>();

        headers.add("ID");
        maxWidths.add("ID".length());

        for (Method method : clazz.getDeclaredMethods()) {
            if (isGetter(method)) {
                String propertyName = getPropertyName(method);
                if (!excludedProperties.contains(propertyName)) {
                    headers.add(propertyName);
                    maxWidths.add(propertyName.length());
                }
            }
        }

        for (Object obj : list) {
            try {
                int id = getId(obj);
                maxWidths.set(0, Math.max(maxWidths.get(0), String.valueOf(id).length()));

                for (Method method : clazz.getDeclaredMethods()) {
                    if (isGetter(method)) {
                        String propertyName = getPropertyName(method);
                        if (!excludedProperties.contains(propertyName)) {
                            Object value = method.invoke(obj);
                            int width = (value != null ? formatValue(value).length() : 4);
                            int index = headers.indexOf(propertyName);
                            if (index != -1 && width > maxWidths.get(index)) {
                                maxWidths.set(index, width);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        printFormattedRow(headers, maxWidths);

        for (Object obj : list) {
            try {
                List<String> row = new ArrayList<>();
                int id = getId(obj);
                row.add(String.valueOf(id));

                for (Method method : clazz.getDeclaredMethods()) {
                    if (isGetter(method)) {
                        String propertyName = getPropertyName(method);
                        if (!excludedProperties.contains(propertyName)) {
                            Object value = method.invoke(obj);
                            row.add(formatValue(value));
                        }
                    }
                }
                printFormattedRow(row, maxWidths);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String formatValue(Object value) {
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (value instanceof Date) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(value);
        }
        return value != null ? value.toString() : "null";
    }

    private static void printFormattedRow(List<String> row, List<Integer> maxWidths) {
        StringBuilder formattedRow = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            formattedRow.append(String.format("%-" + maxWidths.get(i) + "s\t", row.get(i)));
        }
        System.out.println(formattedRow.toString().trim());
    }

    private static boolean isGetter(Method method) {
        return !method.getName().equals("getId") && method.getName().startsWith("get")
                && method.getParameterCount() == 0;
    }

    private static String getPropertyName(Method method) {
        String name = method.getName().substring(3);
        StringBuilder formattedName = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c) && formattedName.length() > 0) {
                formattedName.append(" ");
            }
            formattedName.append(c);
        }
        return formattedName.toString().substring(0, 1).toUpperCase() + formattedName.toString().substring(1);
    }

    private static Integer getId(Object obj) throws Exception {
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("getId") && method.getReturnType() == Integer.class) {
                return (Integer) method.invoke(obj);
            }
        }
        throw new IllegalArgumentException("No valid getId method found.");
    }
}
