package com.book.book.entity;

import java.util.HashMap;
import java.util.Map;

public class BookMark {

    private String name;
    private String path;

    private Integer page;


    public BookMark(String name, String path, Integer page) {
        this.name = name;
        this.path = path;
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String toData() {
        return  name + '|' +
                 path + '|' +
                page ;
    }

    public static BookMark toEntity(String str) {
        String[] split = str.split("\\|");
        if(split.length != 3) {
            return null;
        }

        BookMark bookMark = new BookMark(split[0],split[1],Integer.valueOf(split[2]));
        return bookMark;
    }

    @Override
    public String toString() {
        return name;
    }
}
