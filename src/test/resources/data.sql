-- Inserir usuários
INSERT INTO master.users (login, password, name, phone, email, profile, paypal_payer_id)
VALUES
('john_admin', '123123', 'John Admin', '11988888888','admin@example.com', 1, NULL),
('john_doe', '123456', 'John Doe', '11999999999', 'john@example.com', 2, NULL);

-- Inserir produtos
INSERT INTO master.products (name, description, type, price)
VALUES
  ('Arroz Doce', 'Arroz doce tradicional', 'MEAL', 15.50),
  ('Feijão Tropeiro', 'Feijão tropeiro mineiro', 'MEAL', 20.00),
  ('Suco de Laranja', 'Suco natural', 'DRINK', 7.00);

-- Inserir pedido
INSERT INTO master.orders (creation, status, total, user_id, payment_id)
VALUES (CURRENT_TIMESTAMP(), 'PENDING', 35.50, 2, NULL);

-- Inserir itens do pedido
INSERT INTO master.orderItems (order_id, product_id, quantity, item_value)
VALUES
  (1, 1, 1, 15.50),
  (1, 2, 1, 20.00);

-- Inserir carteira
INSERT INTO master.wallets (user_id, balance, updated_at)
VALUES (1, 100.00, CURRENT_TIMESTAMP());

-- Inserir transações da carteira
INSERT INTO master.wallet_transactions (wallet_id, type, amount, description, status, created_at)
VALUES
  (1, 'CREDIT', 50.00, 'Depósito inicial', 'SUCCESS', CURRENT_TIMESTAMP()),
  (1, 'DEBIT', 15.50, 'Pagamento pedido #1', 'SUCCESS', CURRENT_TIMESTAMP());
