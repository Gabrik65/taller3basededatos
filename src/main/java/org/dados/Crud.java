package org.dados;

import java.util.Scanner;

public class Crud {
    private final Scanner sc;

    public Crud(Scanner sc) {
        this.sc = sc;
    }

    public void insert() {
        String[][] tabla = tabla();
        String table = tabla[0][0];
        sc.nextLine();

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        Object[] params = new Object[tabla[0].length - 2]; // minus table + pk

        int p = 0;
        for (int i = 2; i < tabla[0].length; i++) {
            String col = tabla[0][i];
            columns.append(col);
            placeholders.append("?");

            System.out.print("Enter " + col + ": ");
            params[p++] = sc.nextLine();

            if (i < tabla[0].length - 1) {
                columns.append(", ");
                placeholders.append(", ");
            }
        }

        String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")";
        int rows = Database.executeUpdate(sql, params);
        System.out.println("Inserted rows: " + rows);
    }


    public void select() {
        String[][] tabla = tabla();
        String table = tabla[0][0];
        String pk = tabla[0][1];
        sc.nextLine();

        System.out.println("1- Select ALL");
        System.out.println("2- Select by ID");
        String opt = sc.nextLine();

        if ("1".equals(opt)) {
            Database.executeQueryAndPrint("SELECT * FROM " + table);
        } else if ("2".equals(opt)) {
            System.out.print("Enter " + pk + ": ");
            String id = sc.nextLine();
            Database.executeQueryAndPrint("SELECT * FROM " + table + " WHERE " + pk + " = ?", id);
        } else {
            System.out.println("Invalid option");
        }
    }


    public void update() {
        String[][] tabla = tabla();
        String table = tabla[0][0];
        String pk = tabla[0][1];
        sc.nextLine();

        System.out.print("Enter " + pk + " to update: ");
        String id = sc.nextLine();

        System.out.println("Choose column to update:");
        int optionNumber = 1;

        for (int i = 2; i < tabla[0].length; i++) {
            System.out.println(optionNumber + "- " + tabla[0][i]);
            optionNumber++;
        }

        boolean isPrestamos = "prestamos".equals(table);
        if (isPrestamos) {
            System.out.println(optionNumber + "- fecha_devolucion (set return date)");
        }

        String opt = sc.nextLine();
        if (!isInt(opt)) {
            System.out.println("Invalid option");
            return;
        }

        int chosen = Integer.parseInt(opt);

        // If user chose the extra prestamos option
        if (isPrestamos && chosen == optionNumber) {
            System.out.print("Enter fecha_devolucion (YYYY-MM-DD) or empty for NULL: ");
            String fecha = sc.nextLine();

            String sql = "UPDATE " + table + " SET fecha_devolucion = ? WHERE " + pk + " = ?";
            Object value = (fecha == null || fecha.isBlank()) ? null : fecha;
            int rows = Database.executeUpdate(sql, value, id);

            System.out.println("Updated rows: " + rows);
            return;
        }

        int normalCount = tabla[0].length - 2;
        if (chosen < 1 || chosen > normalCount) {
            System.out.println("Invalid option");
            return;
        }

        String col = tabla[0][chosen + 1]; // chosen=1 -> meta index 2
        System.out.print("Enter new value for " + col + ": ");
        String newValue = sc.nextLine();

        String sql = "UPDATE " + table + " SET " + col + " = ? WHERE " + pk + " = ?";
        int rows = Database.executeUpdate(sql, newValue, id);

        System.out.println("Updated rows: " + rows);
    }


    public void delete() {
        String[][] tabla = tabla();
        String table = tabla[0][0];
        String pk = tabla[0][1];
        sc.nextLine();

        System.out.print("Enter " + pk + " to delete: ");
        String id = sc.nextLine();

        String sql = "DELETE FROM " + table + " WHERE " + pk + " = ?";
        int rows = Database.executeUpdate(sql, id);

        System.out.println("Deleted rows: " + rows);
    }


    private String[][] tabla() {
        System.out.println("Seleccione una tabla");
        System.out.println("1- Usuario");
        System.out.println("2- Libros");
        System.out.println("3- Prestamos");
        int n = sc.nextInt();

        return switch (n) {
            // {tableName, pkColumn, insert/update columns...}
            case 1 -> new String[][] { { "usuarios", "id_usuario", "nombre", "email" } };
            case 2 -> new String[][] { { "libros", "id_libro", "titulo", "autor" } };
            case 3 -> new String[][] { { "prestamos", "id_prestamo", "id_usuario", "id_libro" } };
            default -> tabla();
        };
    }


    private boolean isInt(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
