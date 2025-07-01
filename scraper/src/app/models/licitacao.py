import uuid
from tortoise import fields
from tortoise.models import Model

from app.models.licitacao_item import LicitacaoItem


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
