package com.ai.apac.smartenv.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: ExcelUtil
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/8/9
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/8/9     zhaidx           v1.0.0               修改原因
 */
@Slf4j
public class ExcelUtil {

    /**
     * @param @param fileName  Excel文件名称
     * @param @param combinedCellHint   Excel首行合并单元格提示
     * @param @param headers   Excel列标题(数组)
     * @param @param downData  下拉框数据(数组)
     * @param @param downCols  下拉列的序号(数组,序号从0开始)
     * @param headers
     * @param downCols
     * @return void
     * @throws
     * @Title: createExcelTemplate
     * @Description: 生成Excel导入模板
     */
    public static void createExcelTemplate(String fileName, String combinedCellHint, String[] headers, List<String[]> downData, Integer[] downCols,
                                           HttpServletRequest request, HttpServletResponse response) {
        HSSFWorkbook wb = new HSSFWorkbook();//创建工作薄

        //表头样式
        HSSFCellStyle style = createCellStyle(wb, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, "微软雅黑", true, (short) 12);

        //新建sheet
        HSSFSheet sheet1 = wb.createSheet("Sheet1");
        HSSFSheet sheet2 = wb.createSheet("Sheet2");

        //创建合并单元格对象
        CellRangeAddress callRangeAddress = new CellRangeAddress(0, 0, 0, headers.length-1);//起始行,结束行,起始列,结束列
        sheet1.addMergedRegion(callRangeAddress);
        //生成sheet1内容
        HSSFRow rowFirst = sheet1.createRow(0);//第一个sheet的第一行为合并单元格说明
        rowFirst.setHeightInPoints((short) 30);
        HSSFCell combinedCell = rowFirst.createCell(0);//取第一行合并的单元格
        combinedCell.setCellValue(new HSSFRichTextString(combinedCellHint)); //设置单元格内提示信息内容
        HSSFCellStyle cellStyle = createCellStyle(wb, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, null, false, (short) 8);//单元格样式
        cellStyle.setWrapText(true);
        combinedCell.setCellStyle(cellStyle);

        HSSFRow rowSecond = sheet1.createRow(1);//第一个sheet的第二行为标题
        //写标题
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = rowSecond.createCell(i); //获取第二行的每个单元格
            sheet1.setColumnWidth(i, 4000); //设置每列的列宽
            cell.setCellStyle(style); //加样式
            cell.setCellValue(headers[i]); //往单元格里写数据
            cell.setCellType(CellType.STRING);
        }

        //设置下拉框数据
        String[] arr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        int index = 0;
        HSSFRow row = null;
        for (int column = 0; column < downCols.length; column++) {
            String[] dlData = downData.get(column);//获取下拉对象
            int colNum = downCols[column];
            if (Arrays.toString(dlData).length() <= 255) { //总字符在255以内的下拉
                //255以内的下拉,参数分别是：作用的sheet、下拉内容数组、起始行、终止行、起始列、终止列
                sheet1.addValidationData(setDataValidationLessThan255(sheet1, dlData, 2, 1001, colNum, colNum)); //超过255个报错
            } else { //255以上的下拉，即下拉列表元素很多的情况
                //1、设置有效性
                String strFormula = "Sheet2!$" + arr[index] + "$1:$" + arr[index] + "$1000"; //Sheet2第A1到A1000作为下拉列表来源数据 "Sheet2!$A$1:$A$1000"
                sheet2.setColumnWidth(column, 4000); //设置每列的列宽
                //设置数据有效性加载在哪个单元格上,参数分别是：从sheet2获取A1到A1000作为一个下拉的数据、起始行、终止行、起始列、终止列
                sheet1.addValidationData(setDataValidationMoreThan255(strFormula, 2, 1001, colNum, colNum)); //下拉列表元素很多的情况
                //2、生成sheet2内容
                for (int j = 0; j < dlData.length; j++) {
                    if (index == 0) { //第1个下拉选项，直接创建行、列
                        row = sheet2.createRow(j); //创建数据行
                        sheet2.setColumnWidth(j, 4000); //设置每列的列宽
                        row.createCell(0).setCellValue(dlData[j]); //设置对应单元格的值

                    } else { //非第1个下拉选项
                        int rowCount = sheet2.getLastRowNum();
                        if (j <= rowCount) { //前面创建过的行，直接获取行，创建列
                            //获取行，创建列
                            sheet2.getRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值
                        } else { //未创建过的行，直接创建行、创建列
                            sheet2.setColumnWidth(j, 4000); //设置每列的列宽
                            //创建行、创建列
                            sheet2.createRow(j).createCell(index).setCellValue(dlData[j]); //设置对应单元格的值
                        }
                    }
                }
                index++;
            }
        }
        try {
 /*           //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
            response.setContentType("multipart/form-data");

            //2.设置文件头：最后一个参数是设置下载文件名  
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + encodeChineseDownloadFileName(request, fileName + ".xls") + "\"");*/

            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName + ".xls", "UTF-8"));
            
            //3.通过response获取OutputStream对象(out)
            OutputStream out = new BufferedOutputStream(response.getOutputStream());

            //4.写到输出流(out)中  
            out.flush();
            wb.write(out);
            out.close();
        } catch (IOException e) {
            log.error("写入模板数据失败", e);
        }
    }

    /**
     * 单元格样式
     * @param workbook
     * @param hAlig
     * @param vAlig
     * @param fontName
     * @param fontBold
     * @param fontsize
     * @return HSSFCellStyle
     */
    public static HSSFCellStyle createCellStyle(HSSFWorkbook workbook, HorizontalAlignment hAlig, VerticalAlignment vAlig,
                                                 String fontName, boolean fontBold, short fontsize) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(hAlig);//水平位置
        style.setVerticalAlignment(vAlig);//垂直位置
        //创建字体
        HSSFFont font = workbook.createFont();
        if (!StringUtils.isEmpty(fontName)) {
            font.setFontName(fontName);
        }
        font.setBold(fontBold);
        font.setFontHeightInPoints(fontsize);
        //加载字体
        style.setFont(font);
        return style;
    }

    /**
     * @param @param  strFormula
     * @param @param  firstRow   起始行
     * @param @param  endRow     终止行
     * @param @param  firstCol   起始列
     * @param @param  endCol     终止列
     * @param @return
     * @return HSSFDataValidation
     * @throws
     * @Title: SetDataValidation
     * @Description: 下拉列表元素很多的情况 (255以上的下拉)
     */
    private static HSSFDataValidation setDataValidationMoreThan255(String strFormula, int firstRow, int endRow, int firstCol, int endCol) {
        // 设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
        dataValidation.createErrorBox("错误", "请从下拉框中选择数据");
        dataValidation.createPromptBox("", null);
        return dataValidation;
    }


    /**
     * @param @param  sheet
     * @param @param  textList
     * @param @param  firstRow
     * @param @param  endRow
     * @param @param  firstCol
     * @param @param  endCol
     * @param @return
     * @return DataValidation
     * @throws
     * @Title: setDataValidation
     * @Description: 下拉列表元素不多的情况(255以内的下拉)
     */
    private static DataValidation setDataValidationLessThan255(Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        //加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        constraint.setExplicitListValues(textList);
        //设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList((short) firstRow, (short) endRow, (short) firstCol, (short) endCol);
        //数据有效性对象
        return helper.createValidation(constraint, regions);
    }

    /**
     * @param @param url 文件路径
     * @param @param fileName  文件名
     * @param @param response
     * @return void
     * @throws
     * @Title: getExcel
     * @Description: 下载指定路径的Excel文件
     */
    public static void getExcel(String url, String fileName, HttpServletResponse response, HttpServletRequest request) {
        try {
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
            response.setContentType("multipart/form-data");

            //2.设置文件头：最后一个参数是设置下载文件名  
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + encodeChineseDownloadFileName(request, fileName + ".xls") + "\"");
//            response.setHeader("Content-Disposition", "attachment;filename="  
//                    + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".xls"); //中文文件名

            //通过文件路径获得File对象
            File file = new File(url);

            FileInputStream in = new FileInputStream(file);
            //3.通过response获取OutputStream对象(out)  
            OutputStream out = new BufferedOutputStream(response.getOutputStream());

            int b = 0;
            byte[] buffer = new byte[2048];
            while ((b = in.read(buffer)) != -1) {
                out.write(buffer, 0, b); //4.写到输出流(out)中  
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("下载Excel模板异常", e);
        }
    }

    /**
     * @param @param  request
     * @param @param  pFileName
     * @param @return
     * @param @throws UnsupportedEncodingException
     * @return String
     * @throws
     * @Title: encodeChineseDownloadFileName
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private static String encodeChineseDownloadFileName(HttpServletRequest request, String pFileName)
            throws UnsupportedEncodingException {

        String filename = null;
        String agent = request.getHeader("USER-AGENT");

        if (null != agent) {
            if (agent.contains("Firefox")) {//Firefox  
                filename = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(pFileName.getBytes("UTF-8")))) + "?=";
            } else if (agent.contains("Chrome")) {//Chrome  
                filename = new String(pFileName.getBytes(), "ISO8859-1");
            } else {//IE7+  
                filename = java.net.URLEncoder.encode(pFileName, "UTF-8");
                filename = StringUtils.replace(filename, "+", "%20");//替换空格
            }
        } else {
            filename = pFileName;
        }

        return filename;
    }

    /**
     * @param @param filePath  文件路径
     * @return void
     * @throws
     * @Title: delFile
     * @Description: 删除文件
     */
    public static void delFile(String filePath) {
        File delFile = new File(filePath);
        delFile.delete();
    }
}
