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
    total_order REAL DEFAULT 0,
    discount    REAL DEFAULT 0,
    PRIMARY KEY (stu_no)
);

CREATE TABLE ORDERS
(
    order_no    CHAR(10),
    stu_no      CHAR(8)     NOT NULL,
    order_date  DATE        NOT NULL,
    status      INT  DEFAULT 0,
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

CREATE OR REPLACE TRIGGER add_dislevel_constraint
    AFTER INSERT OR UPDATE
    ON ORDERS
    FOR EACH ROW
DECLARE
    c REAL;
BEGIN
    UPDATE STUDENTS SET total_order = total_order + :new.total_price WHERE stu_no = :new.stu_no;
    SELECT total_order INTO c FROM STUDENTS WHERE stu_no = :new.stu_no;
    IF c >= 2000 THEN
        UPDATE STUDENTS SET discount=0.2;
    ELSIF c >= 1000 THEN
        UPDATE STUDENTS SET discount=0.1;
    END IF;
END;
.
/

CREATE OR REPLACE TRIGGER del_dislevel&amount_constraint
    AFTER DELETE
    ON ORDERS
    FOR EACH ROW
DECLARE
    c   REAL;
    cnt INT;
    i INT;
BEGIN
    UPDATE STUDENTS SET total_order = total_order - :old.total_price WHERE stu_no = :old.stu_no;
    SELECT total_order INTO c FROM STUDENTS WHERE stu_no = :old.stu_no;
    IF c >= 2000 THEN
        UPDATE STUDENTS SET discount=0.2;
    ELSIF c >= 1000 THEN
        UPDATE STUDENTS SET discount=0.1;
    END IF;
END;
.
/

INSERT INTO STUDENTS
VALUES ('11111111', 'Kurt', 'M', 'COMP', 1900, 0.1);
INSERT INTO STUDENTS
VALUES ('22222222', 'Rex', 'M', 'COMP', 900, 0);
INSERT INTO STUDENTS
VALUES ('33333333', 'Jerry', 'M', 'COMP', 300, 0);

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
        FROM ORDERS O NATURAL JOIN STUDENTS S WHERE order_no = :new.order_no;
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
        FROM ORDERS O NATURAL JOIN STUDENTS S WHERE order_no = :new.order_no;
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
        FROM ORDERS O NATURAL JOIN STUDENTS S WHERE order_no = :old.order_no;
    UPDATE ORDERS SET total_price = total_price - :old.qty * p * (1 - d) WHERE order_no = :old.order_no;
END;
.
/
