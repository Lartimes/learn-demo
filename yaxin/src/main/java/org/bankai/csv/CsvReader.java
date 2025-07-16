package org.bankai.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class CsvReader {
    public static void main(String[] args) {
        CsvReader csvReader = new CsvReader();
        csvReader.test("yaxin/src/main/resources/DACP_META_PROC_STEP_LOG+_20250620.csv");
    }
    public void test(String filePath) {
        int expectedSeparators = 22; // 22个欧元符号，23个字段

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder record = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                record.append(line).append("\n");
                if (countChar(record, '€') >= expectedSeparators) {
                    String[] fields = record.toString().split("€", -1);
                    // 这里处理fields，比如写入数据库、打印、保存等
                    System.out.println("字段数: " + fields.length + "，内容示例: " + Arrays.toString(fields));
                    record.setLength(0); // 清空
                }
            }
            // 处理最后一条（如果文件末尾没有换行）
            if (!record.isEmpty()) {
                String[] fields = record.toString().split("€", -1);
                System.out.println("字段数: " + fields.length + "，内容示例: " + fields[0]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int countChar(CharSequence seq, char c) {
        int count = 0;
        for (int i = 0; i < seq.length(); i++) if (seq.charAt(i) == c) count++;
        return count;
    }
}
