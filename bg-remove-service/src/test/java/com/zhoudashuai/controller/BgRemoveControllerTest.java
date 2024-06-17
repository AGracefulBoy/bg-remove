package com.zhoudashuai.controller;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class BgRemoveControllerTest {
    // 根据模板导出word文档
    public static void main(String[] args) throws IOException {
        // 创建一个空的Word文档
        FileInputStream fileInputStream = new FileInputStream("/Users/zhouguobing/Desktop/moban.docx");

        XWPFDocument document = new XWPFDocument(fileInputStream);

        Document htmlDoc = Jsoup.parse("");
        Elements elements = htmlDoc.body().children();

        for (Element element : elements) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(element.text());

            if (element.tagName().equals("h1")) {
                run.setBold(true);
                run.setFontSize(20);
            } else if (element.tagName().equals("p")) {
                run.setFontSize(12);
            }
        }

        // 输出到文件
        try (FileOutputStream out = new FileOutputStream("example.docx")) {
            document.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Word文档已生成！");
    }
}