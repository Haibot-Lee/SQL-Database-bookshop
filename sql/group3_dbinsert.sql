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
    status      INT  DEFAULT 4, -- 0: Confirmed; 1: Shipping; 2: Completed; 3: Cancelled; 4: Incomplete;
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
    IF :old.status = 4 AND :new.status = 0 THEN -- Incomplete --> Confirmed
        UPDATE STUDENTS
        SET total_order = total_order + (:old.total_price)
        WHERE stu_no = :new.stu_no;
    ELSIF :old.status = 0 AND :new.status = 3 THEN -- Confirmed --> Cancelled
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

-- Calculating total_price of an objects.Order
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


--check the card number of credit card
CREATE OR REPLACE TRIGGER card_no_payment
    BEFORE INSERT OR UPDATE
    ON ORDERS
    FOR EACH ROW
BEGIN
    IF (:NEW.pay_method='Credit Card' AND :NEW.card_no='')
    THEN
        RAISE_APPLICATION_ERROR(-20000, 'INVALID CARD NUMBER');
    END IF;
END;
.
/


CREATE OR REPLACE PROCEDURE add_book_in_order(o_no CHAR, b_no CHAR, add_qty INT) AS
    cnt INT;
BEGIN
    SELECT COUNT(*)
    INTO cnt
    FROM BOOK_IN_ORDERS
    WHERE order_no = o_no
      AND book_no = b_no;
    IF cnt = 0 THEN
        INSERT INTO BOOK_IN_ORDERS
        VALUES (o_no, b_no, add_qty, NULL);
    ELSE
        UPDATE BOOK_IN_ORDERS SET QTY = QTY + add_qty WHERE order_no = o_no AND book_no = b_no;
    END IF;
END;
.
/

CREATE OR REPLACE PROCEDURE update_status_books_and_order(o_no CHAR, b_no CHAR, day Date) AS
    cnt  INT;
BEGIN
    UPDATE BOOK_IN_ORDERS SET DELIVER_DATE = day
    WHERE order_no = o_no AND book_no = b_no;
    SELECT COUNT(*)
    INTO cnt
    FROM BOOK_IN_ORDERS
    WHERE order_no = o_no
      AND deliver_date IS NULL;
    IF cnt = 0 THEN
        UPDATE ORDERS SET status=2 WHERE order_no = o_no;
    ELSIF cnt > 0 THEN
        UPDATE ORDERS SET status=1 WHERE order_no = o_no;
    END IF;
END;
.
/

CREATE OR REPLACE PROCEDURE cancel_status_books_and_order(o_no CHAR, b_no CHAR, st_qty INT) AS
    s INT;
    dt DATE;
BEGIN
    SELECT status INTO s FROM ORDERS WHERE ORDERS.order_no = o_no;
    IF s = 1 THEN
        RAISE_APPLICATION_ERROR(-20001, 'SHIPPING');
    ELSIF s = 2 THEN
        RAISE_APPLICATION_ERROR(-20002, 'COMPLETED');
    ELSIF s = 3 THEN
        RAISE_APPLICATION_ERROR(-20003, 'CANCELLED');
    END IF;
    SELECT order_date INTO dt FROM ORDERS WHERE ORDERS.order_no = o_no;
    IF (CURRENT_DATE-dt) > 7 THEN
        RAISE_APPLICATION_ERROR(-20017, 'INVALID ORDER DATE');
    END IF;
    UPDATE BOOKS SET stock = stock + st_qty
    WHERE BOOKS.book_no = b_no;
END;
.
/

INSERT INTO STUDENTS
VALUES ('11111111', 'Kurt', 'M', 'COMP', 1900, 0.1);
INSERT INTO STUDENTS
VALUES ('22222222', 'Rex', 'M', 'COMP', 600, 0);
INSERT INTO STUDENTS
VALUES ('33333333', 'Jerry', 'M', 'COMP', 300, 0);

INSERT INTO BOOKS
VALUES ('001', 'Harry Potter I', 'J. K. Rowling', 300, 11);
INSERT INTO BOOKS
VALUES ('002', 'Harry Potter II', 'J. K. Rowling', 300, 2);
INSERT INTO BOOKS
VALUES ('003', 'Harry Potter III', 'J. K. Rowling', 400, 20);

INSERT INTO ORDERS
VALUES ('2201123456', '22222222', '2-MAY-2020', 0, 0, 'Credit Card', '123456789');
INSERT INTO ORDERS
VALUES ('1101123456', '11111111', '28-APR-2020', 0, 30, 'Credit Card', '987654321');

INSERT INTO BOOK_IN_ORDERS
VALUES ('2201123456', '001', 1, '3-MAY-2020');
INSERT INTO BOOK_IN_ORDERS
VALUES ('2201123456', '002', 1, NULL);
UPDATE ORDERS SET status=1 WHERE order_no='2201123456';

INSERT INTO BOOK_IN_ORDERS
VALUES ('1101123456', '002', 1, NULL);

COMMIT;