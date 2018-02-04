package com.mycompany.readexel;

/*
 * Класс описания данных из БД
 * (для упрощения - все свойства открыты)
 */
public class DataPhone {
    
    public int 		id;
    public String 	phone;
    public boolean 	is_mobile;
    public boolean 	is_locked;
        
    public void setData(int keyId, String ph, boolean mobile, boolean locked) {
      id = keyId;      
      phone = ph;
      is_mobile = mobile;
      is_locked = locked;      
    }    
}
