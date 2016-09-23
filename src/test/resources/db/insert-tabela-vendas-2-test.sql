--gera registros aleatoriamente
insert into venda (data_criacao, valor_total, status, codigo_cliente, codigo_usuario) 
  values (
    FROM_UNIXTIME(ROUND((RAND() * (1473292800 - 1454284800) + 1454284800)))
  , round(rand() * 10000, 2)
  , 'EMITIDA'
  , round(rand() * 7) + 1
  , round(rand() * 2) + 1)
  
  
  
  --executar relatorio, utilizado no jasperreports (tibco: jaspersoft studio)
SELECT 
	  v.codigo as codigo_venda
    , v.data_criacao  as data_criacao
    , v.valor_total as valor_total
    , c.nome as nome_cliente
    , u.nome as nome_vendedor
FROM venda v
INNER JOIN cliente c ON (v.codigo_cliente = c.codigo)
INNER JOIN usuario u ON (v.codigo_usuario = u.codigo)
WHERE 1=1
AND v.status = 'EMITIDA'
AND v.data_criacao BETWEEN '2016-02-01 00:00:00' and '2016-12-01 00:00:00' 