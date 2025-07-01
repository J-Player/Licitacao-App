import asyncio
from dotenv import load_dotenv
from app.core.licitacao_bot import LicitacaoBot

load_dotenv()


async def main():
    bot = LicitacaoBot()
    await bot.start()


if __name__ == "__main__":
    asyncio.run(main())
