// r1.asl - Agente interativo e inteligente

/* Goal inicial: começar a missão e repetir o ciclo de trabalho */
!start_mission.

+!start_mission
   <- .print("R1: Missão iniciada. Entrando no ciclo de trabalho.");
      !work_cycle.

// O ciclo de trabalho: checa o local, decide o que fazer, e explora.
+!work_cycle
   <- !check_slot;
      !manage_inventory;
      !explore;
      !work_cycle.

/* Planos de Ação baseados em Percepções */

// Se há OURO aqui e não estou carregando nada, PEGO e vou DEPOSITAR.
+!check_slot : gold_here & not carrying(gold) & not carrying(garbage)
   <- .print("R1: OURO encontrado! Coletando e levando para o depósito...");
      +carrying(gold);
      pick(gold);
      !deposit_gold_safely.

// Se há LIXO aqui e não estou carregando nada, PEGO e vou ENTREGAR.
+!check_slot : garbage_here & not carrying(gold) & not carrying(garbage)
   <- .print("R1: Lixo encontrado. Coletando e levando para o R2...");
      +carrying(garbage);
      pick(garb);
      !deliver_to_r2.

// Se não há nada de interessante ou já estou carregando algo, ignoro.
+!check_slot.

/* Plano de Exploração (se não tiver nada melhor para fazer) */
+!explore : not carrying(gold) & not carrying(garbage)
   <- .print("R1: Nada para fazer, explorando próximo local.");
      next(slot).
+!explore.

/* Planos de Gerenciamento de Inventário */
+!manage_inventory : carrying(gold)  <- !deposit_gold_safely.
+!manage_inventory : carrying(garbage) <- !deliver_to_r2.
+!manage_inventory.

/* SUB-GOALS: Tarefas específicas */

// Levar lixo para o R2
+!deliver_to_r2 : not at_r2
   <- ?r2_position(X,Y);
      !navigate_to(X,Y);
      !deliver_to_r2.
+!deliver_to_r2 : at_r2
   <- .print("R1: Cheguei no R2. Entregando lixo.");
      give_garbage;
      -carrying(garbage).

// Levar ouro para o depósito
+!deposit_gold_safely : not at_gold_deposit
   <- ?gold_deposit_pos(X,Y);
      !navigate_to(X,Y);
      !deposit_gold_safely.
+!deposit_gold_safely : at_gold_deposit
   <- .print("R1: Cheguei no depósito. Deixando o ouro.");
      deposit_gold;
      -carrying(gold).

/* Plano de Navegação Genérico */
+!navigate_to(TargetX, TargetY) : pos(r1, CurrentX, CurrentY) & CurrentX < TargetX 
   <- move(east); !navigate_to(TargetX, TargetY).
+!navigate_to(TargetX, TargetY) : pos(r1, CurrentX, CurrentY) & CurrentX > TargetX 
   <- move(west); !navigate_to(TargetX, TargetY).
+!navigate_to(TargetX, TargetY) : pos(r1, CurrentX, CurrentY) & CurrentY < TargetY 
   <- move(south); !navigate_to(TargetX, TargetY).
+!navigate_to(TargetX, TargetY) : pos(r1, CurrentX, CurrentY) & CurrentY > TargetY 
   <- move(north); !navigate_to(TargetX, TargetY).
+!navigate_to(TargetX, TargetY) : pos(r1, TargetX, TargetY) 
   <- .print("R1: Cheguei ao destino.").