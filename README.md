# Cleaning Gold Project 🤖💰

Este repositório contém a implementação do Projeto Inicial (PI) para a disciplina de **Tópicos em Inteligência Computacional II**. O objetivo é simular um sistema multiagentes (MAS) baseado na arquitetura BDI utilizando o framework **Jason**.

## 🎯 Objetivo do Projeto
A simulação consiste em dois robôs (R1 e R2) que operam em um ambiente de grade (*grid*):
- **Agente R1 (Coletor):** Explora o ambiente, identifica lixo (G) e ouro, e realiza a coleta.
- **Agente R2 (Queimador):** Responsável por incinerar o lixo coletado.
- **Ambiente:** Uma grade composta por *slots* onde a posição é definida por `pos(robot, x, y)`.

## 🧠 Especificação PEAS
- **Performance:** Quantidade de ouro protegida e lixo removido.
- **Environment:** Grade/Grid (slots) com lixo e itens de valor.
- **Actuators:** Rodas para movimentação (`next(slot)`), braço para coleta (`pick`).
- **Sensors:** Sensores de localização e detecção de objetos.

## 🛠️ Tecnologias e Metodologia
- **Framework:** [Jason 3.3.0](https://github.com/jason-lang/jason)
- **Linguagem:** AgentSpeak (ASL) e Java (Ambiente).
- **Metodologia de Design:** Prometheus (Modelagem BDI).

## 🚀 Como Executar
1. Certifique-se de ter o Java JDK e o Jason instalados.
2. No terminal (GitBash), navegue até a pasta `src`.
3. Execute o comando:
   ```bash
   jason cleaning_gold.mas2j
