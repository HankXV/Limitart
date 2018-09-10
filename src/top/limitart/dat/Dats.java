package top.limitart.dat;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;
import top.limitart.net.binary.BinaryMessageCodecException;
import top.limitart.net.binary.BinaryMeta;
import top.limitart.util.FileUtil;
import top.limitart.util.StringUtil;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 静态数据辅助
 *
 * @author hank
 * @version 2018/9/7 0007 16:44
 */
public class Dats {
    private static Configuration cfg;
    private static Template template;

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, URISyntaxException, TemplateException, IllegalAccessException, BinaryMessageCodecException, InstantiationException {
        File file = new File(Class.class.getResource("/dat_proto.xlsx").getPath());
        List<DatProtoFileInfo> datProtoFileInfos = readDatProtoFile(file);
        generateDatJava(datProtoFileInfos, "d://test");
        generateDatBin(file, "d://test");
    }

    /**
     * 解析数据Excel文件
     *
     * @param file
     * @return
     */
    public static List<DatProtoFileInfo> readDatProtoFile(File file) throws IOException {
        Workbook workbook = createWorkBook(file);
        List<DatProtoFileInfo> infos = new ArrayList<>();
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            DatProtoFileInfo info = new DatProtoFileInfo();
            infos.add(info);
            Sheet sheet = workbook.getSheetAt(i);
            //解析表名
            String sheetName = sheet.getSheetName();
            info.set_name(sheetName);
            Row fieldRow = sheet.getRow(0);
            //解析表中文注释
            String packageName = fieldRow.getCell(0).getStringCellValue();
            info.set_package(packageName);
            String explain = fieldRow.getCell(1).getStringCellValue();
            info.set_explain(explain);
            //解析字段
            Row nameRow = sheet.getRow(1);
            Row fieldExplainRow = sheet.getRow(2);
            Row fieldTypeRow = sheet.getRow(3);
            //以字段类型为准,如果没有则判定为注释字段
            short lastCellNum = fieldExplainRow.getLastCellNum();
            for (int c = 0; c < lastCellNum; ++c) {
                Cell typeCell = fieldTypeRow.getCell(c);
                String type = typeCell.getStringCellValue();
                if (StringUtil.empty(type)) {
                    continue;
                }
                Cell nameCell = nameRow.getCell(c);
                String name = nameCell.getStringCellValue();
                if (StringUtil.empty(name)) {
                    continue;
                }
                Cell explainCell = fieldExplainRow.getCell(c);
                DatProtoFileInfo.ColInfo colInfo = new DatProtoFileInfo.ColInfo();
                colInfo.set_explain(explainCell.getStringCellValue());
                colInfo.set_type(type);
                colInfo.set_name(name);
                info.get_cols().add(colInfo);
            }

        }
        return infos;
    }

    private static Workbook createWorkBook(File file) throws IOException {
        Workbook workbook;
        String profix = FileUtil.getFileNameExtention(file.getName());
        // 97-03
        if (profix.equals("xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(file));
        } else if (profix.equals("xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } else {
            throw new FileNotFoundException("not a xls or xlsx");
        }
        return workbook;
    }

    /**
     * 生成Java类文件
     *
     * @param infos
     * @param outPath
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TemplateException
     */
    public static void generateDatJava(List<DatProtoFileInfo> infos, String outPath) throws URISyntaxException, IOException, ParserConfigurationException, SAXException, TemplateException {
        if (cfg == null) {
            cfg = new Configuration();
            cfg.setDirectoryForTemplateLoading(new File(Class.class.getResource("/").toURI()));
            template = cfg.getTemplate("dat_model.ftl");
        }
        for (DatProtoFileInfo info : infos) {
            File outDir = new File(outPath + File.separator + info.get_package().replace('.', File.separatorChar));
            if (!outDir.exists()) {
                outDir.mkdirs();
            } else if (!outDir.isDirectory()) {
                throw new IOException("need a directory!");
            }
            try (FileWriter writer = new FileWriter(new File(outDir.getPath() + File.separator + info.get_name() + ".java"))) {
                template.process(info, writer);
            }
        }
    }

    public static void generateDatBin(File file, String outPath) throws IOException {
        Workbook workbook = createWorkBook(file);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            BinaryMeta meta = new BinaryMeta();
            ByteBuf buffer = Unpooled.buffer();
            meta.buffer(buffer);
            Sheet sheet = workbook.getSheetAt(i);
            //解析表名
            String sheetName = sheet.getSheetName();
            //解析字段
            int lastRowNum = sheet.getLastRowNum();
            Row fieldTypeRow = sheet.getRow(3);
            for (int r = 4; r < lastRowNum; ++r) {
                Row contentRow = sheet.getRow(r);
                if (isRowEmpty(contentRow)) {
                    continue;
                }
                for (int c = 0; c < contentRow.getLastCellNum(); ++c) {
                    Cell contentCell = contentRow.getCell(c);
                    Cell typeCell = fieldTypeRow.getCell(c);
                    String type = typeCell.getStringCellValue();
                    if (StringUtil.empty(type)) {
                        continue;
                    }
                    if (type.equalsIgnoreCase("int")) {
                        if (contentCell == null) {
                            meta.putInt(0);
                        } else {
                            meta.putInt((int) contentCell.getNumericCellValue());
                        }
                    } else if (type.equalsIgnoreCase("String")) {
                        if (contentCell == null) {
                            meta.putString(null);
                        } else {
                            meta.putString(contentCell.getStringCellValue());
                        }
                    } else if (type.equalsIgnoreCase("byte")) {
                        if (contentCell == null) {
                            meta.putByte((byte) 0);
                        } else {
                            meta.putByte((byte) contentCell.getNumericCellValue());
                        }
                    } else if (type.equalsIgnoreCase("short")) {
                        if (contentCell == null) {
                            meta.putShort((short) 0);
                        } else {
                            meta.putShort((short) contentCell.getNumericCellValue());
                        }
                    } else if (type.equalsIgnoreCase("long")) {
                        if (contentCell == null) {
                            meta.putLong(0l);
                        } else {
                            meta.putLong((long) contentCell.getNumericCellValue());
                        }
                    } else if (type.equalsIgnoreCase("boolean")) {
                        if (contentCell == null) {
                            meta.putBoolean(false);
                        } else {
                            meta.putBoolean(contentCell.getBooleanCellValue());
                        }
                    } else if (type.equalsIgnoreCase("float")) {
                        if (contentCell == null) {
                            meta.putFloat(0f);
                        } else {
                            meta.putFloat((float) contentCell.getNumericCellValue());
                        }
                    } else if (type.equalsIgnoreCase("double")) {
                        if (contentCell == null) {
                            meta.putDouble(0d);
                        } else {
                            meta.putDouble(contentCell.getNumericCellValue());
                        }
                    }
                }
            }
            byte[] result = new byte[buffer.readableBytes()];
            buffer.readBytes(result);
            FileUtil.writeNewFile(outPath, sheetName + ".bytes", result);
            buffer.release();
        }
    }

    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }

    public static <T extends BinaryMeta> List<T> readDatBin(Class<T> clazz, File file) throws IllegalAccessException, InstantiationException, IOException, BinaryMessageCodecException {
        List<T> result = new ArrayList<>();
        byte[] bytes = FileUtil.readFile1(file);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(bytes);
        while (buf.readableBytes() > 0) {
            T t = clazz.newInstance();
            result.add(t);
            t.buffer(buf);
            t.decode();
        }
        return result;
    }
}