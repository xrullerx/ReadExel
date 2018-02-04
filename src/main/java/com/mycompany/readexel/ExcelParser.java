package com.mycompany.readexel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
 

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Класс чтения Exel-файла
 */
public class ExcelParser {
    InputStream     inputStream = null;     // файловый поток
    HSSFWorkbook    workBook    = null;     // книга в exel
    Sheet           sheet       = null;     // лист в книге
    Iterator<Row>   itRow       = null;     // строка
    
    // Инициализация
    public void initReadExel(String fileName) {
        //если файл открыт - закрываем
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ExcelParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            inputStream = null;
        }
        
        //открываем новый файл
        try {
            inputStream = new FileInputStream(fileName);
            workBook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    
        //разбираем первый лист входного файла на объектную модель
        sheet = workBook.getSheetAt(0);
        itRow = sheet.iterator();  
        
        //пропускаем названия полей (1 строка)
        itRow.next();
    }
    
    // Чтение телефона из файла
    public DataResult readPhoneCell() {
	DataResult result = new DataResult();   // Результат чтения
        
        // Файл не открыт
        if (inputStream == null) {            
            result.setData(DataResult.RESULT_ERROR, "");
            return result;           
        }
        
        // Данных больше нет
        if (!itRow.hasNext()) {
            result.setData(DataResult.RESULT_END_ROWS, "");
            return result;         
        }
        
        Row row = itRow.next();
        Iterator<Cell> cells = row.iterator();
            
        // выбираем только колонку с номером телефона
        Cell cell = row.getCell(1);
        
        // принудительно ставим тип
        cell.setCellType(Cell.CELL_TYPE_STRING);
        
        result.setData(DataResult.RESULT_OK, cell.getStringCellValue());
        return result;
    }
}
