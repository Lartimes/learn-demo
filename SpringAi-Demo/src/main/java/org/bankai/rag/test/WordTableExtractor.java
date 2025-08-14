package org.bankai.rag.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordTableExtractor {

    public static void main(String[] args) throws Exception {
        String inputPath = "SpringAi-Demo/src/main/resources/word-sample.docx";
        String csvDir = "output_csv";
        String jsonDir = "output_json";
        new File(csvDir).mkdirs();
        new File(jsonDir).mkdirs();

        try (FileInputStream fis = new FileInputStream(inputPath);
             XWPFDocument docx = new XWPFDocument(fis)) {
//            docx.getTables()
            List<XWPFTable> tables = docx.getTables();
            int tableIndex = 0;
            for (XWPFTable table : tables) {
                System.out.println("==== 表格 #" + (++tableIndex) + " ====");
                Object[][] matrix = tableToMatrix(table);
                // 打印
                for (Object[] row : matrix) {
                    System.out.println(Arrays.toString(row));
                }
                // 导出CSV
                writeCsv(matrix, csvDir + "/table_" + tableIndex + ".csv");
                // 导出JSON
                writeJson(matrix, jsonDir + "/table_" + tableIndex + ".json");
            }
        }
    }

    // 1. 表格还原为二维数组（支持嵌套表格，合并单元格自动填充）
    public static Object[][] tableToMatrix(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        int rowCount = rows.size();
        int colCount = getMaxColCount(rows);

        Object[][] matrix = new Object[rowCount][colCount];
        int[][] vMergeTracker = new int[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            XWPFTableRow row = rows.get(i);
            List<XWPFTableCell> cells = row.getTableCells();
            int colPointer = 0;
            for (int j = 0; j < cells.size(); j++) {
                while (colPointer < colCount && matrix[i][colPointer] != null) colPointer++;
                if (colPointer >= colCount) break;

                XWPFTableCell cell = cells.get(j);
                Object cellContent = getCellContent(cell);

                int gridSpan = 1;
                int vMergeRows = 1;
                CTTcPr tcPr = cell.getCTTc().getTcPr();
                if (tcPr != null) {
                    if (tcPr.isSetGridSpan()) {
                        gridSpan = tcPr.getGridSpan().getVal().intValue();
                    }
                    if (tcPr.isSetVMerge()) {
                        STMerge.Enum vMergeVal = tcPr.getVMerge().getVal();
                        if (vMergeVal != null && vMergeVal.intValue() == 2) {
                            vMergeRows = countVMergeRows(rows, i, colPointer, cell);
                        } else {
                            colPointer += gridSpan;
                            continue;
                        }
                    }
                }
                for (int r = i; r < i + vMergeRows && r < rowCount; r++) {
                    for (int c = colPointer; c < colPointer + gridSpan && c < colCount; c++) {
                        matrix[r][c] = cellContent;
                        vMergeTracker[r][c] = vMergeRows;
                    }
                }
                colPointer += gridSpan;
            }
        }
        return matrix;
    }

    // 统计垂直合并的行数
    private static int countVMergeRows(List<XWPFTableRow> rows, int startRow, int col, XWPFTableCell cell) {
        int count = 1;
        for (int i = startRow + 1; i < rows.size(); i++) {
            List<XWPFTableCell> cells = rows.get(i).getTableCells();
            if (col < cells.size()) {
                XWPFTableCell nextCell = cells.get(col);
                CTTcPr tcPr = nextCell.getCTTc().getTcPr();
                if (tcPr != null && tcPr.isSetVMerge()) {
                    STMerge.Enum vMergeVal = tcPr.getVMerge().getVal();
                    if (vMergeVal != null && vMergeVal.intValue() == 1) {
                        count++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return count;
    }

    // 获取最大列数（考虑合并）
    private static int getMaxColCount(List<XWPFTableRow> rows) {
        int max = 0;
        for (XWPFTableRow row : rows) {
            int count = 0;
            for (XWPFTableCell cell : row.getTableCells()) {
                int gridSpan = 1;
                CTTcPr tcPr = cell.getCTTc().getTcPr();
                if (tcPr != null && tcPr.isSetGridSpan()) {
                    gridSpan = tcPr.getGridSpan().getVal().intValue();
                }
                count += gridSpan;
            }
            max = Math.max(max, count);
        }
        return max;
    }

    // 获取单元格内容（支持嵌套表格）
    private static Object getCellContent(XWPFTableCell cell) {
        List<XWPFTable> nestedTables = cell.getTables();
        if (!nestedTables.isEmpty()) {
            List<Object[][]> nestedMatrices = new ArrayList<>();
            for (XWPFTable nested : nestedTables) {
                nestedMatrices.add(tableToMatrix(nested));
            }
            return nestedMatrices;
        } else {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph para : cell.getParagraphs()) {
                sb.append(para.getText().trim()).append(" ");
            }
            return sb.toString().trim();
        }
    }

    // 2. 写CSV（嵌套表格以JSON字符串输出）
    public static void writeCsv(Object[][] matrix, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Object[] row : matrix) {
                pw.println(csvEscape(row, mapper));
            }
        }
    }

    private static String csvEscape(Object[] row, ObjectMapper mapper) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            String cell;
            if (row[i] instanceof List || row[i] instanceof Object[][]) {
                cell = mapper.writeValueAsString(row[i]);
            } else {
                cell = row[i] == null ? "" : row[i].toString().replace("\"", "\"\"");
            }
            sb.append("\"").append(cell).append("\"");
            if (i < row.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    // 3. 写JSON
    public static void writeJson(Object[][] matrix, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), matrix);
    }

    // 打印矩阵（嵌套表格以JSON字符串显示）
    public static void printMatrix(Object[][] matrix) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        for (Object[] row : matrix) {
            List<String> cells = new ArrayList<>();
            for (Object cell : row) {
                if (cell instanceof List || cell instanceof Object[][]) {
                    cells.add(mapper.writeValueAsString(cell));
                } else {
                    cells.add(cell == null ? "" : cell.toString());
                }
            }
            System.out.println(cells);
        }
    }
}