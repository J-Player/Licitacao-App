import re
import unicodedata


def normalize_string(text: str):
    # Normalize the string to remove inconsistencies in character representation
    normalized_text = (
        unicodedata.normalize("NFKD", text).encode("ascii", "ignore").decode("utf-8")
    )
    return normalized_text


def normalize_string_keeping_symbol(text: str):
    """
    Normaliza a string substituindo NBSP por espaço comum e mantendo símbolos especiais

    Args:
        input_str (str): String de entrada contendo possíveis NBSP

    Returns:
        str: String normalizada
    """
    if not text:
        return text
    normalized = text.replace("\u00a0", " ")
    return re.sub(r"\s+", " ", normalized).strip()


def camel_case_split(str):
    
    return re.findall(r"[A-Z](?:[a-z]+|[A-Z]*(?=[A-Z]|$))", str)
