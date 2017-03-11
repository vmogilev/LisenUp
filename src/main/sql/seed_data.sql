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
    'Please answer the following question(s):',
    'LinkedIn',
    1);

insert into lisenupdb.group_questions_all (
  gqa_id,
  uga_id,
  created_by,
  modified_by,
  gqa_title,
  gqa_text,
  gqa_active)
values (
    1,
    1,
    'VMOGILEV',
    'VMOGILEV',
    'What could I have done better?',
    'If you ever wanted to give me a constructive criticism - now is your chance.  What could I have done better?',
    1
);

insert into lisenupdb.group_questions_all (
  gqa_id,
  uga_id,
  created_by,
  modified_by,
  gqa_title,
  gqa_text,
  gqa_active)
values (
    2,
    1,
    'VMOGILEV',
    'VMOGILEV',
    'Would you work with me again?',
    'Given another oportunity, would you work with me again?',
    1
);
