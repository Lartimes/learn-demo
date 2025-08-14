package org.bankai.rag.test;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
//StAX	极低	超大型文件、严格控制内存
//SAX（当前）	低	大文件、只需顺序处理元素

// 更轻量的纯SAX解析实现（不依赖XWPFDocument）
public class LightweightDocxParser {
    private static final String W_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
    private static final String M_NS = "http://schemas.openxmlformats.org/officeDocument/2006/math";

    private static final String BASE_PATH = "SpringAi-Demo/src/main/resources/";

    public static void main(String[] args) throws Exception {
//        parse(BASE_PATH + "word-sample.docx");
//        parse(BASE_PATH + "output.docx");
//        parse(BASE_PATH + "large_output.docx");
        parse(BASE_PATH + "test.docx");
    }

    public static void parse(String filePath) throws Exception {
        ZipSecureFile.setMinInflateRatio(0.005); // 允许更低的压缩比
        File file = new File(filePath);
        try (OPCPackage opcPackage = OPCPackage.open(file)) {
            List<PackagePart> partsByName = opcPackage.getPartsByName(Pattern.compile("/word/document.xml"));
            for (PackagePart packagePart : partsByName) {
                InputStream docStream = packagePart.getInputStream();
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                saxParserFactory.setNamespaceAware(true); // 启用命名空间支持

                // 创建 SAXParser 并解析
                SAXParser parser = saxParserFactory.newSAXParser();
                DocHandler handler = new DocHandler(opcPackage);
                parser.parse(docStream, handler);
                System.out.println(handler.set);
                System.out.println(handler.relationships);
            }
        }
    }

    static class DocHandler extends DefaultHandler {
        private final OPCPackage opcPackage;
        private final StringBuilder currentText = new StringBuilder(); //文本buffer
        private final StringBuilder latexBuffer = new StringBuilder(); //公式buffer
        private final StringBuilder tableBuffer = new StringBuilder(); //表格buffer
        private final Map<String, String> relationships = new HashMap<>();
        private final Set<String> set = new HashSet<>();
        private final Map<String, String> nsMap = new HashMap<>();
        private boolean inParagraph = false;
        private boolean inTable = false;
        private boolean inFormula = false;
        private String formulaRootTag = null;

        public DocHandler(OPCPackage opcPackage) {
            this.opcPackage = opcPackage;
            // 加载关系映射（用于解析图片路径）
            loadRelationships();
        }

        // 加载关系映射（用于解析图片路径）
        private void loadRelationships() {
            try {
                List<PackagePart> partsByName = opcPackage.getPartsByName(Pattern.compile("/word/_rels/document.xml.rels"));
                for (PackagePart packagePart : partsByName) {
                    InputStream relStream = packagePart.getInputStream();
                    XMLReader reader = XMLReaderFactory.createXMLReader();
                    reader.setContentHandler(new DefaultHandler() {
                        @Override
                        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                            if ("Relationship".equals(localName)) {
                                String id = attributes.getValue("Id");
                                String target = attributes.getValue("Target");
                                if (id != null && target != null) {
                                    relationships.put(id, target);
                                }
                            }
                        }
                    });
                    reader.parse(new InputSource(relStream));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            super.startPrefixMapping(prefix, uri);
            System.out.println("start prefix:" + prefix + "   uri:" + uri);
            nsMap.put(prefix, uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            super.endPrefixMapping(prefix);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            set.add(localName);
            if (M_NS.equals(uri) && ("oMath".equals(localName) || "oMathPara".equals(localName))) {
                inFormula = true;
                formulaRootTag = qName; // 记录根标签名
                latexBuffer.setLength(0); // 清空缓冲
                latexBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
                latexBuffer.append("<").append(qName);
                for (Map.Entry<String, String> entry : nsMap.entrySet()) {
                    latexBuffer.append(" xmlns");
                    if (!entry.getKey().isEmpty()) {
                        latexBuffer.append(":").append(entry.getKey());
                    }
                    latexBuffer.append("=\"").append(entry.getValue()).append("\"");
                }

                for (int i = 0; i < attributes.getLength(); i++) {
                    latexBuffer.append(" ")
                            .append(attributes.getQName(i))
                            .append("=\"")
                            .append(attributes.getValue(i))
                            .append("\"");
                }
                latexBuffer.append(">");
            } else if (inFormula) {
                // 公式内部所有标签都收集
                latexBuffer.append("<").append(qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    latexBuffer.append(" ")
                            .append(attributes.getQName(i))
                            .append("=\"")
                            .append(attributes.getValue(i))
                            .append("\"");
                }
                latexBuffer.append(">");
            } else if (W_NS.equals(uri) && "p".equals(localName)) {
                inParagraph = true;
                System.out.println("\n新段落");
            } else if (W_NS.equals(uri) && "tbl".equals(localName)) {
                inTable = true;
                System.out.println("\n新表格");
            } else if (W_NS.equals(uri) && "t".equals(localName)) {
                currentText.setLength(0); // 清空文本缓冲区
//            } else if (W_NS.equals(uri) && "drawing".equals(localName)) {
//                String embedId = extractImageId(attributes);
//                String value = attributes.getValue(1);
//                if (embedId != null) {
//                    processImage(embedId);
//                }
            } else if ("blip".equals(localName) && "http://schemas.openxmlformats.org/drawingml/2006/main".equals(uri)) {
                String embedId = attributes.getValue("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "embed");
                if (embedId != null) {
                    processImage(embedId);
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (W_NS.equals(uri) && "p".equals(localName)) {
                inParagraph = false;
            } else if (W_NS.equals(uri) && "tbl".equals(localName)) {
                inTable = false;
            } else if (inFormula) {

                latexBuffer.append("</").append(qName).append(">");
                if (M_NS.equals(uri) && "oMath".equals(localName)) {
                    // 公式根标签结束
                    if (qName.equals(formulaRootTag)) {
                        System.out.println(formulaRootTag);
                        inFormula = false;
                        String ommlXml = latexBuffer.toString();
                        // 这里就是完整的 OMML 公式（带根标签）
                        System.out.println("公式OMML片段：\n" + ommlXml);
                    }
                }
            } else if (W_NS.equals(uri) && "t".equals(localName)) {
                System.out.println("  文本: " + currentText);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inFormula) {
                System.out.println("test: " + new String(ch, start, length));
                latexBuffer.append(ch, start, length);
            } else if (inTable) {
                System.out.println("在表格里面");
            }
            if (inParagraph) {
                currentText.append(ch, start, length);
            }

        }

        // 处理图片
        private void processImage(String embedId) {
            String imagePath = relationships.get(embedId);
            if (imagePath != null && imagePath.startsWith("media/")) {
                try {
                    List<PackagePart> partsByName = opcPackage.getPartsByName(Pattern.compile("/word/" + imagePath));

                    for (PackagePart packagePart : partsByName) {
                        InputStream imageStream = packagePart.getInputStream();
                        byte[] bytes = imageStream.readAllBytes();
                        try (var out = new FileOutputStream(UUID.randomUUID() + "." + imagePath.substring(imagePath.indexOf(".") + 1))) {
                            out.write(bytes);
                        }
                        // 保存图片或处理二进制数据
                        System.out.println("  图片: " + imagePath);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}