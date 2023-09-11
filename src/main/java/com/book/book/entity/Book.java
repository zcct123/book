package com.book.book.entity;

import com.book.book.util.PageReader;

import java.io.File;
import java.io.IOException;

public class Book {

    private String name;

    private String fontSize = "20";

    private String lineHeight = "1.2";

    // 文件路径
    private String path;

    private Integer page = 1;
    private Integer size = 200;

    private String backGround = "#376a59";


    public Book() {
    }

    public Book(String name, String path) {
        this.name = name;
        this.path = path;
        this.pageReader = new PageReader(path, size);
    }

    public String getBackGround() {
        return backGround;
    }

    public void setBackGround(String backGround) {
        this.backGround = backGround;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(String lineHeight) {
        this.lineHeight = lineHeight;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String toData() {
        return
                name + '|' +
                        fontSize + '|' +
                        lineHeight + '|' +
                        path + '|' +
                        page + '|' +
                        size + '|' +
                        backGround ;
    }

    public static Book toEntity(String str) {
        String[] split = str.split("\\|");
        if(split.length != 7) {
            return null;
        }

        Book book = new Book(split[0], split[3]);
        book.setFontSize(split[1]);
        book.setLineHeight(split[2]);
        book.setPage(Integer.valueOf(split[4]));
        book.setSize(Integer.valueOf(split[5]));
        book.setBackGround(split[6]);

        File file = new File(book.getPath());
        if(!file.exists()) {
            return null;
        }

        return book;
    }

    @Override
    public String toString() {
        return name;
    }

    private PageReader pageReader;


    public String getContent() {
        try {
            String s = this.pageReader.readPage(page);
            return s;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPageTotal() {
        return this.pageReader.getTotalPages();
    }
}
