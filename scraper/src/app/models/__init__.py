import uuid
from tortoise import fields
from tortoise.models import Model


class Licitacao(Model):
    id = fields.UUIDField(pk=True, default=uuid.uuid4)
    unidade = fields.TextField(null=True)
    uasg = fields.IntField()
    numero_pregao = fields.IntField()
    objeto = fields.TextField(null=True)
    horario_edital = fields.CharField(max_length=255, null=True)
    endereco = fields.CharField(max_length=255, null=True)
    telefone = fields.CharField(max_length=255, null=True)
    fax = fields.CharField(max_length=255, null=True)
    entrega_da_proposta = fields.DatetimeField(null=True)
    itens: fields.ReverseRelation["LicitacaoItem"]

    class Meta:
        table = "licitacao"
        unique_together = (("uasg", "numero_pregao"),)
        ordering = ["entrega_da_proposta"]


class LicitacaoItem(Model):
    id = fields.UUIDField(pk=True, default=uuid.uuid4)
    licitacao: fields.ForeignKeyRelation["Licitacao"] = fields.ForeignKeyField("models.Licitacao", related_name="itens", on_delete=fields.CASCADE, null=False)
    nome = fields.CharField(max_length=255, null=True)
    descricao = fields.TextField(null=True)
    tratamento_diferenciado = fields.CharField(max_length=255, null=True)
    aplicabilidade_decreto_7174 = fields.CharField(max_length=255, null=True)
    aplicabilidade_margem_de_preferencia = fields.CharField(max_length=255, null=True)
    quantidade = fields.IntField(null=True)
    unidade_de_fornecimento = fields.CharField(max_length=255, null=True)

    class Meta:
        table = "licitacaoitem"