module com.book.book {
    requires javafx.controls;
    requires javafx.fxml;

        requires org.controlsfx.controls;
                        requires org.kordamp.bootstrapfx.core;
    requires javafx.media;

    opens com.book.book to javafx.fxml;
    exports com.book.book;
    exports com.book.book.view;
    opens com.book.book.view to javafx.fxml;
}
