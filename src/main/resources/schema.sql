CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    image_url VARCHAR(255) NOT NULL
);

CREATE TABLE member (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE cart_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE orders_status (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    orders_status_id BIGINT NOT NULL,
    used_point INT NOT NULL,
    create_at DATE NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (orders_status_id) REFERENCES orders_status(id)
);

CREATE TABLE orders_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    orders_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total_price INT NOT NULL,
    FOREIGN KEY (orders_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE point (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    orders_id BIGINT NOT NULL,
    earned_point INT NOT NULL,
    comment VARCHAR(255) NOT NULL,
    create_at DATE NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    expired_at DATE NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (orders_id) REFERENCES orders(id)
);

CREATE TABLE order_point (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    orders_id BIGINT NOT NULL,
    point_id BIGINT NOT NULL,
    used_point INT NOT NULL,
    FOREIGN KEY (orders_id) REFERENCES orders(id),
    FOREIGN KEY (point_id) REFERENCES point(id)
)
