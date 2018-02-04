package com.mycompany.readexel;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

/**
 * Основной класс программы
 * @author lsds
 */
public class App {
    public static void main(String[] args) {
        DataResult  res      = null;        // Результат чтения Exel - файла
        Scanner     sc       = null;        // Класс для ввода данных с клавиатуры
        ExcelParser excel    = null;        // Парсер Exel - файла
        Thread      scanner  = null;        // Поток сканирования БД
        int         key      = 0;           // Введенное значение с клавиатуры
        DbWork 	    db      = null;         // Класс работы с БД
        
        System.out.println("Start app");

        // создаем поток сканирования БД
        scanner = new ScanePhone("Scanner");
        scanner.start();
        
        // открываем соединение с БД
        db = new DbWork();        
        db.connect();
        
        // Создаем парсер
        excel = new ExcelParser();
        excel.initReadExel("phone.xls");

        // Читаем данные из парсера и отправляем в БД
        // (можно выделить в отдельную функцию)
        do {
            res = excel.readPhoneCell();    

            if (res.getError() == DataResult.RESULT_OK) {
                try {
                    db.insertPhone(res.getVal());
                } catch (SQLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } 
        while (res.getError() == DataResult.RESULT_OK);
        
        // зацикливаем выполнение программы пока не нажата цифра 1
        sc = new Scanner(System.in);
        System.out.println("Enter command: 1 - stop, ");
        
        while (true) {
            if(sc.hasNextInt()) { 
                key = sc.nextInt(); 
                if (key == 1) {                                        
                    break;
                }
            }                
        }
        scanner.stop();
        System.out.println("End app");
    }
}
