/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba3;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author nasry
 */
public class Empresa {

    public static void main(String[] args) throws IOException {
        Scanner lea = new Scanner(System.in);
        EmpleadoManager manager = new EmpleadoManager();
        int opcion = 0;

        do {
            System.out.println("***** MENU PRINCIPAL *****");
            System.out.println("1. Agregar Empleado");
            System.out.println("2. Listar Empleados NO Despedidos");
            System.out.println("3. Agregar Venta a Empleado");
            System.out.println("4. Pagar Empleado");
            System.out.println("5. Despedir Empleado");
            System.out.println("6. Ver reporte de Empleado");
            System.out.println("7. Salir");
            System.out.print("Escoja una opcion: ");
            opcion = lea.nextInt();
            lea.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Nombre: ");
                    String nombre = lea.nextLine();
                    System.out.print("Salario base: ");
                    double salario = lea.nextDouble();
                    lea.nextLine();
                    manager.addEmployee(nombre, salario);
                    System.out.println("Empleado agregado.");
                    break;

                case 2:
                    manager.employeeList();
                    break;

                case 3:
                    System.out.print("Codigo del empleado: ");
                    int codVenta = lea.nextInt();
                    System.out.print("Monto de venta: ");
                    double monto = lea.nextDouble();
                    lea.nextLine();
                    manager.addSaleToEmployee(codVenta, monto);
                    break;

                case 4:
                    System.out.print("Codigo del empleado a pagar: ");
                    int codPago = lea.nextInt();
                    lea.nextLine();
                    manager.payEmployee(codPago);
                    break;

                case 5:
                    System.out.print("Codigo del empleado a despedir: ");
                    int codDesp = lea.nextInt();
                    lea.nextLine();
                    manager.fireEmployee(codDesp);
                    break;

                case 6:
                    System.out.print("Codigo del empleado: ");
                    int codReporte = lea.nextInt();
                    lea.nextLine();
                    manager.printEmployee(codReporte);
                    break;

                case 7:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 7);

        lea.close();
    }
}