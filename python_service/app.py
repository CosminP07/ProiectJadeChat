import os
from fastapi import FastAPI
from pydantic import BaseModel
from pydantic_ai import Agent
from pydantic_ai.models.openai import OpenAIChatModel

# 1. Configurare prin variabile de mediu (cea mai robustă metodă)
# Acestea spun clientului intern să se uite la Ollama, nu la serverele OpenAI
os.environ["OPENAI_BASE_URL"] = "http://localhost:11434/v1"
os.environ["OPENAI_API_KEY"] = "ollama"

# 2. Inițializăm modelul simplu (va citi automat config-ul de mai sus)
model = OpenAIChatModel('mistral:latest')

# 3. Definim agentul
agent = Agent(model, system_prompt="Ești un asistent util într-un chat. Răspunsurile tale sunt scurte și la obiect.")

app = FastAPI()

class ChatRequest(BaseModel):
    message: str

class ChatResponse(BaseModel):
    suggestion: str

@app.post("/assist", response_model=ChatResponse)
async def assist_user(request: ChatRequest):
    # Rulăm agentul
    result = await agent.run(f"Sugerează un răspuns scurt pentru: '{request.message}'")
    
    # --- DEBUGGING START ---
    # Afișăm în terminal ce atribute are obiectul 'result'
    print(f"DEBUG: Tipul obiectului este: {type(result)}")
    print(f"DEBUG: Atributele disponibile sunt: {dir(result)}")
    # --- DEBUGGING END ---

    # Încercăm să extragem răspunsul text
    answer_text = "N/A"
    
    if hasattr(result, 'data'):
        answer_text = result.data
    elif hasattr(result, 'output'): # Posibilă denumire alternativă
        answer_text = result.output
    elif hasattr(result, 'content'): # Altă variantă
        answer_text = result.content
    else:
        # Fallback: transformăm tot obiectul în string pentru a nu da eroare 500
        answer_text = str(result)

    return ChatResponse(suggestion=str(answer_text))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)