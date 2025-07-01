from tortoise.contrib.pydantic import pydantic_model_creator
from app.models import Licitacao, LicitacaoItem

Licitacao_Pydantic = pydantic_model_creator(
    Licitacao,
    name="Licitacao",
)

LicitacaoIn_Pydantic = pydantic_model_creator(
    Licitacao,
    name="LicitacaoIn",
    exclude_readonly=True,
)

LicitacaoItem_Pydantic = pydantic_model_creator(
    LicitacaoItem,
    name="LicitacaoItem",
)

LicitacaoItemIn_Pydantic = pydantic_model_creator(
    LicitacaoItem,
    name="LicitacaoItemIn",
    model_config={
        "extra": "allow",
    },
    include=(
        "nome",
        "descricao",
        "tratamento_diferenciado",
        "aplicabilidade_decreto_7174",
        "aplicabilidade_margem_de_preferencia",
        "quantidade",
        "unidade_de_fornecimento",
    ),
    exclude_readonly=True,
)
