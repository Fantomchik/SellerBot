CREATE TABLE IF NOT EXISTS price_request (
    id SERIAL PRIMARY KEY,
    model_key VARCHAR(255) NOT NULL,
    model_display VARCHAR(255) NOT NULL,
    status VARCHAR(16) NOT NULL,
    manager_chat_id BIGINT NOT NULL,
    manager_request_message_id INTEGER NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    resolved_at TIMESTAMP NULL,
    resolved_price INTEGER NULL
);

-- Ensure only one OPEN request per model_key
CREATE UNIQUE INDEX IF NOT EXISTS uq_price_request_open_model_key
    ON price_request (model_key)
    WHERE status = 'OPEN';

-- Link manager reply -> request (allow NULL during creation)
CREATE UNIQUE INDEX IF NOT EXISTS uq_price_request_manager_message
    ON price_request (manager_chat_id, manager_request_message_id)
    WHERE manager_request_message_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS price_request_waiter (
    id SERIAL PRIMARY KEY,
    request_id INTEGER NOT NULL REFERENCES price_request(id) ON DELETE CASCADE,
    waiter_chat_id BIGINT NOT NULL,
    waiter_message_id INTEGER NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_price_request_waiter_unique
    ON price_request_waiter (request_id, waiter_chat_id);

