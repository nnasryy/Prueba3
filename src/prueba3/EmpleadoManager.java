/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prueba3;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author nasry
 */
public class EmpleadoManager {

    //El mismo programa daria el codigo
    /*
    Formato:
    1- File Codigos.emp:
    int code -> 4 bytes Mantener
    
    2- File Empleados.emp:
    int code 
    String name
    double salario
    long fechaContratacion -> Cuando se crea el empleado
    long fechaDespido
     */
    private RandomAccessFile rcods, remps;
 
    public EmpleadoManager() {
        File mf = new File("company");
        mf.mkdir();
        try {
            rcods = new RandomAccessFile("company/codigo.emp", "rw");
            remps = new RandomAccessFile("company/empleado.emp", "rw");
            initCodes();
        } catch (IOException e) {
            System.out.println("Error al inicializar los archivos: " + e.getMessage());
        }
    }
 
    private void initCodes() throws IOException {
        if (rcods.length() == 0) {
            rcods.writeInt(1);
        }
    }
 
    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code + 1);
        return code;
    }
 
    public void addEmployee(String name, double salary) throws IOException {
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        createEmployeeFolder(code);
    }
 
    private String employeeFolder(int code) {
        return "company/empleado" + code;
    }
 
    private RandomAccessFile salesFilefor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String path = dirPadre + "/ventas" + yearActual + ".emp";
        return new RandomAccessFile(path, "rw");
    }
 
    private RandomAccessFile billsFilefor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        String path = dirPadre + "/recibos.emp";
        return new RandomAccessFile(path, "rw");
    }
 
    private void createSalesFileFor(int code) throws IOException {
        RandomAccessFile ryear = salesFilefor(code);
        if (ryear.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                ryear.writeDouble(0);
                ryear.writeBoolean(false);
            }
        }
        ryear.close();
    }
 
    private void createEmployeeFolder(int code) throws IOException {
        File edir = new File(employeeFolder(code));
        edir.mkdir();
        createSalesFileFor(code);
    }
 
    public void employeeList() throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int code = remps.readInt();
            String name = remps.readUTF();
            double sal = remps.readDouble();
            Date fecha = new Date(remps.readLong());
            if (remps.readLong() == 0) {
                System.out.println(code + " - " + name + " - Lps. " + sal + " Contratado el: " + fecha);
            }
        }
    }
 
    private boolean isEmployeeActive(int code) throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int codeI = remps.readInt();
            long pos = remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(8);
            remps.skipBytes(8);
            long fechaDespido = remps.readLong();
            if (fechaDespido == 0 && codeI == code) {
                remps.seek(pos);
                return true;
            }
        }
        return false;
    }
 
    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            String name = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a " + name);
            return true;
        }
        System.out.println("No se pudo despedir al empleado");
        return false;
    }
 
    public void addSaleToEmployee(int code, double ven) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("Empleado no encontrado");
            return;
        }
        RandomAccessFile sales = salesFilefor(code);
        int pos = Calendar.getInstance().get(Calendar.MONTH) * 9;
        sales.seek(pos);
        double monto = sales.readDouble();
        sales.seek(pos);
        sales.writeDouble(monto + ven);
        sales.close();
    }
 
    private boolean isEmployeePayed(int code) throws IOException {
        RandomAccessFile sales = salesFilefor(code);
        int mes = Calendar.getInstance().get(Calendar.MONTH);
        int pos = mes * 9;
        sales.seek(pos);
        sales.skipBytes(8);
        boolean pagado = sales.readBoolean();
        sales.close();
        return pagado;
    }
 
    public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code) || isEmployeePayed(code)) {
            System.out.println("No se pudo pagar");
            return;
        }
 
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int mes = Calendar.getInstance().get(Calendar.MONTH);
 
        RandomAccessFile sales = salesFilefor(code);
        int posVentas = mes * 9;
        sales.seek(posVentas);
        double ventas = sales.readDouble();
 
        isEmployeeActive(code);
        String name = remps.readUTF();
        double salarioBase = remps.readDouble();
 
        double sueldo = salarioBase + (ventas * 0.10);
        double deduccion = sueldo * 0.035;
        double total = sueldo - deduccion;
 
        RandomAccessFile bills = billsFilefor(code);
        bills.seek(bills.length());
        bills.writeLong(Calendar.getInstance().getTimeInMillis());
        bills.writeDouble(sueldo);
        bills.writeDouble(deduccion);
        bills.writeInt(year);
        bills.writeInt(mes);
        bills.close();
 
        sales.seek(posVentas + 8);
        sales.writeBoolean(true);
        sales.close();
 
        System.out.printf("Empleado %s se le pago Lps. %.2f%n", name, total);
    }
 
    public void printEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("Empleado no encontrado o inactivo");
            return;
        }
 
        String name = remps.readUTF();
        double salario = remps.readDouble();
        Date fechaContratacion = new Date(remps.readLong());
 
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaContratacion);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int mes = cal.get(Calendar.MONTH) + 1;
        int anio = cal.get(Calendar.YEAR);
 
        System.out.println("Codigo: " + code + " Nombre: " + name + " Salario: " + salario
                + " Fecha de contratacion: " + dia + "/" + mes + "/" + anio);
 
        RandomAccessFile sales = salesFilefor(code);
        sales.seek(0);
        double totalVentas = 0;
        for (int m = 0; m < 12; m++) {
            double montoMes = sales.readDouble();
            sales.skipBytes(1);
            System.out.println("Mes " + (m + 1) + " : " + montoMes);
            totalVentas += montoMes;
        }
        sales.close();
        System.out.println("Total de ventas del año: " + totalVentas);
 
        RandomAccessFile bills = billsFilefor(code);
        int totalRecibos = 0;
        int tamRecibo = 8 + 8 + 8 + 4 + 4;
        bills.seek(0);
        while (bills.getFilePointer() < bills.length()) {
            bills.skipBytes(tamRecibo);
            totalRecibos++;
        }
        bills.close();
        System.out.println("Total de pagos realizados: " + totalRecibos);
    }
}