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
        try {
            File mf = new File("company");
            mf.mkdir();
            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");
            initCode();
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }

    private void initCode() throws IOException {
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
        createEmployeeFolders(code);
    }

    private String employeeFolder(int code) {
        return "company/empleado" + code;
    }

    private RandomAccessFile salesFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String dir = dirPadre + "/ventas" + year + ".emp";
        return new RandomAccessFile(dir, "rw");
    }

    private RandomAccessFile billsFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        String dir = dirPadre + "/recibos.emp";
        return new RandomAccessFile(dir, "rw");
    }

    private void createYearSalesFilesFor(int code) throws IOException {
        RandomAccessFile rventas = salesFileFor(code);
        if (rventas.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                rventas.writeDouble(0);
                rventas.writeBoolean(false);
            }
        }
        rventas.close();
    }

    private void createEmployeeFolders(int code) throws IOException {
        File dir = new File(employeeFolder(code));
        dir.mkdir();
        createYearSalesFilesFor(code);
    }

    public void employeeList() throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int code = remps.readInt();
            String name = remps.readUTF();
            double salario = remps.readDouble();
            Date dateH = new Date(remps.readLong());
            if (remps.readLong() == 0) {
                System.out.println("Codigo: " + code + " Nombre: " + name + " Salario: $" + salario + " Fecha: " + dateH);
            }
        }
    }

    private long seekToEmployee(int code) throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int cod = remps.readInt();
            long pos = remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(8);
            remps.skipBytes(8);
            long fechaDespido = remps.readLong();

            if (cod == code) {
                if (fechaDespido == 0) {
                    remps.seek(pos);
                    return pos;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    private boolean isEmployeeActive(int code) throws IOException {
        return seekToEmployee(code) != -1;
    }

    public boolean fireEmployee(int code) throws IOException {
        long pos = seekToEmployee(code);
        if (pos != -1) {
            String name = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a: " + name);
            return true;
        }
        System.out.println("No se pudo despedir al empleado");
        return false;
    }

}
