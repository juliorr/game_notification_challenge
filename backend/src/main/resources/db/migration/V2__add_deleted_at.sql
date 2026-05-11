alter table notifications add column deleted_at timestamptz null;
create index ix_notifications_user_active on notifications (user_id, created_at desc) where deleted_at is null;
