from tortoise import Tortoise


async def init_db(db_url: str):
    await Tortoise.init(db_url=db_url, modules={"models": ["app.models"]}, timezone="America/Sao_Paulo", use_tz=True)
    Tortoise.init_models(["app.models"], "models")
    await Tortoise.generate_schemas()


async def close_db():
    await Tortoise.close_connections()
