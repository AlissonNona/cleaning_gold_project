// r2.asl - Agente que queima lixo

+!start_mission.
+!start_mission <- .print("R2: Pronto para queimar lixo.").

+garbage_delivered : has_garbage
   <- .print("R2: Recebi lixo do R1. Queimando...");
      burn(garb);
      -has_garbage.

+has_garbage.