from typing import Optional, List

from app.base.service_base import BaseService
from app.models import Licitacao, LicitacaoItem
from app.schemas import *


class LicitacaoService(BaseService):
    model = Licitacao
    create_schema = Create_Licitacao
    get_schema = Get_Licitacao

    async def get_all_uasg_and_numero_pregao(self):
        # Exception has occurred: AttributeError 'ValuesQuery' object has no attribute 'prefetch_related'
        return await self.model.all().values("uasg", "numero_pregao")


class LicitacaoItemService(BaseService):
    model = LicitacaoItem
    create_schema = Create_Licitacao_Item
    get_schema = Get_Licitacao_Item
