//package top.limitart.dat;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import io.netty.buffer.Unpooled;
//import top.limitart.util.FileUtil;
//
//public class ExcelLoader implements AutoCloseable{
//    Workbook workBook = null;
//
//    public ExcelLoader(String excelPath) throws IOException {
//        String profix = FileUtil.getFileNameExtention(excelPath);
//        // 97-03
//        if (profix.equals("xls")) {
//            workBook = new HSSFWorkbook(new FileInputStream(new File(excelPath)));
//        } else if (profix.equals("xlsx")) {
//            workBook = new XSSFWorkbook(new FileInputStream(new File(excelPath)));
//        }
//    }
//
//    public MessageXmlData load() throws IOException {
//        MessageXmlData data = new MessageXmlData();
//        data.setModname("bean");
//        data.setExplain("静态数据资源自动生成");
//        int numberOfSheets = workBook.getNumberOfSheets();
//        for (int i = 0; i < numberOfSheets; i++) {
//            MessageData mdata = new MessageData();
//            data.getJavaMessageDatas().add(mdata);
//            Sheet sheetAt = workBook.getSheetAt(i);
//            mdata.setIsBean(1);
//            mdata.setName(sheetAt.getSheetName());
//            mdata.setExplain(sheetAt.getRow(0).getCell(0).getStringCellValue());
//            // 字段
//            Row fieldRow = sheetAt.getRow(1);
//            Row fieldExplainRow = sheetAt.getRow(2);
//            Row fieldTypeRow = sheetAt.getRow(3);
//            for (int q = 0; q < fieldRow.getLastCellNum(); ++q) {
//                FieldInfo info = new FieldInfo();
//                Cell fieldNameCell = fieldRow.getCell(q);
//                if (fieldNameCell == null) {
//                    continue;
//                }
//                String name = fieldNameCell.getStringCellValue();
//                if (StringUtil.isEmptyOrNull(name)) {
//                    continue;
//                }
//                Cell typeCell = fieldTypeRow.getCell(q);
//                if (typeCell == null) {
//                    continue;
//                }
//                String typeName = typeCell.getStringCellValue();
//                if (StringUtil.isEmptyOrNull(typeName)) {
//                    continue;
//                }
//                mdata.getFields().add(info);
//                info.setName(name);
//                info.setType(typeName);
//                info.setExplain(fieldExplainRow.getCell(q).getStringCellValue());
//                if (q == 0) {
//                    mdata.setPrimaryKeyType(info.getType());
//                    mdata.setPrimaryKeyName(info.getName());
//                }
//            }
//
//        }
//        workBook.close();
//        return data;
//    }
//
//    public void writeDat(String outDir) throws Exception {
//        int numberOfSheets = workBook.getNumberOfSheets();
//        for (int i = 0; i < numberOfSheets; i++) {
//            Sheet sheetAt = workBook.getSheetAt(i);
//            String resName = sheetAt.getSheetName() + ".bytes";
//            // 类型行
//            Row typeRow = sheetAt.getRow(3);
//            // 数据行
//            MessageMeta sumMeta = new MessageMeta() {
//            };
//            sumMeta.buffer(Unpooled.buffer());
//            // 写入文件名
//            sumMeta.putString(sheetAt.getSheetName());
//            int cellNum = sheetAt.getRow(1).getLastCellNum();
//            for (int j = 4; j <= sheetAt.getLastRowNum(); ++j) {
//                Row dataRow = sheetAt.getRow(j);
//                MessageMeta meta = new MessageMeta() {
//
//                    @Override
//                    public void encode() throws Exception {
//                        for (int k = 0; k < cellNum; ++k) {
//                            if (typeRow.getCell(k) == null) {
//                                continue;
//                            }
//                            String typeCell = typeRow.getCell(k).getStringCellValue();
//                            Cell dataCell = dataRow.getCell(k);
//                            if (typeCell.equalsIgnoreCase("int")) {
//                                if (dataCell == null) {
//                                    putInt(0);
//                                } else {
//                                    putInt((int) dataCell.getNumericCellValue());
//                                }
//                            } else if (typeCell.equalsIgnoreCase("String")) {
//                                if (dataCell == null) {
//                                    putString(null);
//                                } else {
//                                    putString(dataCell.getStringCellValue());
//                                }
//                            } else if (typeCell.equalsIgnoreCase("byte")) {
//                                if (dataCell == null) {
//                                    putByte((byte) 0);
//                                } else {
//                                    putByte((byte) dataCell.getNumericCellValue());
//                                }
//                            } else if (typeCell.equalsIgnoreCase("short")) {
//                                if (dataCell == null) {
//                                    putShort((short) 0);
//                                } else {
//                                    putShort((short) dataCell.getNumericCellValue());
//                                }
//                            } else if (typeCell.equalsIgnoreCase("long")) {
//                                if (dataCell == null) {
//                                    putLong(0l);
//                                } else {
//                                    putLong((long) dataCell.getNumericCellValue());
//                                }
//                            } else if (typeCell.equalsIgnoreCase("boolean")) {
//                                if (dataCell == null) {
//                                    putBoolean(false);
//                                } else {
//                                    putBoolean(dataCell.getBooleanCellValue());
//                                }
//                            } else if (typeCell.equalsIgnoreCase("float")) {
//                                if (dataCell == null) {
//                                    putFloat(0f);
//                                } else {
//                                    putFloat((float) dataCell.getNumericCellValue());
//                                }
//                            } else if (typeCell.equalsIgnoreCase("double")) {
//                                if (dataCell == null) {
//                                    putDouble(0d);
//                                } else {
//                                    putDouble((double) dataCell.getNumericCellValue());
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void decode() throws Exception {
//
//                    }
//                };
//                meta.buffer(Unpooled.buffer());
//                meta.encode();
//                sumMeta.buffer().writeBytes(meta.buffer());
//                meta.buffer().release();
//                meta.buffer(null);
//            }
//            byte[] temp = new byte[sumMeta.buffer().readableBytes()];
//            sumMeta.buffer().readBytes(temp);
//            FileUtil.writeNewFile(outDir, resName, temp);
//            sumMeta.buffer().release();
//            sumMeta.buffer(null);
//        }
//    }
//}
