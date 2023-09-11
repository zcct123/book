package com.book.book.entity;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {
    private static String BOOK_FILE_PATH = "./book";
    private static String MARK_FILE_PATH = "./bookMark";


    public static void writeBook(List<Book> book) throws IOException {
        File file = new File(BOOK_FILE_PATH);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(BOOK_FILE_PATH)));
        book.forEach(item -> {
            try {
                writer.write(item.toData());
                writer.write("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        writer.close();
    }

    // 读 books
    public static List<Book> readBook() throws IOException {

        List<Book> books = new ArrayList<>();
        File file = new File(BOOK_FILE_PATH);
        if(!file.exists()) {
            return new ArrayList<>();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if(line == null || "".equals(line)) {
                continue;
            }
            Book entity = Book.toEntity(line);
            if(entity != null) {
                books.add(entity);
            }
        }
        reader.close();

        return books;
    }

    // 写配置
    public static void writeMark(List<BookMark> mark) throws IOException {
        File file = new File(MARK_FILE_PATH);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(MARK_FILE_PATH)));
        mark.forEach(item -> {
            try {
                writer.write(item.toData());
                writer.write("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        writer.close();
    }

    // 读 books
    public static List<BookMark> readMark() throws FileNotFoundException, IOException {

        List<BookMark> bookMarks = new ArrayList<>();
        File file = new File(MARK_FILE_PATH);
        if(!file.exists()) {
            return new ArrayList<>();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if(line == null || "".equals(line)) {
                continue;
            }
            BookMark entity = BookMark.toEntity(line);
            if(entity != null) {
                bookMarks.add(entity);
            }
        }
        reader.close();
        return bookMarks;
    }
}
