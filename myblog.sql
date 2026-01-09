use myblog;

select * from user_role_tb;
select * from user_tb;

select u.*, r.role
from user_tb u
left join user_role_tb r
	on u.id = r.user_id
where u.username = "aaa";

insert into user_role_tb(user_id, role) value(8, 'USER');
insert into user_role_tb(user_id, role) value(8, 'ADMIN');