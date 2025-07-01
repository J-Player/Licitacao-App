from app.schemas import LicitacaoIn_Pydantic
from app.repositories.licitacao_repository import LicitacaoRepository


class LicitacaoService:

    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super().__new__(cls, *args, **kwargs)
        return cls._instance

    def __init__(self):
        if not hasattr(self, "repository"):
            self.__repository = LicitacaoRepository()

    async def get_all_uasg_and_numero_pregao(self):
        return await self.__repository.get_all_uasg_and_numero_pregao()

    async def create(self, licitacao: LicitacaoIn_Pydantic):  # type: ignore
        return await self.__repository.create(licitacao)
