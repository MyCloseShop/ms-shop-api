-- Migration pour ajouter le support Stripe Connect aux shops
ALTER TABLE shop ADD COLUMN stripe_account_id VARCHAR(255);

-- Index pour optimiser les recherches par stripe_account_id
CREATE INDEX idx_shop_stripe_account_id ON shop(stripe_account_id);
