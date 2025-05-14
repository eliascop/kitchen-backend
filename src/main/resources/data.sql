-- Inserir produtos para o tipo sobremesa
INSERT INTO products (name, description, type, price) VALUES
('Bolo de Chocolate', 'Bolo de chocolate com recheio cremoso e cobertura de ganache.', 'dessert', 14.90),
('Torta de Morango', 'Torta com morangos frescos e base crocante.', 'dessert', 14.90),
('Pudim de Leite Condensado', 'Clássico pudim de leite condensado, suave e delicioso.', 'dessert', 14.90),
('Mousse de Maracujá', 'Mousse fresca e leve, feita com maracujá natural.', 'dessert', 14.90),
('Cheesecake de Frutas Vermelhas', 'Cheesecake com uma mistura de frutas vermelhas e base crocante.', 'dessert', 14.90);

-- Inserir produtos para o tipo refeição
INSERT INTO products (name, description, type, price) VALUES
('Feijoada', 'Feijoada tradicional com arroz, farofa e couve.', 'meal', 28.90),
('Churrasco Misto', 'Churrasco com carnes variadas, arroz e batata frita.', 'meal', 28.90),
('Frango Grelhado', 'Frango grelhado acompanhado de arroz e salada.', 'meal', 28.90),
('Lasanha à Bolonhesa', 'Lasanha com molho à bolonhesa, queijo e presunto.', 'meal', 28.90),
('Strogonoff de Carne', 'Strogonoff de carne com molho cremoso e arroz branco.', 'meal', 28.90),
('Salmão Grelhado', 'Salmão fresco grelhado com legumes salteados.', 'meal', 35.50),
('Risoto de Cogumelos', 'Risoto cremoso com variedade de cogumelos frescos.', 'meal', 32.00),
('Parmegiana de Frango', 'Filé de frango à parmegiana com arroz e purê de batata.', 'meal', 29.50),
('Nhoque de Batata', 'Nhoque de batata com molho de tomate fresco e manjericão.', 'meal', 26.80),
('Escondidinho de Carne Seca', 'Escondidinho cremoso de carne seca com purê de mandioca.', 'meal', 31.20);

-- Inserir produtos para o tipo bebida
INSERT INTO products (name, description, type, price) VALUES
('Suco de Morango', 'Suco natural de morango, fresco e saboroso.', 'drink', 9.90),
('Suco de Laranja', 'Suco natural de laranja, refrescante e vitaminado.', 'drink', 9.90),
('Suco de Abacaxi', 'Suco de abacaxi fresco, doce e levemente ácido.', 'drink', 9.90),
('Suco de Limão', 'Suco natural de limão com um toque de açúcar.', 'drink', 9.90),
('Suco de Manga', 'Suco natural de manga, cremoso e delicioso.', 'drink', 9.90),
('Água Mineral', 'Garrafa de água mineral (500ml).', 'drink', 5.00),
('Refrigerante Lata', 'Lata de refrigerante (Coca-Cola, Guaraná, etc.).', 'drink', 7.00),
('Cerveja Long Neck', 'Garrafa long neck de cerveja (marcas variadas).', 'drink', 12.00);

-- Inserir produtos para o tipo pizzas
INSERT INTO products (name, description, type, price) VALUES
('Pizza Margherita', 'Pizza tradicional com molho de tomate, mussarela e manjericão.', 'pizza', 45.90),
('Pizza Pepperoni', 'Pizza com molho de tomate, mussarela e pepperoni.', 'pizza', 48.50),
('Pizza Calabresa', 'Pizza com molho de tomate, mussarela e linguiça calabresa.', 'pizza', 47.00);

-- Inserir produtos para o tipo lanche
INSERT INTO products (name, description, type, price) VALUES
('Hambúrguer Clássico', 'Hambúrguer com carne, queijo, alface, tomate e maionese.', 'snack', 22.50),
('Ckicken Crispy', 'Hambúrguer com de frango com queijo, alface, tomate e molhos especiais.', 'snack', 26.00),
('Cheeseburger', 'Hambúrguer com carne e queijo.', 'snack', 20.00),
('X-Bacon', 'Hambúrguer com carne, queijo e bacon.', 'snack', 25.00);

-- Inserir produtos para o tipo acompanhamento
INSERT INTO products (name, description, type, price) VALUES
('Batata Frita', 'Porção de batata frita crocante.', 'accompaniment', 12.00),
('Anéis de Cebola', 'Porção de anéis de cebola empanados.', 'accompaniment', 15.50),
('Arroz Branco', 'Porção de arroz branco soltinho.', 'accompaniment', 8.00),
('Farofa', 'Porção de farofa temperada.', 'accompaniment', 9.50),
('Salada Mista', 'Salada com folhas verdes, tomate, pepino e cenoura.', 'accompaniment', 14.00);

-- Inserir usuario admin
INSERT INTO users (name, email, phone, profile, user, password) VALUES
('Administrator', 'admin@admin.com', '11-2444-2244', 1, 'admin', '$2a$10$O4peZtrhK6DmTTgW6BVIwOgtWmfR8XDCxyPZbk7osS97NDiTU3a06')
