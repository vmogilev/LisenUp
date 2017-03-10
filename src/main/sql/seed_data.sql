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
    'LinkedIn Colleagues',
    'You are my LinkedIn colleague and I value your feedback on the following questions:',
    'LinkedIn',
    1);

insert into lisenupdb.group_questions_all (
  gqa_id,
  uga_id,
  created_by,
  modified_by,
  gqa_title,
  gqa_text)
values (
    1,
    1,
    'VMOGILEV',
    'VMOGILEV',
    'What could I have done better?',
    'If you ever wanted to give me a constructive criticism - now is your chance.  What could I have done better?'
);

