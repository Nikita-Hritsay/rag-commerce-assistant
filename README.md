# 🛒 RAG Commerce Assistant

![Java](https://img.shields.io/badge/Java-21-blue) 
![Python](https://img.shields.io/badge/Python-3.11-green)
![Postgres](https://img.shields.io/badge/Postgres-pgvector-blueviolet)
![Telegram Bot](https://img.shields.io/badge/Telegram-Bot-2CA5E0?logo=telegram&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-informational)

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

```mermaid
flowchart TD
    A[Seller Web (Java)] -->|Upload Excel/CSV| B[(MySQL/Postgres + pgvector)]
    B --> C[AI Gateway (Python)]
    C --> D[(Vector DB: Qdrant/pgvector)]
    C --> E[Telegram Bot (Python)]
    C --> F[n8n Workflows]
    
    A -->|Reports| B
    E -->|Customer Chat| C
    F -->|ETL/Cron/Integrations| C
