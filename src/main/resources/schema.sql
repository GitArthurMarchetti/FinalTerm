-- schema.sql
CREATE TABLE IF NOT EXISTS receipts (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  created_at TEXT NOT NULL,
  customer_name TEXT NOT NULL,
  subtotal TEXT NOT NULL,
  tax TEXT NOT NULL,
  total TEXT NOT NULL,
  content TEXT NOT NULL
);