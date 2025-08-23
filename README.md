```markdown
# 🛒 RAG Commerce Assistant

**RAG Commerce Assistant** — це AI-асистент для продавців, який допомагає швидко відповідати на запити клієнтів, працювати з товарами та формувати звіти.  
Система поєднує **RAG (Retrieval-Augmented Generation)**, класичний бекенд на Java, векторні бази та Telegram-бота.

---

## 🚀 Основні можливості
- **Імпорт товарів** через Excel/CSV (SKU, назва, опис, ціна, залишки).  
- **Індексація та Embeddings** для кожного продавця (multi-tenant підтримка).  
- **Чат для продавця**: генерація відповідей на основі товарів і політик.  
- **Telegram-бот** для клієнтів: відповідає на питання, підтягує дані з бази.  
- **Звіти та аналітика**: найпопулярніші товари, часті запити клієнтів, статистика продажів.  
- **Гібридний стек**: частина функціоналу через код (Java/Python), частина через **n8n** (ETL/крони/інтеграції).  

---

## 🏗️ Архітектура

```

+---------------------+        +------------------+
\|   Seller Web (Java) | <----> |   MySQL/Postgres |
\|  - Upload Excel     |        |   + pgvector     |
\|  - Manage Products  |        +------------------+
\|  - Reports          |
+----------+----------+
|
v
+---------------------+        +------------------+
\|  AI Gateway (Python)| <----> | Vector DB (Qdrant|
\|  - Embeddings       |        |  or pgvector)    |
\|  - Retrieval (kNN)  |        +------------------+
\|  - RAG Chat         |
+----------+----------+
|
v
+---------------------+
\| Telegram Bot (Python|
\|  - Customer Chat    |
\|  - Order Requests   |
+---------------------+
|
v
+---------------------+
\| n8n Workflows       |
\|  - ETL Imports      |
\|  - Cron Jobs        |
\|  - Integrations     |
+---------------------+

````

---

## ⚙️ Технологічний стек
- **Backend**: Java 21, Spring Boot, JSP (MVP), Spring Security, Apache POI (Excel)  
- **Database**: MySQL (MVP) або Postgres + pgvector (рекомендовано)  
- **AI Gateway**: Python 3.11, FastAPI, LangChain/LlamaIndex, sentence-transformers  
- **Vector DB**: Qdrant / pgvector  
- **Bot**: Python Telegram Bot  
- **Integrations**: n8n (ETL, крони, Shopify/Woo інтеграції)  
- **Infra**: Docker Compose, Nginx reverse proxy, Let’s Encrypt TLS  

---

## 🔁 Потік даних
1. Продавець завантажує Excel/CSV → Java API → MySQL.  
2. AI Gateway обробляє товари → ріже на чанки → embeddings → зберігає у векторній БД.  
3. Клієнт пише у Telegram → бот викликає `/chat?sellerId=…`.  
4. RAG виконує пошук + (опційно) виклики API (`/products/{sku}`, `/orders/{id}`) → формує відповідь із цитатами.  
5. Відповідь віддається у чат, логується у звіти.  

---

## 📊 Приклади запитів
- **Клієнт**: “Чи є у вас взуття розміру 42?”  
  - **Бот**: “Так ✅. Доступні моделі: Nike Air Zoom (SKU-123, 3200 грн), Adidas UltraBoost (SKU-456, 3500 грн).”  

- **Клієнт**: “Хочу повернути товар, як це зробити?”  
  - **Бот**: “Політика повернення: ви маєте 14 днів для повернення товару (джерело: policy.pdf, розділ 2.1).”  

- **Продавець**: “Покажи мені найчастіші питання за останній тиждень.”  
  - **Web**: “ТОП-3 питання: доставка, наявність розміру, політика повернення.”  

---

## 🧰 Локальний запуск (MVP)
```bash
# 1. Клонувати репозиторій
git clone https://github.com/<your-username>/rag-commerce-assistant.git
cd rag-commerce-assistant

# 2. Запустити Docker Compose (DB + Qdrant + FastAPI + Java)
docker-compose up -d

# 3. Запустити бекенд (Java)
./mvnw spring-boot:run

# 4. Запустити AI Gateway (Python)
cd ai-gateway
uvicorn main:app --reload --port 8001

# 5. Запустити Telegram бота
python bot/main.py
````

---

## 📅 План розвитку

* [x] MVP: імпорт Excel, базовий чат, TG-бот.
* [ ] Аналітика запитів + дашборд.
* [ ] Інтеграція з Shopify/WooCommerce.
* [ ] Multi-tenant support (ізоляція даних).
* [ ] Reports & BI (Grafana).
* [ ] SaaS-версія з підписками.