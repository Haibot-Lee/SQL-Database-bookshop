CREATE TABLE BOOKS
(
    book_no CHAR(13),
    title   VARCHAR(50) NOT NULL,
    author  VARCHAR(30) NOT NULL,
    price   REAL        NOT NULL,
    amount  INT         NOT NULL,
    PRIMARY KEY (book_no)
);

CREATE TABLE STUDENTS
(
    stu_no      CHAR(8),
    name        VARCHAR(30) NOT NULL,
    gender      CHAR(1)     NOT NULL,
    major       VARCHAR(20) NOT NULL,
    total_order REAL,
    discount    REAL,
    PRIMARY KEY (stu_no)
);

CREATE TABLE ORDERS
(
    order_no    CHAR(10),
    stu_no      CHAR(8),
    order_date  DATE        NOT NULL,
    status      INT,
    total_price REAL,
    pay_method  VARCHAR(30) NOT NULL,
    card_no     CHAR(16),
    PRIMARY KEY (order_no),
    FOREIGN KEY (stu_no) REFERENCES STUDENTS ON DELETE CASCADE
);

CREATE TABLE BOOK_IN_ORDERS
(
    order_no  CHAR(10),
    book_no   CHAR(13),
    qty       INT,
    delivered INT,
    PRIMARY KEY (order_no, book_no),
    FOREIGN KEY (book_no) REFERENCES BOOKS ON DELETE CASCADE,
    FOREIGN KEY (order_no) REFERENCES ORDERS ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER students_constraint
    BEFORE INSERT OR UPDATE
    ON STUDENTS
    FOR EACH ROW
BEGIN
    IF (:new.gender != 'M' AND :new.gender != 'm' AND :new.gender != 'F' AND :new.gender != 'f') THEN
        RAISE_APPLICATION_ERROR(-20000, 'invalid gender');
    END IF;
END;
.
/


-- Calculating total_price of an Order
CREATE OR REPLACE TRIGGER total_price_insert
    AFTER INSERT
    ON BOOK_IN_ORDERS
    FOR EACH ROW
DECLARE
    p REAL;
    d REAL;
BEGIN
    SELECT price INTO p FROM BOOKS WHERE BOOKS.book_no = :new.book_no;
    SELECT discount INTO d
        FROM BOOK_IN_ORDERS NATURAL JOIN ORDERS O NATURAL JOIN STUDENTS S;
    UPDATE ORDERS SET total_price = total_price + :new.qty * p * (1 - d) WHERE order_no = :new.order_no;
END;
.
/

CREATE OR REPLACE TRIGGER total_price_update
    AFTER UPDATE    -- when a new book is already in the order
    ON BOOK_IN_ORDERS
    FOR EACH ROW
DECLARE
    p REAL;
    d REAL;
BEGIN
    SELECT price INTO p FROM BOOKS WHERE BOOKS.book_no = :new.book_no;
    SELECT discount INTO d
        FROM BOOK_IN_ORDERS NATURAL JOIN ORDERS O NATURAL JOIN STUDENTS S;
    UPDATE ORDERS SET total_price = total_price + (:new.qty - :old.qty) * p * (1 - d) WHERE order_no = :new.order_no;
END;
.
/

CREATE OR REPLACE TRIGGER total_price_delete
    AFTER DELETE    -- TODO Review the DELETE condition later
    ON BOOK_IN_ORDERS
    FOR EACH ROW
DECLARE
    p REAL;
    d REAL;
BEGIN
    SELECT price INTO p FROM BOOKS WHERE BOOKS.book_no = :old.book_no;
    SELECT discount INTO d
        FROM BOOK_IN_ORDERS NATURAL JOIN ORDERS O NATURAL JOIN STUDENTS S;
    UPDATE ORDERS SET total_price = total_price - :old.qty * p * (1 - d) WHERE order_no = :old.order_no;
END;
.
/
