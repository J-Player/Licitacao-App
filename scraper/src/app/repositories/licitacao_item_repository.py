from app.models import LicitacaoItem
from app.schemas import LicitacaoItem_Pydantic, LicitacaoItemIn_Pydantic


class LicitacaoItemRepository:
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super().__new__(cls, *args, **kwargs)
        return cls._instance

    async def create(self, licitacao_item_data: LicitacaoItemIn_Pydantic):  # type: ignore
        dados_criacao = licitacao_item_data.dict()
        licitacao_obj = await LicitacaoItem.create(**dados_criacao.dict())
        return await LicitacaoItem_Pydantic.from_tortoise_orm(licitacao_obj)

    async def bulk_create(self, licitacao_items_data: list[LicitacaoItemIn_Pydantic]):  # type: ignore
        await LicitacaoItem.bulk_create(licitacao_items_data)
