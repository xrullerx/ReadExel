package com.mycompany.readexel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Класс работы с БД
 */
public class DbWork {    
    // параметры доступа к БД
    // (их можно вынести в отдельный файл)
    String URL_DB = "jdbc:postgresql://localhost:5432/phonedb";
    String LOGIN  = "postgres";
    String PSW    = "postgres";

    private Connection  con     = null; // Соединение с БД
    private Statement   stmt    = null; // Для запросов к БД
    private int         keyId   = 0;    // Id последнего обработанного телефона
        
    // подключение к БД
    public void connect() {
	if (isConnect()) {
	  return;
	}
    
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(URL_DB, LOGIN, PSW);
            keyId = 0;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        System.out.println("DB to connected.");	
    }
    
    // проверка подключения
    public boolean isConnect () {
	if (con == null) {
	    return false;
	}
	return true;
    }
    
    // Получить не заблокированный телефон.
    // Данные берутся по одной записи. В дальнейшем можно модифицировать, что бы данные брались блоками
    public DataPhone getPhoneNoLocked() throws SQLException {
	DataPhone result    = null; // Данны телефона из БД
	ResultSet rSet      = null; // Полученные данные из БД
	String    sqlSelect = "SELECT * FROM phone WHERE id > "+keyId+" AND is_locked = false";
	String    sqlUpdate;
        
        if ( !isConnect()) {
	  return null;
	}    
	
	con.setAutoCommit(false);
        
        try {
	    stmt = con.createStatement();
	    rSet = stmt.executeQuery(sqlSelect);    	
	    if (rSet.next()) {
		result = new DataPhone();
		result.setData (rSet.getInt("id"), rSet.getString("phone"), rSet.getBoolean("is_mobile"), rSet.getBoolean("is_locked"));
		// блокируем запись
		sqlUpdate = "UPDATE phone SET is_locked = true WHERE id = "+result.id+" AND is_locked = false";
		
                stmt.executeUpdate(sqlUpdate);
	    } else {
		//данных по выборке больше нет - в следующий раз начнем выборку с начала
		keyId = 0;
	    }
            // Комитим транзакцию 
	    con.commit();
            
	    rSet.close();	    
	    stmt.close();
        } catch (Exception e) {
            return null;
        } 
	
	con.setAutoCommit(true);
        
        // Запоминаем Id обработанного телефона
	if (result != null) {
	    keyId = result.id;
	}
	
	return result;
    }
    
    // доабавление телефона
    public void insertPhone(String phone) throws SQLException {	
	String sql;
        
        if ( !isConnect()) {
	  return;
	}    
    
	if (phone == "" ) {
	  return;
	}
	
	// тут можно проверить - существует ли таблица в БД
	// на данный момент пропустим...
	
	stmt = con.createStatement();
        sql = "INSERT INTO phone (phone, is_mobile, is_locked) VALUES ("+phone+", false, false )";
        stmt.executeUpdate(sql);    
	stmt.close();
        System.out.println("Insert phone "+phone);	        
    }
    
    // Обновление телефона в БД
    public void updatePhone(DataPhone phone) throws SQLException {
	String    sqlUpdate;
	
	if (phone == null) {
	    return;
	}
	
	sqlUpdate = "UPDATE phone SET is_mobile = " + phone.is_mobile +", is_locked = " + phone.is_locked+ " WHERE id = " + phone.id;
                
        System.out.println(sqlUpdate);
        
	stmt = con.createStatement();
        stmt.executeUpdate(sqlUpdate);    
	stmt.close();
        System.out.println("UPDATE phone "+phone.phone);	        
    }
    
    // закрытие БД
    public void closeDb() throws SQLException {
	if (isConnect()) {
	  con.close();
	  con = null;
	}	
    }    
    
    // создание БД
    // (пока не используется, но дает представление о структуре БД)
    private void createDb() throws SQLException {
    	String sql;
	
	stmt = con.createStatement();
      
	sql = "CREATE TABLE phone " +
                "(id             SERIAL," +
                " phone          TEXT, " +
                " is_mobile      BOOLEAN, " +
                " is_locked      BOOLEAN)";
	stmt.executeUpdate(sql);      
	stmt.close();
        System.out.println("-- Table created successfully");	
    }
}
