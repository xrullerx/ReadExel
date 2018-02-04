package com.mycompany.readexel;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Поток обработки телефонов из БД
 */
public class ScanePhone extends Thread {
    boolean isRun   = false;    // Для остановки потока
    DbWork  db      = null;     // Класс работы с БД
    // Для проверки телефона через регулярные выражения
    Matcher matcher = null;
    Pattern pattern = null;
    
    // конструктор
    public ScanePhone(String name) {
        super(name);
        
        //pattern = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{10}$");
        pattern = Pattern.compile("(\\+{0})([78])\\d{10}");

        // открываем соединение с БД
        db = new DbWork();        
        db.connect();        
    }
    
    public void stopScan() {
        isRun = false;
    }

    @Override
    public void run() {
        //System.out.println("Start scanner phone " + Thread.currentThread().getName());
        
        isRun = true;
        try {
            while (isRun) {
                // засыпаем
                Thread.sleep(1000);
                
                //обработка БД
                doDBProcessing();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(ScanePhone.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            db.closeDb();
        } catch (SQLException ex) {
            Logger.getLogger(ScanePhone.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //System.out.println("End scanner phone " + Thread.currentThread().getName());
    }
        
    // Обработка телефона
    private void doDBProcessing() throws InterruptedException, SQLException {       
        DataPhone phone;
        
        // выбираем телефон, который не заблокирован
        phone = db.getPhoneNoLocked();
        if (phone == null) {
	    //нет данных - ничего не делаем
	    return;
        }
        
        //проверяем телефон и обновляем данные
        matcher = pattern.matcher(phone.phone);
        phone.is_mobile = matcher.find();
	phone.is_locked = false;              
        db.updatePhone(phone);
        
        //System.out.println("Db processing... ");
    }     
}