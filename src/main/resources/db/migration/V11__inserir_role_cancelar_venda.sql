INSERT INTO permissao VALUES ((select max(aux.codigo)+1 from permissao aux), 'ROLE_CANCELAR_VENDA');

INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1	, (select codigo from permissao where nome = 'ROLE_CANCELAR_VENDA') );
