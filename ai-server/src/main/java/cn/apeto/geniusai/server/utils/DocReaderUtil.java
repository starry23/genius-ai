package cn.apeto.geniusai.server.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class DocReaderUtil {

    public static String readDoc(File file) throws IOException {
        try (InputStream fis = Files.newInputStream(file.toPath());
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {
            return RegexUtils.filterSpecialCharacters(extractor.getText());
        }
    }

    public static String readDocx(File file) throws IOException {
        try (InputStream fis = Files.newInputStream(file.toPath());
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return RegexUtils.filterSpecialCharacters(extractor.getText());
        }
    }



    public static void main(String[] args) throws IOException {
        System.out.println(readDocx(new File("/Users/wanmingyu/Downloads/新支付接口文档new2.0.docx")));
    }
}
