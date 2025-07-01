CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS Licitacao(
	id UUID DEFAULT uuid_generate_v4(),
	unidade TEXT,
	uasg INTEGER NOT NULL,
	numero_pregao INTEGER NOT NULL,
	objeto TEXT,
	horario_edital VARCHAR(255),
	endereco VARCHAR(255),
	telefone VARCHAR(255),
	fax VARCHAR(255),
	entrega_da_proposta TIMESTAMP WITH TIME ZONE,
	PRIMARY KEY (id),
	UNIQUE(uasg, numero_pregao)
);

CREATE TABLE IF NOT EXISTS LicitacaoItem(
	id UUID DEFAULT uuid_generate_v4(),
	licitacao_id UUID NOT NULL,
	nome VARCHAR(255),
	descricao TEXT,
	tratamento_diferenciado VARCHAR(255),
	aplicabilidade_decreto_7174 VARCHAR(255),
	aplicabilidade_margem_de_preferencia VARCHAR(255),
	quantidade INTEGER,
	unidade_de_fornecimento VARCHAR(255),
	PRIMARY KEY (id),
	FOREIGN KEY (licitacao_id) REFERENCES Licitacao(id) ON DELETE CASCADE
);