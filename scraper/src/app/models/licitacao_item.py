import uuid
from tortoise import fields
from tortoise.models import Model


class LicitacaoItem(Model):
    id = fields.UUIDField(pk=True, default=uuid.uuid4)
    licitacao_id = fields.ForeignKeyField("models.Licitacao", related_name="itens", on_delete=fields.CASCADE)
    nome = fields.CharField(max_length=255, null=True)
    descricao = fields.TextField(null=True)
    tratamento_diferenciado = fields.CharField(max_length=255, null=True)
    aplicabilidade_decreto_7174 = fields.CharField(max_length=255, null=True)
    aplicabilidade_margem_de_preferencia = fields.CharField(max_length=255, null=True)
    quantidade = fields.IntField(null=True)
    unidade_de_fornecimento = fields.CharField(max_length=255, null=True)

    class Meta:
        table = "licitacaoitem"
