from tortoise.contrib.pydantic import pydantic_model_creator
from app.models import Licitacao, LicitacaoItem

Create_Licitacao = pydantic_model_creator(Licitacao, exclude_readonly=True)
Get_Licitacao = pydantic_model_creator(Licitacao)

Create_Licitacao_Item = pydantic_model_creator(LicitacaoItem, exclude_readonly=True, model_config={"extra": "allow"})
Get_Licitacao_Item = pydantic_model_creator(LicitacaoItem)
