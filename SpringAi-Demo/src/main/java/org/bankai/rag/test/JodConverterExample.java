package org.bankai.rag.test;

import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import java.io.File;

public class JodConverterExample {
    private static final String BASE_PATH = "SpringAi-Demo/src/main/resources/";

    public static void main(String[] args) {
        convertDocToDocx(BASE_PATH + "Bug61268.doc", BASE_PATH + "output.docx");
    }

    public static void convertDocToDocx(String inputFilePath, String outputFilePath) {
        // 启动 LibreOffice/OpenOffice 服务
        OfficeManager officeManager = LocalOfficeManager.builder()
                .officeHome("F:\\Dev\\office\\")
                .install()
                .build();

        try {
            officeManager.start();
            LocalConverter converter = LocalConverter.make();

            // 执行转换
            converter.convert(new File(inputFilePath))
                    .to(new File(outputFilePath))
                    .execute();

            System.out.println(" 转换完成，文件位于: " + outputFilePath);

        } catch (OfficeException e) {
            e.printStackTrace();
        } finally {
            // 停止服务
            try {
                officeManager.stop();
            } catch (OfficeException e) {
                e.printStackTrace();
            }
        }
    }
}