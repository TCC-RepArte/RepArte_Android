# Guia de Animações - RepArte Android

Este guia explica como usar as animações implementadas no app para melhorar a experiência do usuário.

## 🎨 Animações Disponíveis

### 1. **button_click.xml** - Animação de Clique
- **Efeito**: Escala o botão para 95% e retorna ao normal
- **Duração**: 200ms
- **Uso**: Para feedback visual em botões

### 2. **fade_in.xml** - Fade In
- **Efeito**: Elemento aparece gradualmente
- **Duração**: 500ms
- **Uso**: Para elementos que aparecem na tela

### 3. **bounce.xml** - Bounce
- **Efeito**: Elemento "pula" com efeito elástico
- **Duração**: 400ms
- **Uso**: Para feedback positivo (sucesso, confirmação)

### 4. **shake.xml** - Shake
- **Efeito**: Elemento treme horizontalmente
- **Duração**: 150ms (3 repetições)
- **Uso**: Para feedback de erro

### 5. **slide_up.xml** - Slide Up
- **Efeito**: Elemento sobe da parte inferior da tela
- **Duração**: 300ms
- **Uso**: Para elementos que entram na tela

## 🛠️ Como Usar

### Usando a Classe AppAnimationUtils

```java
// Animação de clique em botão
AppAnimationUtils.animateButtonClick(meuBotao);

// Animação de clique com callback
AppAnimationUtils.animateButtonClick(meuBotao, () -> {
    // Código que executa após a animação
    fazerAlgumaCoisa();
});

// Animação de fade in
AppAnimationUtils.animateFadeIn(minhaView);

// Animação de bounce
AppAnimationUtils.animateBounce(minhaView);

// Animação de shake (para erros)
AppAnimationUtils.animateShake(campoComErro);

// Animação de slide up
AppAnimationUtils.animateSlideUp(minhaView);
```

### Exemplo Prático - Botão com Animação

```java
Button btnSalvar = findViewById(R.id.btnSalvar);

btnSalvar.setOnClickListener(v -> {
    // Aplica animação de clique
    AppAnimationUtils.animateButtonClick(btnSalvar, () -> {
        // Executa a ação após a animação
        salvarDados();
    });
});
```

### Exemplo - Validação com Animação de Erro

```java
EditText campoEmail = findViewById(R.id.campoEmail);

if (email.isEmpty()) {
    campoEmail.setError("Campo obrigatório");
    AppAnimationUtils.animateShake(campoEmail);
    campoEmail.requestFocus();
    return;
}
```

### Exemplo - Sucesso com Animação de Bounce

```java
if (operacaoSucesso) {
    AppAnimationUtils.animateBounce(btnConfirmar);
    Toast.makeText(this, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show();
}
```

## 🎯 Animações de Entrada de Tela

Para animar elementos quando a tela carrega:

```java
private void animateElementsOnStart() {
    // Anima elementos com delays diferentes para criar sequência
    View elemento1 = findViewById(R.id.elemento1);
    elemento1.setAlpha(0f);
    elemento1.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(200)
            .start();

    View elemento2 = findViewById(R.id.elemento2);
    elemento2.setAlpha(0f);
    elemento2.setTranslationY(30f);
    elemento2.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(400)
            .start();
}
```

## 🎨 Animações de Transição entre Telas

As animações de transição já estão configuradas:

```java
// Transição da esquerda para direita
overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

// Transição da direita para esquerda
overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
```

## 💡 Dicas de Uso

1. **Não exagere**: Use animações com moderação para não sobrecarregar o usuário
2. **Consistência**: Mantenha o mesmo tipo de animação para ações similares
3. **Performance**: Animações muito complexas podem afetar a performance
4. **Feedback**: Use animações para dar feedback visual das ações do usuário
5. **Acessibilidade**: Considere usuários que podem ter desabilitado animações

## 🔧 Personalização

Para criar novas animações, adicione arquivos XML na pasta `res/anim/`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <scale
        android:duration="300"
        android:fromXScale="1.0"
        android:fromYScale="1.0"
        android:pivotX="50%"
        android:pivotY="50%"
        android:toXScale="1.2"
        android:toYScale="1.2" />
</set>
```

## 📱 Telas com Animações Implementadas

- ✅ **ExibirPerfil**: Botões, elementos de navegação, entrada de elementos
- ✅ **MainActivity**: Splash screen com animações sequenciais
- ✅ **Login**: Campos, botões, feedback de erro/sucesso
- ✅ **Tela**: Tela principal com animações de entrada

## 🚀 Próximos Passos

Para aplicar animações em outras telas:

1. Importe a classe `AppAnimationUtils`
2. Adicione animações nos `OnClickListener` dos botões
3. Crie método para animar elementos de entrada
4. Teste a performance e ajuste conforme necessário

---

**Lembre-se**: As animações devem melhorar a experiência do usuário, não atrapalhar! 🎯 