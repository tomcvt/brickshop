-- V2__add_shipment_order_index.sql
-- Add an index on the order_order_id column in the shipments table

CREATE INDEX idx_shipment_order_id
        ON shipments (order_order_id);
