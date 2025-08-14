package org.bankai.rag;

import org.apache.poi.openxml4j.util.ZipSecureFile;

public class StaticResource {
    static {
        ZipSecureFile.setMinInflateRatio(0.005); // 允许更低的压缩比
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

    }
}
