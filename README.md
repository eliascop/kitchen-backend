# Backend de Cozinha - Serviços

Este README descreve os serviços principais do backend da aplicação de cozinha, fornecendo informações sobre suas funcionalidades e responsabilidades.

## Configurações da Aplicação (`application.properties`)

As seguintes configurações são utilizadas para configurar a aplicação Spring Boot:

* `spring.application.name=kitchen-backend`: Define o nome da aplicação como "kitchen-backend".
* `server.address=0.0.0.0`: Configura o servidor para escutar em todos os endereços IPv4 disponíveis.
* `server.port=8082`: Define a porta em que o servidor Tomcat irá rodar como 8082.

### Configurações do Banco de Dados MySQL

* `spring.datasource.url=jdbc:mysql://localhost:3306/master`: Define a URL de conexão com o banco de dados MySQL na porta 3306 e utilizando o schema `master`.
* `spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect`: Especifica o dialeto do Hibernate para o MySQL 8.
* `spring.jpa.generate-ddl=true`: Configura o JPA para gerar o DDL (Data Definition Language) baseado nas entidades.
* `spring.jpa.hibernate.ddl-auto=update`: Configura o Hibernate para atualizar o schema do banco de dados ao iniciar a aplicação, sem excluir dados.
* `spring.jpa.properties.hibernate.format_sql=true`: Habilita a formatação da saída SQL no log.
* `spring.jpa.show-sql=true`: Exibe as queries SQL geradas pelo Hibernate no log.
* `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`: Especifica o driver JDBC para conectar ao MySQL.
* `spring.datasource.username=root`: Define o nome de usuário para a conexão com o banco de dados como "root".
* `spring.datasource.password=root`: Define a senha para a conexão com o banco de dados como "root".
* `server.connection-timeout=10000`: Define o timeout de conexão do servidor em milissegundos (10 segundos).
* `spring.datasource.initialization-mode=always`: Configura o Spring para sempre inicializar o datasource.
* `spring.datasource.schema=classpath:schema.sql`: Especifica o arquivo SQL a ser usado para inicializar o schema do banco de dados.
* `spring.datasource.data=classpath:data.sql`: Especifica o arquivo SQL a ser usado para popular o banco de dados com dados iniciais.

### Configurações JWT (JSON Web Tokens)

* `jwt.secret = thesecretisasecretethesecretisasecrete`: Define a chave secreta usada para assinar e verificar os tokens JWT. **Esta é uma chave de exemplo e deve ser substituída por uma chave forte e segura em produção.**
* `jwt.expiration=3600000`: Define o tempo de expiração dos tokens JWT em milissegundos (3600000 ms = 1 hora).

### Configurações do Kafka

* `spring.kafka.bootstrap-servers=localhost:9092`: Define a lista de servidores bootstrap do Kafka para a conexão.
* `spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer`: Especifica o serializador para as chaves das mensagens produzidas.
* `spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer`: Especifica o serializador para os valores (payloads) das mensagens produzidas, utilizando JSON.
* `spring.kafka.producer.properties.spring.json.add.type.headers=false`: Desabilita a adição de headers de tipo JSON nas mensagens produzidas.
* `spring.kafka.producer.properties.spring.json.trusted.packages=*`: Define os pacotes confiáveis para desserialização JSON como todos (`*`). **Em produção, é recomendado restringir os pacotes confiáveis por segurança.**

### Configurações de Logging

* `logging.level.org.springframework.jdbc.datasource.init=DEBUG`: Define o nível de log para a classe `org.springframework.jdbc.datasource.init` como DEBUG, o que pode fornecer informações detalhadas sobre a inicialização do datasource.

## Serviços

### `CartService`

Este serviço é responsável por gerenciar as operações relacionadas ao carrinho de compras dos usuários. Ele estende `GenericService` para fornecer funcionalidades CRUD básicas para a entidade `Cart`.

**Responsabilidades:**

* **Salvar um carrinho (`save(Cart cart)`):**
    * Persiste ou atualiza um carrinho no banco de dados.
    * Se o carrinho contiver itens (`CartItems`), realiza as seguintes ações:
        * Associa cada item ao carrinho.
        * Busca o produto correspondente no banco de dados usando o `ProductRepository`.
        * Define o produto resolvido no item do carrinho.
        * Calcula o valor total do item (`item.calculateItemValue()`).
    * Calcula o valor total do carrinho somando o valor de todos os seus itens.
    * Define o total no carrinho.
    * Retorna o carrinho salvo.

**Dependências:**

* `CartRepository`: Repositório para acessar e manipular dados da entidade `Cart`.
* `ProductRepository`: Repositório para acessar dados da entidade `Product`.

### `CustomUserDetailsService`

Este serviço implementa a interface `UserDetailsService` do Spring Security. Ele é responsável por carregar os detalhes de um usuário com base no seu nome de usuário para autenticação e autorização.

**Responsabilidades:**

* **Carregar usuário por nome de usuário (`loadUserByUsername(String strUser)`):**
    * Recebe um nome de usuário (`strUser`).
    * Utiliza o `UserRepository` para buscar um usuário no banco de dados com o nome de usuário fornecido.
    * Se o usuário não for encontrado, lança uma `UsernameNotFoundException`.
    * Se o usuário for encontrado, cria uma instância de `CustomUserDetails` (uma implementação personalizada de `UserDetails`) com o usuário e uma lista contendo uma única autoridade: `ROLE_USER`.

**Dependências:**

* `UserRepository`: Repositório para acessar dados da entidade `User`.

### `OrderService`

Este serviço gerencia as operações relacionadas aos pedidos (`Order`). Ele estende `GenericService` para fornecer funcionalidades CRUD básicas para a entidade `Order`.

**Responsabilidades:**

* **Salvar um pedido (`save(Order order)`):**
    * Define o status inicial do pedido como "PENDING".
    * Define a data de criação do pedido.
    * Se o pedido tiver um usuário associado, busca o usuário no banco de dados usando o `UserRepository`.
    * Se o pedido tiver um carrinho associado com itens, realiza as seguintes ações para cada item:
        * Associa o item ao carrinho.
        * Busca o produto correspondente no banco de dados usando o `ProductRepository`.
        * Define o produto resolvido no item do carrinho.
        * Calcula o valor total do item (`item.calculateItemValue()`).
    * Retorna o pedido salvo.
* **Criar um pedido a partir de um carrinho (`createOrderFromCart(Long cartId, Long userId)`):**
    * Busca um carrinho no banco de dados usando o `CartRepository` com o ID fornecido. Lança uma exceção se não encontrado.
    * Busca um usuário no banco de dados usando o `UserRepository` com o ID fornecido. Lança uma exceção se não encontrado.
    * Cria uma nova instância de `Order`.
    * Associa o carrinho e o usuário ao novo pedido.
    * Define o status do pedido como "PENDING".
    * Define a data de criação do pedido.
    * Retorna o pedido salvo.
* **Encontrar pedidos por ID de usuário (`findOrdersByUserId(Long userId)`):**
    * Utiliza o `OrderRepository` para buscar uma lista de pedidos associados ao ID do usuário fornecido.
    * Retorna um `Optional` contendo a lista de pedidos (pode estar vazia).
* **Encontrar um pedido por ID e ID de usuário (`findOrderByIdAndUserId(Long id, Long userId)`):**
    * Utiliza o `OrderRepository` para buscar um pedido específico com o ID do pedido e o ID do usuário fornecidos.
    * Retorna um `Optional` contendo o pedido (pode estar vazio).

**Dependências:**

* `CartRepository`: Repositório para acessar dados da entidade `Cart`.
* `OrderRepository`: Repositório para acessar dados da entidade `Order`.
* `ProductRepository`: Repositório para acessar dados da entidade `Product`.
* `UserRepository`: Repositório para acessar dados da entidade `User`.

### `ProductService`

Este serviço gerencia as operações relacionadas aos produtos (`Product`). Ele estende `GenericService` para fornecer funcionalidades CRUD básicas para a entidade `Product`.

**Responsabilidades:**

* **Salvar um produto (`save(Product product)`):**
    * Persiste ou atualiza um produto no banco de dados utilizando o `ProductRepository`.
    * Retorna o produto salvo.

**Dependências:**

* `ProductRepository`: Repositório para acessar e manipular dados da entidade `Product`.

### `UserService`

Este serviço gerencia as operações relacionadas aos usuários (`User`). Ele estende `GenericService` para fornecer funcionalidades CRUD básicas para a entidade `User`.

**Responsabilidades:**

* **Registrar um novo usuário (`registerUser(User user)`):**
    * Verifica se já existe um usuário com o mesmo nome de usuário no banco de dados utilizando o `UserRepository`. Se existir, lança uma `RuntimeException`.
    * Codifica a senha do usuário utilizando um `PasswordEncoder` (injetado via `@Autowired`).
    * Define a senha codificada no objeto `User`.
    * Salva o novo usuário no banco de dados utilizando o `UserRepository`.
    * Retorna o usuário salvo.

**Dependências:**

* `UserRepository`: Repositório para acessar e manipular dados da entidade `User`.
* `PasswordEncoder`: Componente do Spring Security para codificar senhas.
