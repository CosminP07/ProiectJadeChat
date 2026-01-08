# Sistem de Chat Multi-Agent cu Asistență AI (JADE + Pydantic AI)

Acest proiect implementează un ecosistem de agenți inteligenți capabili să comunice între ei (chat) și să solicite asistență de la un LLM local (prin Ollama) pentru sugerarea răspunsurilor. Proiectul a fost realizat pentru laboratorul de Sisteme Inteligente și Agenți.

## Funcționalități Principale

1. **Arhitectură Multi-Agent:** Implementată folosind JADE Framework.
2. **Interfață Grafică (GUI):** Pentru fiecare agent de chat și pentru launcher.
3. **Descoperire Dinamică:** Agenții se văd automat între ei folosind Directory Facilitator (DF).
4. **Persistență:** Istoricul conversațiilor este salvat local în fișiere text (chat_history_[Agent].txt).
5. **AI Assistant:** Integrare cu Python (Pydantic AI + FastAPI) și Ollama pentru generarea de răspunsuri.
6. **Control Centralizat:** Posibilitatea de a opri întreaga platformă dintr-un singur buton.

## Cerințe (Prerequisites)

- **Java JDK 21** (sau mai nou).
- **Python 3.10+**.
- **Ollama** instalat și rulând local.
- **Eclipse IDE** (pentru rularea proiectului Java).
- **Librării JADE:** Fișierul `jade.jar` (trebuie adăugat în Build Path).

## Instalare și Configurare

### Pasul 1: Configurare AI (Backend Python)

1. Asigură-te că ai Ollama pornit.
2. Descarcă modelul Mistral (recomandat pentru logică și limba română):
   
   ollama pull mistral:latest

3. Navighează în folderul serviciului Python și instalează dependențele:

   cd python_service
   python -m venv .venv
   
   # Activare mediu virtual (Windows):
   .venv\Scripts\activate
   
   # Instalare pachete necesare:
   pip install fastapi uvicorn pydantic-ai openai

### Pasul 2: Configurare JADE (Java)

1. Importă folderul `jade_project` (sau proiectul existent) în Eclipse.
2. Click dreapta pe proiect -> Build Path -> Configure Build Path.
3. La tab-ul "Libraries", asigură-te că `jade.jar` este adăugat la **Classpath**.

## Cum se rulează aplicația

Ordinea de execuție este strictă pentru a asigura conectivitatea.

### 1. Pornire Server AI
În terminalul `python_service` (cu venv activat), rulează:

python app.py

*Trebuie să vezi mesajul: "Application startup complete".*

### 2. Pornire Agenți JADE
În Eclipse:
1. Deschide clasa `chat_system.LauncherAgent`.
2. Click dreapta -> Run As -> Java Application.

### 3. Utilizare
1. În fereastra mică (Launcher), scrie un nume (ex: `User1`) și apasă **Launch**.
2. Scrie un alt nume (ex: `User2`) și apasă **Launch**.
3. Așteaptă câteva secunde ca lista de utilizatori din dreapta să se actualizeze (prin DF).
4. Selectează destinatarul și trimite mesaje.
5. Apasă butonul **AI Help** pentru a primi o sugestie de la Mistral.

## Structură Proiect

- **/jade_project**: Codul sursă Java (Agenții JADE, GUI, Logică).
- **/python_service**: Codul sursă Python (API-ul FastAPI și Pydantic AI).
- **chat_history_*.txt**: Fișierele de log generate automat la rulare (Persistență).

## Autor

Nume: Pintilei Cosmin - Ionuț
Grupa: 3141a