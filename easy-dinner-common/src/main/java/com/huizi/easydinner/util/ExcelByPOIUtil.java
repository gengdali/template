package com.huizi.easydinner.util;

import com.huizi.easydinner.anotation.ExcelHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 * 基于POI的excel解析
 * */
public class ExcelByPOIUtil {

    /**
     * 读取excel数据
     *
     * @param path
     */
    public static ArrayList<Map<String, String>> readExcelToObj(String path) {

        Workbook wb = null;
        ArrayList<Map<String, String>> result = null;
        try {
            wb = WorkbookFactory.create(new File(path));
            result = readExcel(wb, 0, 1, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取excel文件
     *
     * @param wb
     * @param sheetIndex    sheet页下标：从0开始
     * @param startReadLine 开始读取的行:从0开始
     * @param tailLine      去除最后读取的行
     */
    public static ArrayList<Map<String, String>> readExcel(Workbook wb, int sheetIndex, int startReadLine, int tailLine) {
        Sheet sheet = wb.getSheetAt(sheetIndex);
        Row row = null;
        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (int i = startReadLine; i < sheet.getLastRowNum() - tailLine + 1; i++) {

            row = sheet.getRow(i);
            Map<String, String> map = new HashMap<String, String>();
            for (Cell c : row) {
                String returnStr = "";
                boolean isMerge = isMergedRegion(sheet, i, c.getColumnIndex());
                //判断是否具有合并单元格
                if (isMerge) {
                    String rs = getMergedRegionValue(sheet, row.getRowNum(), c.getColumnIndex());
                    returnStr = rs;
                } else {
                    returnStr = c.getRichStringCellValue().getString();
                }
                map.put(c.getColumnIndex() + "列" + i + "行", returnStr);
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }
        return null;
    }

    /**
     * 判断合并了行
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static boolean isMergedRow(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row == firstRow && row == lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断sheet页中是否含有合并单元格
     *
     * @param sheet
     * @return
     */
    private boolean hasMerged(Sheet sheet) {
        return sheet.getNumMergedRegions() > 0 ? true : false;
    }

    /**
     * 合并单元格
     *
     * @param sheet
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     */
    private void mergeRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {

        if (cell == null) return "";

        if (cell.getCellType() == CellType.STRING) {

            return cell.getStringCellValue();

        } else if (cell.getCellType() == CellType.BOOLEAN) {

            return String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == CellType.FORMULA) {

            return NumberToTextConverter.toText(cell.getNumericCellValue());

        } else if (cell.getCellType() == CellType.NUMERIC) {
            String cellValue = "";
            short format = cell.getCellStyle().getDataFormat();
            if (DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = null;
                if (format == 20 || format == 32) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = DateUtil
                            .getJavaDate(value);
                    cellValue = sdf.format(date);
                } else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
                try {
                    cellValue = sdf.format(cell.getDateCellValue());// 日期
                } catch (Exception e) {
                    try {
                        throw new Exception("exception on get date data !".concat(e.toString()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } finally {
                    sdf = null;
                }
            } else {
                BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
            }
            return cellValue;
        }
        return "";
    }

    /**
     * 从excel读取内容
     */


    /**
     * @param data 需要导出的数据
     * @param clz  数据对应的实体类
     * @param <T>  泛型
     * @return Excel文件
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static <T> HSSFWorkbook exportExcel(List<T> data, Class<T> clz) throws NoSuchFieldException, IllegalAccessException {

        Field[] fields = clz.getDeclaredFields();
        List<String> headers = new LinkedList<>();
        List<String> variables = new LinkedList<>();

        // 创建工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();//这里也可以设置sheet的Name
        // 创建工作表对象
        HSSFSheet sheet = workbook.createSheet();
        // 创建表头
        Row rowHeader = sheet.createRow(0);
        // 表头处理
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(ExcelHeader.class)) {
                // 表头
                ExcelHeader annotation = field.getAnnotation(ExcelHeader.class);
                headers.add(annotation.value());
                rowHeader.createCell(i).setCellValue(annotation.value());
                // 字段
                variables.add(field.getName());
            }
        }
        // 数据处理
        for (int i = 0; i < data.size(); i++) {
            //创建工作表的行(表头占用1行, 这里从第二行开始)
            HSSFRow row = sheet.createRow(i + 1);
            // 获取一行数据
            T t = data.get(i);
            // 填充列数据
            for (int j = 0; j < variables.size(); j++) {
                Field declaredField = clz.getDeclaredField(variables.get(j));
                declaredField.setAccessible(true);
                Object value = declaredField.get(t);
                row.createCell(j).setCellValue(value.toString());
            }
        }
        return workbook;
    }


    /**
     * 导出excel到指定路径下
     *
     * @param data
     * @param clzz
     * @param filePath
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static String exportToDestinationFilePath(List<T> data, Class<T> clzz, String filePath) throws NoSuchFieldException, IllegalAccessException {
        // 生成文件名称
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        Field[] fields = clzz.getDeclaredFields();
        List<String> headers = new LinkedList<>();
        List<String> variables = new LinkedList<>();

        // 创建工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();//这里也可以设置sheet的Name
        // 创建工作表对象
        HSSFSheet sheet = workbook.createSheet();
        // 创建表头
        Row rowHeader = sheet.createRow(0);
        // 表头处理
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(ExcelHeader.class)) {
                // 表头
                ExcelHeader annotation = field.getAnnotation(ExcelHeader.class);
                headers.add(annotation.value());
                rowHeader.createCell(i).setCellValue(annotation.value());
                // 字段
                variables.add(field.getName());
            }
        }
        // 数据处理
        for (int i = 0; i < data.size(); i++) {
            //创建工作表的行(表头占用1行, 这里从第二行开始)
            HSSFRow row = sheet.createRow(i + 1);
            // 获取一行数据
            T t = data.get(i);
            Class<?> clazz = t.getClass();
            // 填充列数据
            for (int j = 0; j < variables.size(); j++) {
                Field declaredField = clazz.getDeclaredField(variables.get(j));
                declaredField.setAccessible(true);
                String key = declaredField.getName();
                Object value = declaredField.get(t);
                row.createCell(j).setCellValue(value.toString());
            }
        }
        filePath = filePath + fileName;
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    /**
     * 返回实体类标注的excel注解
     *
     * @param clazz
     * @return
     */
    public static Map<String, Object> getAnnotations(Class clazz) {
        Map<String, Object> annotations = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 判断字段是否包含 ExcelHeader 注解
            if (field.isAnnotationPresent(ExcelHeader.class)) {
                // 获取 ExcelHeader 注解信息
                ExcelHeader apiModelProperty = field.getAnnotation(ExcelHeader.class);
                annotations.put(field.getName(), apiModelProperty.value());
            }
        }
        return annotations;
    }

    /**
     * 导出到浏览器
     *
     * @param fileName
     * @param workbook
     * @param request
     * @param response
     */
    public static void writeResponse(String fileName, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("User-Agent");
        //根据不同浏览器进行不同的编码
        String filenameEncoder = "";
        try {
            if (agent.contains("MSIE")) {
                // IE浏览器
                filenameEncoder = URLEncoder.encode(fileName, "UTF8");
                filenameEncoder = filenameEncoder.replace("+", " ");
            } else if (agent.contains("Firefox")) {
                // 火狐浏览器
                filenameEncoder = new String(fileName.getBytes(), "ISO8859-1");

            } else {
                // 其它浏览器
                filenameEncoder = URLEncoder.encode(fileName, "UTF8");

            }
            response.setHeader("Content-Disposition", "attachment;filename=" + filenameEncoder);
            response.setContentType("application/vnd.ms-excel");
            //客户端不缓存
            response.setCharacterEncoding("UTF8");
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
