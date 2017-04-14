delete from lisenupdb.topic_feedback_all where gta_id in (select gta_id from lisenupdb.group_topics_all where created_by = 'SILVERADO');
delete from lisenupdb.group_users_all where uga_id in (select uga_id from lisenupdb.user_groups_all where created_by = 'SILVERADO');
delete from lisenupdb.group_topics_all where created_by = 'SILVERADO';
delete from lisenupdb.user_groups_all where created_by = 'SILVERADO';
delete from lisenupdb.users_all where created_by = 'SILVERADO';


insert into lisenupdb.users_all (
  ua_id,
  created_by,
  modified_by,
  ua_username,
  ua_name,
  ua_email,
  ua_password,
  ua_active,
  ua_gravatar_hash,
  ua_greeting)
values (
    99,
    'SILVERADO',
    'SILVERADO',
    'sbp',
    'Silverado Beverly Place',
    'mvetmp-jz@yahoo.com',
    'change',
    1,
    '6ea6e5e0932828618681fdd519d063d3',
    'Silverado Beverly Place Memory Care Community');


insert into lisenupdb.user_groups_all (
  uga_id,
  ua_id,
  created_by,
  modified_by,
  uga_name,
  uga_desc,
  uga_slug,
  uga_active,
  uga_public)
values (
    3,
    99,
    'SILVERADO',
    'SILVERADO',
    'Culinary / Kitchen',
    'Our Director of Culinary Services - Daniel Quadra would love to get your candid feedback on the following:',
    'Kitchen',
    1,
    1);

insert into lisenupdb.user_groups_all (
  uga_id,
  ua_id,
  created_by,
  modified_by,
  uga_name,
  uga_desc,
  uga_slug,
  uga_active,
  uga_public)
values (
    4,
    99,
    'SILVERADO',
    'SILVERADO',
    'Health Services',
    'Our Director of Health Services - Jean de Guzman would love to get your candid feedback on the following:',
    'Health',
    1,
    1);

insert into lisenupdb.user_groups_all (
  uga_id,
  ua_id,
  created_by,
  modified_by,
  uga_name,
  uga_desc,
  uga_slug,
  uga_active,
  uga_public)
values (
    5,
    99,
    'SILVERADO',
    'SILVERADO',
    'Management / Administration',
    'Our Administrator of Beverly Place - Jason Russo would love to get your candid feedback on the following:',
    'Admin',
    1,
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
    6,
    5,
    'SILVERADO',
    'SILVERADO',
    'Constructive Criticism',
    'If you ever wanted to give Beverly Place a constructive criticism - please do not hesitate to do so now:',
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
    7,
    3,
    'SILVERADO',
    'SILVERADO',
    'Food Quality',
    'How is our food quality and variety?',
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
    8,
    3,
    'SILVERADO',
    'SILVERADO',
    'Stock',
    'Are we keeping up with stocking the kitchen and refrigerator with supplies?',
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
    9,
    4,
    'SILVERADO',
    'SILVERADO',
    'Caregivers',
    'Are you happy with our caregivers?',
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
    10,
    4,
    'SILVERADO',
    'SILVERADO',
    'Nurses',
    'Are you happy with our nurses?',
    1
);
