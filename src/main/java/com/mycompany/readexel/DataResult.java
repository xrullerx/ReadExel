package com.mycompany.readexel;

/*
 * Класс передачи результата чтения Exel-файла
 */
public class DataResult {
    
    // Коды ошибок
    static final int  RESULT_OK    = 0;         // Все нормально
    static final int  RESULT_ERROR = -1;	// Какая-то ошибка
    static final int  RESULT_END_ROWS = -2;	// Конец парсинга
    
    private int errorCode = RESULT_END_ROWS;
    private String values = "";
    
    // Запись данных
    public void setData(int error, String val) {
      errorCode = error;
      values = val;
    }
    
    // Получение кода ошибки
    public int getError() {
      return errorCode;
    }

    // Получение значения результата
    public String getVal() {
      return values;
    }    
}
