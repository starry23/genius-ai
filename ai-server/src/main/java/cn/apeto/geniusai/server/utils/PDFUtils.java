package cn.apeto.geniusai.server.utils;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/03/17:45
 * @Description:
 */
@Slf4j
public class PDFUtils {
    public static void main(String[] args) {

        String s =
                readPDF(new File("C:\\Users\\86135\\Documents\\WeChat Files\\wxid_olqvljttuxg222\\FileStorage\\File\\2023-07\\文心千帆客户开通指引及资料汇总(1)(1).pdf"));
        System.out.println(s);
    }

    /*
     * 读PDF文件，使用了pdfbox开源项目
     */
    public static String readPDF(File file) {
        StringBuilder resString = new StringBuilder();
        try {

            // 获取解析后得到的PDF文档对象
            PDDocument pdDocument = PDDocument.load(file);

            //新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            //sort设置为true 则按照行进行读取，默认是false
            stripper.setSortByPosition(true);

            // 写入到文件

            for(int i=1;i<=pdDocument.getNumberOfPages();i++) {
                //本页所有文字
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String result;
                try {
                    result = stripper.getText(pdDocument);
                } catch (IOException e) {
                    log.warn("解析文本出错:{}",e.getMessage());
                    continue;
                }
                resString.append(RegexUtils.filterSpecialCharacters(result));
                //读取图片
                PDPage page = pdDocument.getPage(i-1);
                PDResources pdResources = page.getResources();

                // 本页所有图片
                String pictureText = "";
                for(COSName csName : pdResources.getXObjectNames()) {
                    PDXObject pdxObject = pdResources.getXObject(csName);
                    if(pdxObject instanceof PDImageXObject){
                        // 图片转文字
                        PDStream pdStream = pdxObject.getStream();
                        PDImageXObject image = new PDImageXObject(pdStream, pdResources);
                        BufferedImage imgBuff = image.getImage();
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream();){
                            ImageIO.write(imgBuff, "jpeg", out);
                            byte[] imageByte = out.toByteArray();
                            String tmpString = BaiduOCRUtils.pictureToText(imageByte);
                            if(StrUtil.isNotBlank(tmpString)) {
                                resString.append(pictureText.concat("\t" + tmpString));
                            }
                            // 睡150毫秒
                            ThreadUtil.sleep(150);
                        }catch (Exception e){
                            log.warn("获取输出流异常:{}",e.getMessage());
                        }
                    }
                }
                resString.append(pictureText);
            }
            return RegexUtils.filterSpecialCharacters(resString.toString());
        }
        catch (IOException e) {
            log.error("读取PDF文件{}失败！" + e,file.getName());
        }
        return "";
    }
}
