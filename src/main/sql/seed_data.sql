delete from lisenupdb.topic_feedback_all where gta_id in (select gta_id from lisenupdb.group_topics_all where created_by = 'VMOGILEV');
delete from lisenupdb.group_users_all where uga_id in (select uga_id from lisenupdb.user_groups_all where created_by = 'VMOGILEV');
delete from lisenupdb.group_topics_all where created_by = 'VMOGILEV';
delete from lisenupdb.user_groups_all where created_by = 'VMOGILEV';
delete from lisenupdb.users_all where created_by = 'VMOGILEV';

insert into lisenupdb.users_all (
  ua_id,
  created_by,
  modified_by,
  ua_username,
  ua_name,
  ua_email,
  ua_password,
  ua_active,
  ua_gravatar_hash)
values (
    1,
    'SEED',
    'SEED',
    'anonymous',
    'Anonymous',
    'null@lisenup.com',
    'junk',
    0,
    '-');


insert into lisenupdb.users_all (
  ua_id,
  created_by,
  modified_by,
  ua_username,
  ua_name,
  ua_email,
  ua_password,
  ua_active,
  ua_gravatar_hash)
values (
    100,
    'VMOGILEV',
    'VMOGILEV',
    'mve',
    'Vitaliy',
    'admin@lisenup.com',
    'junk',
    1,
    '310bc337aaf8b7ff17ce233a36934fe6');


insert into lisenupdb.user_groups_all (
  uga_id,
  ua_id,
  created_by,
  modified_by,
  uga_name,
  uga_desc,
  uga_slug,
  uga_active)
values (
    1,
    100,
    'VMOGILEV',
    'VMOGILEV',
    'My Job',
    'I want to improve my Job and would like to get your candid feedback on the following:',
    'Job',
    1);

insert into lisenupdb.group_topics_all (
  gta_id,
  uga_id,
  created_by,
  modified_by,
  gta_title,
  gta_text,
  gta_active)
values (
    1,
    1,
    'VMOGILEV',
    'VMOGILEV',
    'Constructive Criticism',
    'If you ever wanted to give me a constructive criticism - please do not hesitate to do so now:',
    1
);

insert into lisenupdb.group_topics_all (
  gta_id,
  uga_id,
  created_by,
  modified_by,
  gta_title,
  gta_text,
  gta_active)
values (
    2,
    1,
    'VMOGILEV',
    'VMOGILEV',
    'I am Know For ...',
    'If someone asked you what I am known for?  What is the one thing that comes to mind?',
    1
);

insert into lisenupdb.group_topics_all (
  gta_id,
  uga_id,
  created_by,
  modified_by,
  gta_title,
  gta_text,
  gta_active)
values (
    3,
    1,
    'VMOGILEV',
    'VMOGILEV',
    'One Thing Better ...',
    'If we were to work together again, what is the one thing that I should do better?',
    1
);
