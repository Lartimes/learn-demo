package org.bankai.rag.test;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;

//OMML → MathML
//MathML → LaTeX
public class OMML2MathML {
    public static void main(String[] args) throws Exception {
        String omml = """
                <xml-fragment
                        xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                        xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                >
                    <m:f>
                        <m:fPr>
                            <m:ctrlPr>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:fPr>
                        <m:num>
                            <m:r>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                </w:rPr>
                                <m:t>a</m:t>
                            </m:r>
                            <m:ctrlPr>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:num>
                        <m:den>
                            <m:func>
                                <m:funcPr>
                                    <m:ctrlPr>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:funcPr>
                                <m:fName>
                                    <m:r>
                                        <m:rPr>
                                            <m:sty m:val="p"/>
                                        </m:rPr>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                        <m:t>sin</m:t>
                                    </m:r>
                                    <m:ctrlPr>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                            <w:i/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:fName>
                                <m:e>
                                    <m:r>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                        <m:t>A</m:t>
                                    </m:r>
                                    <m:ctrlPr>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                            <w:i/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:e>
                            </m:func>
                            <m:ctrlPr>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:den>
                    </m:f>
                    <m:r>
                        <m:rPr/>
                        <w:rPr>
                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                        </w:rPr>
                        <m:t>=</m:t>
                    </m:r>
                    <m:f>
                        <m:fPr>
                            <m:ctrlPr>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:fPr>
                        <m:num>
                            <m:r>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                </w:rPr>
                                <m:t>b</m:t>
                            </m:r>
                            <m:ctrlPr>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:num>
                        <m:den>
                            <m:func>
                                <m:funcPr>
                                    <m:ctrlPr>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:funcPr>
                                <m:fName>
                                    <m:r>
                                        <m:rPr>
                                            <m:sty m:val="p"/>
                                        </m:rPr>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                        <m:t>sin</m:t>
                                    </m:r>
                                    <m:ctrlPr>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                            <w:i/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:fName>
                                <m:e>
                                    <m:r>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                        <m:t>B</m:t>
                                    </m:r>
                                    <m:ctrlPr>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                            <w:i/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:e>
                            </m:func>
                            <m:ctrlPr>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:den>
                    </m:f>
                    <m:r>
                        <m:rPr/>
                        <w:rPr>
                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                        </w:rPr>
                        <m:t>=</m:t>
                    </m:r>
                    <m:f>
                        <m:fPr>
                            <m:ctrlPr>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:fPr>
                        <m:num>
                            <m:r>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                </w:rPr>
                                <m:t>c</m:t>
                            </m:r>
                            <m:ctrlPr>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:num>
                        <m:den>
                            <m:func>
                                <m:funcPr>
                                    <m:ctrlPr>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:funcPr>
                                <m:fName>
                                    <m:r>
                                        <m:rPr>
                                            <m:sty m:val="p"/>
                                        </m:rPr>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                        <m:t>sin</m:t>
                                    </m:r>
                                    <m:ctrlPr>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                            <w:i/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:fName>
                                <m:e>
                                    <m:r>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                        </w:rPr>
                                        <m:t>C</m:t>
                                    </m:r>
                                    <m:ctrlPr>
                                        <m:rPr/>
                                        <w:rPr>
                                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                            <w:i/>
                                        </w:rPr>
                                    </m:ctrlPr>
                                </m:e>
                            </m:func>
                            <m:ctrlPr>
                                <m:rPr/>
                                <w:rPr>
                                    <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                                    <w:i/>
                                </w:rPr>
                            </m:ctrlPr>
                        </m:den>
                    </m:f>
                    <m:r>
                        <m:rPr/>
                        <w:rPr>
                            <w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"/>
                        </w:rPr>
                        <m:t>=2R</m:t>
                    </m:r>
                </xml-fragment>
                """;
        omml = """
                <?xml version="1.0" encoding="utf-8" ?><m:oMath xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas" xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup" xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml" xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:wpsCustomData="http://www.wps.cn/officeDocument/2013/wpsCustomData" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape"><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>w=∆k=</m:t></m:r><m:f><m:fPr><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:fPr><m:num><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>1</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:num><m:den><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>2</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:den></m:f><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>m</m:t></m:r><m:sSup><m:sSupPr><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:sSupPr><m:e><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>v</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:e><m:sup><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>2</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:sup></m:sSup><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>−</m:t></m:r><m:f><m:fPr><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:fPr><m:num><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>1</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:num><m:den><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>2</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:den></m:f><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>m</m:t></m:r><m:sSubSup><m:sSubSupPr><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:sSubSupPr><m:e><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>v</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:e><m:sub><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>0</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:sub><m:sup><m:r><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts></w:rPr><m:t>2</m:t></m:r><m:ctrlPr><m:rPr></m:rPr><w:rPr><w:rFonts w:ascii="Cambria Math" w:hAnsi="Cambria Math" w:cs="Arial"></w:rFonts><w:b w:val="0"></w:b><w:i></w:i></w:rPr></m:ctrlPr></m:sup></m:sSubSup></m:oMath>
                """;
        System.out.println(convert(omml));
    }

    public static String convert(String omml) throws Exception {
        // 加载Office自带的OMML转MathML的XSL文件（通常在Office安装目录下，或网上下载）
        StreamSource xslSource = new StreamSource("C:\\Users\\Administrator\\Downloads\\OMML2MML.XSL");
        Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
        // 执行转换
        StringWriter result = new StringWriter();
        transformer.transform(
                new StreamSource(new java.io.StringReader(omml)),
                new StreamResult(result)
        );
        return result.toString();
    }
}