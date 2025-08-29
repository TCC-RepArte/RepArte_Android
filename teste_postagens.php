<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Dados de teste para verificar se o Android está recebendo corretamente
$postagens_teste = array(
    array(
        'id' => 'teste_001',
        'titulo' => 'Resenha de Teste',
        'texto' => 'Esta é uma resenha de teste para verificar se o sistema está funcionando.',
        'id_usuario' => '2147483',
        'nome_usuario' => 'Usuário Teste',
        'foto_usuario' => 'avatar.jpg',
        'id_obra' => '123456',
        'titulo_obra' => 'Filme de Teste',
        'poster_obra' => '/teste_poster.jpg',
        'data_criacao' => '2025-08-29 19:30:00'
    ),
    array(
        'id' => 'teste_002',
        'titulo' => 'Segunda Resenha',
        'texto' => 'Outra resenha para testar o sistema de postagens.',
        'id_usuario' => '2147483',
        'nome_usuario' => 'Usuário Teste',
        'foto_usuario' => 'avatar.jpg',
        'id_obra' => '789012',
        'titulo_obra' => 'Livro de Teste',
        'poster_obra' => '/teste_livro.jpg',
        'data_criacao' => '2025-08-29 19:35:00'
    )
);

$response = array(
    'success' => true,
    'message' => 'Dados de teste carregados com sucesso',
    'postagens' => $postagens_teste,
    'total' => count($postagens_teste)
);

echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>
