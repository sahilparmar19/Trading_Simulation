
CREATE DATABASE IF NOT EXISTS BookStore;
USE BookStore;

DROP TABLE IF EXISTS reports;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS books;

CREATE TABLE books (
    book_id INT(10) AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    author VARCHAR(50),
    price DOUBLE,
    stock INT(10)
);

INSERT INTO books (title, author, price, stock) VALUES
('Java Programming', 'Ankur Patel', 555.00, 10),
('FEE', 'Nikunj Singhada', 666.00, 8),
('Maths', 'Ankit Acharya', 444.00, 10),
('DBMS', 'Riddhish Thakore', 888.00, 18),
('DS', 'Rupal Ravia', 777.00, 5);

CREATE TABLE sales (
    sale_id INT(10) AUTO_INCREMENT PRIMARY KEY,
    book_id INT(10),
    quantity INT(10),
    customer_name VARCHAR(50),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

CREATE TABLE reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    report_type VARCHAR(20),
    report_text LONGTEXT
);
