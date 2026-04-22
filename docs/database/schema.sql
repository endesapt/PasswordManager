CREATE TABLE security_meta (
    id INTEGER NOT NULL PRIMARY KEY,
    saltBase64 TEXT NOT NULL,
    verifierBase64 TEXT NOT NULL,
    iterations INTEGER NOT NULL,
    createdAt INTEGER NOT NULL
);

CREATE TABLE vault_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    username TEXT NOT NULL,
    encryptedPassword TEXT NOT NULL,
    website TEXT NOT NULL,
    notes TEXT NOT NULL,
    isFavorite INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);
