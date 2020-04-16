CREATE TABLE BOOKS
(
    book_no CHAR(13),
    title   VARCHAR(50) NOT NULL,
    author  VARCHAR(30) NOT NULL,
    price   REAL        NOT NULL,
    stock   INT         NOT NULL,
    PRIMARY KEY (book_no)
);

CREATE TABLE STUDENTS
(
    stu_no      CHAR(8),
    name        VARCHAR(30) NOT NULL,
    gender      CHAR(1)     NOT NULL,
    major       VARCHAR(20) NOT NULL,
    total_order REAL DEFAULT 0,
    discount    REAL DEFAULT 0,
    PRIMARY KEY (stu_no)
);

CREATE TABLE ORDERS
(
    order_no    CHAR(10),
    stu_no      CHAR(8)     NOT NULL,
    order_date  DATE        NOT NULL,
    status      INT  DEFAULT 0, -- 0未下单，1已下单(可取消)，2不可取消，3完成， 4已取消
    total_price REAL DEFAULT 0,
    pay_method  VARCHAR(30) not null,
    card_no     CHAR(16),
    PRIMARY KEY (order_no),
    FOREIGN KEY (stu_no) REFERENCES STUDENTS ON DELETE CASCADE
);

CREATE TABLE BOOK_IN_ORDERS
(
    order_no     CHAR(10),
    book_no      CHAR(13),
    qty          INT,
    deliver_date DATE DEFAULT NULL,
    PRIMARY KEY (order_no, book_no),
    FOREIGN KEY (book_no) REFERENCES BOOKS ON DELETE CASCADE,
    FOREIGN KEY (order_no) REFERENCES ORDERS ON DELETE CASCADE
);

-- update the discount level after update orders
CREATE OR REPLACE TRIGGER addCancel_disLevel_constraint
    AFTER UPDATE
    ON ORDERS
    FOR EACH ROW
DECLARE
    c REAL;
BEGIN
    IF :new.status = 0 THEN --add or delete book
        UPDATE STUDENTS
        SET total_order = total_order + (:new.total_price - :old.total_price)
        WHERE stu_no = :new.stu_no;
    ELSIF :new.status = 4 THEN -- cancel order
        UPDATE STUDENTS
        SET total_order = total_order - :old.total_price
        WHERE stu_no = :new.stu_no;
    END IF;

    SELECT total_order INTO c FROM STUDENTS WHERE stu_no = :new.stu_no;
    IF c >= 2000 THEN
        UPDATE STUDENTS SET discount=0.2 WHERE stu_no = :new.stu_no;
    ELSIF c >= 1000 THEN
        UPDATE STUDENTS SET discount=0.1 WHERE stu_no = :new.stu_no;
    END IF;
END;
.
/

-- Calculating total_price of an Order
CREATE OR REPLACE TRIGGER total_price_stock_insert
    AFTER INSERT
    ON BOOK_IN_ORDERS
    FOR EACH ROW
DECLARE
    p REAL;
    d REAL;
BEGIN
    SELECT price INTO p FROM BOOKS WHERE BOOKS.book_no = :new.book_no;
    SELECT discount
    INTO d
    FROM ORDERS O
             NATURAL JOIN STUDENTS S
    WHERE order_no = :new.order_no;
    UPDATE ORDERS SET total_price = total_price + :new.qty * p * (1 - d) WHERE order_no = :new.order_no;

    UPDATE BOOKS SET stock = stock - :new.qty WHERE book_no = :new.book_no;
END;
.
/

CREATE OR REPLACE TRIGGER total_price_stock_update
    AFTER UPDATE -- when a new book is already in the order
    ON BOOK_IN_ORDERS
    FOR EACH ROW
DECLARE
    p REAL;
    d REAL;
BEGIN
    SELECT price INTO p FROM BOOKS WHERE BOOKS.book_no = :new.book_no;
    SELECT discount
    INTO d
    FROM ORDERS O
             NATURAL JOIN STUDENTS S
    WHERE order_no = :new.order_no;
    UPDATE ORDERS
    SET total_price = total_price + (:new.qty - :old.qty) * p * (1 - d)
    WHERE order_no = :new.order_no;

    UPDATE BOOKS SET stock = stock - (:new.qty - :old.qty) WHERE book_no = :new.book_no;
END;
.
/

CREATE OR REPLACE TRIGGER total_price_stock_delete
    AFTER DELETE -- TODO Review the DELETE condition later
    ON BOOK_IN_ORDERS
    FOR EACH ROW
DECLARE
    p REAL;
    d REAL;
BEGIN
    SELECT price INTO p FROM BOOKS WHERE BOOKS.book_no = :old.book_no;
    SELECT discount
    INTO d
    FROM ORDERS O
             NATURAL JOIN STUDENTS S
    WHERE order_no = :old.order_no;
    UPDATE ORDERS SET total_price = total_price - :old.qty * p * (1 - d) WHERE order_no = :old.order_no;

    UPDATE BOOKS SET stock = stock + :old.qty WHERE book_no = :old.book_no;
END;
.
/


INSERT INTO STUDENTS
VALUES ('11111111', 'Kurt', 'M', 'COMP', 1630, 0.1);
INSERT INTO STUDENTS
VALUES ('22222222', 'Rex', 'M', 'COMP', 300, 0);
INSERT INTO STUDENTS
VALUES ('33333333', 'Jerry', 'M', 'COMP', 300, 0);

INSERT INTO BOOKS
VALUES ('001', 'Harry Potter I', 'J. K. Rowling', 300, 11);
INSERT INTO BOOKS
VALUES ('002', 'Harry Potter II', 'J. K. Rowling', 300, 2);
INSERT INTO BOOKS
VALUES ('003', 'Harry Potter III', 'J. K. Rowling', 400, 20);

INSERT INTO ORDERS
VALUES ('222222221', '22222222', '02-APR-2020', 0, 0, 'Credit Card', '123456789');
INSERT INTO ORDERS
VALUES ('111111111', '11111111', '07-APR-2020', 0, 30, 'Credit Card', '987654321');

INSERT INTO BOOK_IN_ORDERS
VALUES ('222222221', '001', 1, '9-APR-2020');
INSERT INTO BOOK_IN_ORDERS
VALUES ('222222221', '002', 1, NULL);
INSERT INTO BOOK_IN_ORDERS
VALUES ('111111111', '002', 1, NULL);

UPDATE ORDERS SET status = 2 WHERE status = 0;