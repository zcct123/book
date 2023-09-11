package com.book.book.view;

import com.book.book.BookApplication;
import com.book.book.entity.Book;
import com.book.book.entity.BookMark;
import com.book.book.entity.ConfigUtils;
import com.book.book.util.PageReader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class BookController implements Initializable {


    private boolean automaticPageFlag = false;
    private Integer automaticPageTime = 3;

    private Book currBook;

    @FXML
    public BorderPane root;

    @FXML
    public ChoiceBox<String> fontSizeChoiceBox;

    @FXML
    public ChoiceBox<String> lineHeightBox;

    @FXML
    private ListView<Book> bookListView;
    @FXML
    private ListView<BookMark> markListView;

    @FXML
    private TextArea textAera;

    @FXML
    private MenuItem openMenu;

    @FXML
    private Pagination pagination;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button markButton;

    @FXML
    private CheckBox checkBox;

    @FXML
    private Button delMark;


    @FXML
    protected void openMenuAction(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择txt");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("文本类型","*.txt"));
        File file = fileChooser.showOpenDialog((Stage) root.getScene().getWindow());
        //避免空指针异常
        if(file == null){
            return;
        }
        Book book = new Book(file.getName(), file.getPath());
        BookApplication.bookList.add(book);
        bookListView.getItems().add(book);
        BookApplication.saveBook();
        this.currBook = book;
        showBook();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Book> books = null;
        try {
            books = ConfigUtils.readBook();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObservableList<Book> strList = FXCollections.observableArrayList(books);
        bookListView.setItems(strList);
        bookListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Book>() {
            @Override
            public void changed(ObservableValue<? extends Book> observableValue, Book book, Book t1) {
                Book book1 = BookApplication.bookList.stream().filter(item -> {
                    return item.getPath().equals(t1.getPath());
                }).collect(Collectors.toList()).get(0);
                bookListViewClick(book1);
            }
        });

        pagination.setDisable(true);

        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer integer) {
                toPage(integer + 1);
                Label mLabel = new Label();
                mLabel.setText("这是第" + (integer+1) + "页");
                return mLabel;
            }
        });

        ObservableList<String> fontSizeConvertList = FXCollections.observableArrayList("14","16","18","20","22","24","26","28");
        fontSizeChoiceBox.setItems(fontSizeConvertList);
        ObservableList<String> lineHeightConvertList = FXCollections.observableArrayList("1","1.5","2","2.5","3");
        lineHeightBox.setItems(lineHeightConvertList);

        fontSizeChoiceBox.setDisable(true);
        lineHeightBox.setDisable(true);

        fontSizeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                fontSizeClick(t1);
            }
        });

        lineHeightBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                lineHeighClick(t1);
            }
        });

        colorPicker.setDisable(true);
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                colorPickerClick(colorPicker.getValue());
            }
        });

        markButton.setDisable(true);

        markButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(noHasBookError()) {
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("添加书签");
                dialog.setContentText("输入书签名称:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()){
                    String s = result.get();
                    saveMark(s);
                }

            }});

        markListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookMark>() {
            @Override
            public void changed(ObservableValue<? extends BookMark> observableValue, BookMark t2, BookMark t1) {
                changeMark(t1);
            }
        });

        checkBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(checkBox.isSelected()) {
                    toAutomaticPage();
                }else {
                    toStopAutomaticPage();
                }
            }
        });

        // 启动自动翻页
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(automaticPageTime * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if( automaticPageFlag && this.currBook != null  && this.currBook.getPage() < this.currBook.getPageTotal()) {

                    Platform.runLater(() -> {
                        this.currBook.setPage(this.currBook.getPage() + 1);
                        this.pagination.setCurrentPageIndex(this.currBook.getPage() - 1);
                     //   this.pagination.setPageCount(this.currBook.getPageTotal());
                        showBook();
                    });

                }
            }

        });
        thread.start();

        delMark.setOnAction(e -> {
            int selectedIndex = markListView.getSelectionModel().getSelectedIndex();
            BookMark selectedItem = markListView.getSelectionModel().getSelectedItem();
            if (selectedIndex != -1) {
                markListView.getItems().remove(selectedIndex);
                BookMark book1 = BookApplication.bookMarkList.stream().filter(item -> {
                    return item.getPath().equals(selectedItem.getPath());
                }).collect(Collectors.toList()).get(0);
                BookApplication.bookMarkList.remove(book1);
                BookApplication.saveBookMark();
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("提示");
                alert.setContentText("请选择书签");
                alert.showAndWait();
            }

        });
    }

    private void toStopAutomaticPage() {
        this.automaticPageFlag = false;
    }

    private void toAutomaticPage() {
        TextInputDialog dialog = new TextInputDialog("3");
        dialog.setTitle("设置自动翻页时间");
        dialog.setHeaderText("");
        dialog.setContentText("输入自动翻页时间(s):");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String s = result.get();
            if(isNumeric2(s)) {
                this.automaticPageFlag = true;
                this.automaticPageTime = Integer.valueOf(s);
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("提示");
                alert.setContentText("请输入纯数字");
                alert.showAndWait();
            }
        }
    }

    private void saveMark(String s) {
        BookMark bookMark = new BookMark(s, this.currBook.getPath(), this.currBook.getPage());
        BookApplication.bookMarkList.add(bookMark);
        BookApplication.saveBookMark();
        showMark();
    }

    private boolean noHasBookError() {
        boolean flag = this.currBook == null;
        if(flag) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("提示");
            alert.setContentText("未打开文本");
            alert.showAndWait();
        }

        return flag;
    }

    private void colorPickerClick(Color value) {
        this.currBook.setBackGround("#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase());
        showBook();
    }

    private void lineHeighClick(String t1) {
        this.currBook.setLineHeight(t1);
        showBook();
    }

    private void fontSizeClick(String t1) {
        this.currBook.setFontSize(t1);
        showBook();
    }

    private void toPage(Integer integer) {
        if(this.currBook == null) return;
        this.currBook.setPage(integer);
        showBook();
        tip();
    }

    // 左侧图书点击事件
    private void bookListViewClick(Book book) {
        this.currBook = null;

        showBook();
        this.currBook = book;
        this.currBook.setPage(this.currBook.getPage());
    //    this.pagination.setPageCount(this.currBook.getPageTotal());
        this.pagination.setCurrentPageIndex(this.currBook.getPage()-1);
        // 设置 字号 行间距  背景色 默认值
        fontSizeChoiceBox.setValue(book.getFontSize());
        lineHeightBox.setValue(book.getLineHeight());
        colorPicker.setValue(Color.web(this.currBook.getBackGround()));
        showMark();
    }

    private void showMark() {
        Map<String, List<BookMark>> bookMarkMap = BookApplication.bookMarkList.stream().collect(Collectors.toMap(BookMark::getPath, part -> {
            List<BookMark> list = new ArrayList<>();
            list.add(part);
            return list;
        }, (List<BookMark> newValueList, List<BookMark> oldValueList) ->
        {
            oldValueList.addAll(newValueList);
            return oldValueList;
        }));
        List<BookMark> bookMarks = bookMarkMap.get(this.currBook.getPath());
        if(bookMarks == null) bookMarks = new ArrayList<>();
        ObservableList<BookMark> strList = FXCollections.observableArrayList(bookMarks);
        if( markListView.getItems() != null) {
            markListView.getItems().clear();

        }
        markListView.setItems(strList);
    }

    private void changeMark(BookMark t1) {
        if(this.currBook != null&& t1 != null) {
            this.currBook.setPage(t1.getPage() - 1);
            this.pagination.setCurrentPageIndex(t1.getPage() - 1);
          //  this.pagination.setPageCount(this.currBook.getPageTotal());
            showBook();
        }
    }

    private void showBook() {
        if(this.currBook == null) return;
        BookApplication.saveBook();
        String content = this.currBook.getContent();
        this.textAera.setText(content);
        this.textAera.setStyle(
                        "-fx-font-size: " + this.currBook.getFontSize()+"px;" + "-fx-line-spacing: " + this.currBook.getLineHeight());

        Region region = (Region) this.textAera.lookup(".content");
        Text text = (Text) this.textAera.lookup(".text");
        text.setStyle("-fx-line-spacing: " + this.currBook.getLineHeight());
        region.setBackground(new Background(new BackgroundFill(Color.web(this.currBook.getBackGround()), CornerRadii.EMPTY, Insets.EMPTY)));
    //    pagination.setPageCount(this.currBook.getPageTotal());
        pagination.setDisable(false);
        lineHeightBox.setDisable(false);
        fontSizeChoiceBox.setDisable(false);
        colorPicker.setDisable(false);
        markButton.setDisable(false);


    }

    // Helper method
    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public static boolean isNumeric2(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    public  void tip() {
        URL resource = getClass().getClassLoader().getResource("mp.mp3");
        Media sound = new Media("file:"+resource.getPath());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}
