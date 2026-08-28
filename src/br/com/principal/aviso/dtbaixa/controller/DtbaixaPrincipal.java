package br.com.principal.aviso.dtbaixa.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import com.sankhya.util.JdbcUtils;

import br.com.principal.aviso.dtbaixa.service.EnvioEmailService;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class DtbaixaPrincipal implements ScheduledAction {

	@Override
	public void onTime(ScheduledActionContext sac) {
		EnvioEmailService emailService = new EnvioEmailService();
		String titulo = "Comprovante de Pagamento - ArgoFruta";
		JdbcWrapper jdbc = null;
		NativeSql queryVoa = null;
		ResultSet rset = null;
		SessionHandle hnd = null;

		try {
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();

			queryVoa = new NativeSql(jdbc);

			queryVoa.appendSql(
					"SELECT VGFFIN.VLRBAIXA, " +
							"FIN.NUFIN, " +
							"CAB.NUMNOTA, " +
							"CAB.AD_INVOICECMA, " +
							"VGFFIN.DHBAIXA, " +
							"PAR.RAZAOSOCIAL, " +
							"PAR.AD_ATIVOVLRBAIXA, " +
							"PAR.EMAIL, " +
							"PAR.AD_EMAILCLIENT, " +
							"PAR.CODPARC " +
							"FROM VGFFIN VGFFIN " +
							"INNER JOIN TGFVEN VEN ON (VEN.CODVEND = VGFFIN.CODVEND) " +
							"INNER JOIN TGFPAR PAR ON (PAR.CODPARC = VGFFIN.CODPARC) " +
							"INNER JOIN TSIEMP EMP ON (EMP.CODEMP = VGFFIN.CODEMP) " +
							"INNER JOIN TGFNAT NAT ON (NAT.CODNAT = VGFFIN.CODNAT) " +
							"INNER JOIN TGFTIT TIT ON (TIT.CODTIPTIT = VGFFIN.CODTIPTIT) " +
							"INNER JOIN TSICUS CUS ON (CUS.CODCENCUS = VGFFIN.CODCENCUS) " +
							"INNER JOIN TSIBCO BCO ON (BCO.CODBCO = VGFFIN.CODBCO) " +
							"INNER JOIN TGFFIN FIN ON (FIN.NUFIN = VGFFIN.NUFIN) " +
							"INNER JOIN TGFTOP TPP ON (TPP.CODTIPOPER = VGFFIN.CODTIPOPER AND TPP.DHALTER = VGFFIN.DHTIPOPER) " +
							"INNER JOIN TCSPRJ PROJ ON (PROJ.CODPROJ = VGFFIN.CODPROJ) " +
							"LEFT JOIN TSICTA CTA ON CTA.CODCTABCOINT = VGFFIN.CODCTABCOINT " +
							"LEFT JOIN TSIMOE Moeda ON Moeda.CODMOEDA = VGFFIN.CODMOEDA " +
							"LEFT JOIN TGFCAB CAB ON (CAB.NUNOTA = FIN.NUNOTA) " +
							"WHERE VGFFIN.RECDESP = -1 " +
							"AND TRUNC(VGFFIN.DHBAIXA) = TRUNC(SYSDATE) - 1 " +
							"AND NOT (VGFFIN.PROVISAO = 'S' AND VGFFIN.DHBAIXA IS NOT NULL) " +
							"AND VGFFIN.PROVISAO IN ('N', 'S') " +
							"AND PAR.AD_ATIVOVLRBAIXA = 'S' " +
							"AND (PAR.AD_EMAILCLIENT IS NOT NULL OR PAR.EMAIL IS NOT NULL) " +
							"AND VGFFIN.DHBAIXA IS NOT NULL " +
							"AND VGFFIN.VLRBAIXA > 0 " +
							"AND VGFFIN.CODTIPOPERBAIXA = 1500 " +
							"AND NVL(FIN.AD_NOTIFENVIADA, 'N') = 'N'"
			);

			rset = queryVoa.executeQuery();

			Set<BigDecimal> titulosNotificados = new HashSet<>();

			while (rset.next()) {
				BigDecimal nroUnico = rset.getBigDecimal("NUFIN");
				BigDecimal numNota  = rset.getBigDecimal("NUMNOTA");
				BigDecimal vlrBaixa = rset.getBigDecimal("VLRBAIXA");
				BigDecimal codParc  = rset.getBigDecimal("CODPARC");

				Date dataBaixa = rset.getDate("DHBAIXA");
				SimpleDateFormat dtBaixaFormat = new SimpleDateFormat("dd/MM/yyyy");
				String dataBaixaFormatada = dtBaixaFormat.format(dataBaixa);

				String parceiro = rset.getString("RAZAOSOCIAL");
				String email    = rset.getString("EMAIL");
				String emailClient = rset.getString("AD_EMAILCLIENT");
				String invoiceCma = rset.getString("AD_INVOICECMA");
				String ativoParaNotificar = rset.getString("AD_ATIVOVLRBAIXA");

				if (titulosNotificados.contains(nroUnico)) {
					sac.info("NUFIN " + nroUnico + " já processado nesta execução. Ignorando.");
					continue;
				}

				List<String> emailsDestino = resolverEmails(emailClient, email);
				String emailsDestinoStr = String.join(", ", emailsDestino);

				String mensagemNotificacao = buildEmailHtml(numNota, invoiceCma, dataBaixaFormatada, vlrBaixa, parceiro, emailsDestinoStr);

				if (!emailsDestino.isEmpty()
						&& dataBaixa != null
						&& vlrBaixa != null
						&& vlrBaixa.compareTo(BigDecimal.ZERO) > 0
						&& "S".equals(ativoParaNotificar)) {

					emailService.enviarEmail(titulo, mensagemNotificacao, emailsDestino);

					NativeSql updateSql = new NativeSql(jdbc);
					try {
						updateSql.appendSql("UPDATE TGFFIN SET AD_NOTIFENVIADA = 'S' WHERE NUFIN = " + nroUnico);
						updateSql.executeUpdate();
					} finally {
						NativeSql.releaseResources(updateSql);
					}

					titulosNotificados.add(nroUnico);
					sac.info("Email enviado para " + parceiro + " (" + emailsDestinoStr + ") + cópia | NUFIN: " + nroUnico + " | NUMNOTA: " + numNota + " | InvoiceCMA: " + invoiceCma);
				} else {
					sac.info("Condições inválidas para NUFIN: " + nroUnico
							+ " | emails=[" + emailsDestinoStr + "]"
							+ " | vlrBaixa=[" + vlrBaixa + "]"
							+ " | ativoNotif=[" + ativoParaNotificar + "]");
				}
			}

			sac.info("Execução finalizada. Total de títulos notificados: " + titulosNotificados.size());

		} catch (Exception e) {
			e.printStackTrace();
			sac.info("Erro no Evento Programado de notificação de baixa: " + e.getMessage());
		} finally {
			JdbcUtils.closeResultSet(rset);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			NativeSql.releaseResources(queryVoa);
		}
	}

	private List<String> resolverEmails(String emailClient, String emailPadrao) {
		Set<String> emails = new LinkedHashSet<>();

		if (emailPadrao != null && !emailPadrao.trim().isEmpty()) {
			emails.add(emailPadrao.trim());
		}

		if (emailClient != null && !emailClient.trim().isEmpty()) {
			for (String parte : emailClient.split("[,;]")) {
				String emailTratado = parte.trim();
				if (!emailTratado.isEmpty()) {
					emails.add(emailTratado);
				}
			}
		}

		return new ArrayList<>(emails);
	}

	private String formatarMoeda(BigDecimal valor) {
		if (valor == null) {
			return "";
		}
		DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
		DecimalFormat df = new DecimalFormat("#,##0.00", simbolos);
		return "R$ " + df.format(valor);
	}

	private String buildEmailHtml(BigDecimal numNota, String invoiceCma, String dataBaixa, BigDecimal vlrBaixa, String parceiro, String email) {
		// Invoice CMA: só entra no comprovante quando o parceiro tem o campo preenchido (CMA / F-Trade).
		String invoiceCmaBloco = "";
		if (invoiceCma != null && !invoiceCma.trim().isEmpty()) {
			invoiceCmaBloco =
					"                                <h4 style=\"color: #333333; margin: 5px 0;\">Invoice CMA:</h4>\r\n"
					+ "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + invoiceCma.trim() + "</p>\r\n"
					+ "                                <hr style=\"border: 0; border-top: 1px solid #ccc;\">\r\n";
		}

		return "<html>\r\n"
				+ "<head>\r\n"
				+ "    <title>Comprovante ArgoFruta</title>\r\n"
				+ "    <link href=\"https://fonts.googleapis.com/css?family=Poppins:200,300,400,500,600,700\" rel=\"stylesheet\">\r\n"
				+ "</head>\r\n"
				+ "<body style=\"background-color: #f4f4f4; margin: 0; padding: 0; width: 100%; height: 100%; font-family: Poppins, sans-serif; color: rgba(0, 0, 0, .4);\">\r\n"
				+ "    <table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n"
				+ "        <tr>\r\n"
				+ "            <td align=\"center\" valign=\"top\" style=\"padding-top: 20px; padding-bottom: 20px;\">\r\n"
				+ "                <table width=\"600\" border=\"0\" cellpadding=\"20\" cellspacing=\"0\" style=\"background-color: white; margin: auto; box-shadow: 0 0 10px rgba(0,0,0,0.1); min-height: 400px;\">\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td align=\"center\" style=\"margin-bottom: 20px;\">\r\n"
				+ "                            <img src=\"https://argofruta.com/wp-content/uploads/2021/05/Logo-text-green.png\" alt=\"ArgoFruta Logo\" width=\"250\" style=\"margin-top: 30px;\">\r\n"
				+ "                        </td>\r\n"
				+ "                    </tr>\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td>\r\n"
				+ "                            <h2 style=\"font-family: Poppins, sans-serif; color: #000000; margin-top: 0; font-weight: 400; text-align: center;\">Comprovante Argo Fruta</h2>\r\n"
				+ "                            <div style=\"border: 1px solid rgba(0, 0, 0, .05); max-width: 80%; margin: 0 auto; padding: 1.5em;\">\r\n"
				+ "                                <p style=\"font-size: 14px; color: #333333; margin: 5px 0;\">ARGOFRUTA COMERCIAL EXPORTADORA LTDA</p>\r\n"
				+ "                                <p style=\"font-size: 14px; color: #333333; margin: 5px 0;\">BR-407 - Núcleo 02 - PISNC, Lote - 615 - S/N Zona Rural, PE, 56300-000</p>\r\n"
				+ "                                <p style=\"font-size: 14px; color: #333333; margin: 5px 0;\">CNPJ: 07.344.594/0005-20</p>\r\n"
				+ "                            </div>\r\n"
				+ "                            <br>\r\n"
				+ "                            <div style=\"border: 1px solid rgba(0, 0, 0, .05); max-width: 80%; margin: 0 auto; padding: 1.5em;\">\r\n"
				+ "                                <h4 style=\"color: #333333; margin: 5px 0;\">Número da Nota:</h4>\r\n"
				+ "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + numNota + "</p>\r\n"
				+ "                                <hr style=\"border: 0; border-top: 1px solid #ccc;\">\r\n"
				+ invoiceCmaBloco
				+ "                                <h4 style=\"color: #333333; margin: 5px 0;\">Data do pagamento:</h4>\r\n"
				+ "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + dataBaixa + "</p>\r\n"
				+ "                                <hr style=\"border: 0; border-top: 1px solid #ccc;\">\r\n"
				+ "                                <h4 style=\"color: #333333; margin: 5px 0;\">Valor total:</h4>\r\n"
				+ "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + formatarMoeda(vlrBaixa) + "</p>\r\n"
				+ "                                <hr style=\"border: 0; border-top: 1px solid #ccc;\">\r\n"
				+ "                                <h4 style=\"color: #333333; margin: 5px 0;\">Parceiro:</h4>\r\n"
				+ "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + parceiro + "</p>\r\n"
				+ "                                <hr style=\"border: 0; border-top: 1px solid #ccc;\">\r\n"
				+ "                                <h4 style=\"color: #333333; margin: 5px 0;\">E-mail:</h4>\r\n"
				+ "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + email + "</p>\r\n"
				+ "                            </div>\r\n"
				+ "                            <br>\r\n"
				+ "                        </td>\r\n"
				+ "                    </tr>\r\n"
				+ "                </table>\r\n"
				+ "            </td>\r\n"
				+ "        </tr>\r\n"
				+ "    </table>\r\n"
				+ "</body>\r\n"
				+ "</html>";
	}
}