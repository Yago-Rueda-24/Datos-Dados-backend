use datosdados;
select * from spell_entity join user_entity on spell_entity.user_id= user_entity.id where user_entity.username != 'admin' and spell_entity.public_visible = 1;

