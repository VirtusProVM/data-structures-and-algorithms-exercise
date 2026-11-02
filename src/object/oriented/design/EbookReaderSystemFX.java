package object.oriented.design;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
Write a Java program that simulates a system that supports the functions of an ebook
reader. You should include methods for users of your system to “buy” new
books, view their list of purchased books, and read their purchased books. Your
system should use actual books, which have expired copyrights and are available
on the Internet, to populate your set of available books for users of your system
to “purchase” and read.
*/

public class EbookReaderSystemFX extends Application {

    private final User user = new User("Alice");
    private final EbookStore store = new EbookStore();

    private ListView<Book> catalogList;
    private ListView<Book> purchasedList;
    private TextArea detailsArea;
    private TextArea readingArea;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        catalogList = new ListView<>(store.getAvailableBooks());
        purchasedList = new ListView<>(user.getPurchasedBooks());

        catalogList.setPrefWidth(260);
        purchasedList.setPrefWidth(260);

        Button buyButton = new Button("Buy Selected Book");
        Button readButton = new Button("Read Selected Book");
        Button refreshButton = new Button("Refresh");

        statusLabel = new Label("Select a book to view details.");
        statusLabel.setWrapText(true);

        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setPrefHeight(180);
        detailsArea.setWrapText(true);

        readingArea = new TextArea();
        readingArea.setEditable(false);
        readingArea.setPrefHeight(220);
        readingArea.setWrapText(true);

        Label catalogLabel = new Label("Available Books");
        Label purchasedLabel = new Label("Purchased Books");
        Label infoLabel = new Label("Book Information");
        Label readLabel = new Label("Reading View");

        catalogLabel.setFont(Font.font(14));
        purchasedLabel.setFont(Font.font(14));
        infoLabel.setFont(Font.font(14));
        readLabel.setFont(Font.font(14));

        catalogList.getSelectionModel().selectedItemProperty().addListener((obs, oldBook, newBook) -> {
            if (newBook != null) {
                detailsArea.setText(
                        "Title: " + newBook.getTitle() + "\n" +
                        "Author: " + newBook.getAuthor() + "\n" +
                        "Price: $" + newBook.getPrice() + "\n" +
                        "Public Domain URL: " + newBook.getPublicDomainUrl() + "\n\n" +
                        "Summary:\n" + newBook.getSummary()
                );
                statusLabel.setText("Selected: " + newBook.getTitle());
            }
        });

        purchasedList.getSelectionModel().selectedItemProperty().addListener((obs, oldBook, newBook) -> {
            if (newBook != null) {
                readingArea.setText(newBook.getContent());
                statusLabel.setText("Reading: " + newBook.getTitle());
            }
        });

        buyButton.setOnAction(e -> {
            Book selected = catalogList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("No book selected to buy.");
                return;
            }

            if (user.buyBook(store, selected)) {
                statusLabel.setText("You bought: " + selected.getTitle());
                purchasedList.setItems(user.getPurchasedBooks());
            } else {
                statusLabel.setText("You already own: " + selected.getTitle());
            }
        });

        readButton.setOnAction(e -> {
            Book selected = purchasedList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("No purchased book selected to read.");
                return;
            }

            readingArea.setText(selected.getContent());
            statusLabel.setText("Reading: " + selected.getTitle());
        });

        refreshButton.setOnAction(e -> {
            purchasedList.setItems(user.getPurchasedBooks());
            statusLabel.setText("Library refreshed.");
        });

        HBox controls = new HBox(10, buyButton, readButton, refreshButton);
        controls.setPadding(new Insets(10, 0, 0, 0));

        VBox left = new VBox(8, catalogLabel, catalogList);
        VBox middle = new VBox(8, infoLabel, detailsArea, controls, statusLabel);
        VBox right = new VBox(8, purchasedLabel, purchasedList);

        middle.setPrefWidth(360);

        HBox mainLayout = new HBox(20, left, middle, right);
        mainLayout.setPadding(new Insets(15));

        VBox root = new VBox(10, mainLayout, readLabel, readingArea);
        root.setPadding(new Insets(10));

        stage.setTitle("Ebook Reader System");
        stage.setScene(new Scene(root, 1100, 700));
        stage.show();
    }

    public static class Book {
        private final String title;
        private final String author;
        private final double price;
        private final String publicDomainUrl;
        private final String summary;
        private final String content;

        public Book(String title, String author, double price, String publicDomainUrl, String summary, String content) {
            this.title = title;
            this.author = author;
            this.price = price;
            this.publicDomainUrl = publicDomainUrl;
            this.summary = summary;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public double getPrice() {
            return price;
        }

        public String getPublicDomainUrl() {
            return publicDomainUrl;
        }

        public String getSummary() {
            return summary;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return title + " by " + author + " ($" + price + ")";
        }
    }

    public static class User {
        private final String name;
        private final ObservableList<Book> purchasedBooks = FXCollections.observableArrayList();

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public ObservableList<Book> getPurchasedBooks() {
            return purchasedBooks;
        }

        public boolean buyBook(EbookStore store, Book book) {
            if (book == null) {
                return false;
            }

            for (Book existing : purchasedBooks) {
                if (existing.getTitle().equalsIgnoreCase(book.getTitle())) {
                    return false;
                }
            }

            purchasedBooks.add(book);
            return true;
        }

        public boolean owns(Book book) {
            for (Book existing : purchasedBooks) {
                if (existing.getTitle().equalsIgnoreCase(book.getTitle())) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class EbookStore {
        private final Map<String, Book> catalog = new LinkedHashMap<>();

        public EbookStore() {
            addBook(new Book(
                    "Pride and Prejudice",
                    "Jane Austen",
                    0.00,
                    "https://www.gutenberg.org/ebooks/1342",
                    "A witty exploration of manners, marriage, and class in Regency England.",
                    "It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.\n\nHowever little known the feelings or views of such a man may be on his first entering a neighbourhood, this truth is so well fixed in the minds of the surrounding families, that he is considered as the rightful property of some one or other of their daughters."
            ));

            addBook(new Book(
                    "Alice's Adventures in Wonderland",
                    "Lewis Carroll",
                    0.00,
                    "https://www.gutenberg.org/ebooks/11",
                    "A fantasy story about a curious girl who falls down a rabbit hole into a strange world.",
                    "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, 'and what is the use of a book,' thought Alice 'without pictures or conversations?'"
            ));

            addBook(new Book(
                    "The Adventures of Sherlock Holmes",
                    "Arthur Conan Doyle",
                    0.00,
                    "https://www.gutenberg.org/ebooks/1661",
                    "Classic detective stories featuring the famous Sherlock Holmes.",
                    "In the year 1878 I took my degree of Doctor of Medicine of the University of London, and proceeded to Netley to go through the course prescribed for surgeons in the army."
            ));

            addBook(new Book(
                    "A Tale of Two Cities",
                    "Charles Dickens",
                    0.00,
                    "https://www.gutenberg.org/ebooks/98",
                    "A historical novel set in London and Paris during the French Revolution.",
                    "It was the best of times, it was the worst of times..."
            ));

            addBook(new Book(
                    "The Complete Works of William Shakespeare",
                    "William Shakespeare",
                    0.00,
                    "https://www.gutenberg.org/ebooks/100",
                    "A collection of Shakespeare's plays and poetry.",
                    "To be, or not to be: that is the question..."
            ));
        }

        public ObservableList<Book> getAvailableBooks() {
            return FXCollections.observableArrayList(catalog.values());
        }

        public void addBook(Book book) {
            catalog.put(book.getTitle().toLowerCase(), book);
        }

        public Book findBookByTitle(String title) {
            return catalog.get(title.toLowerCase());
        }
    }
}
