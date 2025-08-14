package org.bankai.rag;

public enum DocumentType {
    TXT("txt", "纯文本文件"),
    MD("md", "Markdown文件"),
    DOCX("docx", "Word文档"),
    DOC("doc", "Word文档"),
    PDF("pdf", "PDF文档"),
    PPT("ppt", "PPT"),
    PPTX("pptx", "PPTX"),
    HTML("html", "HTML文件");


    private final String extension;
    private final String description;

    DocumentType(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    public String getExtension() { return extension; }
    public String getDescription() { return description; }

    public static DocumentType fromFileName(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        for (DocumentType type : values()) {
            if (type.getExtension().equals(ext)) return type;
        }
        throw new IllegalArgumentException("Unsupported file type: " + ext);
    }

    @Override
    public String toString() {
        return "DocumentType{" +
                "extension='" + extension + '\'' +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }

    public static void main(String[] args) {
        DocumentType documentType = DocumentType.fromFileName("a.docx");
        System.out.println(documentType);
    }
}