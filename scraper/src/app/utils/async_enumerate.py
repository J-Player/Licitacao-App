async def async_gen(iterable):
    for i in iterable:
        yield i


async def async_enumerate(async_iterable, start=0):
    """
    Asynchronously enumerates an async iterable, yielding (index, value) pairs.
    """
    index = start
    async for value in async_iterable:
        yield index, value
        index += 1
