package com.book.book;

import com.book.book.entity.Book;
import com.book.book.entity.BookMark;
import com.book.book.entity.ConfigUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * 功能需求：
 * 可导入文本文件中的内容，以分页的方式显示在 Java 图形界面上。
 * 可以左右翻页。
 * 可设置该阅读器的字体大小，行间距，背景颜色，世可设置间隔时间，使之自动翻页。
 * 可插入书签，记录当前阅读的位 置到文件中。下次启动时，可载入书签，以方便阅读。
 * 可以记录阅读过的文本文件，形成历史记录，支持后期的随时载入。
 * 技术要求：
 * 工、课程设计必须使用面向对象中的封装性、继承性以及多态性（类、继承、抽象类、接口、多态)，且类设计必须合理
 * 12、可视化一律采用 JavaFX（不允许使用 Swing 或 AWT)
 * 13、所有题目均要设计数据存储，且数据存储采用文件（文本文件或
 * •二进制文件）
 */
public class BookApplication extends Application {

    public static List<BookMark> bookMarkList;
    public static  List<Book> bookList;

    static {
        try {
            bookMarkList = ConfigUtils.readMark();
            bookList = ConfigUtils.readBook();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BookApplication.class.getResource("book-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);
        stage.setTitle("阅读器");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


    public static void saveBook(){
        try {
            ConfigUtils.writeBook(bookList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBookMark(){
        try {
            ConfigUtils.writeMark(bookMarkList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
