package com.fyyzi.common.utils;

import com.fyyzi.common.enums.CodeMsgEnum;
import com.fyyzi.exceptions.ExcelException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Excel导出方式的SuperClass
 *
 * @author 息阳
 */
@Slf4j
public abstract class AbstractSimpleExcel<E> {

    /**
     * 获取Excel对象
     *
     * @return 直接从Controller返回即可实现导出
     * @SuperWarning 抑制类继承层数过高的异常(Sonar)
     */
    @SuppressWarnings("squid:MaximumInheritanceDepth")
    public AbstractXlsxView getExcel() {
        return new AbstractXlsxView() {
            @Override
            protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
                String encode = URLEncoder.encode(fileName, "UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "attachement; filename=\"" + encode + ".xlsx");
                createDefaultSheet(workbook);
                createOtherSheet(workbook);
            }
        };
    }

    /**
     * 添加Header信息
     *
     * @param cellValues 请将Excel表头数据添加至该对象中
     */
    public abstract void createHeaderCellValueList(List<String> cellValues);

    /**
     * 将model数据add至List中
     *
     * @param cellValues 将model中的具体内容映射到Excel相应位置中 cellValues.add(model.getId)
     * @param model      {@link E}
     */
    public abstract void createBodyCellValuesList(List<Object> cellValues, E model);

    /** excel 最大行数 */
    private static final int MAX_ROW = 1 << 16;
    /** 文件名 */
    protected String fileName;
    /** sheet页名 */
    protected String sheetName;
    /** body体List集合 */
    protected List<E> bodyList;

    /** header字体 */
    protected static final String HEADER_FONT_NAME = "微软雅黑";
    /** body字体 */
    protected static final String BODY_FONT_NAME = "宋体";

    /** 执行Excel宽度自适应方法的行阈值 */
    protected static final int AUTO_SIZE_COLUMN_ROW_THRESHOLD = 1 << 11;
    /** 日期格式化 */
    protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 语言(中文) */
    private static final String LANGUAGE_ZH = "zh";
    /** 语言(英文) */
    private static final String LANGUAGE_EN = "en";

    /** BigDecimal格式化 */
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    /**
     * @param fileName  文件名
     * @param sheetName sheet页名
     * @param bodyList  body体List集合
     */
    public AbstractSimpleExcel(String fileName, String sheetName, List<E> bodyList) {
        super();
        this.fileName = fileName;
        this.sheetName = sheetName;
        if (bodyList.size() >= MAX_ROW) {
            throw new ExcelException(CodeMsgEnum.EXCEL_EXPORT_TOO_MUCH);
        }
        this.bodyList = bodyList;
    }

    /**
     * 创建默认Sheet页
     *
     * @param workbook {@link Workbook}
     */
    private void createDefaultSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(sheetName);
        addSheetHeader(workbook, sheet, null);
        addSheetBody(workbook, sheet, null);
    }

    /**
     * 该方法用于拓展sheet页
     *
     * @param workbook {@link Workbook}
     */
    public void createOtherSheet(Workbook workbook) {
        // 该方法用于拓展sheet页
    }

    /**
     * 对Sheet页添加Header
     *
     * @param sheet      Sheet页对象
     * @param workbook   {@link Workbook}
     * @param cellValues 文件头List  若为null  则自动创建,并调用继承类中的createHeaderCellValueList方法
     */
    protected final void addSheetHeader(Workbook workbook, Sheet sheet, List<String> cellValues) {
        Row header = sheet.createRow(0);
        String thisSheetName = sheet.getSheetName();

        header.setHeight((short) 400);

        if (cellValues == null) {
            cellValues = new ArrayList<>();
            String language = LocaleContextHolder.getLocale().getLanguage();
            if (LANGUAGE_ZH.equals(language)) {
                cellValues.add("序号");
            } else if (LANGUAGE_EN.equals(language)) {
                cellValues.add("NO.");
            }
            createHeaderCellValueList(cellValues);
        }

        int column = setCellValue(header, cellValues);

        int rowNum = header.getRowNum();
        log.trace("Excel Sheet页({})第{}行共含有:{}列。", thisSheetName, rowNum, column);

        for (int i = 0; i < cellValues.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        setHeaderStyle(workbook, sheet);
    }

    /**
     * 将bodyList数据添加到Excel body里面
     *
     * @param workbook {@link Workbook}
     * @param sheet    Sheet页
     * @param bodyList bodyList
     */
    protected final void addSheetBody(Workbook workbook, Sheet sheet, List<?> bodyList) {
        boolean rowNumerFlag = false;
        if (bodyList == null) {
            rowNumerFlag = true;
            bodyList = this.bodyList;
        }

        String thisSheetName = sheet.getSheetName();
        int listSize = bodyList.size();
        for (int i = 0; i < listSize; i++) {
            Object t = bodyList.get(i);
            Integer rowNumber = i + 1;
            Row body = sheet.createRow(rowNumber);

            List<Object> cellValues = new ArrayList<>();
            cellValues.add(rowNumber);
            if (rowNumerFlag) {
                createBodyCellValuesList(cellValues, (E) t);
            } else {
                addObjectToCellValue(t, cellValues);
            }
            int column = setCellValue(body, cellValues);
            log.trace("Excel Sheet页({})第{}行共含有:{}列。", thisSheetName, (rowNumber + 1), column);
            if (i == AUTO_SIZE_COLUMN_ROW_THRESHOLD) {
                setBodyStyle(workbook, sheet);
                setSizeColumn(sheet);
            }
        }
        if (listSize < AUTO_SIZE_COLUMN_ROW_THRESHOLD) {
            setBodyStyle(workbook, sheet);
            setSizeColumn(sheet);
        }
    }

    /**
     * 将 数据  添加到cellValues内
     *
     * @param t          判断 如果类型为 List 则将其添加到cellValues中
     * @param cellValues Excel内容
     * @throws ExcelException bodyList格式不正确
     */
    private void addObjectToCellValue(Object t, List<Object> cellValues) {
        if (t instanceof List<?>) {
            List<?> list = (List<?>) t;
            for (int j = 0; j < list.size(); j++) {
                Object object = list.get(j);
                cellValues.add(object);
            }
            return;
        }
        throw new ExcelException(CodeMsgEnum.EXCEL_EXPORT_CLASS_CASE);
    }


    /**
     * 设置Header样式
     *
     * @param workbook {@link Workbook}
     * @param sheet    {@link Sheet}
     */
    public static CellStyle setHeaderStyle(Workbook workbook, Sheet sheet) {
        Row header = sheet.getRow(sheet.getFirstRowNum());
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);

        // 设置字体
        font.setFontName(HEADER_FONT_NAME);
        // 设置字体大小
        font.setFontHeight((short) (11 * 20));
        // 粗体显示
        font.setBold(true);
        // 对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 单元格颜色
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        // 填充方式
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 将样式刷入每一个单元格
        int physicalNumberOfCells = header.getPhysicalNumberOfCells();
        for (int i = 0; i < physicalNumberOfCells; i++) {
            Cell cell = header.getCell(i);
            cell.setCellStyle(style);
        }
        return style;
    }

    /**
     * 设置Body样式
     *
     * @param workbook {@link Workbook}
     * @param sheet    {@link Sheet}
     * @return {@link CellStyle}
     */
    public static CellStyle setBodyStyle(Workbook workbook, Sheet sheet) {

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);

        // 设置字体
        font.setFontName(BODY_FONT_NAME);
        // 设置字体大小
        font.setFontHeight((short) (11 * 20));
        // 粗体显示
        font.setBold(false);
        // 对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 单元格颜色
        style.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex());
        // 填充方式
        style.setFillPattern(FillPatternType.NO_FILL);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // 将样式刷入每一个单元格
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < physicalNumberOfRows; i++) {
            Row body = sheet.getRow(i);
            int physicalNumberOfCells = body.getPhysicalNumberOfCells();
            for (int j = 0; j < physicalNumberOfCells; j++) {
                Cell cell = body.getCell(j);
                cell.setCellStyle(style);
            }
        }
        return style;
    }

    /**
     * 将List中的数据放入Row中
     *
     * @param row        Sheet.getRow();
     * @param cellValues 行数据
     * @return 当前Row 含有元素个数
     */
    public static int setCellValue(Row row, List<?> cellValues) {
        int column = 0;
        for (int i = 0; i < cellValues.size(); i++) {
            String cellValue = "--";
            Object object = cellValues.get(i);
            Cell cell = row.createCell(column++);
            boolean setCellFlag = true;
            if (object != null) {
                if (object instanceof Date) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(DateFormatUtils.format((Date) object, DATE_FORMAT));
                    setCellFlag = false;
                } else if (object instanceof BigDecimal) {
                    decimalFormat((BigDecimal) object, cell);
                    setCellFlag = false;
                } else if (object instanceof Double) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Double) object);
                    setCellFlag = false;
                } else if (object instanceof Integer) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Integer) object);
                    setCellFlag = false;
                } else if (object instanceof Boolean) {
                    cellValue = (Boolean) object ? "是" : "否";
                } else {
                    cell.setCellType(CellType.STRING);
                    cellValue = object.toString();
                }
            }
            setCellValue(cellValue, cell, setCellFlag);
        }
        return column;
    }

    /**
     * BigDecimal 格式化
     *
     * @param bigDecimal {@link BigDecimal}
     * @param cell       {@link Cell}
     */
    private static void decimalFormat(BigDecimal bigDecimal, Cell cell) {
        BigDecimal obj = bigDecimal;
        cell.setCellType(CellType.STRING);
        String s = DECIMAL_FORMAT.format(obj);
        if (obj.compareTo(BigDecimal.ZERO) == 0) {
            cell.setCellValue("0.00");
        } else if (obj.compareTo(BigDecimal.ZERO) > 0 && obj.compareTo(new BigDecimal(1)) < 0) {
            cell.setCellValue("0" + s);
        } else {
            cell.setCellValue(s);
        }
    }

    /**
     * 将CellValue放入Cell中(Excel对应的位置)
     *
     * @param cellValue   Excel单元格的内容
     * @param cell        {@link Cell} Excel单元格
     * @param setCellFlag 是否执行
     */
    private static void setCellValue(String cellValue, Cell cell, boolean setCellFlag) {
        if (setCellFlag) {
            cell.setCellValue(cellValue);
        }
    }

    /**
     * Sheet页宽度自适应
     *
     * @param sheet {@link Sheet} Sheet页对象
     */
    public static void setSizeColumn(Sheet sheet) {
        int physicalNumberOfCells = sheet.getRow(sheet.getFirstRowNum()).getPhysicalNumberOfCells();
        for (int columnNum = 0; columnNum <= physicalNumberOfCells; columnNum++) {
            sheet.autoSizeColumn(columnNum, true);
        }
        autoSizeColumn(sheet);
    }

    /**
     * 自适应宽度(中文支持)
     *
     * @param sheet {@link Sheet} sheet页对象
     */
    public static void autoSizeColumn(Sheet sheet) {
        int physicalNumberOfCells = sheet.getRow(sheet.getFirstRowNum()).getPhysicalNumberOfCells();
        for (int columnNum = 0; columnNum <= physicalNumberOfCells; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null || row.getCell(columnNum) == null) {
                    continue;
                }
                Cell currentCell = row.getCell(columnNum);
                if (currentCell.getCellType() == CellType.STRING) {
                    int length = currentCell.getStringCellValue().getBytes().length;
                    if (columnWidth < length) {
                        columnWidth = length;
                    }
                }
            }
            serColumnNum(sheet, columnNum, columnWidth);
        }
    }

    /**
     * 自动设置宽度
     *
     * @param sheet       sheet
     * @param columnNum   行
     * @param columnWidth 宽
     */
    private static void serColumnNum(Sheet sheet, int columnNum, int columnWidth) {
        int maxColumnWidth = 6;
        if (columnWidth < 1 << maxColumnWidth) {
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        } else {
            sheet.setColumnWidth(columnNum, 20 * 256);
        }
    }
}
