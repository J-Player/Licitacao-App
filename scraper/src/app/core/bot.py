from abc import ABC, abstractmethod
from enum import Enum
from time import time
from datetime import datetime
import logging
import os
import time
from app.utils import get_logger, camel_case_split


class BotState(Enum):
    READY = 1
    RUNNING = 2
    STOPPED = 3


class Bot(ABC):

    def __init__(self):
        self.__state = BotState.READY
        self._logger: logging.Logger = get_logger(
            self.__class__.__name__,
            level=logging.DEBUG,
            file_name=f"./logs/{self.__class__.__name__}/{self.__class__.__name__}_{datetime.now().strftime("%d_%m_%Y")}.log",
        )

    @property
    def state(self):
        return self.__state

    async def _setup(self): ...

    async def _teardown(self): ...

    @abstractmethod
    async def _start(self):
        raise NotImplementedError("O método _start deve ser implementado.")

    async def start(self):
        if self.__state == BotState.READY:
            try:
                self.__state = BotState.RUNNING
                self._logger.debug(f"Inicializando a execução do bot...")
                START_TIME = time.time()
                await self._setup()
                await self._start()
                await self._teardown()
            except Exception as err:
                self._logger.error(err)
            else:
                self._logger.debug(f"Bot executado com sucesso!")
            finally:
                self._logger.debug(f"Finalizando a execução do bot.")
                END_TIME = time.time()
                DURATION = END_TIME - START_TIME
                self._logger.debug(
                    f"Duração total de execução: {DURATION:.2f} segundos"
                )
                await self.stop()

    async def stop(self):
        if self.__state == BotState.RUNNING:
            self.__state = BotState.STOPPED

    async def reset(self):
        if self.__state == BotState.STOPPED:
            self.__state = BotState.READY
