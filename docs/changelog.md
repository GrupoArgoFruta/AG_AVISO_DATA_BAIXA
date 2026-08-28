# Changelog — AG_AVISO_DATA_BAIXA

Todas as mudanças relevantes do projeto são documentadas aqui.
Formato: `[versão] - data | tipo(EB-XX): descrição`

---

## [1.2.0] - 2026-08-27

Leva de ajustes solicitada pelo Financeiro (e-mail de 29/05) e alinhada na reunião
"Automação para envio de comprovantes fornecedores" de 27/08.

### feat(AG-04): múltiplos e-mails por parceiro via AD_EMAILCLIENT

**Arquivos:** `DtbaixaPrincipal.java`, `EnvioEmailService.java`

- Adicionado campo customizado `TGFPAR.AD_EMAILCLIENT`, que aceita um ou mais e-mails adicionais separados por vírgula (`,`) ou ponto-e-vírgula (`;`).
- Novo método `resolverEmails(emailClient, emailPadrao)` em `DtbaixaPrincipal`: sempre inclui o `EMAIL` padrão do parceiro (quando preenchido) e soma os endereços extras de `AD_EMAILCLIENT` (split, trim e deduplicação via `LinkedHashSet`). `AD_EMAILCLIENT` é aditivo, não substitui o `EMAIL`.
- `EnvioEmailService.enviarEmail` alterado de `String emailParceiro` para `List<String> emailsParceiro` — agora insere um registro na fila `TGFMSG` por endereço resolvido, mantendo uma única cópia interna por `NUFIN`.
- WHERE da consulta SQL ajustado de `PAR.EMAIL IS NOT NULL` para `(PAR.AD_EMAILCLIENT IS NOT NULL OR PAR.EMAIL IS NOT NULL)`.

### fix(AG-05): disparo no dia seguinte ao pagamento

**Arquivo:** `DtbaixaPrincipal.java`

- Filtro de data alterado de `TRUNC(VGFFIN.DHBAIXA) = TRUNC(SYSDATE)` para `TRUNC(VGFFIN.DHBAIXA) = TRUNC(SYSDATE) - 1`.
- O comprovante passa a ser enviado **no dia seguinte** ao pagamento — a ação agendada roda em D+1 e consulta as baixas de D.

### fix(AG-06): comprovante exibe o número da nota fiscal

**Arquivo:** `DtbaixaPrincipal.java`

- O corpo do e-mail exibia o número único do financeiro (`NUFIN`), que os fornecedores não reconhecem.
- Adicionado `LEFT JOIN TGFCAB CAB ON (CAB.NUNOTA = FIN.NUNOTA)` e a coluna `CAB.NUMNOTA` no `SELECT`.
- `buildEmailHtml` passou a receber `numNota` e o rótulo mudou de "Número do Documento" para "Número da Nota".

### fix(AG-07): apenas títulos de despesa (pagamentos a fornecedores)

**Arquivo:** `DtbaixaPrincipal.java`

- WHERE alterado de `VGFFIN.RECDESP = 1` (receita) para `VGFFIN.RECDESP = -1` (despesa).
- Pagamentos a fornecedores são títulos de despesa; títulos de receita não devem gerar comprovante.

### fix(AG-08): restringe o disparo à TOP de baixa 1500

**Arquivo:** `DtbaixaPrincipal.java`

- WHERE alterado para `VGFFIN.CODTIPOPERBAIXA = 1500` (pagamento normal via banco).
- Elimina envios indevidos em baixas de **compensação** (TOPs 1501 / 1407) e **devolução**, que vinham gerando comprovantes incorretos desde maio.
- O filtro é pela TOP da **baixa** (`CODTIPOPERBAIXA`), não pela TOP do título.

### feat(AG-09): número da Invoice CMA no comprovante (opcional)

**Arquivo:** `DtbaixaPrincipal.java`

- Novo campo `TGFCAB.AD_INVOICECMA` (texto, cabeçalho da nota) para o número da fatura da CMA / F-Trade, que não operam com boleto.
- Coluna `CAB.AD_INVOICECMA` adicionada ao `SELECT`; `buildEmailHtml` recebe `invoiceCma`.
- O bloco "Invoice CMA" só aparece no HTML do comprovante **quando o campo está preenchido**; para os demais fornecedores o e-mail continua idêntico.
- Campo **não obrigatório** no Dicionário de Dados — exigência restrita a CMA e F-Trade.

### fix(AG-10): valor total formatado como moeda no comprovante

**Arquivo:** `DtbaixaPrincipal.java`

- O corpo do e-mail exibia `vlrBaixa` como número cru (ex.: `85680`), sem separador de milhar nem centavos.
- Novo método `formatarMoeda(BigDecimal)` — padrão pt-BR com prefixo `R$` (ex.: `R$ 85.680,00`); retorna string vazia para valor nulo.
- `buildEmailHtml` passou a exibir `formatarMoeda(vlrBaixa)` no campo "Valor total".

---

## [1.1.0] - 2026-05-20

### fix(AG-02): data de baixa dinâmica via SYSDATE

**Arquivo:** `DtbaixaPrincipal.java`

- Substituído filtro hardcoded `TRUNC(TO_DATE('2024-05-14', 'yyyy-mm-dd'))` por `TRUNC(SYSDATE)` no WHERE da consulta SQL.
- A ação agendada agora sempre consulta baixas do **dia corrente** automaticamente, sem necessidade de alteração manual da data.

### feat(AG-03): notificação única por parceiro por dia

**Arquivo:** `DtbaixaPrincipal.java`

- Adicionado `Set<BigDecimal> parceirosNotificados` para controlar quais parceiros (`CODPARC`) já receberam e-mail durante a execução do dia.
- Se um parceiro possui múltiplas baixas no mesmo dia, apenas a **primeira** dispara o envio de e-mail; os demais registros são ignorados com log via `sac.info(...)`.
- Comportamento: **1 e-mail por parceiro por execução diária** — sem spam para parceiros com vários títulos baixados no mesmo dia.

---

## [1.0.0] - versão inicial

- Ação agendada (`ScheduledAction`) que consulta baixas financeiras do dia na VGFFIN.
- Filtra parceiros com flag `AD_ATIVOVLRBAIXA = 'S'` e envia comprovante HTML por e-mail via `TGFMSG` (fila de mensagens Sankhya).
- Serviços: `EnvioEmailService`, `NotificacaoService`.
