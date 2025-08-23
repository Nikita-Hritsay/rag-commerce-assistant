-- базові таблиці
CREATE TABLE IF NOT EXISTS seller (
                                      id UUID PRIMARY KEY,
                                      name TEXT NOT NULL,
                                      created_at TIMESTAMPTZ DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS product (
                                       id UUID PRIMARY KEY,
                                       seller_id UUID NOT NULL REFERENCES seller(id),
    sku TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    price NUMERIC(10,2),
    currency TEXT DEFAULT 'UAH',
    updated_at TIMESTAMPTZ DEFAULT now()
    );
CREATE UNIQUE INDEX IF NOT EXISTS ux_product_seller_sku ON product(seller_id, sku);

-- RAG таблиці
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS product_chunk (
                                             id UUID PRIMARY KEY,
                                             seller_id UUID NOT NULL REFERENCES seller(id),
    product_id UUID NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL
    );
CREATE INDEX IF NOT EXISTS ix_chunk_seller ON product_chunk(seller_id);

CREATE TABLE IF NOT EXISTS product_embedding (
                                                 chunk_id UUID PRIMARY KEY REFERENCES product_chunk(id) ON DELETE CASCADE,
    embedding vector(1536) -- підбереш під свою модель ембеддингів
    );

-- індекс для швидкого ANN пошуку
CREATE INDEX IF NOT EXISTS ix_product_embedding_ann
    ON product_embedding USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
