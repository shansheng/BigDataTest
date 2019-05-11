///*
// * Copyright (c) 2017, 1DAOYUN and/or its affiliates. All rights reserved.
// * 1DAOYUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
// */
//package com.xiandian.douxue.insight.server.utils;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
///**
// * 解析Excel表格公共类
// * 
// * @author XianDian Cloud Team
// * @since V2.0
// * 
// */
//public class ReadExcel {
//	
//	/**
//	 * read the Excel file
//	 * 
//	 * @param path the path of the Excel file
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readExcel(String path,int columnNum) throws IOException {
//		if (path == null || "".equals(path)) {
//			return null;
//		} else {
//			String postfix =getPostfix(path);
//			if (!"".equals(postfix)) {
//				if ("xls".equals(postfix)) {
//					return readXls(path,columnNum);
//				} else if ("xlsx".equals(postfix)) {
//					return readXlsx(path,columnNum);
//				}
//			} else {
//				System.out.println(path +" : Not the Excel file!");
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * Read the Excel 2010
//	 * 
//	 * @param path the path of the excel file
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readXlsx(String path,int columnNum) throws IOException {
//		System.out.println("Processing..." + path);
//		InputStream is = new FileInputStream(path);
//		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
//		// Read the Sheet
//		List<Object[]> values=new ArrayList<Object[]>();
//		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
//			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
//			if (xssfSheet == null) {
//				continue;
//			}
//			// Read the Row
//			for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
//				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
//				Object[] value=new Object[columnNum];
//				if (xssfRow != null) {
//					for(int i=0;i<columnNum;i++){
//						XSSFCell cell = xssfRow.getCell(i);
//						value[i]=getValue(cell);
//					}
//				}
//				values.add(value);
//			}
//		}
//		return values;
//	}
//
//	/**
//	 * Read the Excel 2003-2007
//	 * 
//	 * @param path
//	 *            the path of the Excel
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readXls(String path,int columnNum) throws IOException {
//		System.out.println("Processing..." + path);
//		InputStream is = new FileInputStream(path);
//		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
//		// Read the Sheet
//		List<Object[]> values=new ArrayList<Object[]>();
//		Object[] value=new Object[columnNum];
//		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
//			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
//			if (hssfSheet == null) {
//				continue;
//			}
//			// Read the Row
//			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
//				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
//				if (hssfRow != null) {
//					for(int i=0;i<columnNum;i++){
//						HSSFCell cell = hssfRow.getCell(i);
//						value[i]=getValue(cell);
//					}
//				}
//				values.add(value);
//			}
//		}
//		return values;
//	}
//
//	private static String getValue(XSSFCell xssfRow) {
//		if(xssfRow==null){
//			return null;
//		}else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
//			return String.valueOf(xssfRow.getBooleanCellValue());
//		} else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
//			return String.valueOf(xssfRow.getNumericCellValue());
//		} else {
//			return String.valueOf(xssfRow.getStringCellValue());
//		}
//	}
//
//	private static String getValue(HSSFCell hssfCell) {
//		if(hssfCell==null){
//			return null;
//		}else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
//			return String.valueOf(hssfCell.getBooleanCellValue());
//		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
//			return String.valueOf(hssfCell.getNumericCellValue());
//		} else {
//			return String.valueOf(hssfCell.getStringCellValue());
//		}
//	}
//
//	public static String getPostfix(String path) {
//		if (path == null || "".equals(path.trim())) {
//			return "";
//		}
//		if (path.contains(".")) {
//			return path.substring(path.lastIndexOf(".") + 1, path.length());
//		}
//		return "";
//	}
//	
//	
//	public static List<Object[]> readPlan(String path,int startRowNum,int[] columnIndexs) throws Exception {
//		System.out.println("Processing..." + path);
//		InputStream is = new FileInputStream(path);
//		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
//		// Read the Sheet
//		List<Object[]> values=new ArrayList<Object[]>();
//		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
//			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
//			if (xssfSheet == null) {
//				continue;
//			}
//			// Read the Row
//			for (int rowNum = startRowNum; rowNum <= xssfSheet.getLastRowNum()-1; rowNum++) {
//				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
//				Object[] value=new Object[columnIndexs.length];
//				if (xssfRow != null) {
//					for(int i=0;i<columnIndexs.length;i++){
//						XSSFCell cell = xssfRow.getCell(columnIndexs[i]);
//						value[i]=getValue(cell);
//					}
//				}
//				values.add(value);
//			}
//		}
//		return values;         
//    }
//	
//	
//	//add4DynamicColumnNumAndFirstRow
//	/**
//	 * read the Excel file
//	 * 
//	 * @param path the path of the Excel file
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readExcel(String path) throws IOException {
//		if (path == null || "".equals(path)) {
//			return null;
//		} else {
//			String postfix =getPostfix(path);
//			if (!"".equals(postfix)) {
//				if ("xls".equals(postfix)) {
//					return readXls(path);
//				} else if ("xlsx".equals(postfix)) {
//					return readXlsx(path);
//				}
//			} else {
//				System.out.println(path +" : Not the Excel file!");
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * Read the Excel 2010
//	 * 
//	 * @param path the path of the excel file
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readXlsx(String path) throws IOException {
//		System.out.println("Processing..." + path);
//		InputStream is = new FileInputStream(path);
//		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
//		// Read the Sheet
//		List<Object[]> values=new ArrayList<Object[]>();
//		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
//			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
//			if (xssfSheet == null) {
//				continue;
//			}
//			// Read the Row
//			for (int rowNum = 0,columnNum =xssfSheet.getRow(0).getPhysicalNumberOfCells(); rowNum <=xssfSheet.getLastRowNum() ; rowNum++) {
//				
//				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
//				Object[] value=new Object[columnNum];
//				if (xssfRow != null) {
//					for(int i=0;i<columnNum;i++){
//						XSSFCell cell = xssfRow.getCell(i);
//						value[i]=getValue(cell);
//					}
//				}
//				values.add(value);
//			}
//		}
//		return values;
//	}
//
//	/**
//	 * Read the Excel 2003-2007
//	 * 
//	 * @param path
//	 *            the path of the Excel
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readXls(String path) throws IOException {
//		System.out.println("Processing..." + path);
//		InputStream is = new FileInputStream(path);
//		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
//		// Read the Sheet
//		List<Object[]> values=new ArrayList<Object[]>();
//	
//		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
//			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
//			if (hssfSheet == null) {
//				continue;
//			}
//			// Read the Row
//			for (int rowNum = 0,columnNum=hssfSheet.getRow(0).getPhysicalNumberOfCells(); rowNum <=hssfSheet.getLastRowNum() ; rowNum++) {
//				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
//				Object[] value=new Object[columnNum];
//				if (hssfRow != null) {
//					for(int i=0;i<columnNum;i++){
//						HSSFCell cell = hssfRow.getCell(i);
//						value[i]=getValue(cell);
//					}
//				}
//				values.add(value);
//			}
//		}
//		return values;
//	}
//	
//	//数字当纯文本处理
//	/**
//	 * read the Excel file
//	 * 
//	 * @param path the path of the Excel file
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readExcel4Text(String path,int columnNum) throws IOException {
//		if (path == null || "".equals(path)) {
//			return null;
//		} else {
//			String postfix =getPostfix(path);
//			if (!"".equals(postfix)) {
//				if ("xls".equals(postfix)) {
//					return readXls4Text(path,columnNum);
//				} else if ("xlsx".equals(postfix)) {
//					return readXlsx4Text(path,columnNum);
//				}
//			} else {
//				System.out.println(path +" : Not the Excel file!");
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * Read the Excel 2010
//	 * 
//	 * @param path the path of the excel file
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readXlsx4Text(String path,int columnNum) throws IOException {
//		System.out.println("Processing..." + path);
//		InputStream is = new FileInputStream(path);
//		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
//		// Read the Sheet
//		List<Object[]> values=new ArrayList<Object[]>();
//		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
//			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
//			if (xssfSheet == null) {
//				continue;
//			}
//			// Read the Row
//			for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
//				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
//				Object[] value=new Object[columnNum];
//				if (xssfRow != null) {
//					for(int i=0;i<columnNum;i++){
//						XSSFCell cell = xssfRow.getCell(i);
//						value[i]=getValue4Text(cell);
//					}
//				}
//				values.add(value);
//			}
//		}
//		return values;
//	}
//
//	/**
//	 * Read the Excel 2003-2007
//	 * 
//	 * @param path
//	 *            the path of the Excel
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<Object[]> readXls4Text(String path,int columnNum) throws IOException {
//		System.out.println("Processing..." + path);
//		InputStream is = new FileInputStream(path);
//		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
//		// Read the Sheet
//		List<Object[]> values=new ArrayList<Object[]>();
//		Object[] value=new Object[columnNum];
//		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
//			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
//			if (hssfSheet == null) {
//				continue;
//			}
//			// Read the Row
//			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
//				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
//				if (hssfRow != null) {
//					for(int i=0;i<columnNum;i++){
//						HSSFCell cell = hssfRow.getCell(i);
//						value[i]=getValue4Text(cell);
//					}
//				}
//				values.add(value);
//			}
//		}
//		return values;
//	}
//
//	private static String getValue4Text(XSSFCell xssfRow) {
//		if(xssfRow==null){
//			return null;
//		}else if (xssfRow.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
//			return String.valueOf(xssfRow.getBooleanCellValue());
//		} else if (xssfRow.getCellType() == Cell.CELL_TYPE_NUMERIC) {
//			return String.valueOf((int)xssfRow.getNumericCellValue());
//		} else {
//			return String.valueOf(xssfRow.getStringCellValue());
//		}
//	}
//
//	private static String getValue4Text(HSSFCell hssfCell) {
//		if(hssfCell==null){
//			return null;
//		}else if (hssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
//			return String.valueOf(hssfCell.getBooleanCellValue());
//		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
//			return String.valueOf((int)hssfCell.getNumericCellValue());
//		} else {
//			return String.valueOf(hssfCell.getStringCellValue());
//		}
//	}
//}
