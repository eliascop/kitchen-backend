CREATE SCHEMA IF NOT EXISTS master;
SET SCHEMA master;

-- USERS
CREATE TABLE IF NOT EXISTS master.users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  login VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  email VARCHAR(255),
  profile INT,
  paypal_payer_id VARCHAR(255)
);

-- PRODUCTS
CREATE TABLE IF NOT EXISTS master.products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  type VARCHAR(50),
  price DECIMAL(10,2)
);

-- WALLETS
CREATE TABLE IF NOT EXISTS master.wallets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  balance DECIMAL(10,2),
  updated_at TIMESTAMP,
  CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES master.users(id)
);

-- WALLET_TRANSACTIONS
CREATE TABLE IF NOT EXISTS master.wallet_transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  wallet_id BIGINT NOT NULL,
  type VARCHAR(20), -- CREDIT, DEBIT, etc.
  amount DECIMAL(10,2),
  description VARCHAR(255),
  status VARCHAR(50),
  created_at TIMESTAMP,
  secure_token VARCHAR(32),
  CONSTRAINT fk_wallet_trans_wallet FOREIGN KEY (wallet_id) REFERENCES master.wallets(id)
);

-- ORDERS
CREATE TABLE IF NOT EXISTS master.orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  creation TIMESTAMP,
  status VARCHAR(50),
  total DECIMAL(10,2),
  user_id BIGINT NOT NULL,
  payment_id BIGINT,
  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES master.users(id)
  -- payment_id FK, se tiver tabela payments, deve criar também
);

-- ORDER_ITEMS
CREATE TABLE IF NOT EXISTS master.orderItems (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT,
  item_value DECIMAL(10,2),
  CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES master.orders(id),
  CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES master.products(id)
);
