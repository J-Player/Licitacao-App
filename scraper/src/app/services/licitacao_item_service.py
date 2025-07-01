import os
from tortoise import Tortoise

from app.schemas import LicitacaoItem_Pydantic
from app.repositories.licitacao_item_repository import LicitacaoItemRepository


class LicitacaoItemService:

    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super().__new__(cls, *args, **kwargs)
        return cls._instance

    def __init__(self):
        if not hasattr(self, "repository"):
            self.__repository = LicitacaoItemRepository()

    async def create(self, licitacao_item: LicitacaoItem_Pydantic):  # type: ignore
        return await self.__repository.create(licitacao_item)

    async def bulk_create(self, licitacao_items: list[LicitacaoItem_Pydantic]):  # type: ignore
        return await self.__repository.bulk_create(licitacao_items)
