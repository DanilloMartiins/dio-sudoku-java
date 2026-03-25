# Sudoku em Java (DIO)

Projeto de estudo baseado no desafio de Sudoku da DIO, com foco em:
- Programacao orientada a objetos
- Manipulacao de colecoes e regras de negocio
- Interacao via terminal
- Organizacao de codigo para evolucao incremental

O projeto possui duas entradas:
- `br.com.dio.Main` (terminal)
- `br.com.dio.UIMain` (interface Swing)

## Funcionalidades

No modo terminal (`Main`):
- Iniciar novo jogo
- Inserir numero
- Remover numero
- Verificar status do jogo
- Limpar jogo
- Finalizar jogo
- Sair com confirmacao (`[Y / N]`) quando houver jogo em andamento

Melhorias aplicadas:
- Validacao de jogada em tempo real (linha, coluna e setor 3x3)
- Exibicao automatica do tabuleiro apos inserir/remover numero
- Mensagens claras quando a jogada nao e aplicada
- Configuracao padrao carregada automaticamente quando nao ha argumentos
- Validacao forte dos argumentos de entrada

## Estrutura principal

```text
src/
  br/com/dio/
    Main.java
    UIMain.java
    model/
    service/
    ui/
    util/
```

## Como executar

## Requisitos
- JDK 17+ (recomendado: 21)

### 1) Compilar

```powershell
$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if (!(Test-Path out)) { New-Item -ItemType Directory out | Out-Null }
javac -d out $files
```

### 2) Rodar no terminal

Com tabuleiro padrao (sem argumentos):

```powershell
java -cp out br.com.dio.Main
```

Com argumentos customizados:

```powershell
java -cp out br.com.dio.Main "0,0;4,false" "1,0;7,false" "2,0;9,true" ...
```

Formato de cada argumento:

```text
coluna,linha;valor,fixed
```

Exemplo:
- `0,0;4,false` -> posicao (0,0), valor esperado 4, nao fixo
- `2,0;9,true` -> posicao (2,0), valor esperado 9, fixo

### 3) Rodar interface Swing

```powershell
java -cp out br.com.dio.UIMain
```

## Sobre o status do jogo

- `nao iniciado`: nenhum movimento valido foi aplicado
- `incompleto`: existe ao menos um movimento valido e ainda ha espacos em aberto
- `completo`: todos os espacos foram preenchidos

---

Projeto para fins educacionais e evolucao de portfolio.
