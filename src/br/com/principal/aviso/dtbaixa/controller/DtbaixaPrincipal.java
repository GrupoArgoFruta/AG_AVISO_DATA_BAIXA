package br.com.principal.aviso.dtbaixa.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import com.sankhya.util.JdbcUtils;

import br.com.principal.aviso.dtbaixa.service.EnvioEmailService;
import br.com.principal.aviso.dtbaixa.service.NotificacaoService;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class DtbaixaPrincipal implements ScheduledAction{

	@Override
	public void onTime(ScheduledActionContext sac) {
		// TODO Auto-generated method stub
		NotificacaoService notify = new NotificacaoService();
		EnvioEmailService EmailServiceNotif = new EnvioEmailService();
		String titulo = "titulo";
		JdbcWrapper jdbc = null;
		NativeSql queryVoa = null;
		ResultSet rset = null;
		SessionHandle hnd = null;
		
		try {
			// Abre uma sessão no banco de dados
			hnd = JapeSession.open();
			hnd.setFindersMaxRows(-1);
			// Obtém uma instância para interagir com o banco de dados
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			
			// Cria uma consulta SQL
			queryVoa = new NativeSql(jdbc);
			
			queryVoa.appendSql("SELECT VGFFIN.VLRBAIXA, \r\n"
					+ "FIN.NUFIN, \r\n"
					+ "VGFFIN.DHBAIXA, \r\n"
					+ "PAR.RAZAOSOCIAL,\r\n"
					+ "PAR.AD_ATIVOVLRBAIXA,\r\n"
					+ "PAR.EMAIL, \r\n"
					+ "PAR.CODPARC\r\n"
					+ "\r\n"
					+ "FROM\r\n"
					+ "\r\n"
					+ "VGFFIN VGFFIN\r\n"
					+ "INNER JOIN TGFVEN VEN ON\r\n"
					+ "(VEN.CODVEND = VGFFIN.CODVEND)\r\n"
					+ "INNER JOIN TGFPAR PAR ON\r\n"
					+ "(PAR.CODPARC = VGFFIN.CODPARC)\r\n"
					+ "INNER JOIN TSIEMP EMP ON\r\n"
					+ "(EMP.CODEMP = VGFFIN.CODEMP)\r\n"
					+ "INNER JOIN TGFNAT NAT ON\r\n"
					+ "(NAT.CODNAT = VGFFIN.CODNAT)\r\n"
					+ "INNER JOIN TGFTIT TIT ON\r\n"
					+ "(TIT.CODTIPTIT = VGFFIN.CODTIPTIT)\r\n"
					+ "INNER JOIN TSICUS CUS ON\r\n"
					+ "(CUS.CODCENCUS = VGFFIN.CODCENCUS)\r\n"
					+ "INNER JOIN TSIBCO BCO ON\r\n"
					+ "(BCO.CODBCO = VGFFIN.CODBCO)\r\n"
					+ "INNER JOIN TGFFIN FIN ON\r\n"
					+ "(FIN.NUFIN = VGFFIN.NUFIN)\r\n"
					+ "INNER JOIN TGFTOP TPP ON\r\n"
					+ "(TPP.CODTIPOPER = VGFFIN.CODTIPOPER\r\n"
					+ "	AND TPP.DHALTER = VGFFIN.DHTIPOPER)\r\n"
					+ "INNER JOIN TCSPRJ PROJ ON\r\n"
					+ "(PROJ.CODPROJ = VGFFIN.CODPROJ)\r\n"
					+ "LEFT JOIN TSICTA CTA ON\r\n"
					+ "CTA.CODCTABCOINT = VGFFIN.CODCTABCOINT\r\n"
					+ "LEFT JOIN TSIMOE Moeda ON\r\n"
					+ "Moeda.CODMOEDA = VGFFIN.CODMOEDA\r\n"
					+ "\r\n"
					+ "\r\n"
					+ "WHERE VGFFIN.RECDESP = -1\r\n"
					+ "AND (VGFFIN.DHBAIXA = TRUNC(TO_DATE('2024-05-14', 'yyyy-mm-dd')))\r\n"
					+ "AND NOT (VGFFIN.PROVISAO = 'S'\r\n"
					+ "	AND VGFFIN.DHBAIXA IS NOT NULL)\r\n"
					+ "AND VGFFIN.PROVISAO IN ('N', 'S')\r\n"
					+ "AND PAR.AD_ATIVOVLRBAIXA = 'S'"
					+ "AND PAR.EMAIL IS NOT NULL -- Verifica se possui e-mail"
					+ "AND VGFFIN.DHBAIXA IS NOT NULL -- Verifica se possui data de baixa"
					+ "AND VGFFIN.VLRBAIXA > 0 -- Verifica se possui valor de baixa positivo");
			
			// Executa a consulta SQL e obtém o conjunto de resultados
			rset = queryVoa.executeQuery();
			
			while (rset.next()) {
				// Obtém os valores das colunas do resultado da consulta
				BigDecimal NroUnico = rset.getBigDecimal("NUFIN");
				BigDecimal vlrbaixa = rset.getBigDecimal("VLRBAIXA");
				// Converte as datas para o formato desejado
				
				Date databaixa = rset.getDate("DHBAIXA");
				SimpleDateFormat dtbaixaform = new SimpleDateFormat("dd/MM/yyyy");
				String databaixxFormatada = dtbaixaform.format(databaixa);
				String  parceiro = rset.getString("RAZAOSOCIAL");
				String  email = rset.getString("EMAIL");
				String  AtivoParaNotificar = rset.getString("AD_ATIVOVLRBAIXA");
				
				
				String mensagemNotificacao = "<html>\r\n"
					    + "<head>\r\n"
					    + "    <title>Comprovante Argo Fruta</title>\r\n"
					    + "    <link href=\"https://fonts.googleapis.com/css?family=Poppins:200,300,400,500,600,700\" rel=\"stylesheet\">\r\n"
					    + "</head>\r\n"
					    + "<body style=\"background-color: #f4f4f4; margin: 0; padding: 0; width: 100%; height: 100%; font-family: Poppins, sans-serif; color: rgba(0, 0, 0, .4);\">\r\n"
					    + "    <table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n"
					    + "        <tr>\r\n"
					    + "            <td align=\"center\" valign=\"top\" style=\"padding-top: 20px; padding-bottom: 20px;\">\r\n"
					    + "                <table width=\"600\" border=\"0\" cellpadding=\"20\" cellspacing=\"0\" style=\"background-color: white; margin: auto; box-shadow: 0 0 10px rgba(0,0,0,0.1); min-height: 400px;\">\r\n"
					    + "                    <tr>\r\n"
					    + "                        <td align=\"center\" style=\"margin-bottom: 20px;\">\r\n"
					    + "                            <img src=\"https://argofruta.com/wp-content/uploads/2021/05/Logo-text-green.png\" alt=\"Argo Fruta Logo\" width=\"250\" style=\"margin-top: 30px;\">\r\n"
					    + "                        </td>\r\n"
					    + "                    </tr>\r\n"
					    + "                    <tr>\r\n"
					    + "                        <td>\r\n"
					    + "                            <h2 style=\"font-family: Poppins, sans-serif; color: #000000; margin-top: 0; font-weight: 400;text-align: center;\">Comprovante Argo Fruta</h2>\r\n"
					    + "                            \r\n"
					    + "                            <div style=\"border: 1px solid rgba(0, 0, 0, .05); max-width: 80%; margin: 0 auto; padding: 1.5em;\">\r\n"
					    + "                                <p style=\"font-size: 14px; color: #333333; margin: 5px 0;\">ARGOFRUTA COMERCIAL EXPORTADORA LTDA</p>\r\n"
					    + "                                <p style=\"font-size: 14px; color: #333333; margin: 5px 0;\">BR-407 - Núcleo 02 - PISNC, Lote - 615 - S/N Zona Rural, PE, 56300-000</p>\r\n"
					    + "                                <p style=\"font-size: 14px; color: #333333; margin: 5px 0;\">CNPJ: 07.344.594/0005-20</p>\r\n"
					    + "                            </div>\r\n"
					    + "                            <br>\r\n"
					    + "                            <div style=\"border: 1px solid rgba(0, 0, 0, .05); max-width: 80%; margin: 0 auto; padding: 1.5em;\">\r\n"
					    + "                                <h4 style=\"color: #333333; margin: 5px 0;\">Número do Documento:</h4>\r\n"
					    + "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + NroUnico + "</p>\r\n"
					    + "                                <hr style=\"border: 0; border-top: 1px solid #ccc;\">\r\n"
					    + "                                <h4 style=\"color: #333333; margin: 5px 0;\">Data do pagamento:</h4>\r\n"
					    + "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + databaixxFormatada + "</p>\r\n"
					    + "                                <hr style=\"border: 0; border-top: 1px solid #ccc;\">\r\n"
					    + "                                <h4 style=\"color: #333333; margin: 5px 0;\">Valor total:</h4>\r\n"
					    + "                                <p style=\"font-size: 14px; color: #000000; margin: 5px 0;\">" + vlrbaixa + "</p>\r\n"
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

				if (email != null && !email.isEmpty() && databaixa != null && vlrbaixa != null 	&& vlrbaixa.compareTo(BigDecimal.ZERO) > 0 && "S".equals(AtivoParaNotificar)) {
//					notify.notifUsuUnico(titulo, mensagemNotificacao);
					EmailServiceNotif.enviarEmail(titulo, mensagemNotificacao);

				} else {
					sac.info("Registro com condições inválidas encontrado. Email não enviado.");
					
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			// Exibir erro
			e.printStackTrace();
			sac.info("Erro Evento Programado Atualização de emails para contratos: " + e.getMessage());
		}finally {
			// Liberação de recursos e fechamento da sessão
			JdbcUtils.closeResultSet(rset);
			JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
			NativeSql.releaseResources(queryVoa);
		}	
	}

}
