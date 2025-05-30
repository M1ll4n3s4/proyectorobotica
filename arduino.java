const int primerPinLED = 22; // D22 hasta D52 para los 31 LEDs
String tareas[32]; // tareas[1] a tareas[31]
int diaActual = 1;
unsigned long tiempoAnterior = 0;
unsigned long intervaloDia = 30000; // 30 segundos = 1 "día"

void setup() {
  Serial.begin(9600);

  // Configurar pines de LEDs
  for (int i = 0; i < 31; i++) {
    pinMode(primerPinLED + i, OUTPUT);
    digitalWrite(primerPinLED + i, LOW);
  }

  Serial.println("=== CALENDARIO MENSUAL CON LEDS ===");
  Serial.println("Comandos:");
  Serial.println("  tarea <dia> <texto>  → agendar tarea");
  Serial.println("  borrar <dia>         → eliminar tarea");
  Serial.println("  ver                  → ver todas las tareas");
  Serial.println("-----------------------------------");
}

void loop() {
  // Simular el paso de un día
  if (millis() - tiempoAnterior >= intervaloDia) {
    tiempoAnterior = millis();

    Serial.print("📅 Día actual: ");
    Serial.println(diaActual);

    // Apagar todos los LEDs
    for (int i = 0; i < 31; i++) {
      digitalWrite(primerPinLED + i, LOW);
    }

    // Si hay tarea en el día actual → encender LED
    if (tareas[diaActual] != "") {
      Serial.print("📌 Tarea de hoy: ");
      Serial.println(tareas[diaActual]);
      Serial.println(diaActual);
      digitalWrite(primerPinLED + diaActual - 1, HIGH);
    }

    diaActual++;
    if (diaActual > 31) diaActual = 1;
  }

  // Leer comandos desde el Monitor Serial
  if (Serial.available()) {
    String entrada = Serial.readStringUntil('\n');
    entrada.trim();

    if (entrada.startsWith("tarea ")) {
      int esp1 = entrada.indexOf(' ');
      int esp2 = entrada.indexOf(' ', esp1 + 1);

      if (esp2 != -1) {
        int dia = entrada.substring(esp1 + 1, esp2).toInt();
        String desc = entrada.substring(esp2 + 1);

        if (dia >= 1 && dia <= 31) {
          tareas[dia] = desc;
          Serial.print("✅ Tarea agendada para el día ");
          Serial.print(dia);
          Serial.print(": ");
          Serial.println(desc);
        } else {
          Serial.println("❌ Día inválido. Usa 1–31.");
        }
      } else {
        Serial.println("❌ Formato inválido. Usa: tarea <día> <texto>");
      }

    } else if (entrada.startsWith("borrar ")) {
      int dia = entrada.substring(7).toInt();
      if (dia >= 1 && dia <= 31) {
        tareas[dia] = "";
        Serial.print("🗑 Tarea del día ");
        Serial.print(dia);
        Serial.println(" eliminada.");
      } else {
        Serial.println("❌ Día inválido. Usa 1–31.");
      }

    } else if (entrada == "ver") {
      Serial.println("📋 Tareas programadas:");
      bool alguna = false;
      for (int i = 1; i <= 31; i++) {
        if (tareas[i] != "") {
          Serial.print("  Día ");
          Serial.print(i);
          Serial.print(": ");
          Serial.println(tareas[i]);
          alguna = true;
        }
      }
      if (!alguna) Serial.println("  (No hay tareas)");
    } else {
      Serial.println("❌ Comando no reconocido.");
    }
  }
}