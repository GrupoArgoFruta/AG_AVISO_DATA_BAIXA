# Changelog — AG_AVISO_DATA_BAIXA

Todas as mudanças relevantes do projeto são documentadas aqui.
Formato: `[versão] - data | tipo(EB-XX): descrição`

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
