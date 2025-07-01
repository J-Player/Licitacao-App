# Licitacao App

Uma aplicação que extrai, transforma e carrega dados sobre as licitações do dia.

## Notas do Desenvolvedor

Este projeto faz parte de um teste técnico com duração limite de até 3 dias para finalizar. Ainda há muitas mudanças e melhorias que planejo implementar neste projeto posteriormente.

Aqui estão algumas ideias que tive enquanto desenvolvia:
- API
   - Implementar um sistema de cache simples ou distribuído usando Redis para reduzir o tráfego entre a API e o banco de dados
- Frontend
   - Melhorar o design da página e talvez criar novas rotas conforme a aplicação escala
- Scraper
   - Criar um servidor para o Scraper para administradores possam:
      - Agendar execuções
      - Executar manualmente o Scraper

## Pré-requisitos

- Java
- NodeJS
- Python
- Docker

## Características

A aplicação é dividida em três principais componentes:

- **Scraper**: Desenvolvido em **Python** para realizar a extração, transformação e carregamento dos dados obtidos do site das licitações para o banco de dados. **As licitações são atualizadas uma vez por dia, portanto é possível agendar uma execução por dia para o Scraper pegar as novas licitações**.
- **API**: Desenvolvida em **Java** para gerenciar e fornecer dados estruturados para o frontend da aplicação
- **Website**: Desenvolvido em **VueJS** com uma interface simples masa que possibilita ao usuário consultar e filtrar dados de licitações.

## Como Usar?

1. Inicialize o banco de dados _containerizado_ com **Docker**:

   ```
   cd docker
   docker compose up -d
   cd ..
   ```

2. Execute o scraper que fará a extração, transformação e carregamento dos dados do site para o banco de dados:
   ```python
   cd scraper
   python -m venv .venv # (opcional - cria um ambiente virtual)
   .venv\Scripts\activate # (opcional - ativa o ambiente virtual)
   pip install -r requirements.txt
   python src\main.py
   ```
3. Execute a API da aplicação
   ```
   cd backend
   ./gradlew run
   ```
4. Execute o frontend da aplicação
   ```
   cd frontend
   npm install
   npm run dev
   ```
5. Acesse a aplicação localmente através da URL:
   ```
   http://localhost:5173/
   ```
