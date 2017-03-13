insert into lisenupdb.users_all (
  ua_id,
  created_by,
  modified_by,
  ua_username,
  ua_firstname,
  ua_lastname,
  ua_email,
  ua_password,
  ua_active)
values (
    1,
    'VMOGILEV',
    'VMOGILEV',
    'mve',
    'Vitaliy',
    'Mogilevskiy',
    'admin@lisenup.com',
    'junk',
    1);


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
    1,
    'VMOGILEV',
    'VMOGILEV',
    'LinkedIn Colleagues!',
    'I value your candid feedback - fire away.',
    'LinkedIn',
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
    'If you ever wanted to give me a constructive criticism - now is your chance to provide your candid feedback.',
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
    'Vitaliy Is Know For ...',
    'If someone asked you what is Vitaliy known for?  What is the one thing that comes to your mind?  No bullshit please!',
    1
);
