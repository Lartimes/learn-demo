package org.bankai.rag.test;

import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.Section;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.*;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

@Service
public class DocxTest {


    private static final String BASE_PATH = "SpringAi-Demo/src/main/resources/";
    static {
        try {
            Class.forName("org.bankai.rag.StaticResource");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
//        test();
//        test02();
        test03();
    }

    public static void test03() throws Exception {
//        XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(BASE_PATH + "output.docx"));
        XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(BASE_PATH + "large_output.docx"));
        Iterator<XWPFParagraph> paragraphsIterator = xwpfDocument.getParagraphsIterator();
        StringBuilder sb = new StringBuilder();

        while (paragraphsIterator.hasNext()) {
            XWPFParagraph next = paragraphsIterator.next();
            System.out.println(next.getText());
            List<CTOMath> oMathList = next.getCTP().getOMathList();
            if (!oMathList.isEmpty()) {
                for (CTOMath ctoMath : oMathList) {
                }

            }
        }
        System.out.println(sb.toString());

    }

    private static String convertOMMLToMathML(CTOMath oMath) throws Exception {
        // 将CTOMath对象转换为DOM Document
        org.w3c.dom.Document doc = oMath.getDomNode().getOwnerDocument();

        // 转换为XML字符串
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));

        return sw.toString();
    }




    public static void test02() throws OpenXML4JException, IOException {
        XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(BASE_PATH + "word-sample.docx"));
        List<XWPFTable> tables = xwpfDocument.getTables();
        tables.forEach(System.out::println);
        List<XWPFPictureData> allPictures = xwpfDocument.getAllPictures();
        allPictures.forEach(System.out::println);
        List<PackagePart> allEmbeddedParts = xwpfDocument.getAllEmbeddedParts();
        allEmbeddedParts.forEach(System.out::println);
        List<IBodyElement> bodyElements = xwpfDocument.getBodyElements();
        List<XWPFChart> charts = xwpfDocument.getCharts();
//        System.out.println(xwpfDocument.getDocument().getBody());
        List<XWPFParagraph> paragraphs = xwpfDocument.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            System.out.println(paragraph.getText());
        }
        System.out.println("=========");
        for (IBodyElement bodyElement : bodyElements) {
            System.out.println(bodyElement);
            for (XWPFParagraph paragraph : bodyElement.getBody().getParagraphs()) {
                System.out.println(paragraph.getText());
                String pictureText = paragraph.getPictureText();
                System.out.println("pictureText: " + pictureText);
                BigInteger numID = paragraph.getNumID();
//                pictureDataByID.get
            }
            System.out.println(bodyElement.getPartType());
            System.out.println(bodyElement.getPart());
            System.out.println("body====");
            List<XWPFPictureData> allPictures1 = bodyElement.getBody().getXWPFDocument().getAllPictures();
            System.out.println("pictures====");
            allPictures1.forEach(System.out::println);
        }
    }

    public static void test() throws FileNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream("SpringAi-Demo/src/main/resources/word-sample.doc");
             HWPFDocument word = new HWPFDocument(fileInputStream)) {
            Range range = word.getRange();
            System.out.println(range.text());
            System.out.println(range.text().length());
            for (int i = 0; i < 149; i++) {
                Paragraph paragraph = range.getParagraph(i);
                boolean inTable = paragraph.isInTable();
                boolean tableRowEnd = paragraph.isTableRowEnd();
                System.out.println(inTable);
                System.out.println(tableRowEnd);
                if (inTable || tableRowEnd) {
                    System.out.println(i);
                    System.out.println(paragraph.text());
                    Table table = range.getTable(paragraph);
                    int i1 = table.numRows();

                    int tableLevel = table.getTableLevel();
                    System.out.println("rows : " + i1);
                    System.out.println("tablelevel : " + tableLevel);
                    int rows = table.numRows();
                    for (int i2 = 0; i2 < rows; i2++) {
                        TableRow row = table.getRow(i2);
                        int i3 = row.numCells();
                        for (int i4 = 0; i4 < i3; i4++) {
                            TableCell cell = row.getCell(i4);
                            int i5 = cell.numParagraphs();
                            // 合并单元格内的所有段落文本
                            StringBuilder stringBuilder = new StringBuilder();

                            for (int p = 0; p < i5; p++) {
                                Paragraph para = cell.getParagraph(p);
                                stringBuilder.append(para.text());
                            }
                            System.out.println(stringBuilder);
                        }
                    }


                }

            }
            try {
                int a = 1 / 0;
            } catch (Exception e) {
                throw new FileNotFoundException();
            }

            int i = range.numCharacterRuns(); //544个字符
            System.out.println(i);
            CharacterRun characterRun = range.getCharacterRun(2);
            CharacterRun characterRun1 = range.getCharacterRun(3);
            System.out.println(characterRun1.toString());
            System.out.println(characterRun.text());

            int i1 = range.numParagraphs(); //149段 \r\n为换行
            System.out.println(i1);
            int i2 = range.numSections(); //1个section
            System.out.println(i2);
            Paragraph paragraph = range.getParagraph(148);
            String text = paragraph.text();
            // ======================

            int i3 = word.characterLength(); //bit位
            System.out.println(i3 / 8);
            // ======================

            Bookmarks bookmarks = word.getBookmarks();
            System.out.println(bookmarks);
            int bookmarksCount = bookmarks.getBookmarksCount();
            System.out.println(bookmarksCount);
            Bookmark bookmark = bookmarks.getBookmark(2);
            int start = bookmark.getStart();
            System.out.println(start / 8);
            String name = bookmark.getName();
            System.out.println(name);
            System.out.println("=================summary");

            DocumentSummaryInformation documentSummaryInformation = word.getDocumentSummaryInformation();
            System.out.println("==================");
            System.out.println(documentSummaryInformation.getApplicationVersion()); //727256
            System.out.println(documentSummaryInformation.getByteCount());
            System.out.println(documentSummaryInformation.getCategory());
            System.out.println(documentSummaryInformation.getLanguage());
            System.out.println("==================");
            List<Section> sections = documentSummaryInformation.getSections();
            System.out.println(sections.size());

            System.out.println("=============");
            PicturesTable picturesTable = word.getPicturesTable();
            List<Picture> allPictures = picturesTable.getAllPictures();
            System.out.println(allPictures.size());
            for (Picture picture : allPictures) {
                byte[] content = picture.getContent();
                String mimeType = picture.getMimeType();
                System.out.println(mimeType);
                System.out.println(picture.getDescription());
                Base64.Encoder encoder = Base64.getEncoder();
                byte[] encode = encoder.encode(content);
                System.out.println(new String(encode));
                try (FileOutputStream output = new FileOutputStream(UUID.randomUUID() + "."
                        + mimeType.substring(mimeType.lastIndexOf("/") + 1))) {
                    output.write(content);
                } catch (Exception ignore) {

                }

            }
            System.out.println("=======table");
            TextPieceTable textTable = word.getTextTable();
            System.out.println(textTable.getText());
            System.out.println("=======dir");
            DirectoryNode directory = word.getDirectory();
            for (Entry entry : directory) {
                String name1 = entry.getName();
                System.out.println(name1);
            }
            System.out.println(directory.getEntryCount());
            System.out.println("=================");
            Fields fields = word.getFields();
            Collection<Field> fields1 = fields.getFields(FieldsDocumentPart.ANNOTATIONS);
            System.out.println("================");

//            try (FileOutputStream fileOutputStream = new FileOutputStream(UUID.randomUUID() + ".doc")) {
//                fileOutputStream.write(dataStream);
//            }
            byte[] mainStream = word.getMainStream();
            System.out.println(new String(mainStream));
            PAPBinTable paragraphTable = word.getParagraphTable();
            ArrayList<PAPX> paragraphs = paragraphTable.getParagraphs();
            for (PAPX papx : paragraphs) {
                System.out.println(papx);
            }
            System.out.println("=====table");
            textTable = word.getTextTable();
            System.out.println(textTable.getText());
            List<TextPiece> textPieces = textTable.getTextPieces();
            for (TextPiece textPiece : textPieces) {
                System.out.println(textPiece.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void test1() {
        String base = "SpringAi-Demo/src/main/resources/";
        String path = base + "word-sample.docx";
//        path = "classpath:word-sample.docx";
        path = "classpath:/1.doc";
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(path);
        List<Document> read = tikaDocumentReader.read();
        read.forEach(a -> {
            Map<String, Object> metadata = a.getMetadata();
            System.out.println(metadata);
            Media media = a.getMedia();
            if (media != null) {
                MimeType mimeType = media.getMimeType();
                System.out.println("=======mimetype");
                System.out.println(media);
                System.out.println(mimeType);
            }
//            MimeType mimeType = media.getMimeType();
//            byte[] dataAsByteArray = media.getDataAsByteArray();
//            ContentFormatter contentFormatter = a.getContentFormatter();
        });
    }


}
