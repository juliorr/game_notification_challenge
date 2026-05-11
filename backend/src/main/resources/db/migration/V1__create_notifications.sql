create table notifications (
  id uuid primary key,
  user_id bigint not null,
  type varchar(64) not null,
  category varchar(64) not null,
  message text not null,
  created_at timestamptz not null,
  read_at timestamptz null
);
create index ix_notifications_user_created on notifications (user_id, created_at desc);
create index ix_notifications_user_unread on notifications (user_id) where read_at is null;
