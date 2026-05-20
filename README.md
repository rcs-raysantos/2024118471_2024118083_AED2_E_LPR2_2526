# 2024118471_2024118083_AED2_E_LPR2_2526
Course: Algorithms and Data Structures 2 + Programming Languages 2
Aqui tens um **README completo e bem estruturado** para a **Fase 1 — Base de Dados + API de Consulta**, pronto para colocares no teu repositório (GitHub, por exemplo):

---

# 📌 Fase 1 — Base de Dados + API de Consulta

## 📖 Descrição Geral

Nesta fase do projeto, foi desenvolvida a base estrutural do sistema, incluindo a modelização das entidades, armazenamento eficiente de dados e implementação de uma API de consulta. Foram utilizadas estruturas de dados como **HashMap** e **Red-Black BST** para garantir desempenho e organização eficiente.

---

## 🧩 R1 — Diagramas UML

Foram criados diagramas de classes UML para representar todas as entidades do sistema.

### 📌 Entidades principais:

* **User**
* **Content** *(superclasse)*

  * Movie
  * Series
  * Documentary
* **Artist** *(superclasse)*

  * Actor
  * Director
* **Genre**

### 📌 Características:

* Atributos com tipos adequados *(String, LocalDate, etc.)*
* Métodos:

  * Construtores
  * Getters e Setters
* Relações entre classes:

  * `Content` → lista de `Artist`
  * `User` → lista de géneros preferidos

### 🛠 Ferramenta utilizada:

* ArgoUML

  * Ficheiros `.zargo`
  * Exportação em `.png`

---

## 🗂 R2 — Symbol Table (HashMap)

Foi utilizada a estrutura **HashMap** para armazenar entidades com chave não ordenada.

### 📌 Estruturas implementadas:

```java
HashMap<String, User> users;
HashMap<String, Content> contents;
HashMap<String, Artist> artists;
```

### 📌 Operações disponíveis:

* `insert(key, value)` → Inserção
* `remove(key)` → Remoção
* `edit(key, newValue)` → Atualização
* `listAll()` → Listagem

### 📌 Serviços:

* `UserService`
* `ContentService`
* `ArtistService`

---

## 🌳 R3 — BST Balanceada (Red-Black)

Foi utilizada a estrutura **RedBlackBST** para suportar pesquisas ordenadas.

### 📌 Estruturas implementadas:

```java
RedBlackBST<LocalDate, User> usersByDate;
RedBlackBST<String, Artist> artistsByName;
RedBlackBST<Integer, Content> contentsByYear;
```

### 📌 Funcionalidades:

* 🔍 Pesquisa por chave exata
* 📊 Pesquisa por intervalo (`keys(lo, hi)`)
* 🔎 Pesquisa por substring
* 🔗 Pesquisas combinadas:

  * Exemplo: artistas por nacionalidade + género + idade

---

## 🔄 R4 — Consistência entre Estruturas

Foi implementado um sistema para garantir consistência entre todas as estruturas de dados.

### 📌 Método principal:

```java
archiveUser(String id)
```

### 📌 Funcionalidades:

1. Remove o utilizador do:

   * HashMap
   * RedBlackBST
2. Guarda os dados em:

   * `archived_users.txt`
3. Remove relações associadas (grafo — Fase 2)

### 📌 Classe responsável:

* `ConsistencyManager`

---

## 🧪 R5 — Casos de Teste

Foram criadas classes de teste para validar cada funcionalidade.

### 📌 Exemplo:

```java
UserServiceTest
```

### 📌 Métodos de teste:

* `testInsertUser()`
* `testSearchByDateRange()`

### 📌 Estrutura dos testes:

* Criação de dados (manual ou ficheiros)
* Execução da operação
* Output no terminal:

```java
System.out.println("PASS: inserção correta");
```

---

## 📚 R6 — Javadoc

Toda a aplicação foi documentada com **Javadoc**.

### 📌 Exemplo:

```java
/**
 * Inserts a new user into the system.
 * @param id unique identifier for the user
 * @param user the User object to insert
 * @throws IllegalArgumentException if id is null or empty
 */
public void insertUser(String id, User user) { ... }
```

### 📌 Geração da documentação:

```bash
javadoc -d docs src/**/*.java
```

### 📌 Resultado:

* Documentação HTML disponível na pasta `/docs`

---

## 📦 Estrutura do Projeto

```
src/
 ├── model/
 ├── service/
 ├── data_structures/
 ├── utils/
 ├── tests/
docs/
uml/
 ├── diagrams.zargo
 ├── diagrams.png
```

---
