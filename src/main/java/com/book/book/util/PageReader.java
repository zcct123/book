package com.book.book.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageReader {
    private String filePath;
    private int pageSize;
    private int totalPages;

    public PageReader(String filePath, int pageSize) {
        this.filePath = filePath;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) getContentLength() / pageSize);
    }

    public int getTotalPages() {
        return totalPages;
    }


    public String readPage(int pageNum) throws IOException {
        String line;
        int pageContentStart = (pageNum - 1) * pageSize;
        int pageContentEnd = pageNum * pageSize;
        StringBuilder pageContent = new StringBuilder();

        long length = 0;

        try (FileReader reader = new FileReader(filePath)) {
            char[] c = new char[1];
            while ((reader.read(c)) != -1) {
                if(length >= pageContentStart && length<pageContentEnd) {
                    pageContent.append(c);
                }
                if(length >= pageContentEnd) {
                    continue;
                }
                length++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageContent.toString();

    }

    private long getContentLength() {
        long length = 0;
        try (FileReader reader = new FileReader(filePath)) {
            char[] c = new char[1];
            while ((reader.read(c)) != -1) {
                length++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length;
    }

    public static void main(String[] args) throws IOException {
        String filePath = "/Users/zhaochong/work/idea_work/book/《铁血宏图》.txt";
        int pageSize = 200;
        PageReader reader = new PageReader(filePath, pageSize);
        for (int pageNum = 1; pageNum <= reader.getTotalPages(); pageNum++) {

            System.out.println("Page " + pageNum + ": " + reader.readPage(pageNum));
        }
    }
}
